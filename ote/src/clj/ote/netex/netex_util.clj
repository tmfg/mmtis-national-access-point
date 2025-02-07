(ns ote.netex.netex_util
  "Utilities for working with netex objects"
  (:require [specql.core :as specql]
            [specql.op :as op]
            [jeesql.core :refer [defqueries]]
            [ote.db.netex :as netex]
            [ote.db.transport-service :as t-service]
            [ote.integration.tis-vaco :as tis-vaco]))

(defqueries "ote/tasks/gtfs.sql")
(declare fetch-latest-gtfs-vaco-status)

(defn file-download-url [{{base-url :base-url} :environment} transport-service-id file-id]
  (format "%sexport/netex/%d/%d" base-url transport-service-id file-id))

(defn assoc-download-url [config conversions]
  (map
    #(if (= :ok (keyword (::netex/status %)))
       (assoc %
         :url
         (file-download-url config
                            (::netex/transport-service-id %)
                            (::netex/id %)))
       %)
    conversions))

(defn fetch-conversions-for-services
  "Fetches successful netex conversion records for services in `service-id`s and appends each with a download :url key.
  Returns a sequence of collections."
  [config db service-ids]
  (->> (specql/fetch db
                     ::netex/netex-conversion
                     (specql/columns ::netex/netex-conversion)
                     (op/and
                       {::netex/transport-service-id (op/in service-ids)}
                       {::netex/status :ok}
                       {::netex/filename op/not-null?}))
       (assoc-download-url config)))

(defn append-ote-netex-urls
  "`services` = collection of services
  `config` = object which contains ote configuration properties for the environment
  `db`= ote database
  `ext-ifs-key` = A key, within a service object, which contains external interface objects collection
  Return: collection of services with an ote-generated netex appended to those external interfaces for which a netex url is available."
  [services config db ext-ifs-key]
  (let [conversions (fetch-conversions-for-services config db (set (map #(::t-service/id %)
                                                                        services)))]
    (if (seq conversions)
      (mapv (fn [service]
              (update service
                      ext-ifs-key
                      (fn [interfaces]
                        (vec
                          (for [interface interfaces
                                :let [interface-id (::t-service/id interface)]]
                            (if-let [interface-conversion (some (fn [conversion]
                                                                  (when (= interface-id
                                                                           (::netex/external-interface-description-id conversion))
                                                                    conversion))
                                                                conversions)]
                              (assoc interface
                                :url-ote-netex (file-download-url config
                                                                  (::netex/transport-service-id interface-conversion)
                                                                  (::netex/id interface-conversion))
                                :tis-vaco (tis-vaco/fetch-public-data db config interface-id (::netex/package_id interface-conversion)))
                              interface))))))
            services)
      ;; If there is not netex data, add only a vaco link to get more information
      (mapv (fn [service]
                (update service
                        ext-ifs-key
                        (fn [interfaces]
                            (vec
                              (for [interface interfaces
                                    :let [interface-id (::t-service/id interface)
                                          service-id (::t-service/transport-service-id interface)
                                          latest-gtfs (first (fetch-latest-gtfs-vaco-status db {:service-id service-id
                                                                                                :interface-id interface-id}))]]
                                   (assoc interface
                                          :tis-vaco {:gtfs/tis-magic-link (:tis-magic-link latest-gtfs)
                                                     :gtfs/tis-entry-public-id (:tis-entry-public-id latest-gtfs)
                                                     :api-base-url (get-in config [:tis-vaco :api-base-url])}))))))
            services))))
