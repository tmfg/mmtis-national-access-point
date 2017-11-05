(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [reagent.core :as r]
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
    "active"))

(defn- is-user-menu-active [app]
  (when (= true (get-in app [:ote-service-flags :user-menu-open]))
    "active"))

 (defn user-menu [e! user-name]
   [ui/select-field
    {:value 0 ;; Force user-name to be selected at all times
     :label-style {:color "#f2f2f2"}
     :on-click #(e! (fp-controller/->OpenUserMenu))
     :anchor-origin {:horizontal "right" :vertical "bottom"}
     :target-origin {:horizontal "right" :vertical "top"}
     }
    [ui/menu-item {:value 0 ;; First and only element that can be selected in this select field
                   :primary-text user-name
                   :selected-text-color (color :grey900)
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]
    [ui/menu-item {:primary-text (tr [:common-texts :user-menu-service-guide])
                   :selected-text-color (color :grey900)
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]
    [ui/menu-item {:primary-text (tr [:common-texts :user-menu-service-operator])
                   :selected-text-color (color :grey900)
                   :on-click #(e! (fp-controller/->ChangePage :transport-operator))} ]
    [ui/menu-item {:primary-text " Näytä debug state"
                   :selected-text-color (color :grey900)
                   :on-click #(e! (fp-controller/->ToggleDebugState))} ]
    [ui/menu-item {:primary-text "Siirry NAP -palveluun"
                  ; :on-click #(e! (fp-controller/->GoNAPService))
                  :on-click (fn [_] (set! (.-location js/document) "/"))} ] ;; TODO: Fixme
    [ui/menu-item {:primary-text "Kirjaudu ulos"
                   :selected-text-color (color :grey900)
                   :on-click #(e! (fp-controller/->ChangePage :front-page))} ]])

(defn- flash-message [msg]
  [ui/snackbar {:open (boolean msg)
                :message msg
                :style style-base/flash-message
                :auto-hide-duration 5000}])

(defn- top-nav [e! app]
  [:div {:class "topnav"}
   [:a.main-icon {:href "#"
                  :on-click #(e! (fp-controller/->ChangePage :front-page))}
    [:img {:src "img/icons/nap-logo.svg" }]]
   [:a.ote-nav
    {:class (is-topnav-active :front-page (:page app))
     :href "#"
     :on-click #(e! (fp-controller/->ChangePage :front-page))}
    (tr [:common-texts :navigation-front-page]) ]
   [:a.ote-nav
    {:class (is-topnav-active :own-services (:page app))
     :on-click #(e! (fp-controller/->ChangePage :own-services))}
    (tr [:common-texts :navigation-own-service-list]) ]
   [:div.user-menu {:class (is-user-menu-active app) }
    (r/as-element (user-menu e! (get-in app [:user :name])))]])

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
                 :text-color     (color :grey900)
                }

       :button  {:labelColor "#fff"}
       :menu-item {:selected-text-color (color :blue700)} ;; Change drop down list items selected color

       })}

    [:div.ote-sovellus.container-fluid
     (top-nav e! app)
     ;; NOTE: debug state is VERY slow if app state is HUGE
     ;; (it tries to pr-str it)

     [:div
      (when (= :front-page (:page app))
        [fp/front-page e! app])
      (when (= :own-services (:page app))
        [fp/own-services e! app])
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

     [flash-message (:flash-message app)]

     (when (= true (get-in app [:ote-service-flags :show-debug]))
       [:div.row
        [debug/debug app]])]]]))
