(ns ote.views.transit-visualization.change-utilities
  "Helper ui components for transit visualization."
  (:require [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.transit-changes :as style]
            [ote.ui.icons :as ote-icons]
            [ote.ui.icon_labeled :as icon-l]
            [ote.localization :refer [tr]]))

(defn section [{:keys [open? toggle!]} title help-content body-content]
  [:div.transit-visualization-section (stylefy/use-style (if open?
                                                           style/section
                                                           style/section-closed))
   [:div.transit-visualization-section-title (merge
                                               (stylefy/use-style style/section-title)
                                               {:on-click toggle!})
    [(if open?
       ic/navigation-expand-less
       ic/navigation-expand-more) {:color "white"
                                   :style {:position "relative"
                                           :top "6px"
                                           :margin-right "0.5rem"}}]
    title]
   (when open?
     [:span
      [:div.transit-visualization-section-header (stylefy/use-style style/section-header)
       help-content]
      [:div.transit-visualization-section-body (stylefy/use-style style/section-body)
       body-content]])])

(defn route-changes-legend []
  [:div.transit-changes-legend (stylefy/use-style style/transit-changes-legend-container)
   [:div
    [:b "Taulukon ikonien selitteet"]]
   [:div (stylefy/use-style style/transit-changes-icon-legend-row-container)
    (doall
      (for [[icon color label] [[ote-icons/outline-add-box {} (tr [:transit-changes :trips-new])]
                                [ote-icons/outline-indeterminate-checkbox {} (tr [:transit-changes :trips-removed])]
                                [ic/action-timeline {} (tr [:transit-changes :stop-changes-per-trip])]
                                [ic/action-query-builder {} (tr [:transit-changes :schedule-changes-per-trip])]
                                [ic/av-not-interested {:color style/remove-color} (tr [:transit-changes :no-traffic])]
                                [ic/content-remove-circle-outline {:color style/remove-color} (tr [:transit-changes :trip-end-potential])]]]
        ^{:key (str "transit-visualization-route-changes-legend-" (rand-int 9999999))} ;; Ensure that all icons have unique key
        [icon-l/icon-labeled style/transit-changes-icon [icon color] label]))]])
