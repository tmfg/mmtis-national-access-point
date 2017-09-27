(ns ote.communication
  "Communication with OTE server. Wrappers for AJAX calls and helpers for URL paths."
  (:require [ajax.core :as ajax :refer [GET POST]]))

(defn get!
  "Make a GET  request to the given URL.
  URL parameters can be given with the `:params` key.
  Callbacks for successfull and failure are provided with `:on-success` and `:on-failure`
  keys respectively"
  [url {:keys [params on-success on-failure response-format]}]
  (GET url {:params params
            :handler on-success
            :error-handler on-failure
            :response-format (or response-format :transit)}))
