(ns ote.integration.import.kalkati
  "Kalkati file import functionality."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.integration.import.gtfs :as gtfs-import]
            [amazonica.aws.lambda :as lambda]
            [cheshire.core :as cheshire]
            [clojure.walk :as walk]
            [taoensso.timbre :as log]))


(defn- decode-payload [lambda-response]
  (-> lambda-response
      :payload
      .array
      (String. "UTF-8")
      (cheshire/decode keyword)))

(defn kalkati-to-gtfs
  "Invoke an kalkati_to_gtfs Lambda function directly through AWS SDK.
  Returns InvokeResult."
  [kalkati-url headers]
  (let [headers (select-keys headers ["if-modified-since"])]
    (decode-payload
     (lambda/invoke :function-name "kalkati_to_gtfs"
                    :region "eu-central-1"
                    :invocation-type "RequestResponse"
                    ;; We also want to support invoking lambda functions through API Gateway lambda proxy
                    ;; so we'll have to encode the :body separately.
                    :payload (cheshire/encode {:body (cheshire/encode kalkati-url)
                                               :headers headers})))))



(defmethod gtfs-import/load-transit-interface-url :kalkati [_ db interface-id url last-import-date saved-etag]
  (log/info "Loading kalkati interface: " url)
  (let [{:keys [statusCode headers] :as response}
        (kalkati-to-gtfs url {"if-modified-since" last-import-date})]
    (log/debug "Got kalkati-to-gtfs response: " response)
    (if (= statusCode 303)
      (http-client/get (:Location headers) {:as :byte-array})
      (do
        (log/warn "Kalkati to GTFS conversion failed, returned: " (pr-str response))
        {:error (str "Kalkati - GTFS muunnos epäonnistui tiedostolle, joka löytyi osoitteesta: " url)}))))

(defn load-kalkati [url headers]
  (let [json (kalkati-to-gtfs url headers)
        resp-headers (:headers json)
        status-code (:statusCode json)
        gtfs-url (:Location resp-headers)]

    ;; If lambda invocation has errors, or there are errors in the third-party kalkati interface
    ;; get error headers and status code from Lambda function payload and pass them through.
    ;; Note: kalkati2gtfs Lambda returns 303 after a successful execution.
    (if-not (= status-code 303)
      (merge
        {:status status-code}
        (when resp-headers
          {:headers (walk/stringify-keys
                      (dissoc resp-headers :Location))}))
      (gtfs-import/load-gtfs gtfs-url))))

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
