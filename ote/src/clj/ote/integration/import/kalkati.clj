(ns ote.integration.import.kalkati
  "Kalkati file import functionality."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs-import]
            [ote.db.transport-service :as t-service]
            [amazonica.aws.lambda :as lambda]
            [cheshire.core :as cheshire]
            [clojure.walk :as walk]
            [taoensso.timbre :as log]
            [specql.core :as specql]
            [ote.gtfs.kalkati-to-gtfs :as kalkati-to-gtfs]))




(defn kalkati-to-gtfs
  "Invoke an kalkati_to_gtfs Lambda function directly through AWS SDK.
  Returns InvokeResult."
  [kalkati-url headers]
  (let [headers (select-keys headers ["if-modified-since"])]
    ()
    (decode-payload
     (lambda/invoke :function-name "kalkati_to_gtfs"
                    :region "eu-central-1"
                    :invocation-type "RequestResponse"
                    ;; We also want to support invoking lambda functions through API Gateway lambda proxy
                    ;; so we'll have to encode the :body separately.
                    :payload (cheshire/encode {:body (cheshire/encode kalkati-url)
                                               :headers headers})))))





(defn load-kalkati [url headers]
  (let [resp (http-client/get url {:headers headers
                                   :as :stream})]
    (if (= (:status resp) 200)
      (gtfs-import/load-gtfs (update resp :body kalkati-to-gtfs/convert))
      resp)))

(defrecord KalkatiImport []
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
                (http/publish! http {:authenticated? false}
                               (routes
                                 (GET "/import/kalkati" {params :query-params
                                                         headers :headers}
                                   (load-kalkati (get params "url") headers))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
