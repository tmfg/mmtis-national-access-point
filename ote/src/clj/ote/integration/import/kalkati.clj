(ns ote.integration.import.kalkati
  "Kalkati file import functionality."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs-import]
            [amazonica.aws.lambda :as lambda]
            [cheshire.core :as cheshire]))


(defn kalkati-to-gtfs
  "Invoke an kalkati_to_gtfs Lambda function directly through AWS SDK.
  Returns InvokeResult."
  [kalkati-url]
  (lambda/invoke :function-name "kalkati_to_gtfs"
                 :region "eu-central-1"
                 :invocation-type "Event"
                 :payload (cheshire/encode {:body kalkati-url})))

(defn load-kalkati [url]
  (let [payload (.getPayload (kalkati-to-gtfs url))
        json (cheshire/decode (String. (.array payload) "UTF-8") keyword)
        gtfs-url (:Location (:headers json))]
    (gtfs-import/load-gtfs gtfs-url)))

(defrecord KalkatiImport []
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
                (http/publish! http {:authenticated? false}
                               (routes
                                 (GET "/import/kalkati" {params :query-params}
                                   (load-kalkati (get params "url")))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
