(ns taxiui.app.controller.stats
  (:require [taxiui.app.routes :as routes]
            [tuck.core :as tuck]))

(def test-data [{:name "Lavishbay Oy"      :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.25 :cost-travel-min 1.10 :operation-area "002"}
                {:name "atlas Oy"          :updated 7  :example-trip 46.40 :cost-start-daytime 6.90 :cost-travel-km 1.25 :cost-travel-min 1.10 :operation-area "003"}
                {:name "Putkosen Kyyti Oy" :updated 14 :example-trip 46.40 :cost-start-daytime 6.90 :cost-travel-km 1.25 :cost-travel-min 1.20 :operation-area "003"}
                {:name "Inter Oy"          :updated 8  :example-trip 47.40 :cost-start-daytime 3.90 :cost-travel-km 1.25 :cost-travel-min 0.95 :operation-area "003"}
                {:name "sense Oy"          :updated 7  :example-trip 37.40 :cost-start-daytime 3.90 :cost-travel-km 1.25 :cost-travel-min 0.95 :operation-area "003"}
                {:name "dock Oy"           :updated 4  :example-trip 37.90 :cost-start-daytime 3.90 :cost-travel-km 1.25 :cost-travel-min 1.20 :operation-area "003"}
                {:name "Puresierra Oy"     :updated 9  :example-trip 36.90 :cost-start-daytime 3.90 :cost-travel-km 1.25 :cost-travel-min 1.15 :operation-area "003"}
                {:name "Overustic Oy"      :updated 2  :example-trip 38.90 :cost-start-daytime 3.90 :cost-travel-km 1.25 :cost-travel-min 1.15 :operation-area "002"}
                {:name "Tribecapsule Oy"   :updated 1  :example-trip 38.90 :cost-start-daytime 3.90 :cost-travel-km 1.25 :cost-travel-min 1.25 :operation-area "004"}
                {:name "Peakgram Oy"       :updated 0  :example-trip 38.90 :cost-start-daytime 3.90 :cost-travel-km 1.45 :cost-travel-min 1.25 :operation-area "004"}
                {:name "Yonderness Oy"     :updated 5  :example-trip 28.90 :cost-start-daytime 3.90 :cost-travel-km 1.45 :cost-travel-min 1.25 :operation-area "004"}
                {:name "Isletware Oy"      :updated 4  :example-trip 28.90 :cost-start-daytime 4.90 :cost-travel-km 1.45 :cost-travel-min 1.25 :operation-area "004"}
                {:name "Omnitramp Oy"      :updated 6  :example-trip 28.90 :cost-start-daytime 4.90 :cost-travel-km 1.45 :cost-travel-min 1.25 :operation-area "006"}
                {:name "Outway Oy"         :updated 11 :example-trip 28.90 :cost-start-daytime 4.90 :cost-travel-km 1.40 :cost-travel-min 1.25 :operation-area "006"}
                {:name "Wayeon Oy"         :updated 10 :example-trip 28.20 :cost-start-daytime 5.90 :cost-travel-km 1.40 :cost-travel-min 1.25 :operation-area "006"}
                {:name "Oneventure Oy"     :updated 9  :example-trip 28.20 :cost-start-daytime 5.90 :cost-travel-km 1.40 :cost-travel-min 1.25 :operation-area "002"}
                {:name "Gocompass Oy"      :updated 8  :example-trip 28.20 :cost-start-daytime 5.90 :cost-travel-km 1.50 :cost-travel-min 1.25 :operation-area "002"}
                {:name "Peakdistance Oy"   :updated 7  :example-trip 18.20 :cost-start-daytime 5.90 :cost-travel-km 1.50 :cost-travel-min 1.15 :operation-area "002"}
                {:name "Migratestripe Oy"  :updated 12 :example-trip 18.20 :cost-start-daytime 4.90 :cost-travel-km 1.50 :cost-travel-min 1.05 :operation-area "002"}
                {:name "Flycase Oy"        :updated 13 :example-trip 18.20 :cost-start-daytime 4.90 :cost-travel-km 1.50 :cost-travel-min 1.05 :operation-area "001"}
                {:name "Pioneerload Oy"    :updated 22 :example-trip 18.20 :cost-start-daytime 4.90 :cost-travel-km 1.70 :cost-travel-min 1.05 :operation-area "001"}])

(tuck/define-event LoadStatistics [params]
  {}
  (assoc-in app [:taxi-ui :companies] (->> test-data (random-sample 0.5) shuffle)))

(defmethod routes/on-navigate-event :taxi-ui/stats [{params :params}]
  (->LoadStatistics params))
