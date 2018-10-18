(ns ote.transit-changes.detection-test
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]))

(defn d [year month date]
  (java.time.LocalDate/of year month date))

(def route-name ["TST" "Testington - Terstersby" "Testersby"])

(defn weeks [starting-from & route-maps]
  (vec (map-indexed
        (fn [i routes]
          {:beginning-of-week (.plusDays starting-from (* i 7))
           :end-of-week (.plusDays starting-from (+ 6 (* i 7)))
           :routes routes}) route-maps)))


(def test-traffic-no-run
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" nil nil nil nil]} ; 4 day run
         {route-name [nil nil nil nil nil nil nil]} ; 7 days
         {route-name [nil nil nil nil "h5" "h6" "h7"]} ; 4 days => sum 15
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest no-traffic-run-is-detected
  (is (= {:no-traffic-start-date (d 2018 10 18)
          :no-traffic-end-date (d 2018 11 2)}
         (-> (detection/next-different-weeks test-traffic-no-run)
             (get route-name)
             (select-keys [:no-traffic-start-date :no-traffic-end-date])))))


(def test-traffic-2-different-weeks
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting point
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7" ]} ; wednesday different
         {route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7" ]} ; thursday different
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]})) ; back to normal

(deftest two-week-difference-is-skipped
  (is (nil?
       (get-in (detection/next-different-weeks test-traffic-2-different-weeks)
               [route-name :different-week]))))


(def test-traffic-normal-difference
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; starting point
         {route-name ["h1" "h2" "!!" "h4" "h5" "h6" "h7" ]} ; wednesday different
         {route-name ["h1" "h2" "h3" "!!" "h5" "h6" "h7" ]} ; thursday different
         {route-name ["h1" "h2" "h3" "h4" "!!" "h6" "h7"]} ; friday different
         {route-name ["h1" "h2" "h3" "!!" "!!" "h6" "h7"]})) ;; thu and fri different

(deftest normal-difference
  (is (= {:starting-week-hash ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]
          :starting-week {:beginning-of-week (d 2018 10 15)
                          :end-of-week (d 2018 10 21)}
          :different-week-hash  ["h1" "h2" "!!" "h4" "h5" "h6" "h7"]
          :different-week {:beginning-of-week (d 2018 10 22)
                           :end-of-week (d 2018 10 28)}}
         (get (detection/next-different-weeks test-traffic-normal-difference) route-name))))


(def test-traffic-starting-point-anomalous
  (weeks (d 2018 10 8)
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1!" "h2!" "h3!" "h4!" "h5!" "h5!" "h7!"]} ; starting week is an exception
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]} ; next week same as previous
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}
         {route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}))

(deftest anomalous-starting-point-is-ignore
  (let [{:keys [starting-week different-week] :as res}
        (get (detection/next-different-weeks test-traffic-starting-point-anomalous) route-name)]
    (is (= (d 2018 10 22) (:beginning-of-week starting-week)))
    (is (nil? different-week))))
