(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
            [ote.views.transport-operator :as to]
            [ote.views.front-page :as fp]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.transport-service :as t-service]
            [ote.views.passenger-transportation :as pt]
            [ote.views.terminal :as terminal]
            [ote.views.rental :as rental]
            [ote.views.parking :as parking]
            [ote.views.brokerage :as brokerage]
            [ote.localization :refer [tr tr-key] :as localization]
            [ote.views.place-search :as place-search]
            [ote.ui.debug :as debug]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.topnav :as style-topnav]
            [ote.app.controller.transport-service :as ts]
            [ote.views.theme :refer [theme]]
            [ote.views.service-search :as service-search]))

(defn logged-in? [app]
  (not-empty (get-in app [:user :id])))

(defn- is-topnav-active [give-page nav-page]
  (when (= give-page nav-page)
    "active"))

(defn- is-user-menu-active [app]
  (when (= true (get-in app [:ote-service-flags :user-menu-open]))
    "active"))

 (defn user-menu [e! name username]
   [ui/select-field
    {:label-style {:color "#FFFFFF"}
     :list-style {:background-color "#2D75B4"}
     :on-click #(e! (fp-controller/->OpenUserMenu))
     :anchor-origin {:horizontal "right" :vertical "bottom"}
     :target-origin {:horizontal "right" :vertical "top"}
     }

    [ui/menu-item {:style {:color "#FFFFFF"}
                   :primary-text (tr [:common-texts :user-menu-profile])
                   :on-click #(do (.preventDefault %)
                                  (e! (fp-controller/->GoToUrl (str "/user/edit/" username))))}]
    [ui/menu-item {:style {:color "#FFFFFF"}
                   :primary-text (tr [:common-texts :user-menu-service-guide])
                   :on-click #(do (.preventDefault %)
                                  (e! (fp-controller/->ChangePage :front-page)))} ]
    [ui/menu-item {:style {:color "#FFFFFF"}
                   :primary-text (tr [:common-texts :user-menu-service-operator])
                   :on-click #(do
                                (.preventDefault %)
                                (e! (fp-controller/->ChangePage :transport-operator)))}]
    [ui/menu-item {:style {:color "#FFFFFF"}
                   :primary-text " Näytä debug state"
                   :on-click #(e! (fp-controller/->ToggleDebugState))} ]
    [ui/menu-item {:style {:color "#FFFFFF"}
                   :primary-text (tr [:common-texts :user-menu-log-out])
                   :on-click #(do (.preventDefault %)
                                  (e! (fp-controller/->GoToUrl "/user/_logout")))} ]
   [ui/menu-item {:primary-text name ;; This is here so the user name is appearing in the header
                  :style {:color "#FFFFFF"}
                  :on-click #(do (.preventDefault %)
                                 (e! (fp-controller/->GoToUrl (str "/user/edit/" username)))) }]
                  ])

(defn- flash-message [msg]
  [ui/snackbar {:open (boolean msg)
                :message (or msg "")
                :style style-base/flash-message
                :auto-hide-duration 5000}])

(defn header-links [app]
  (filter some?
          [{:page  :front-page
            :label [:common-texts :navigation-front-page]
            :url   "/"}

           {:page  :services
            :label [:common-texts :navigation-dataset]}

           {:page  :organizations
            :label [:common-texts :navigation-organizations]
            :url   "/organization"}

           (when (logged-in? app)
             {:page  :own-services
              :label [:common-texts :navigation-own-service-list]})]))

(defn- top-nav-links [e! {current-page :page :as app} desktop?]
  [:div (stylefy/use-style style-topnav/clear)
   [:ul (stylefy/use-style style-topnav/ul)
    (when (> (:width app) style-base/mobile-width-px)
      [:li
       [:a
        (merge (stylefy/use-style (if desktop?
                                    style-topnav/desktop-link
                                    style-topnav/link))
               {:href     "#"
                :on-click #(do
                             (.preventDefault %)
                             (e! (fp-controller/->GoToUrl "/")))})
        [:img {:src "img/icons/nap-logo.svg" :style style-topnav/img }]]])

    (doall
     (for [{:keys [page label url]} (header-links app)]
       ^{:key page}
       [:li (if desktop? nil (stylefy/use-style style-topnav/mobile-li))
        [:a
         (merge (stylefy/use-style
                 (if (= page current-page)
                   (if desktop? style-topnav/desktop-active style-topnav/active)
                   (if desktop? style-topnav/desktop-link style-topnav/link)))
                {:href     "#"
                 :on-click #(do
                              (.preventDefault %)
                              (if url
                                (e! (fp-controller/->GoToUrl url))
                                (e! (fp-controller/->ChangePage page))))})
         (tr label)]]))
    [:div.user-menu {:class (is-user-menu-active app)
                     :style (when (> (:width app) style-base/mobile-width-px)
                              {:float "right"})}
     (r/as-element (user-menu e! (get-in app [:user :name]) (get-in app [:user :username])))]
    [:ul (stylefy/use-style style-topnav/ul)
     [:li
      [:a (merge (stylefy/use-style
                   (if desktop? style-topnav/desktop-link style-topnav/link))
                 {:style {:float "right"}
                  :href  "https://goo.gl/forms/MUlsAwAdmvDaZb5W2"})
       "Anna palautetta"]]]]])

(defn- mobile-top-nav-links [e! app]
  [:div
   [:ul (stylefy/use-style style-topnav/ul)
    [:li (stylefy/use-style style-topnav/li)
     [:a (merge
          (stylefy/use-style style-topnav/link)
          {:href     "#"
           :on-click #(do
                        (.preventDefault %)
                        (e! (fp-controller/->GoToUrl "/")))})
       [:img {:src "img/icons/nap-logo.svg"}]]]
     [:li (stylefy/use-style style-topnav/right)
      [ui/icon-button {:on-click #(e! (fp-controller/->OpenHeader))
                       :style {:padding 8
                               :width 56
                               :height 56}
                       :icon-style {:height 40
                                    :width 40}}
       [ic/action-reorder {:style {:color "#fff"
                                   :width 40
                                   :height 40
                                   }}]]]]
  (when (get-in app [:ote-service-flags :header-open])
    (top-nav-links e! app false))])


(defn- top-nav [e! app]
  (let [desktop? (> (:width app) style-base/mobile-width-px)]
    [:div (if desktop? (stylefy/use-style style-topnav/topnav-desktop) (stylefy/use-style style-topnav/topnav) )
     [:div.container-fluid
      (if desktop?
        (top-nav-links e! app true)
        (mobile-top-nav-links e! app))]]))


(defn- footer []
  [:footer.site-footer
   [:div.container-fluid
    [:div.row
     [:div.col-md-2.footer-links
      [:a.logo {:href "#" }
       [:img {:src "/livi_logo_valkoinen.svg" :alt (tr [:common-texts :footer-livi-logo]) }]]]
     [:div.col-md-8.footer-links
      [:ul.unstyled
       [:li
        [:a {:href "https://www.liikennevirasto.fi/"} (tr [:common-texts :footer-livi-url])]]]]]]])

(defn- debug-state [app]
  ;; NOTE: debug state is VERY slow if app state is HUGE
  ;; (it tries to pr-str it)
  (when (= true (get-in app [:ote-service-flags :show-debug]))
    [:div.row
     [debug/debug app]]))

(defn ote-application
  "OTE application main view"
  [e! app]

  ;; init - Get operator and service data from DB when refresh or on usage start
  (e! (fp-controller/->GetTransportOperatorData))

  (fn [e! app]
  [:div {:style (stylefy/use-style style-base/body)}
   [theme
    [:div.ote-sovellus
     (top-nav e! app)


     [:div.container-fluid.wrapper (stylefy/use-style style-base/wrapper)
      (case (:page app)
        :front-page [fp/own-services e! app]
        :own-services [fp/own-services e! app]
        :transport-service [t-service/select-service-type e! (:transport-service app)]
        :transport-operator [to/operator e! (:transport-operator app)]
        :passenger-transportation [pt/passenger-transportation-info e! (:transport-service app)]
        :terminal [terminal/terminal e! (:transport-service app)]
        :rentals [rental/rental e! (:transport-service app)]
        :parking [parking/parking e! (:transport-service app)]
        :brokerage [brokerage/brokerage e! (:transport-service app)]
        :edit-service [t-service/edit-service e! app]
        :services [service-search/service-search e! (:service-search app)]
        [:div "ERROR: no such page " (pr-str (:page app))])]

     (when-let [msg (:flash-message app)] [flash-message msg])
     [debug-state app]
     [footer]]]]))
