(ns taxiui.views.main
  "OTE application view composer"
  (:require [ote.localization :refer [tr]]
            [stylefy.core :as stylefy]
            [taxiui.styles.main :as styles]
            [taxiui.views.front-page :as fp]  ;; TODO copy
            [taxiui.views.components.header :refer [header]]
            ))

(defn taxi-application
  "Taxi UI application main view"
  [_ _]
  (fn [e! app]
    [:div (stylefy/use-style styles/main-flex-container)
     [header app]
     ; TODO: add test env warning for Taxi UI hereabouts
     (case (:page app)
       :front-page [fp/front-page e! app]
       [:div (tr [:common-texts :no-such-page]) (pr-str (:page app))])]))
