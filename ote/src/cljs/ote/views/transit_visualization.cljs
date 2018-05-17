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
                                                        (let [d (time/format-date day)
                                                              hash-color (hash->color (date->hash d))
                                                              highlight (:highlight transit-visualization)]
                                                          (merge
                                                            {:background-color hash-color
                                                             :color "rgb (0, 255, 255)"
                                                             :transition "box-shadow 0.25s"
                                                             :box-shadow "inset 0 0 0 2px transparent,
                                                                          inset 0 0 0 100px transparent"}
                                                            (if (and highlight (= (date->hash d) highlight))
                                                              {;:background (str "repeating-linear-gradient(-45deg," hash-color "," hash-color ",6px, black 3px, black 7px)")
                                                               :box-shadow "inset 0 0 0 2px black,
                                                                            inset 0 0 0 100px rgba(255,255,255,.5)"}
                                                              (when highlight
                                                                {:box-shadow "inset 0 0 0 2px transparent,
                                                                              inset 0 0 0 100px rgba(0,0,0,.25)"})))))
                                           :years [2017 2018]}])

     ]))
