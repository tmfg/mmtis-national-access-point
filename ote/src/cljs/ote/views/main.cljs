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
            [ote.views.terminal :as terminal]
            [ote.views.rental :as rental]
            [ote.views.parking :as parking]
            [ote.views.brokerage :as brokerage]
            [ote.localization :refer [tr tr-key] :as localization]
            [ote.views.place-search :as place-search]
            [ote.ui.debug :as debug]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]))

(defn- is-topnav-active [give-page nav-page]
  (when (= give-page nav-page)
    "active")
  )

(defn- is-user-menu-active [app]
  (when (= true (get-in app [:ote-service-flags :user-menu-open]))
    "active")
  )

 (defn main-menu [e!]
   [ui/icon-menu
    {
     :on-click #(e! (fp-controller/->OpenUserMenu))
     :icon-button-element (reagent/as-element [ui/icon-button [ic/action-view-headline {:color :white}]])
     :anchor-origin {:horizontal "right" :vertical "bottom"}
     :target-origin {:horizontal "right" :vertical "top"}
     }
    [ui/menu-item {:primary-text (tr [:common-texts :header-front-page])
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]
    [ui/menu-item {:primary-text "Organisaation perustiedot"
                   :on-click #(e! (fp-controller/->ChangePage :transport-operator))} ]
    [ui/menu-item {:primary-text " Näytä debug state"
                   :on-click #(e! (fp-controller/->ToggleDebugState))} ]
    [ui/menu-item {:primary-text "Kirjaudu ulos"
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]
    ]
  )

(defn ote-application
  "OTE application main view"
  [e! app]

  ;; init - Get operator and service data from DB when refresh or on usage start
  (e! (fp-controller/->GetTransportOperatorData))

  (fn [e! app]

  [:div {:style (stylefy/use-style style-base/body)}
   [ui/mui-theme-provider
    {:mui-theme
     (get-mui-theme
      {:palette {;; primary nav color - Also Focus color in text fields
                 :primary1-color (color :blue700)

                 ;; Hint color in text fields
                 :disabledColor  (color :grey900)

                 ;; canvas color
                 ;;:canvas-color  (color :lightBlue50)

                 ;; Main text color
                 :text-color     (color :grey900)}

       :button  {:labelColor "#fff"}

       })}

    [:div.ote-sovellus.container-fluid
     [:div {:class "topnav"}
      [:a.main-icon {:href "#home"} [:img {:src "img/icons/nap-logo.svg" }]]
      [:a.ote-nav { :href "/index.html" } (tr [:common-texts :header-front-page]) ]
      [:a.ote-nav
       {:class (is-topnav-active :front-page (:page app))
        :href "#service-operator"
        :on-click #(e! (fp-controller/->ChangePage :front-page))
        } (tr [:common-texts :header-own-service-list]) ]
                                        ; [:div.user-menu-container {:class (is-user-menu-active app)}
      [:div.user-menu {:class (is-user-menu-active app) }
       (reagent/as-element (main-menu e!))
       ]
      [:div.user-data {:class (is-user-menu-active app) }
       [:div.user-name (get-in app [:user :name])]
       [:div.user-organization (get-in app [:transport-operator :ote.db.transport-operator/name])]
       ]
                                        ;]

      ]
     ;; NOTE: debug state is VERY slow if app state is HUGE
     ;; (it tries to pr-str it)

     [:div
      (when (= :front-page (:page app))
        [fp/front-page e! app])
      (when (= :transport-service (:page app))
        [t-service/select-service-type e! (:transport-service app)])
      (when (= :transport-operator (:page app))
        [to/operator e! (:transport-operator app)])
      (when (= :passenger-transportation (:page app))
        [pt/passenger-transportation-info e! (:transport-service app)])
      (when (= :terminal (:page app))
        [terminal/terminal e! (:transport-service app)])
      (when (= :rentals (:page app))
        [rental/rental e! (:transport-service app)])
      (when (= :parking (:page app))
        [parking/parking e! (:transport-service app)])
      (when (= :brokerage (:page app))
        [brokerage/brokerage e! (:transport-service app)])
      ]

     (when (= true (get-in app [:ote-service-flags :show-debug]))
       [:div.row
        [debug/debug app]
        ]
       )
     ]]]))
