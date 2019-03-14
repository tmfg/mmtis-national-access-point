(ns ote.views.transit-visualization.section
  "Container for different sections in transit visualization page."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.transit-changes :as style]
            [ote.app.controller.transit-visualization :as tv]))

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