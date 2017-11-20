(ns ote.communication
  "Communication with OTE server. Wrappers for AJAX calls and helpers for URL paths."
  (:require [ajax.core :as ajax :refer [GET POST]]
            [cognitect.transit :as t]
            [ote.transit :as transit]))

(defonce base-url (atom ""))

(defn set-base-url! [url]
  (reset! base-url url))

(defn transit-request-format []
  (ajax/transit-request-format transit/write-options))

(defn transit-response-format []
  (ajax/transit-response-format {:reader (t/reader :json transit/read-options)
                                 :raw true}))

(defn- request-url [url]
  (str @base-url url))

(defn get!
  "Make a GET request to the given URL.
  URL parameters can be given with the `:params` key.
  Callbacks for successfull and failure are provided with `:on-success` and `:on-failure`
  keys respectively"
  [url {:keys [params on-success on-failure response-format]}]
  (GET (request-url url)
       {:params params
        :handler on-success
        :error-handler on-failure
        :response-format (or response-format (transit-response-format))}))

(defn post!
  "Make a POST request to the given URL.
  URL parameters can be given with the `:body` key.
  Callbacks for successfull and failure are provided with `:on-success` and `:on-failure`
  keys respectively"
  [url body {:keys [on-success on-failure response-format]}]
  (POST (request-url url)
        {:params body
         :handler on-success
         :error-handler on-failure
         :format (transit-request-format)
         :response-format (or response-format (transit-response-format))}))
