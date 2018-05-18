(ns ote.views.transit-visualization
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
            [tuck.core :as tuck]
            [ote.ui.service-calendar :as service-calendar]
            [ote.app.controller.transit-visualization :as tv]
            [taoensso.timbre :as log]
            [ote.time :as time]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.table :as table]))

(defn highlight-style [hash->color date->hash day highlight]
  (let [d (time/format-date day)
        d-hash (date->hash d)
        hash-color (hash->color (date->hash d))
        hover-day (:day highlight)
        hover-hash (:hash highlight)
        mode (:mode highlight)]

    (when hash-color
      (case mode
        :same (if (and (= d-hash hover-hash) (not (= d (time/format-date hover-day))))
                {:box-shadow "inset 0 0 0 2px black, inset 0 0 0 100px rgba(255,255,255,.5)"}
                (if (= d (time/format-date hover-day))
                  {:box-shadow "inset 0 0 0 2px black, inset 0 0 0 100px rgba(255,255,255,.75)"}
                  {:box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 100px rgba(0,0,0,.25)"}))
        :diff (if (and (not (= d-hash hover-hash))
                       (= (time/day-of-week day) (time/day-of-week hover-day)))
                {:box-shadow "inset 0 0 0 2px crimson, inset 0 0 0 3px black, inset 0 0 0 100px rgba(255,255,255,.65)"}
                (if (and (= d-hash hover-hash) (= d (time/format-date hover-day)))
                  {:box-shadow "inset 0 0 0 2px black,
                                inset 0 0 0 3px transparent,
                                inset 0 0 0 100px rgba(255,255,255,.75)"}
                  {:box-shadow "inset 0 0 0 2px transparent,
                                inset 0 0 0 3px transparent,
                                inset 0 0 0 100px rgba(0,0,0,.25)"}))))))

(defn day-style [hash->color date->hash highlight day selected?]
  (let [d (time/format-date day)
        hash-color (hash->color (date->hash d))]

    (merge
      {:background-color hash-color
       :color "rgb (0, 255, 255)"
       :transition "box-shadow 0.25s"
       :box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 3px transparent, inset 0 0 0 100px transparent"}
      (when (:hash highlight)
        (highlight-style hash->color date->hash day highlight)))))

(defn hover-day [e! date->hash day]
  (e! (tv/->HighlightHash (date->hash (time/format-date day)) day))
  (e! (tv/->DaysToFirstDiff (time/format-date day) date->hash)))

(defn select-day [e! day]
  (e! (tv/->SelectDateForComparison day)))

(defn date-comparison [e! {:keys [date->hash compare]}]
  (let [date1 (:date1 compare)
        date2 (:date2 compare)]
    (when (and date1 date2)
      [:div.transit-visualization-compare
       "Vertaillaan päiviä: " [:b date1] " ja " [:b date2]
       (when (= (date->hash date1) (date->hash date2))
         [:div "Päivien liikenne on samanlaista"])
       [table/table {:no-rows-message "Ei reittejä"}
        [{:name "Nimi" :width "20%"}]]
       ])))

(defn days-to-diff-info [e! transit-visualization highlight]
  (if-let [hovered-date (:day highlight)]
    (when (:hash highlight)
      [:div {:style {:position "fixed"
                     :top "80px"
                     :left "50px"
                     :min-height "50px"
                     :width "250px"
                     :border "solid black 1px"
                     :padding "5px"}}
       (str (:days-to-diff transit-visualization)
            " päivää ensimmäiseen muutokseen viikonpäivästä: "
            (time/format-date hovered-date) " (" (case (time/day-of-week hovered-date)
                                                     :monday "Ma"
                                                     :tuesday "Ti"
                                                     :wednesday "Ke"
                                                     :thursday "To"
                                                     :friday "Pe"
                                                     :saturday "La"
                                                     :sunday "Su") ")")])))

(defn transit-visualization [e! {:keys [hash->color date->hash loading? highlight]
                                 :as transit-visualization}]
  [:div
   "tähän palvelun valinta"
   (when (and (not loading?) hash->color)
     [:div.transit-visualization
      [days-to-diff-info e! transit-visualization (:highlight transit-visualization)]
      [:div
       [ui/radio-button-group {:name "select-highlight-mode"
                               :on-change #(e! (tv/->SetHighlightMode (keyword %2)))
                               :value-selected (:mode highlight)
                               :style {:display "flex" :justify-content "flex-start" :flex-direction "row wrap"}}
        [ui/radio-button {:label "Näytä samanlaiset"
                          :value :same
                          :style {:white-space "nowrap"
                                  :width "auto"
                                  :margin-right "20px"
                                  :font-size "12px"
                                  :font-weight "bold"}}]
        [ui/radio-button {:label "Näytä poikkeukset"
                          :value :diff
                          :style {:white-space "nowrap"
                                  :width "auto"
                                  :font-size "12px"
                                  :font-weight "bold"}}]]]
      [service-calendar/service-calendar {:selected-date? (constantly false)
                                          :on-select (r/partial select-day e!)
                                          :on-hover (r/partial hover-day e! date->hash)
                                          :day-style (r/partial day-style hash->color date->hash
                                                                (:highlight transit-visualization))
                                          :years [2017 2018]}]
      [date-comparison e! transit-visualization]])])
