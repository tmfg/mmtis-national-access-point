(ns ote.ui.main-header
  "Main header of the OTE app"
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
            [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]
            [ote.util.text :as text]
            [ote.localization :refer [tr tr-key]]
            [ote.localization :as localization]
            [ote.app.localstorage :as localstorage]
            [ote.app.routes :as routes]
            [ote.app.utils :refer [user-logged-in? user-operates-service-type?]]
            [ote.ui.common :as common :refer [linkify]]
            [ote.style.base :as style-base]
            [ote.style.topnav :as style-topnav]
            [ote.style.base :as base]
            [ote.theme.colors :as colors]
            [ote.app.controller.flags :as flags]
            [ote.app.controller.login :as login]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.theme :refer [theme]]
            [ote.views.footer :as footer]
            [ote.views.front-page :as fp]
            [ote.views.transport-operator :as to]
            [re-svg-icons.feather-icons :as feather-icons]
            [taxiui.app.routes :as taxiui-router]))

(defn esc-press-listener [e! app]
  "Listens to keydown events on document. If esc is clicked call CloseHeaderMenus"
  (let [esc-press (fn [event]
                    (if (= event.keyCode 27)
                      (e! (fp-controller/->CloseHeaderMenus))))]
    (r/create-class
      {:component-did-mount
       (fn [_]
         (.addEventListener js/document "keydown" #(esc-press %)))
       :component-will-unmount
       (fn [_]
         (.removeEventListener js/document "keydown" #(esc-press %)))
       :reagent-render
       (fn [_]
         [:span {:ref "clicksensor"}])})))

(defn get-lang-label [lang]
  (str (->> footer/selectable-languages
            (filter #(= (first %) (name lang)))
            first
            second)))

(defn bottombar-dropdown [e! app desktop? options]
  (let [{:keys [tag entries label prefix-icon menu-click-handler entry-click-handler state-flag]} options
        menu-open?                                                                                (get-in app state-flag)]
   [:div (stylefy/use-style style-topnav/bottombar-menu-section)
    [:button (merge (stylefy/use-style style-topnav/bottombar-entry-button)
                    {:on-click menu-click-handler})
     ; prefix icon
     (when prefix-icon
       [prefix-icon (stylefy/use-style (merge style-topnav/bottombar-entry-icon
                                                        {:margin-right ".5rem"}))])
     ; label
     [:span (stylefy/use-style style-topnav/bottombar-entry-label)
      (or label "")]

     ; dropdown open link
     [(if menu-open?
        feather-icons/chevron-up
        feather-icons/chevron-down)
      (stylefy/use-style style-topnav/bottombar-entry-icon)]]

    ; menu items
    [:ul (merge (stylefy/use-style (merge style-topnav/bottombar-dropdown-items
                                                  (when (not menu-open?)

                                                      {:display "none"}))) ;; Todo jotain mobiilissa psks
                {:id (str (name tag) "-menu")})
     (doall
       (for [{:keys [key label href target force-external-icon?] :or {href "#"}} (filter some? entries)]
         ^{:key (str "link_" (name tag) "_" (name key))}  ; TODO: slugify
         [:li (stylefy/use-style style-topnav/bottombar-dropdown-item)
          [common/linkify
           href
           (str label)
           (merge {:key      (name key)
                   :style    style-topnav/bottombar-dropdown-link
                   :on-click #(entry-click-handler % {:key key :label label :href href})
                   :force-external-icon? force-external-icon?}
                  (when target {:target target})
                  (when force-external-icon? {:force-external-icon? force-external-icon?}))]]))]
    ]))

(defn bottombar-simplelink [e! app options]
  (let [{:keys [label label-styles menu-click-handler]} options]
   [:div (stylefy/use-style {:align-self "center"})
    [:button (merge (stylefy/use-style style-topnav/bottombar-entry-button)
                    {:on-click menu-click-handler})
     ; label
     [:span (stylefy/use-style (merge style-topnav/bottombar-entry-label
                                      (when label-styles
                                        label-styles)))
      (or label "")]
    ]]))

(defn bottombar-linkify [e! app options]
  (let [{:keys [key label href target force-external-icon?] :or {href "#"}} options]
    [:div (stylefy/use-style {:align-self "center"})
     [common/linkify
      href
      (str label)
      (merge {:key      (name key)
              :style    style-topnav/bottombar-dropdown-link
              :force-external-icon? force-external-icon?}
             (when target {:target target})
             (when force-external-icon? {:force-external-icon? force-external-icon?}))]]))


(defn bottombar-spacer
  "Horizontal spacing to give entries a bit of breathing room."
  []
  [:span (stylefy/use-style style-topnav/bottombar-spacer)])

(defn user-is?
  [app kind]
  (= true (or (some-> app :user :admin?)
              (some-> app :user kind))))

(defn nap-bottombar [e! app desktop?]
  (let [menu-open? (get-in app [:ote-service-flags :mobile-bottom-menu-open])]
    [:div (stylefy/use-style style-topnav/header-bottombar)
     ; frontpage link has its own container to keep it always visible
     [:span (stylefy/use-style style-topnav/bottombar-left-aligned-items)
      [:div (stylefy/use-style {:align-self "center"})
       [:button (merge (stylefy/use-style style-topnav/bottombar-entry-button)
                       {:on-click #(do (routes/navigate! :front-page)
                                       (e! (fp-controller/->CloseHeaderMenus)))})
        ; label
        [:span (stylefy/use-style (merge style-topnav/bottombar-frontpage-label
                                         {:font-weight "800"}))
         "NAP"]
        ]]

      ; mobile only right aligned menu toggle button
      [:span (stylefy/use-style style-topnav/bottombar-mobile-menu)
       [:button (merge (stylefy/use-style (merge style-topnav/mobile-only
                                                 style-topnav/bottombar-mobile-nav-button))
                       {:on-click #(e! (fp-controller/->ToggleMobileBottomMenu))})
        [:span {:style {:font-weight 600 :align-self "center"}} (tr [:common-texts :navigation-general-menu])]  ; TODO: translations
        [feather-icons/menu
         (stylefy/use-style style-topnav/bottombar-entry-icon)]]]]

     ; left grouped entries
     [:span (stylefy/use-style (merge style-topnav/bottombar-left-aligned-items
                                      (when (not desktop?)
                                        (if menu-open?
                                          {:display "block"}
                                          {:display "none"}))))

      [bottombar-spacer]

      [bottombar-dropdown e! app desktop? {:tag                 :service-info
                                           :entries             [{:key    :updates
                                                                  :label  (tr [:common-texts :updates-menu-updates])
                                                                  :href   "https://www.fintraffic.fi/fi/fintraffic/tiedotteet"
                                                                  :target "_blank"}
                                                                 {:key :ohjeet
                                                                  :label (tr [:common-texts :user-menu-nap-help])
                                                                  :href (tr [:common-texts :user-menu-nap-help-link])
                                                                  :target "_blank"}
                                                                 {:key :rajapinta
                                                                  :label (tr [:common-texts :navigation-for-developers])
                                                                  :href "https://github.com/finnishtransportagency/mmtis-national-access-point/blob/master/docs/api/README.md"
                                                                  :target "_blank"}
                                                                 (when (flags/enabled? :terms-of-service)
                                                                   {:key :käyttöehdot
                                                                    :label (tr [:common-texts :navigation-terms-of-service-text])
                                                                    :href (tr [:common-texts :navigation-terms-of-service-url])
                                                                    :target "_blank"})
                                                                 {:key :tietosuojaseloste
                                                                  :label (tr [:common-texts :navigation-privacy-policy])
                                                                  :href (tr [:common-texts :navigation-privacy-policy-url])
                                                                  :target "_blank"}]
                                           :label               (tr [:common-texts :navigation-service-info-menu])
                                           :state-flag          [:ote-service-flags :service-info-menu-open]
                                           :menu-click-handler  #(e! (fp-controller/->ToggleServiceInfoMenu))
                                           :entry-click-handler identity}]

      [bottombar-spacer]

      [bottombar-dropdown e! app desktop? {:tag                 :support
                                           :entries             [{:key :channels
                                                                  :label (tr [:common-texts :support-menu-channels])
                                                                  :href (tr [:common-texts :support-menu-channels-url])
                                                                  :target "_blank"}]
                                           :label               (tr [:common-texts :navigation-support-menu])
                                           :state-flag          [:ote-service-flags :support-menu-open]
                                           :menu-click-handler  #(e! (fp-controller/->ToggleSupportMenu))
                                           :entry-click-handler identity}]

      [bottombar-spacer]

      (if (user-logged-in? app)
        [bottombar-dropdown e! app desktop? {:tag                 :my-services
                                             :entries             [{:key :services
                                                                    :label (tr [:document-title :services])
                                                                    :href "#/services"}
                                                                   {:key :own-services
                                                                    :label (tr [:document-title :own-services])
                                                                    :href "#/own-services"}
                                                                   {:key :taxiui_statistics
                                                                    :label (tr [:taxi-ui :cross-promo :myservices-statistics-link])
                                                                    :href (str "/taxiui#" (taxiui-router/resolve :taxi-ui/stats {}))
                                                                    :force-external-icon? true
                                                                    :target "_blank"}
                                                                   {:key :routes
                                                                    :label (tr [:common-texts :navigation-route])
                                                                    :href "#/routes"}
                                                                   {:key :pre-notices
                                                                    :label (tr [:common-texts :navigation-pre-notice])
                                                                    :href "#/pre-notices"}
                                                                   (when (user-is? app :authority-group-admin?)
                                                                     {:key :authority-pre-notices
                                                                      :label (tr [:common-texts :navigation-authority-pre-notices])
                                                                      :href "#/authority-pre-notices"})
                                                                   (when (user-is? app :authority-group-admin?)
                                                                     {:key :admin
                                                                      :label (tr [:document-title :admin])
                                                                      :href "#/admin"})
                                                                   (when (user-is? app :authority-group-admin?)
                                                                     {:key :admin-detected-changes
                                                                      :label (tr [:document-title :admin-detected-changes])
                                                                      :href "#/admin/detected-changes/detect-changes"})
                                                                   (when (user-is? app :authority-group-admin?)
                                                                     {:key :monitor
                                                                      :label (tr [:document-title :monitor])
                                                                      :href "#/monitor"})]
                                             :label               (tr [:common-texts :navigation-my-services-menu])
                                             :state-flag          [:ote-service-flags :my-services-menu-open]
                                             :menu-click-handler  #(e! (fp-controller/->ToggleMyServicesMenu))
                                             :entry-click-handler (fn [e entry]
                                                                    (routes/navigate! (:key entry))
                                                                    (e! (fp-controller/->ToggleMyServicesMenu)))}]
        [bottombar-simplelink e! app {:label              (tr [:document-title :services])
                                      :href               "#/services"
                                      :menu-click-handler #(do (routes/navigate! :services)
                                                               (e! (fp-controller/->CloseHeaderMenus)))}])

      (when-not (user-logged-in? app) [bottombar-spacer])
      (when-not (user-logged-in? app)
        [bottombar-linkify e! app {:key :taxiui_statistics
                                     :label (tr [:taxi-ui :cross-promo :myservices-statistics-link])
                                     :href (str "/taxiui#" (taxiui-router/resolve :taxi-ui/stats {}))
                                     :force-external-icon? true
                                     :target "_blank"}])
      #_{:key :taxiui_statistics
         :label (tr [:taxi-ui :cross-promo :myservices-statistics-link])
         :href (str "/taxiui#" (taxiui-router/resolve :taxi-ui/stats {}))
         :force-external-icon? true
         :target "_blank"}

      (when (and (user-logged-in? app)
                 (user-operates-service-type? app :taxi))
        [common/linkify
         "/taxiui"
         [:span
          {:style {:background-color colors/primary-background-color
                   :color            colors/primary-text-color
                   :padding          ".3em .6em .3em .6em"
                   :border-radius    ".5em"}}
          (tr [:taxi-ui :cross-promo :main-site-header])]
         {:style    (merge style-topnav/bottombar-dropdown-link
                           {:display     "inline-flex"
                            :align-items "center"})
          :target   "_blank"}])
     ]

     ; right aligned entries
     [:span (stylefy/use-style (merge style-topnav/bottombar-right-aligned-items
                                      (when (not desktop?)
                                        (if menu-open?
                                          {:display "block"}
                                          {:display "none"}))))
      (when (user-logged-in? app)
        [bottombar-dropdown e! app desktop? {:tag                 :user-details
                                             :entries             [{:key   :Sähköposti-ilmoitusten-asetukset
                                                                    :label (tr [:common-texts :navigation-email-notification-settings])
                                                                    :href  "#/email-settings"}
                                                                   {:key   :Käyttäjätilin-muokkaus
                                                                    :label (tr [:common-texts :user-menu-profile])
                                                                    :href "#/user"}
                                                                   {:key   :Kirjaudu-ulos
                                                                    :label (tr [:common-texts :user-menu-log-out])}]
                                             :label               (get-in app [:user :name])
                                             :prefix-icon         feather-icons/user
                                             :state-flag          [:ote-service-flags :user-menu-open]
                                             :menu-click-handler  #(e! (fp-controller/->ToggleUserMenu))
                                             :entry-click-handler (fn [e entry]
                                                                    (when (= (:key entry) :Kirjaudu-ulos)
                                                                      (.preventDefault e)
                                                                      (e! (fp-controller/->ToggleUserMenu))
                                                                      (e! (login/->Logout))))}])
      ; reagent version in this project is so old that it doesn't support fragments ([:<>]) so have to do these three
      ; like so...
      (when (and (not (user-logged-in? app))
                 (flags/enabled? :ote-login))
        [bottombar-simplelink e! app {:label              (tr [:common-texts :navigation-login])
                                      :menu-click-handler #(e! (login/->ShowLoginPage))}])

      (when-not (user-logged-in? app) [bottombar-spacer])
      (when-not (user-logged-in? app)
        [bottombar-simplelink e! app {:label              (tr [:common-texts :navigation-register])
                                      :menu-click-handler #(e! (fp-controller/->ToggleRegistrationDialog))}])

      [bottombar-spacer]

      [bottombar-dropdown e! app desktop? {:tag                 :language-selector
                                           :entries             [{:key "fi"
                                                                  :label "Suomeksi"}
                                                                 {:key "sv"
                                                                  :label "På Svenska"}
                                                                 {:key "en"
                                                                  :label "In English"}]
                                           :label               (get-lang-label @localization/selected-language)
                                           :prefix-icon         feather-icons/globe
                                           :state-flag          [:ote-service-flags :lang-menu-open]
                                           :menu-click-handler  #(e! (fp-controller/->ToggleLangMenu))
                                           :entry-click-handler (fn [e entry]
                                                                  (.preventDefault e)
                                                                  (e! (fp-controller/->ToggleLangMenu))
                                                                  (e! (fp-controller/->SetLanguage (:key entry))))}]]]))

(defn fintraffic-quick-links [e! app menu-open? desktop?]
  [:ul (stylefy/use-style (merge style-topnav/fintraffic-quick-links-menu
                                 (when (and (not desktop?)
                                            (not menu-open?))
                                   {:display "none"})))
     (doall
       (for [[href service] (map (juxt common/localized-quicklink-uri identity)
                                 [:traffic-situation
                                  :feedback-channel
                                  :train-departures
                                  :fintraffic-app
                                  :digitraffic
                                  :digitransit
                                  :finap])]
         ^{:key (str "quicklink_" (name service))}
         [:li (stylefy/use-style (merge style-topnav/fintraffic-quick-links-item
                                        (when (= service :finap) style-topnav/fintraffic-quick-links-active)))
          [common/linkify
            href
            (tr [:quicklink-header service])
            {:style               style-topnav/fintraffic-quick-links-link
             :hide-external-icon? true}]
          (when (= service :finap)
            [:div (stylefy/use-style style-topnav/fintraffic-quick-links-uparrow) ""])]))])

(defn- fintraffic-navbar [e! app desktop?]
  (let [menu-open? (get-in app [:ote-service-flags :fintraffic-menu-open])]
    [:div (stylefy/use-style style-topnav/header-topbar)
     [common/linkify
      (common/localized-quicklink-uri :fintraffic)
      [:img {:style style-topnav/fintraffic-logo
             :src   "img/icons/Fintraffic_vaakalogo_valkoinen.svg"}]
      {:style               style-topnav/fintraffic-logo-link
       :hide-external-icon? true}]
     [:nav (stylefy/use-style style-topnav/fintraffic-quick-links)
      [:button (merge (stylefy/use-style (merge style-topnav/mobile-only
                                                style-topnav/fintraffic-mobile-nav-button))
                      {:on-click #(e! (fp-controller/->ToggleFintrafficMenu))})
       [:span {:style {:font-weight 600 :align-self "center"}} "Palvelut"]  ; TODO: translations
       [(if menu-open?
         feather-icons/chevron-up
         feather-icons/chevron-down)
       (stylefy/use-style style-topnav/topbar-entry-icon)]]
      [fintraffic-quick-links e! app menu-open? desktop?]]]))

(defn header [e! app desktop?]
  [:header {:style {:box-shadow "0 2px 10px 0 rgba(0,0,0,0.1)"
                    :z-index    "100"}}
   [fintraffic-navbar e! app desktop?]
   [nap-bottombar e! app desktop?]
   [esc-press-listener e! app]])