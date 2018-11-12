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
            [clojure.spec.alpha :as s]))


;; ui needs:
;; - businessId
;; - name
;; - auxiliaryNames
;; - addresses

(defn parse-iso8601-date [s]
  (when (string? s)
    (format/parse (:date format/formatters) s)))


(defn without-expired-items [result]
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
    
    (walk/postwalk walk-fn result)))

(defn without-expired [result]
  (-> result
      (update :names without-expired)
      (update :auxiliaryNames without-expired)
      (update :addresses without-expired)))

(comment 
  (def company-id-regex #"[0-9-]{2,20}")
  (s/def ::company-id-spec (s/and string? #(re-matches company-id-regex %)))
  (s/fdef fetch-by-company-id
    :args (:company-id ::company-id-spec)
    :ret map?))

(defn fetch-by-company-id [company-id]
  {:pre [(string? company-id)]
   :post [(or (map? %) (nil? %))]}
  (when (re-matches #"[0-9-]{2,20}" company-id)
    (let [url (str "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=10&resultsFrom=0&businessId=" company-id)
          response (http-client/get url {:accept :json
                                         :as :json})]
      (if (and (-> response :status (= 200))
               (-> response :body :totalResults (= "1")))
        (-> response
            :body
            :results
            first
            (select-keys [:name :auxiliaryNames :businessId :addresses])
            walk-removing-expired-items)
        (do
          (log/info (str "YTJ-vastaus ei ok, status: " (:status response))) ;; jos 200, :totalResults oli != 1
          nil)))))

(comment defrecord YTJFetch [config]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
      (http/publish! http {:authenticated? false}
                     (routes
                       (GET "/fetch/ytj" {params :query-params}
                         (load-ytj (get params "url")))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
