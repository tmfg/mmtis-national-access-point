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
            [ote.transit-changes.detection :as detection]
            [ote.db.transport-service :as t-service]
            [ote.time :as time]
            [ote.integration.import.gtfs :as import]
            [clj-time.core :as t]))

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

(defn to-byte-array
  "Convert file to byte[] array"
  [file]
  (let [ary (byte-array (.length file))
        is (java.io.FileInputStream. file)]
    (.read is ary)
    (.close is)
    ary))

(defn upload-gtfs
  "Upload gtfs file, parse and calculate date hashes. Set package created column to the date that is given to
  simulate production situation where package is handled at given day."
  [db service-id date req]
  (try
    (let [uploaded-file (get-in req [:multipart-params "file"])
          _ (assert (and (:filename uploaded-file)
                         (:tempfile uploaded-file))
                    "No uploaded file")
          operator-id (::t-service/transport-operator-id (first (specql/fetch
                                                                  db ::t-service/transport-service
                                                                  #{::t-service/transport-operator-id}
                                                                  {::t-service/id service-id})))
          interface-id (::t-service/id (first (specql/fetch db ::t-service/external-interface-description
                                                            #{::t-service/id}
                                                            {::t-service/transport-service-id service-id})))
          latest-package (import/interface-latest-package db interface-id)
          package (specql/insert! db :gtfs/package
                                  {:gtfs/first_package                     (nil? latest-package)
                                   :gtfs/transport-operator-id             operator-id
                                   :gtfs/transport-service-id              service-id
                                   :gtfs/created                           (time/date-string->inst-date date)
                                   :gtfs/external-interface-description-id interface-id})
          result (import/save-gtfs-to-db db (to-byte-array (:tempfile uploaded-file)) (:gtfs/id package) interface-id service-id)]
      "OK")
    (catch Exception e
      (let [msg (.getMessage e)]
        (log/error "upload-gtfs ERROR" msg)
        (case msg
          "Invalid file type"
          {:status 422
           :body   "Invalid file type."}
          {:status 500
           :body   msg})))))

(defn services-with-route-hash-id [db]
  (fetch-services-with-route-hash-id db))

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

  (GET "/transit-changes/force-monthly-day-hash-calculation" {user :user}
    (when (authorization/admin? user)
      (http/transit-response
        {:status 200
         :body (detection/calculate-monthly-date-hashes-for-packages db)})))

  (GET "/transit-changes/force-all-day-hash-calculation" {user :user}
    (when (authorization/admin? user)
      (http/transit-response
        {:status 200
         :body (detection/calculate-date-hashes-for-all-packages db)})))

  (GET "/transit-changes/load-services-with-route-hash-id" req
    (when (authorization/admin? (:user req))
      (http/transit-response (services-with-route-hash-id db))))

  (POST "/transit-changes/force-detect" req
        (when (authorization/admin? (:user req))
          (gtfs-tasks/detect-new-changes-task db (time/now) true)
          "OK"))

  (POST "/transit-changes/force-detect-date/:date"
        {{:keys [date]} :params
         user           :user
         :as            req}
    (when (authorization/admin? user)
      (gtfs-tasks/detect-new-changes-task db (time/date-string->date-time date) true)
      "OK"))

  ;; Delete row from gtfs_package to make this work. Don't know why, but it must be done.
  ;; Also change external-interface-description.gtfs-imported to past to make import work because we only import new packages.
  (POST "/transit-changes/force-interface-import" req
    (when (authorization/admin? (:user req))
      (gtfs-tasks/update-one-gtfs! config db false)
      "OK")))
