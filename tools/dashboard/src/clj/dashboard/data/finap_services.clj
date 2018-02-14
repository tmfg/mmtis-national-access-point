(ns dashboard.data.finap-services
  (:require [org.httpkit.client :as http-client]
            [cheshire.core :as cheshire]))

(defn fetch-published-service-count []
  (-> "https://finap.fi/ote/service-search?response_format=json"
      http-client/get deref :body (cheshire/decode keyword)
      :total-service-count))
