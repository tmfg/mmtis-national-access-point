(ns ote.services.transit-changes
  (:require [ote.components.service :refer [define-service-component]]
            [ote.components.http :as http]
            [compojure.core :refer [GET POST]]
            [jeesql.core :refer [defqueries]]
            [ote.time :as time]
            [clojure.string :as str]
            [clojure.set :as set]))

(defqueries "ote/services/transit_changes.sql")

(defn- parse-weekhash [weekhash]
  (if (nil? weekhash)
    {:days-with-traffic #{}
     :day->hash {}}
    (let [days (set/rename-keys
                (into {}
                      (map #(str/split % #"=")
                           (str/split weekhash #",")))
                (zipmap (map str (range 1 8)) time/week-days))]
      {:days-with-traffic (into #{}
                                (comp
                                 (map (fn [[day traffic]]
                                        (when-not (str/blank? traffic)
                                          day)))
                                 (remove nil?))
                                (seq days))
       :day->hash days})))

(defn describe-week-difference [difference]
  (assoc difference
         :current-week-traffic (parse-weekhash (:current-weekhash difference))
         :different-week-traffic (parse-weekhash (:different-weekhash difference))))

(defn list-current-changes [db]
  (let [now (java.util.Date.)
        current-packages (list-current-packages db {:date now})]
    (mapv (fn [{package-id :package-id :as row}]
            (assoc row :next-different-week
                   (describe-week-difference
                    (first (next-different-week-for-package db
                                                            {:date now
                                                             :package-id package-id})))))
          current-packages)))

(define-service-component TransitChanges {}

  ^{:unauthenticated true :format :transit}
  (GET "/transit-changes/current" []
       (#'list-current-changes db)))
