(ns taxiui.views.main
  "OTE application view composer"
  (:require [ote.localization :refer [tr]]
            [stylefy.core :as stylefy]
            [taxiui.styles.main :as styles]
            [taxiui.views.front-page :as fp]
            [taxiui.views.login :as l]
            [taxiui.views.pricing-details :as pd]
            [taxiui.views.stats :as s]
            [taxiui.views.components.devtools :as devtools]
            [taxiui.views.components.header :refer [header]]
            [taxiui.views.components.loader :as loader]
            [ote.theme.colors :as colors]
            [re-svg-icons.feather-icons :as feather-icons]))

(defn taxi-application
  "Taxi UI application main view"
  [_ _]
  (fn [e! app]
    [:div (stylefy/use-style styles/main-flex-container)
     [loader/loader app [:taxi-ui :uix :loader]]
     [header app]
     [devtools/env-warning]
     ; TODO: add test env warning for Taxi UI hereabouts
     ; TODO: add cookie banner to this index
     (case (:page app)
       ;  see taxiui.app.routes for more in-depth documentation
       :taxi-ui/front-page      [fp/front-page e! app]
       :taxi-ui/login           [l/login e! app]
       :taxi-ui/pricing-details [pd/pricing-details e! app]
       :taxi-ui/stats           [s/stats e! app]
       [:div (tr [:common-texts :no-such-page]) (pr-str (:page app))])
     [devtools/debug-state app]]))
