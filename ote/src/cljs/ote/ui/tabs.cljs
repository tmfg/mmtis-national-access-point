(ns ote.ui.tabs
  "Simple custom tab component"
  (:require [stylefy.core :as stylefy]
            [ote.style.tabs :as style-tabs]))

(defn tabs

  "Simble tab component that updates app-state using given function.
  We need to let the parent give the update function, because there are endless variations what tab component
  should do when tab is changed.
  Assumes that first tab is selected by default."

  [tabs {:keys [update-fn selected-tab] :as actions}]
  [:div.tab {:style {:padding-bottom "40px"}}
    (doall
      (for [{:keys [label value]} tabs]
        ^{:key (str label)}
        [:div ;(merge
                {:class-name (str
                               ;(:class (stylefy/use-style style-tabs/press-li))
                               " " (str "col-md-" (/ 12 (count tabs))))
                :on-click #(update-fn value)}
                #_ (when (< 767 (:width app))
                  {:style {:width (str (/ 100 (count tabs)) "%")}});)
         [:div (if (or (= value selected-tab) (and (nil? selected-tab) (= value (:value (first tabs)))))
                  (stylefy/use-style style-tabs/tab-selected)
                  (stylefy/use-style style-tabs/tab))
          label]]))]
   ;; Add grey bottom border
   #_ [:div (stylefy/use-style style-tabs/grey-border)])