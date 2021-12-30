(ns taxiui.app.controller.stats
  (:require [taxiui.app.routes :as routes]
            [tuck.core :as tuck :refer-macros [define-event]]))

(def test-data [{:name "Lavishbay Oy"      :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "atlas Oy"          :updated 7  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Putkosen Kyyti Oy" :updated 14 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Inter Oy"          :updated 8  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "sense Oy"          :updated 7  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "dock Oy"           :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Puresierra Oy"     :updated 9  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Overustic Oy"      :updated 2  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Tribecapsule Oy"   :updated 1  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Peakgram Oy"       :updated 0  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Yonderness Oy"     :updated 5  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Isletware Oy"      :updated 4  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Omnitramp Oy"      :updated 6  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Outway Oy"         :updated 11 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Wayeon Oy"         :updated 10 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Oneventure Oy"     :updated 9  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Gocompass Oy"      :updated 8  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Peakdistance Oy"   :updated 7  :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Migratestripe Oy"  :updated 12 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Flycase Oy"        :updated 13 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}
                {:name "Pioneerload Oy"    :updated 22 :example-trip 38.40 :cost-start-daytime 6.90 :cost-travel-km 1.50 :cost-travel-min 1.10 :operation-area "002"}])

(define-event LoadStatistics [params]
              {}
              (assoc-in app [:taxi-ui :companies] (->> test-data (random-sample 0.5) shuffle)))

(defmethod routes/on-navigate-event :taxi-ui/stats [{params :params}]
  (do (js/console.log "On navigate hit!")
  (->LoadStatistics params)))
