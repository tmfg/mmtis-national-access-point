(ns ote.transit-changes.detection-test-utilities
  "Helper methods for detection tests."
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.time :as time]))

(defn to-local-date [year month day]
  (java.time.LocalDate/of year month day))

(def select-keys-detect-changes-for-all-routes [:different-week
                                                :no-traffic-start-date
                                                :no-traffic-end-date
                                                :no-traffic-run
                                                :no-traffic-change
                                                :route-key
                                                :route-end-date
                                                :starting-week])

(def route-name "Raimola")
(def route-name-2 "Esala")
(def route-name-3 "Kyykkävaaranmäki")

(defn weeks
  "Give first day of week (monday) as a starting-from."
  [starting-from route-maps]
  (vec (map-indexed
         (fn [i routes]
           {:beginning-of-week (.plusDays starting-from (* i 7))
            :end-of-week (.plusDays starting-from (+ 6 (* i 7)))
            :routes routes})
         route-maps)))
