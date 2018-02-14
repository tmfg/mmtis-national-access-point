(ns finap-load-test.main
  (:require [clj-gatling.core :as clj-gatling]
            [org.httpkit.client :as http]
            [clojure.string :as str]
            [clojure.core.async :refer [<! >! go put!] :as async]))

(def finap-base-url "https://finap.fi/")

(defn finap-url [& parts]
  (str finap-base-url (str/join parts)))

(defn status-ok? [{:keys [status] :as response}]
  (= 200 status))

(defn finap-get [ok-fn & url-parts]
  (let [ch (async/chan)
        url (apply finap-url url-parts)]
    (http/get url
              (fn [response]
                (put! ch (ok-fn response))))
    ch))

(defn service-search-by-type [type]
  (finap-get status-ok?
             "ote/service-search?type=" (name type)
             "&response_format=json"))

(def service-search-simulation
  {:name "Service search"
   :scenarios [{:name "Search taxis"
                :steps [{:name "Search taxis"
                         :request (fn [ctx]
                                    (service-search-by-type :taxi))}]}]})

(clj-gatling/run service-search-simulation
  {:concurrency 10
   :requests 10000})
