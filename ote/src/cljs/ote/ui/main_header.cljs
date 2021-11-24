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
            [ote.app.utils :refer [user-logged-in?]]
            [ote.ui.common :refer [linkify]]
            [ote.style.base :as style-base]
            [ote.style.topnav :as style-topnav]
            [ote.style.base :as base]
            [ote.app.controller.flags :as flags]
            [ote.app.controller.login :as login]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.theme :refer [theme]]
            [ote.views.footer :as footer]
            [ote.views.front-page :as fp]
            [ote.views.transport-operator :as to]
            [re-svg-icons.feather-icons :as feather-icons]))

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

(defn tos [e! app desktop?]
  (let [page (:page app)
        user (:user app)
        user-logged-in? (not (nil? user))
        show-tos? (not (or (= page :register)
                           (and (not user-logged-in?) (= "true" (localstorage/get-item :tos-ok)))
                           (and user-logged-in? (= "true" (localstorage/get-item (keyword (str (:email user) "-tos-ok")))))))]
    (when show-tos?
      [:div {:style
             (merge
               style-topnav/tos-container
               (when-not desktop?
                 {:padding "2px"}))}
       [:div {:style {:display "inline-flex" :width "90%"}}
        [ic/action-info {:style {:color "#FFFFFF"}}]
        [:span (stylefy/use-style style-topnav/tos-texts)
         (tr [:common-texts :agree-to-privacy-and-terms])
         (linkify (tr [:common-texts :navigation-terms-of-service-url]) (str/lower-case (tr [:common-texts :navigation-terms-of-service])) {:style style-topnav/tos-toplink
                                                                                                                                            :target "_blank"})
         (tr [:common-texts :and])
         (linkify (tr [:common-texts :navigation-privacy-policy-url]) (tr [:common-texts :navigation-privacy-policy-text]) {:style (merge style-topnav/tos-toplink
                                                                                                                                          {:padding-right 0})
                                                                                                                            :target "_blank"})
         (tr [:common-texts :navigation-terms-and-cookies])]]
       [:div {:style (merge
                       {:width "10%" :float "right"}
                       (when-not desktop?
                         {:padding-top "10px"}))}
        [:span {:style {:float "right" :padding-right "10px"}
                :on-click #(do
                             (.preventDefault %)
                             (e! (fp-controller/->CloseTermsAndPrivacy user)))}
         [ic/navigation-close {:style {:color "#FFFFFF"}}]]]])))

(defn tos-notification [e! app desktop?]
  (let [user (:user app)
        page (:page app)
        user-logged-in? (not (nil? user))
        show-tos? (if (flags/enabled? :terms-of-service)
                    (not (or (= page :register)
                             (and (not user-logged-in?) (= "true" (localstorage/get-item :tos-ok)))
                             (and user-logged-in? (= "true" (localstorage/get-item (keyword (str (:email user) "-tos-ok")))))))
                    false)]
  (when (and (flags/enabled? :terms-of-service)
             show-tos?)
    [tos e! app desktop?])))

(defn get-lang-label [lang]
  (str (->> footer/selectable-languages
            (filter #(= (first %) (name lang)))
            first
            second)))

(defn bottombar-dropdown [e! app options]
  (let [{:keys [tag entries label prefix-icon menu-click-handler entry-click-handler state-flag]} options
        menu-open?                                                                            (get-in app state-flag)]
   [:div (stylefy/use-style {:align-self "center"})
    [:button (merge (stylefy/use-style style-topnav/bottombar-entry-button)
                    {:on-click menu-click-handler})
     ; prefix icon
     (when prefix-icon
       [prefix-icon (stylefy/use-style (merge style-topnav/bottombar-entry-icon
                                                        {:margin-right ".5rem"}))])
     ; label
     [:span (stylefy/use-style style-topnav/nap-languages-switcher-active)
      (if (not (nil? label)) label)]

     ; dropdown open link
     [(if menu-open?
        feather-icons/chevron-up
        feather-icons/chevron-down)
      (stylefy/use-style style-topnav/bottombar-entry-icon)]]

    ; menu items
    [:ul (merge (stylefy/use-style (merge style-topnav/nap-languages-switcher-menu
                                                  (when (not menu-open?)
                                                    {:display "none"})))
                {:id (str (name tag) "-menu")})
     (doall
       (for [{:keys [key label href target] :or {href "#"}} (filter some? entries)]
         ^{:key (str "link_" (name tag) "_" (name key))}  ; TODO: slugify
         [:li (stylefy/use-style style-topnav/nap-languages-switcher-item)
          [:a (merge (stylefy/use-style style-topnav/nap-languages-switcher-link)
                     {:key (name key)
                      :href href
                      ; the rewrapping of entry values to map is done manually instead of using map destructuring's
                      ; :as directive because the :as doesn't include default values from :or directive
                      :on-click #(entry-click-handler % {:key key :label label :href href})}
                     (when (some? target)
                       {:target target}))
           (str label)]]))]
    ]))

(defn bottombar-simplelink [e! app options]
  (let [{:keys [label menu-click-handler]} options]
   [:div (stylefy/use-style {:align-self "center"})
    [:button (merge (stylefy/use-style style-topnav/bottombar-entry-button)
                    {:on-click menu-click-handler})
     ; label
     [:span (stylefy/use-style style-topnav/nap-languages-switcher-active)
      (if (not (nil? label)) label)]
    ]]))

(defn bottombar-spacer
  "Horizontal spacing to give entries a bit of breathing room."
  []
  [:span {:style {:margin-right "1.2rem"}}])

(defn nap-bottombar [e! app]
  [:div (stylefy/use-style style-topnav/header-bottombar)
   ; left grouped entries
   [:span (stylefy/use-style {:display "flex"})

    ; TODO: Not sure where "tiedotteet" should be...
    #_[bottombar-dropdown e! app {:tag              :updates
                                  :entries          []#_[[:tiedotteet "Tiedotteet"]]
                                  :label            "Ajankohtaista"
                                  :state-flag [:ote-service-flags :lang-TODO-open]
                                      :menu-click-handler identity
                                  :entry-click-handler identity}]

    #_[bottombar-spacer]

    [bottombar-simplelink e! app {:label              "NAP"
                                  :menu-click-handler #(routes/navigate! :front-page)}]

    [bottombar-spacer]

    [bottombar-dropdown e! app {:tag                 :service-info
                                :entries             [{:key :ohjeet
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

    ; TODO: I don't think we have "Tuen tarjonta" page yet...?
    #_[bottombar-spacer]
    #_[bottombar-dropdown e! app  {:tag                 :support
                                   :entries             [[:tuen-tarjonta "Tuen tarjonta"]]
                                   :label               "Tuki"
                                   :state-flag          [:ote-service-flags :lang-TODO-open]
                                   :menu-click-handler  identity
                                   :entry-click-handler identity}]

    [bottombar-spacer]

    (if (user-logged-in? app)
      [bottombar-dropdown e! app {:tag                 :my-services
                                  :entries             [{:key :services
                                                         :label (tr [:document-title :services])
                                                         :href "#/services"}
                                                        {:key :own-services
                                                         :label (tr [:document-title :own-services])
                                                         :href "#/own-services"}
                                                        {:key :routes
                                                         :label (tr [:common-texts :navigation-route])
                                                         :href "#/routes"}
                                                        {:key :pre-notices
                                                         :label (tr [:common-texts :navigation-pre-notice])
                                                         :href "#/pre-notices"}
                                                        {:key :authority-pre-notices
                                                         :label (tr [:common-texts :navigation-authority-pre-notices])
                                                         :href "#/authority-pre-notices"}
                                                        {:key :admin
                                                         :label (tr [:document-title :admin])
                                                         :href "#/admin"}
                                                        {:key :monitor
                                                         :label (tr [:document-title :monitor])
                                                         :href "#/monitor"}]
                                  :label               (tr [:common-texts :navigation-my-services-menu])
                                  :state-flag          [:ote-service-flags :my-services-menu-open]
                                  :menu-click-handler  #(e! (fp-controller/->ToggleMyServicesMenu))
                                  :entry-click-handler (fn [e entry]
                                                         (routes/navigate! (:key entry))
                                                         (e! (fp-controller/->ToggleMyServicesMenu)))}]
      [bottombar-simplelink e! app {:label              (tr [:document-title :services])
                                    :href               "#/services"
                                    :menu-click-handler #(routes/navigate! :services)}])
   ]
   ; right aligned entries
   [:span (stylefy/use-style {:display "flex" :margin-left "auto"})
    (when (user-logged-in? app)
      [bottombar-dropdown e! app {:tag                 :user-details
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

    (when (not (user-logged-in? app)) [bottombar-spacer])

    (when (not (user-logged-in? app))
      [bottombar-simplelink e! app {:label              (tr [:common-texts :navigation-register])
                                    :menu-click-handler #(e! (fp-controller/->ToggleRegistrationDialog))}])

    [bottombar-spacer]

    [bottombar-dropdown e! app {:tag                 :language-selector
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
                                                       (e! (fp-controller/->SetLanguage (:key entry))))}]]])

(def quicklink-urls
  {:fintraffic        {:url "https://www.fintraffic.fi/fi"                :langs {:fi "/fi" :sv "/sv" :en "/en"}}
   :traffic-situation {:url "https://liikennetilanne.fintraffic.fi"       :langs {:fi "/fi" :sv "/sv" :en "/en"}}
   :feedback-channel  {:url "https://palautevayla.fi/aspa?lang="          :langs {:fi "fi"  :sv "sv"  :en "en"}}
   :train-departures  {:url "https://junalahdot.fi/junalahdot/main?lang=" :langs {:fi "1"   :sv "2"   :en "3"}}
   :skynavx           {:url "https://skynavx.fi/#/drone"                  :langs {}}
   :digitraffic       {:url "https://www.digitraffic.fi"                  :langs {:en "/en/"}}
   :digitransit       {:url "https://digitransit.fi"                      :langs {:en "/en/"}}
   :finap             {:url "https://finap.fi/#/"                         :langs {}}})

(defn- localized-quicklink-uri [quicklink]
  (let [current-language    (or (keyword @localization/selected-language) :fi)
        {:keys [url langs]} (get quicklink-urls quicklink)
        lang                (get langs current-language "")]
    (str url lang)))

(defn fintraffic-quick-links []
  [:ul (stylefy/use-style style-topnav/fintraffic-quick-links-menu)
     (doall
       (for [[href service] (map (juxt localized-quicklink-uri identity)
                                 [:traffic-situation
                                  :feedback-channel
                                  :train-departures
                                  :skynavx
                                  :digitraffic
                                  :digitransit
                                  :finap])]
         ^{:key (str "quicklink_" (name service))}
         [:li (stylefy/use-style (merge style-topnav/fintraffic-quick-links-item
                                        (when (= service :finap) style-topnav/fintraffic-quick-links-active)))
          [:a (merge (stylefy/use-style style-topnav/fintraffic-quick-links-link)
                     {:href href})
           (tr [:quicklink-header service])]
          (when (= service :finap)
            [:div (stylefy/use-style style-topnav/fintraffic-quick-links-uparrow) ""])]))])

(defn- fintraffic-navbar []
  [:div (stylefy/use-style style-topnav/header-topbar)
   [:a (merge (stylefy/use-style style-topnav/fintraffic-logo-link)
              {:href (localized-quicklink-uri :fintraffic)})
    [:img {:style style-topnav/fintraffic-logo
           :src "img/icons/Fintraffic_vaakalogo_valkoinen.svg"}]]
   [:nav {:style {:display "inline-flex"}}
    [fintraffic-quick-links]]])

(defn header [e! app desktop?]
  [:header {:style {:box-shadow "0 2px 10px 0 rgba(0,0,0,0.1)"
                    :z-index    "100"}}
   [fintraffic-navbar]
   [nap-bottombar e! app]
   [esc-press-listener e! app]
   [tos-notification e! app desktop?]])