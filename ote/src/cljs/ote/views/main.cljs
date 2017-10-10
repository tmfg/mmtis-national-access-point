(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.views.transport-operator :as to]
            [ote.views.front-page :as fp]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.transport-service :as t-service]
            [ote.views.passenger_transportation :as pt]
            [ote.localization :as localization]
            [ote.views.place-search :as place-search]
            [ote.ui.debug :as debug]))


 (defn main-menu [e!]
   [ui/icon-menu
    {:icon-button-element (reagent/as-element [ui/icon-button [ic/action-view-headline {:color :white}]])}
    [ui/menu-item {:primary-text "Olennaiset tiedot"
                   :on-click #(e! (fp-controller/->ChangePage :transport-operator))} ]
    [ui/menu-item {:primary-text "Etusivu"
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]
    ]
  )

(defn ote-application
  "OTE application main view"
  [e! app]

  [ui/mui-theme-provider
   {:mui-theme
    (get-mui-theme
     {:palette {;; primary nav color - Also Focus color in text fields
                :primary1-color (color :lightBlue300)

                ;; Hint color in text fields
                :disabledColor (color :grey900)

                ;; canvas color
                ;;:canvas-color  (color :lightBlue50)

                ;; Main text color
                :text-color (color :grey900)}

      :button {:labelColor "#fff"}

      })}
   ;:icon-element-right [(reagent/as-element [ui/flat-button {:label "jee"}])]
   [:div.ote-sovellus.container-fluid
    [ui/app-bar {
                 :title "OTE"
                 :icon-element-left (reagent/as-element (main-menu e!))
                 :icon-element-right (reagent/as-element (main-menu e!))
                 }]
    [:div.container-fluid
     (when (= :front-page (:page app))
       [fp/front-page e! (:front-page app)])
     (when (= :transport-service (:page app))
       [t-service/select-service-type e! (:transport-service app)])
     (when (= :transport-operator (:page app))
               [to/olennaiset-tiedot e! (:transport-operator app)])
     (when (= :passenger-transportation (:page app))
        [pt/passenger-transportation-info e! (:transport-service app)]
       )
     [debug/debug app]
     ]]])
