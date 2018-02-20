(ns ote.services.external
  "CKAN-integration tasks. Functions to invoke CKAN API calls from OTE."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [ote.db.transport-service :as t-service]
            [ote.db.modification :as modification]
            [org.httpkit.client :as httpkit]
            [compojure.core :refer [routes GET POST DELETE]]
            [taoensso.timbre :as log]
            [ote.authorization :as authorization]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.string :as s]
            [specql.core :as specql]
            [clojure.java.io :as io]))

(defn valid-csv-header?
  "Ensure that there is at least 2 elements in header"
  [header]
  (>= (count header) 2))

(defn valid-business-id? [value]
  (let [pattern  #"\d{7}-\d"]
    (boolean (re-matches pattern value))))

(defn ensure-url
  "Add http:// to the beginning of the given url if it doesn't exist."
  [url]
  (if (not (s/includes? url "http"))
     (str "http://" url)
     url))

(defn parse-response->csv
  "Convert given vector to map where map key is given in the first line of csv file."
  [csv-data]
  (let [headers (first csv-data)
        valid-header? (valid-csv-header? headers)
        parsed-data (when valid-header?
                      (map (fn [cells]
                             (let [[business-id name] (map str/trim cells)]
                               {::t-service/business-id business-id
                                ::t-service/name name}))
                            (rest csv-data)))
        validated-data (filter #(valid-business-id? (::t-service/business-id %)) parsed-data)]
    {:result validated-data
     :failed-count (- (count parsed-data) (count validated-data))}))

(defn save-companies
  "Save business-ids, company names to db"
  [db data]
   (let [data (modification/with-modification-fields data ::t-service/id)]
    (specql/upsert! db ::t-service/service-company data)))

(defn- read-csv
  "Read CSV from input stream. Guesses the separator from the first line."
  [input]
  (with-open [in (io/reader input)]
    (let [csv-content (StringBuilder.)]
      (loop [separator nil
             [l & lines] (line-seq in)]
        (if (nil? l)
          (csv/read-csv (str csv-content) :separator separator)
          (do
            (.append csv-content l)
            (.append csv-content "\n")
            (recur (or separator
                       (if (str/includes? l ";")
                         \;
                         \,))
                   lines)))))))

(defn check-csv
  "Fetch csv file from given url, parse it and return status map that contains information about the count of companies
  in the file."
  [url-data]
  (let [url (ensure-url (get url-data :url))
        response @(httpkit/get url {:as :stream
                                    :timeout 30000})]
    (if (= 200 (:status response))
      (try
        (let [data (when (= 200 (:status response))
                     (with-open [in (:body response)]
                       (read-csv in)))
              parsed-data (parse-response->csv data)]
          ;; response to client application
          {:status :success
           :count (count (:result parsed-data))
           :failed-count (:failed-count parsed-data)
           :companies (:result parsed-data)})
        (catch Exception e
          (log/info "CSV parsing failed: " e)
          {:status :failed
           :error :csv-parse-failed}))
      {:status :failed
       :error :url-parse-failed
       :http-status (:status response)})))

(defn- check-external-api
  [url-data]
  (let [url (ensure-url (get url-data :url))
        response @(httpkit/get url {:as :text
                                    :timeout 30000})]
    (if (= 200 (:status response))
      {:status :success}
      {:status :failed})))

(defn- external-routes-auth
  "Routes that require authentication"
  [db nap-config]
  (routes
   (POST  "/check-company-csv" {url-data :body}
          (http/transit-response
           (check-csv (http/transit-request url-data ))))
   (POST  "/check-external-api" {url-data :body}
          (http/transit-response
           (check-external-api
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
