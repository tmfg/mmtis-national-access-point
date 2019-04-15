(ns ote.transit-changes.detection-window-test
  "Detection window is time range for detecting changes from routes. With these tests we ensure that it works properly."
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [ote.transit-changes.detection-test-utilities :as tu]))

(def data-change-at-week-25
  (tu/weeks (tu/to-local-date 2019 1 7)
            (concat (tu/generate-traffic-week 23)
                    [{tu/route-name ["!!" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 06 17 - vk25 !! change
                     {tu/route-name ["!!" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 06 24 - vk26
                     {tu/route-name ["!!" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 07 01 - vk27
                     {tu/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}])))

(def data-change-at-week-33
  (tu/weeks (tu/to-local-date 2019 1 7)
            (concat (tu/generate-traffic-week 32)
                    [{tu/route-name [nil "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 07 08 - vk33 !! change
                     {tu/route-name [nil "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 07 08 - vk34
                     {tu/route-name [nil "h2" "h3" "h4" "h5" "h6" "h7"]}])))

(deftest test-change-at-25-week
  (let [result (-> data-change-at-week-25
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 1 14)
                            :end-of-week (tu/to-local-date 2019 1 20)}
            :different-week {:beginning-of-week (tu/to-local-date 2019 6 17)
                             :end-of-week (tu/to-local-date 2019 6 23)}}
           (-> result
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))

(deftest test-change-at-week-33
  (let [result (-> data-change-at-week-33
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key tu/route-name
            :starting-week {:beginning-of-week (tu/to-local-date 2019 1 14)
                            :end-of-week (tu/to-local-date 2019 1 20)}} ;; HOX! No different week, no change
           (-> result
               first
               (select-keys tu/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))
