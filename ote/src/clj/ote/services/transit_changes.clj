(ns ote.services.transit-changes
  (:require [ote.components.service :refer [define-service-component]]
            [ote.components.http :as http]
            [compojure.core :refer [GET POST]]
            [jeesql.core :refer [defqueries]]
            [ote.time :as time]
            [clojure.string :as str]
            [clojure.set :as set]
            [ote.util.db :refer [PgArray->vec]]
            [ote.db.places :as places]
            [specql.core :as specql]
            [ote.authorization :as authorization]
            [ote.tasks.gtfs :as gtfs-tasks]
            [taoensso.timbre :as log]
            [ote.transit-changes.detection :as detection]))

(defqueries "ote/services/transit_changes.sql")
(defqueries "ote/integration/import/import_gtfs.sql")

(defn- parse-weekhash [weekhash]
  (if (nil? weekhash)
    {:days-with-traffic #{}
     :day->hash {}}
    (let [days (set/rename-keys
                (into {}
                      (map #(let [[day hash] (str/split % #"=")]
                              [day hash]))
                      (str/split weekhash #","))
                (zipmap (map str (range 1 8)) time/week-days))]
      {:days-with-traffic (into #{}
                                (comp
                                 (map (fn [[day traffic]]
                                        (when-not (str/blank? traffic)
                                          day)))
                                 (remove nil?))
                                (seq days))
       :day->hash days})))

(defn describe-week-difference [difference]
  (assoc difference
         :current-week-traffic (parse-weekhash (:current-weekhash difference))
         :different-week-traffic (parse-weekhash (:different-weekhash difference))))

(defn list-current-changes [db]
  {:finnish-regions (specql/fetch db ::places/finnish-regions #{::places/numero ::places/nimi} {})
   :changes (into []
                  (map #(update % :finnish-regions (fn [region-list]
                                                     (when region-list
                                                       (into #{} (str/split region-list #","))))))
                  (upcoming-changes db))})

(define-service-component TransitChanges {:fields [config]}

  ^{:unauthenticated true :format :transit}
  (GET "/transit-changes/current" []
       (#'list-current-changes db))

  (GET "/transit-changes/force-calculate-hashes/:service-id/:package-count" [service-id package-count :as {user :user}]
    (when (authorization/admin? user)
      (detection/calculate-package-hashes-for-service db (Long/parseLong service-id) (Long/parseLong package-count))
      "OK"))

  (GET "/transit-changes/force-calculate-route-hash-id/:service-id/:package-count/:type" [service-id package-count type :as {user :user}]
    (when (authorization/admin? user)
        (detection/calculate-route-hash-id-for-service db (Long/parseLong service-id) (Long/parseLong package-count) type)
        "OK"))

  (POST "/transit-changes/force-detect" req
        (when (authorization/admin? (:user req))
          (gtfs-tasks/detect-new-changes-task db true)
          "OK"))
  ;; Delete row from gtfs_package to make this work. Don't know why, but it must be done.
  ;; Also change external-interface-description.gtfs-imported to past to make import work because we only import new packages.
  (POST "/transit-changes/force-interface-import" req
    (when (authorization/admin? (:user req))
      (gtfs-tasks/update-one-gtfs! config db false)
      "OK")))
