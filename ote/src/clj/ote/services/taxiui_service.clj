(ns ote.services.taxiui-service
  (:require [camel-snake-kebab.core :as csk]
            [clojure.core.memoize :as memo]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [jeesql.core :refer [defqueries]]
            [ote.components.service :refer [define-service-component]]
            [ote.components.http :as http]
            [ote.services.places :as places]
            [ote.authorization :as authorization]
            [taoensso.timbre :as log]
            [ote.db.tx :as tx]
            [clojure.string :as str]
            [specql.core :as specql]
            [specql.op :as op]
            [ote.util.db :as db-util])
  (:import (java.time OffsetDateTime LocalDateTime ZoneOffset)))

(defqueries "ote/services/taxiui_service.sql")

(defn- unknown->long
  [u]
  (if (= String (type u))
    (Long/parseLong u)
    u))

(defn fetch-priceinfo-for-service
  [db user {service-id  :service-id
            operator-id :operator-id :as form-data}]
  (authorization/with-transport-operator-check
    db user (unknown->long operator-id)
    (fn []
      (let [service-id (Integer/parseInt service-id)
            r (->> (select-price-information db {:service-id service-id})
                   first
                   (map (fn [[k v]]
                          [(csk/->kebab-case k)
                           ; XXX: There is a known defect in ns `ote.transit/read-options` function which removes
                           ; BigDecimal from CLJS side, so they have to be carried over as strings.
                           (if (decimal? v)
                             (str v)
                             v)]))
                   (into {}))]
        {:prices          r
         :operating-areas (places/fetch-transport-service-operation-area db service-id)}))))

(defn- store-new-operation-areas
  [db service-id new-areas]
  (places/save-transport-service-operation-area! db service-id new-areas false))

(defn- keep-service-operation-areas
  [db service-id areas-to-keep]
  (when-not (empty? areas-to-keep)
    (specql/delete! db :ote.db.transport-service/operation_area
                    (merge
                      {:ote.db.transport-service/transport-service-id service-id}
                      {:ote.db.transport-service/id (op/not (op/in areas-to-keep))}))))

(defn update-priceinfo-for-service
  [db user operator-id service-id price-info]
  (authorization/with-transport-operator-check
    db user (unknown->long operator-id)
    (fn []
      (tx/with-transaction
        db
        (let [service-id                       (Integer/parseInt service-id)
              {:keys [prices operating-areas]} price-info]
          (insert-price-information! db (into {:service-id service-id
                                               ; all prices default to zero to avoid null checking on the query side
                                               :start-price-daytime              0
                                               :start-price-nighttime            0
                                               :start-price-weekend              0
                                               :price-per-minute                 0
                                               :price-per-kilometer              0
                                               :accessibility-service-stairs     0
                                               :accessibility-service-stretchers 0
                                               :accessibility-service-fare       0}
                                              (map (fn [[k v]] [k (BigDecimal. ^String v)]) prices)))
          (when operating-areas
            (let [places (->> operating-areas
                              (group-by #(cond
                                           (:ote.db.places/id %)            :new-places
                                           (:ote.db.transport-service/id %) :existing-places
                                           :else                            :unknown-places)))]

              ; Note: Order matters here! If reversed, this would automatically clear the just added places.
              (some->> (:existing-places places)
                       (mapv :ote.db.transport-service/id)
                       (keep-service-operation-areas db service-id))

              (some->> (:new-places places)
                       (mapv (fn [place]
                               (merge (places/place-by-id db (:ote.db.places/id place))
                                      {:ote.db.places/primary? true})))
                       (store-new-operation-areas db service-id))

              (some->> (:unknown-places places)
                       (log/warnf "Detected unknown places while updating %s/%s details! %s" operator-id service-id)))))))))

(defn- age-filter
  "Returns age filter as PostgreSQL INTERVAL compatible string."
  [filters]
  (case (:age-filter filters)
    :within-six-months "6 months"
    :within-one-year   "11 months 27 days"  ; this is a minor hack to allow easy "diff >= 1 year" selection in SQL
    :over-year-ago     "1 year"
    "6 months"))

(defn- area-filter
  [filters]
  (-> filters :area-filter :label))

(defn- sanitize-pricing-output
  [pricing-statistics]
  (map (fn [stats] (update stats :operating-areas #(db-util/PgArray->vec %))) pricing-statistics))

; query performance is guarded by memoizing the results (with TTL)
(def ^:private memoized-list-operating-areas (memo/ttl #(list-operating-areas %) :ttl/threshold 60000))        ; 60 seconds
(def ^:private memoized-list-pricing-statistics (memo/ttl #(list-pricing-statistics %) :ttl/threshold 60000))  ; 60 seconds

(defn fetch-pricing-statistics
  [db {:keys [sorting filters]}]
  (let [{:keys [column direction]} sorting
        {:keys [area-filter name age-filter]} filters
        name-filter (if name
                      (fn [row]
                        (str/includes?
                          (str/upper-case (:name row))
                          (str/upper-case name)))
                      identity)
        oa-filter (if area-filter
                    (fn [row] (some (fn [oa]
                                      (str/includes?
                                        (str/upper-case oa)
                                        (str/upper-case (:label area-filter))))
                                    (:operating-areas row)))
                    identity)
        last-published-filter (if age-filter
                                (fn [row]
                                  (let [ts (-> (:timestamp row) .toInstant (LocalDateTime/ofInstant ZoneOffset/UTC))]
                                    (case age-filter
                                      :within-six-months (.isBefore (.minusMonths (LocalDateTime/now) 6) ts)
                                      :within-one-year   (.isBefore (.minusYears (LocalDateTime/now) 1) ts)
                                      :over-year-ago     (.isAfter (.minusYears (LocalDateTime/now) 1) ts))

                                    ))
                                identity)
        filtered (vec (->> (memoized-list-pricing-statistics db)
                           sanitize-pricing-output
                           (filter name-filter)
                           (filter oa-filter)
                           (filter last-published-filter)))]
    (if (nil? column)
      filtered
      (sort-by column (fn [a b] (if (= :ascending direction)
                                  (compare a b)
                                  (compare b a)))
               filtered))))

(defn fetch-service-pricing-statistics
  "Fetch latest approved pricing statistics for specific service."
  [db service-id]
  (first (->> (list-service-pricing-statistics db {:service-id (Long/parseLong service-id)})
              sanitize-pricing-output)))

(defn fetch-operating-areas
  [db {filter :filter}]
  (vec (->> (memoized-list-operating-areas db)
            (clojure.core/filter (fn [row] (str/includes? (:place row) filter))))))

(defn fetch-service-summaries
  [db user {}]
  (let [groups (authorization/user-transport-operators db user)]
    (vec (->> (list-service-summaries db {:operator-ids groups})
              (map (fn [service] (update service :operating-areas #(db-util/PgArray->vec %))))))))

(defn fetch-unapproved-prices
  [db user]
  (if (authorization/admin? user)
    (vec (->> (list-unapproved-prices db)
              (map (fn [service] (update service :operating-areas #(db-util/PgArray->vec %))))))
    (log/warn (str "Non-admin user " (authorization/user-id user) " tried to list unapproved pricings"))))

(defn mark-prices-approved
  [db user {pricing-ids :pricing-ids}]
  (if (authorization/admin? user)
    (doseq [pricing-id (filter some? pricing-ids)]
      (update-approved-status! db {:pricing-id pricing-id
                                   :user-id    (authorization/user-id user)}))
    (log/warn (str "Non-admin user " (authorization/user-id user) " tried to approve pricings " pricing-ids))))

(defrecord TaxiUIService []
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
                [(http/publish!
                   http
                   (routes
                     (POST "/taxiui/price-info" {user      :user
                                                 form-data :body}
                       (http/transit-response
                         (fetch-priceinfo-for-service db user (http/transit-request form-data))))

                     (POST "/taxiui/price-info/:operator-id/:service-id" {{:keys [operator-id service-id]} :params
                                                                          user                             :user
                                                                          form-data                        :body}
                       (http/transit-response
                         (update-priceinfo-for-service db user operator-id service-id (http/transit-request form-data))))

                     (POST "/taxiui/service-summaries" {user      :user
                                                        form-data :body}
                       (http/transit-response
                         (fetch-service-summaries db user (http/transit-request form-data))))

                     (GET "/taxiui/approvals" {user :user}
                       (http/transit-response
                         (fetch-unapproved-prices db user)))

                     (POST "/taxiui/approvals" {user      :user
                                                form-data :body}
                       (http/transit-response
                         (mark-prices-approved db user (http/transit-request form-data))))))
                 (http/publish!
                   http
                   {:authenticated? false}
                   (routes
                     (POST "/taxiui/statistics" {form-data :body}
                       (http/transit-response
                         (fetch-pricing-statistics db (http/transit-request form-data))))
                     (GET "/taxiui/statistics/:service-id" {{:keys [service-id]} :params}
                       (http/transit-response
                         (fetch-service-pricing-statistics db service-id)))
                     (POST "/taxiui/operating-areas" {form-data :body}
                       (http/transit-response
                         (fetch-operating-areas db (http/transit-request form-data))))))]))

  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
