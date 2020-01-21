(ns ote.services.external
  "Integration tasks."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [ote.util.csv :as csv-util]
            [ote.db.transport-service :as t-service]
            [ote.db.modification :as modification]
            [ote.integration.import.gtfs :as import-gtfs]
            [clj-http.client :as http-client]
            [compojure.core :refer [routes GET POST DELETE]]
            [taoensso.timbre :as log]
            [ote.authorization :as authorization]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.string :as s]
            [specql.core :as specql]
            [clojure.java.io :as io]))

(defn ensure-url
  "Add http:// to the beginning of the given url if it doesn't exist."
  [url]
  (str/replace (if-not (s/includes? url "http")
                  (str "http://" url)
                  url)
               #" " "%20"))

(defn validate-company-csv-file
  "Return map of validation data. Create warning message if illegal chars are used. We can be sure that
  company csv cannot contain other chars than [a-ö, A-Ö, 0-9, '.' ',', '-', ' ', '\"', '.']"
  [data]
  (let [header-match #"[^a-öA-Ö-\",\. ]+"
        row-match #"[^a-öA-Ö0-9-\",\. ]+"
        headers (first data)
        corrupted-headers (keep #(re-find header-match %) headers)
        data-rows (rest data)
        corrupted-data (keep-indexed
                         (fn [index row]
                           (let [result (re-find
                                          row-match
                                          (str/join row))]
                             (when result
                               {:row (inc index)            ;; add one because headers are skipped and row number would be off by 1
                                :error result})))  ;; Join vector to string
                         data-rows)]
    (when (or corrupted-headers corrupted-data))
    (merge {}
           (when (not (empty? corrupted-headers))
             {:corrupted-headers corrupted-headers})
           (when (not (empty? corrupted-data))
             {:corrupted-data corrupted-data}))))

(defn parse-response->csv
  "Convert given vector to map where map key is given in the first line of csv file."
  [csv-data]
  (let [headers (first csv-data)
        valid-header? (csv-util/valid-csv-header? headers)
        parsed-data (when valid-header?
                      (map (fn [cells]
                             (let [[business-id name] (map str/trim cells)]
                               {::t-service/business-id business-id
                                ::t-service/name name}))
                            (rest csv-data)))
        validated-data (filter
                         #(and
                            (csv-util/valid-business-id? (::t-service/business-id %))
                            (not (empty? (::t-service/name %))))
                         parsed-data)]
    {:result validated-data
     :failed-count (- (count parsed-data) (count validated-data))}))

(defn save-companies
  "Save business-ids, company names to db"
  [db data]
   (let [data (modification/with-modification-fields data ::t-service/id)]
    (specql/upsert! db ::t-service/service-company data)))

(defn read-csv
  "Read CSV from input stream. Guesses the separator from the first line."
  [input]
  (let [separator (csv-util/csv-separator input)]
    (csv/read-csv input :separator separator)))

(defn check-csv
  "Fetch csv file from given url, parse it and return status map that contains information about the count of companies
  in the file."
  [url-data]
  (try
    (let [url (ensure-url (get url-data :url))
          response (http-client/get url {:as "UTF-8"
                                         :socket-timeout 30000
                                         :conn-timeout 10000})]
      (if (= 200 (:status response))
        (try
          (let [data (when (= 200 (:status response))
                       (read-csv (:body response)))
                parsed-data (parse-response->csv data)]
            ;; response to client application
            {:status :success
             :count (count (:result parsed-data))
             :failed-count (:failed-count parsed-data)
             :companies (:result parsed-data)})
          (catch Exception e
            (log/warn "CSV check failed due to Exception in parsing: " e)
            {:status :failed
             :error :csv-parse-failed}))
        {:status :failed
         :error :url-parse-failed
         :http-status (:status response)}))
    (catch Exception e
      (log/warn "CSV check failed due to Exception in HTTP connection" e)
      {:status :failed
       :error :url-parse-failed})))

(defn interface-type [format]
  (case format
    "GTFS" :gtfs
    "Kalkati.net" :kalkati
    nil))

(defn- check-external-api
  [{:keys [url format]}]
  (try
    (let [url (ensure-url url)
          response (http-client/get url {:as :byte-array
                                         :socket-timeout 30000
                                         :conn-timeout 10000})]

      (if (= 200 (:status response))

        (try
          (when (interface-type format)
            (import-gtfs/validate-interface-zip-package (interface-type format)
                                                        (java.io.ByteArrayInputStream. (:body response))))
          {:status :success}
          (catch Exception e
            {:status :failed
             :error :zip-validation-failed}))
        {:status :failed
         :error :connection-failed}))
    (catch Exception e
      {:status :failed})))

(defn- external-routes-auth
  "Routes that require authentication"
  [db nap-config]
  (routes
   (POST  "/check-company-csv" {url-data :body}
          (http/transit-response
           (check-csv (http/transit-request url-data ))))
   (POST  "/check-external-api" {form-data :body}
     (http/transit-response
       (check-external-api (http/transit-request form-data))))))

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
