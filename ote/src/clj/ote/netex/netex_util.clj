(ns ote.netex.netex_util
  "Utilities for working with netex objects"
  (:require [specql.core :as specql]
            [specql.op :as op]
            [ote.db.netex :as netex]
            [ote.db.transport-service :as t-service]))

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
  (let [conversions (fetch-conversions-for-services config
                                                    db
                                                    (set (map #(::t-service/id %)
                                                              services)))]
    (mapv (fn [service]
            (update service
                    ext-ifs-key
                    (fn [interfaces]
                      (vec
                        (for [interface interfaces
                              :let [service-id (::t-service/id interface)]]
                          (if-let [interface-conversion (some (fn [conversion]
                                                                (when (= service-id
                                                                         (::netex/transport-service-id conversion))
                                                                  conversion))
                                                              conversions)]
                            (assoc interface :url-ote-netex
                                             (file-download-url config
                                                                service-id
                                                                (::netex/id interface-conversion)))
                            interface))))))
          services)))
