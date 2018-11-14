(ns ote.integration.import.ytj
  "YTJ (Finnish company register) API client."
  (:require [clj-http.client :as http-client]            
            [taoensso.timbre :as log]
            [clojure.walk :as walk]
            [clj-time.format :as format]
            [clj-time.core :as t]
            [clojure.string :as string]
            [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clojure.spec.alpha :as s]
            [ote.util.feature :as feature]))


;; ui needs:
;; - businessId
;; - name
;; - auxiliaryNames
;; - addresses

(defn parse-iso8601-date [s]
  (when (string? s)
    (format/parse (:date format/formatters) s)))

(def http-get http-client/get) ;; redef'd to mock in test

(defn without-expired-items [ytj-map]
  (let [now (t/now)        
        date-in-past? (fn [d]
                        (if (nil? d)
                          false
                          (t/before? d now)))
        end-date-expired? (fn [m]
                            (let [ds (:endDate m)
                                  d (when (string? ds)
                                      (parse-iso8601-date ds))]
                              (date-in-past? d)))
        walk-fn (fn [x]
                  (if (and (vector? x) (map? (first x)))
                     (filterv (complement end-date-expired?) x)
                     x))]
    (walk/postwalk walk-fn ytj-map)))

(comment 
  (def company-id-regex #"[0-9-]{2,20}")
  (s/def ::company-id-spec (s/and string? #(re-matches company-id-regex %)))
  (s/fdef fetch-by-company-id
    :args (:company-id ::company-id-spec)
    :ret map?))

(defn fetch-by-company-id [company-id]
  (when (and (string? company-id) (re-matches #"[0-9-]{2,20}" company-id))
    (let [url (str "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=10&resultsFrom=0&businessId=" company-id)
          response (try
                     (http-get url {:accept :json
                                    :as :json})
                     (catch clojure.lang.ExceptionInfo e
                       ;; sadly clj-http wants to communicate status as excpetions
                       (-> e ex-data)))]
      ;; (println "ytj fetch: got response" (pr-str response))
      (if (and (-> response :status (= 200))
               (-> response :body :totalResults str (= "1")))
        (do
          ;; (println "all is good")
          (-> response
                 :body
                 :results
                 first
                 (select-keys [:name :auxiliaryNames :businessId :addresses])
                 without-expired-items))
        (do
          ;; (println "all is not good - " (-> response :status (= 200)) (-> response :body pr-str ))
          (log/info (str "YTJ-vastaus ei ok, status: " (:status response))) ;; jos 200, :totalResults oli != 1
          nil)))))

(defrecord YTJFetch [config]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
           (when (feature/feature-enabled? config :open-ytj-integration)
             ;; require authentication because we don't want to be an open proxy for PRH API (and also there may be rate limits)             
             (http/publish! http {:authenticated? true}
                            (routes                           
                             (GET "/fetch/ytj" [company-id]
                                  (let [r (fetch-by-company-id company-id)]
                                    ;; (println "YTJ: sending as transit:" (pr-str r))
                                    (http/transit-response r))))))))
  (stop [{stop ::stop :as this}]
    (when stop
      (stop))
    (dissoc this ::stop)))
