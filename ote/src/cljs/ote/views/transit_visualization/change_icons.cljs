(ns ote.views.transit-visualization.change_icons
  "Icons related to transit visualization."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.transit-changes :as style]
            [ote.app.controller.transit-visualization :as tv]
            [ote.style.base :as style-base]
            [ote.ui.icon_labeled :as icon-l]
            [ote.ui.icons :as ote-icons]))

;; Utility methods

(defn format-range [lower upper]
  (if (and (nil? lower) (nil? upper))
    "0"
    (if (or (= lower upper)
            (nil? upper))
      (str lower)
      (str lower "\u2014" upper))))

(defn stop-seq-changes-icon [lower upper with-labels?]
  (let [changes (format-range lower upper)]
    [icon-l/icon-labeled
     [ic/action-timeline {:style {:color (when (= "0" changes)
                                           style/no-change-color)}}]
     [:span
      changes
      (when with-labels? " pysäkkimuutosta")]]))

(defn stop-time-changes-icon [lower upper with-labels?]
  (let [changes (format-range lower upper)]
    [icon-l/icon-labeled
     [ic/action-query-builder {:color (when (= "0" changes)
                                        style/no-change-color)}]
     [:span
      changes
      (when with-labels? " aikataulumuutosta")]]))


;; Ui
(defn change-icons
  ([diff]
   [change-icons diff false])
  ([{:keys [added-trips removed-trips
            trip-stop-sequence-changes-lower trip-stop-sequence-changes-upper
            trip-stop-time-changes-lower trip-stop-time-changes-upper] :as diff}
    with-labels?]
   [:div (stylefy/use-style (style-base/flex-container "row"))
    [:div {:style {:width "20%"}}
     [icon-l/icon-labeled
      [ote-icons/outline-add-box {:color (if (= 0 added-trips)
                                           style/no-change-color
                                           style/add-color)}]
      [:span (or added-trips (:gtfs/added-trips diff))      ;; :changes and :changes-route* have different namespace
       (when with-labels? " lisättyä vuoroa")]]]
    [:div {:style {:width "20%"}}
     [icon-l/icon-labeled style/transit-changes-legend-icon
      [ote-icons/outline-indeterminate-checkbox {:color (if (= 0 removed-trips)
                                                          style/no-change-color
                                                          style/remove-color)}]
      [:span (or removed-trips (:gtfs/removed-trips diff))
       (when with-labels? " poistettua vuoroa")]]]

    [:div {:style {:width "30%"}}
     [stop-seq-changes-icon trip-stop-sequence-changes-lower trip-stop-sequence-changes-upper with-labels?]]


    [:div {:style {:width "30%"}}
     [stop-time-changes-icon trip-stop-time-changes-lower trip-stop-time-changes-upper with-labels?]]]))