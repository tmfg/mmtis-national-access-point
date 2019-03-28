(ns ote.transit-changes.detection-window-test
  "Detection window is time range for detecting changes from routes. With these tests we ensure that it works properly."
  (:require [ote.transit-changes.detection :as detection]
            [clojure.test :as t :refer [deftest testing is]]
            [clojure.spec.test.alpha :as spec-test]
            [ote.transit-changes :as transit-changes]
            [ote.transit-changes.detection-test-utilities :as test-utilities]
            [ote.time :as time]))

(defn generate-basic-data-week [number-of-weeks]
  (let [basic-week {test-utilities/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}]
    (vec (repeat number-of-weeks basic-week))))

(def data-change-at-week-25
  (test-utilities/weeks (test-utilities/to-local-date 2019 1 7)
                        (concat (generate-basic-data-week 23)
                                [{test-utilities/route-name ["!!" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 06 17 - vk25 !! change
                                 {test-utilities/route-name ["!!" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 06 24 - vk26
                                 {test-utilities/route-name ["!!" "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 07 01 - vk27
                                 {test-utilities/route-name ["h1" "h2" "h3" "h4" "h5" "h6" "h7"]}])))

(def data-change-at-week-33
  (test-utilities/weeks (test-utilities/to-local-date 2019 1 7)
                        (concat (generate-basic-data-week 32)
                                [{test-utilities/route-name [nil "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 07 08 - vk33 !! change
                                 {test-utilities/route-name [nil "h2" "h3" "h4" "h5" "h6" "h7"]} ;; 2019 07 08 - vk34
                                 {test-utilities/route-name [nil "h2" "h3" "h4" "h5" "h6" "h7"]}])))

(deftest test-change-at-25-week
  (let [result (-> data-change-at-week-25
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key test-utilities/route-name
            :starting-week {:beginning-of-week (test-utilities/to-local-date 2019 1 14)
                            :end-of-week (test-utilities/to-local-date 2019 1 20)}
            :different-week {:beginning-of-week (test-utilities/to-local-date 2019 6 17)
                             :end-of-week (test-utilities/to-local-date 2019 6 23)}}
           (-> result
               first
               (select-keys test-utilities/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))

(deftest test-change-at-week-33
  (let [result (-> data-change-at-week-33
                   detection/changes-by-week->changes-by-route
                   detection/detect-changes-for-all-routes)]
    (is (= {:route-key test-utilities/route-name
            :starting-week {:beginning-of-week (test-utilities/to-local-date 2019 1 14)
                            :end-of-week (test-utilities/to-local-date 2019 1 20)}} ;; HOX! No different week, no change
           (-> result
               first
               (select-keys test-utilities/select-keys-detect-changes-for-all-routes))))
    (is (= 1 (count result)))))