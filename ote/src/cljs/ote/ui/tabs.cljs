(ns ote.ui.tabs
  "Simple custom tab component"
  (:require [stylefy.core :as stylefy]
            [ote.style.tabs :as style-tabs]))

(defn tabs

  "Simble tab component that updates app-state using given function.
  We need to let the parent give the update function, because there are endless variations what tab component
  should do when tab is changed.
  Assumes that first tab is selected by default."

  [update-fn tabs selected-tab]
  [:div.tab {:style {:padding-bottom "20px"}}
   [:ul {:style {:list-style "none" :padding-bottom "12px"}}
    (doall
      (for [{:keys [label value]} tabs]
        ^{:key (str label)}
        [:li {:style    {:display "inline"}
              :on-click #(update-fn value)
              }
         [:span (if (or (= value selected-tab) (and (nil? selected-tab) (= value (:value (first tabs)))))
                  (stylefy/use-style style-tabs/tab-selected)
                  (stylefy/use-style style-tabs/tab))
          label]]))]
   ;; Add grey bottom border
   [:div (stylefy/use-style style-tabs/grey-border)]])

