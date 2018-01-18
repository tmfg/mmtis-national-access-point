(ns ote.services.external
  "CKAN-integration tasks. Functions to invoke CKAN API calls from OTE."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [org.httpkit.client :as httpkit]
            [compojure.core :refer [routes GET POST DELETE]]
            [taoensso.timbre :as log]
            [ote.authorization :as authorization]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.string :as s]))

(defn ensure-url
  "Add http:// to the beginning of the given url if it doesn't exist."
  [url]
  (if (not (s/includes? url "http"))
     (str "http://" url)
     url))

(defn parse-response->csv
  "Convert given vector to map where map key is given in the first line of csv file."
  [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn- check-csv
  "Fetch csv file from given url, parse it and return status map that contains information about the count of companies
  in the file."
  [url-data]
  (let [url (ensure-url (get url-data :url))
        response @(httpkit/get url {:as :text
                                    :timeout 30000})]
    (if (= 200 (:status response))
      (try
        (let [data (when (= 200 (:status response))
                               (csv/read-csv (:body response)))]
            {:status :success
             :count (count (parse-response->csv data))})
        (catch Exception e
          (log/info "CSV parsing failed: " e)
          {:status :failed
           :error :csv-parse-failed}))
      {:status :failed
       :error :url-parse-failed
       :http-status (:status response)})))

(defn- external-routes-auth
  "Routes that require authentication"

  [db nap-config]
  (routes

    (POST  "/check-company-csv"  {url-data :body}
          (http/transit-response
            (check-csv
              (http/transit-request url-data ))))))


(defrecord External [nap-config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http (external-routes-auth db nap-config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
