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
    {
     :icon-button-element (reagent/as-element [ui/icon-button [ic/action-view-headline {:color :white}]])
     :anchor-origin {:horizontal "right" :vertical "bottom"}
     :target-origin {:horizontal "right" :vertical "top"}
     }
    [ui/menu-item {:primary-text "Etusivu"
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]
    [ui/menu-item {:primary-text "Organisaation perustiedot"
                   :on-click #(e! (fp-controller/->ChangePage :transport-operator))} ]
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
                 :disabledColor  (color :grey900)

                 ;; canvas color
                 ;;:canvas-color  (color :lightBlue50)

                 ;; Main text color
                 :text-color     (color :grey900)}

       :button  {:labelColor "#fff"}

      })}
   ;:icon-element-right [(reagent/as-element [ui/flat-button {:label "jee"}])]
   [:div.ote-sovellus.container-fluid

    [:div {:class "topnav"}
     [:a.main-icon {:href "#home"} [:img {:src "/img/icons/liikennevirasto_logo_2x.png" :width "40px"}]]
     [:a {:class "active" :href "#home"} "FINAP"]
     [:a {:href "#news" } "Palvelukatalogi" ]
     [:a {:href "#contact"} "Omat palvelutiedot" ]
     [:div.user-menu
      (reagent/as-element (main-menu e!))
      ]
     [:div.user-data
      [:div.user-name "ERkki Esimerkki"]
      [:div.user-organization "Erkin MAtkat Oy"]
      ]

     ]
    ;; NOTE: debug state is VERY slow if app state is HUGE
    ;; (it tries to pr-str it)

    [:div
     (when (= :front-page (:page app))
       [fp/front-page e! app])
     (when (= :transport-service (:page app))
       [t-service/select-service-type e! (:transport-service app)])
     (when (= :transport-operator (:page app))
               [to/olennaiset-tiedot e! (:transport-operator app)])
     (when (= :passenger-transportation (:page app))
        [pt/passenger-transportation-info e! (:transport-service app)])]

    [:div.row
      [debug/debug app]
    ]
    ]])


