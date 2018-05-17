(ns ote.views.transit-visualization
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
            [tuck.core :as tuck]
            [ote.ui.service-calendar :as service-calendar]
            [ote.app.controller.transit-visualization :as tv]
            [taoensso.timbre :as log]
            [ote.time :as time]))



(defn transit-visualization [e! {:keys [transit-visualization] :as app}]
  (let [hash->color (:hash->color transit-visualization)
        date->hash (:date->hash transit-visualization)]
    [:div "here is the transit visualization"
     (when (and (not (:loading transit-visualization))
                (:hash->color transit-visualization))
       [service-calendar/service-calendar {:selected-date? (constantly false)
                                           :on-select (r/partial e! :D)
                                           :on-hover #(e! (tv/->HighlightHash (date->hash (time/format-date %))))
                                           :day-style (fn [day selected?]
                                                        (let [d (time/format-date day)]
                                                          (merge
                                                           {:background-color (hash->color (date->hash d))
                                                            ;:border "solid 4px white"
                                                            }
                                                           (when (= (date->hash d)
                                                                    (:highlight transit-visualization))
                                                             {:background "repeating-linear-gradient(45deg, transparent, transparent 3px, #ccc 3px, #ccc 6px)"

                                                              ;:box-shadow "inset 0 0 10px #000000"
                                                              ;:border "solid 4px black"
                                                              }))))
                                           :years [2017 2018]}])

     ]))
