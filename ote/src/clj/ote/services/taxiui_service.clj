(ns ote.services.taxiui-service
  (:require [camel-snake-kebab.core :as csk]
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
            [specql.op :as op]))

(defqueries "ote/services/taxiui_service.sql")

(defn fetch-priceinfo-for-service
  [db user {service-id  :service-id
            operator-id :operator-id :as form-data}]
  (log/info (str "fetch-priceinfo-for-service " form-data))
  (authorization/with-transport-operator-check
    db user operator-id
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
    db user operator-id
    (fn []
      (tx/with-transaction
        db
        (let [service-id                       (Integer/parseInt service-id)
              {:keys [prices operating-areas]} price-info]
          (insert-price-information! db (into {:service-id service-id}
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

(defn fetch-pricing-statistics
  [db {:keys [column direction]}]
  (vec (list-pricing-statistics db {:column    (csk/->snake_case_string (or column :start-price-daytime))
                                    :direction (= direction :ascending)})))

(defrecord TaxiUIService []
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
                (http/publish!
                  http
                  (routes
                    (POST "/taxiui/price-info" {user      :user
                                               form-data :body}
                      (log/info "form data " form-data)
                      (http/transit-response
                        (fetch-priceinfo-for-service db user (http/transit-request form-data))))

                    (POST "/taxiui/price-info/:operator-id/:service-id" {{:keys [operator-id service-id]} :params
                                                                         user                             :user
                                                                         form-data                        :body}
                         (http/transit-response
                           (update-priceinfo-for-service db user operator-id service-id (http/transit-request form-data))))

                    ^:unauthenticated
                    (POST "/taxiui/statistics" {form-data :body}
                      (log/info "form data " form-data)
                      (http/transit-response
                        (fetch-pricing-statistics db (http/transit-request form-data))))))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))