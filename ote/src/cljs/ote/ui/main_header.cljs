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

(defn- is-user-menu-active [app]
  (when (= true (get-in app [:ote-service-flags :user-menu-open]))
    "active"))

(defn page-active?
  "Return true if given current-page belongs to given page-group"
  [page-group current-page pages]
  (cond
    (= page-group :front-page current-page) true
    (page-group pages) ((page-group pages) current-page)
    :default false))

(defn- lang-menu [e! app]
  (let [header-open? (get-in app [:ote-service-flags :lang-menu-open])]
    [:div {:style (merge style-topnav/topnav-dropdown
                         (if header-open?
                           {:opacity 1
                            :visibility "visible"}
                           {:opacity 0
                            :visibility "hidden"
                            ;; Remove the element from normal document flow, by setting position absolute.
                            :position "absolute"}))}
     [:div.container
      [:div.row
       [:div.col-sm-4.col-md-4]
       [:div.col-sm-4.col-md-4]
       [:div.col-sm-8.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         (doall
           (for [[lang flag] footer/selectable-languages]
             ^{:key (str "link_" (name lang) "_" flag)}
             [:li
              [:a (merge
                    (stylefy/use-style style-topnav/topnav-dropdown-link)
                    {:key lang
                     :href "#"
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (fp-controller/->OpenLangMenu))
                                  (e! (fp-controller/->SetLanguage lang)))})
               (str (str/upper-case lang) " - " flag)]]))]]]]]))

(defn- user-menu [e! app]
  (when (user-logged-in? app)
    (let [header-open? (get-in app [:ote-service-flags :user-menu-open])]
      [:div {:style (merge style-topnav/topnav-dropdown
                           (if header-open?
                             {:opacity 1
                              :visibility "visible"}
                             {:opacity 0
                              :visibility "hidden"
                              ;; Remove the element from normal document flow, by setting position absolute.
                              :position "absolute"}))}
       [:div.container.user-menu
        [:div.row
         [:div.col-sm-2.col-md-4]
         [:div.col-sm-2.col-md-4]
         [:div.col-sm-8.col-md-4
          [:ul (stylefy/use-style style-topnav/ul)
           (when (get-in app [:user :transit-authority?])
             [:li
              [:a (merge (stylefy/use-style
                           style-topnav/topnav-dropdown-link)
                         {:href "#/email-settings"
                          :on-click #(e! (fp-controller/->OpenUserMenu))})
               (tr [:common-texts :navigation-email-notification-settings])]])
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/user"
                        :on-click #(e! (fp-controller/->OpenUserMenu))})
             (tr [:common-texts :user-menu-profile])]]
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#"
                        :on-click #(do (.preventDefault %)
                                       (e! (fp-controller/->OpenUserMenu))
                                       (e! (login/->Logout)))})
             (tr [:common-texts :user-menu-log-out])]]]]]]])))

(defn- top-nav-drop-down-menu [e! app pages]
  (let [header-open? (get-in app [:ote-service-flags :header-open])]
    [:div {:style (merge style-topnav/topnav-dropdown
                         (if header-open?
                           {:opacity 1
                            :visibility "visible"}
                           {:opacity 0
                            :visibility "hidden"
                            ;; Remove the element from normal document flow, by setting position absolute.
                            :position "absolute"}))}
     [:div.container.general-menu
      [:div.row
       [:div.col-sm-4.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         [:li
          [:a (merge (stylefy/use-style
                       style-topnav/topnav-dropdown-link)
                     {:href "#/"
                      :on-click #(do
                                   (routes/navigate! :front-page)
                                   (e! (fp-controller/->OpenHeader)))})
           (tr [:common-texts :navigation-front-page])]]
         [:li
          [:a (merge (stylefy/use-style
                       style-topnav/topnav-dropdown-link)
                     {:href "#/services"
                      :on-click #(do
                                   (routes/navigate! :services)
                                   (e! (fp-controller/->OpenHeader)))})
           (tr [:document-title :services])]]
         (when (user-logged-in? app)
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/own-services"
                        :on-click #(do
                                     (routes/navigate! :own-services)
                                     (e! (fp-controller/->OpenHeader)))})
             (tr [:document-title :own-services])]])
         (when (user-logged-in? app)
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/routes"
                        :on-click #(do
                                     (routes/navigate! :routes)
                                     (e! (fp-controller/->OpenHeader)))})
             (tr [:common-texts :navigation-route])]])
         (when (user-logged-in? app)
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/pre-notices"
                        :on-click #(do
                                     (routes/navigate! :pre-notices)
                                     (e! (fp-controller/->OpenHeader)))})
             (tr [:common-texts :navigation-pre-notice])]])
         (when (and (flags/enabled? :pre-notice) (get-in app [:user :transit-authority?]))
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/authority-pre-notices"
                        :on-click #(do
                                     (routes/navigate! :authority-pre-notices)
                                     (e! (fp-controller/->OpenHeader)))})
             (tr [:common-texts :navigation-authority-pre-notices])]])
         (when (:admin? (:user app))
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/admin"
                        :on-click #(do
                                     (routes/navigate! :admin)
                                     (e! (fp-controller/->OpenHeader)))})
             (tr [:document-title :admin])]])
         (when (:admin? (:user app))
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/monitor"
                        :on-click #(do
                                     (routes/navigate! :monitor)
                                     (e! (fp-controller/->OpenHeader)))})
             (tr [:document-title :monitor])]])]]
       [:div.col-sm-4.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         [:li
          [linkify (tr [:common-texts :user-menu-nap-help-link]) (tr [:common-texts :user-menu-nap-help])
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]

         ;; TODO: commented out because for now there are no valid videos. Link shall be restored and updated when videos are available.
         ;[:li
         ; [linkify (tr [:common-texts :user-menu-video-tutorials-link]) (tr [:common-texts :user-menu-video-tutorials])
         ;  (merge (stylefy/use-style
         ;           style-topnav/topnav-dropdown-link)
         ;         {:target "_blank"})]]

         [:li
          [linkify "https://github.com/finnishtransportagency/mmtis-national-access-point/blob/master/docs/api/README.md"
           (tr [:common-texts :navigation-for-developers])
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]]]
       [:div.col-sm-4.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         (if (not (user-logged-in? app))
           [:ul (stylefy/use-style style-topnav/ul)
            [:li
             (if (flags/enabled? :ote-login)
               [:a (merge (stylefy/use-style style-topnav/topnav-dropdown-link)
                          {:href "#"
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (fp-controller/->OpenHeader))
                                        (e! (login/->ShowLoginPage)))})
                (tr [:common-texts :navigation-login])]
               [linkify "/user/login" (tr [:common-texts :navigation-login])
                (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link))])]
            [:li
             [:a (merge (stylefy/use-style
                          style-topnav/topnav-dropdown-link)
                        {:href "#"
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (fp-controller/->OpenHeader))
                                      (e! (fp-controller/->ToggleRegistrationDialog)))})
              (tr [:common-texts :navigation-register])]]])

         (when (flags/enabled? :terms-of-service)
           [:li
            [linkify (tr [:common-texts :navigation-terms-of-service-url]) (tr [:common-texts :navigation-terms-of-service-text])
             (merge (stylefy/use-style
                      style-topnav/topnav-dropdown-link)
                    {:target "_blank"})]])

         [:li
          [linkify (tr [:common-texts :navigation-privacy-policy-url]) (tr [:common-texts :navigation-privacy-policy])
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]
         [:li
          [linkify (tr [:common-texts :navigation-feedback-link]) (tr [:common-texts :navigation-give-feedback])
           (merge (stylefy/use-style
                    (merge style-topnav/topnav-dropdown-link
                           {:padding "10px 0 0 0"}))
                  {:target "_blank"})]
          [:span (stylefy/use-style style-topnav/gray-info-text)
           (tr [:common-texts :navigation-feedback-email])]]]]]]]))

(defn- top-nav-links [e! {:keys [user] :as app}]
  (let [current-language @localization/selected-language]
    [:div.navbar (stylefy/use-style style-topnav/clear)
     [:ul (stylefy/use-style style-topnav/ul)
      [:li
       [:a
        {:style (merge
                  style-topnav/desktop-link
                  {:padding-top "11px"})
         :href "/#/"
         :on-click #(do
                      (e! (fp-controller/->CloseHeaderMenus))
                      (routes/navigate! :front-page))}]]

      (doall
        (for [{:keys [page label url]}
              (filter some? [{:page :services
                              :label [:common-texts :navigation-dataset]}
                             (when (user-logged-in? app)
                               {:page :own-services
                                :label [:common-texts :navigation-own-service-list]})])]
          ^{:key page}
          [:li.hidden-xs.hidden-sm
           [:a
            (merge
              (stylefy/use-style style-topnav/desktop-link)
              {:href (str "/#/" (name page))
               :on-click #(do
                            (.preventDefault %)
                            (e! (fp-controller/->CloseHeaderMenus))
                            (routes/navigate! page))})
            [:div
             (tr label)]]]))

      [:li (stylefy/use-style style-topnav/li-right)
       [:div (merge (stylefy/use-style (merge (if (get-in app [:ote-service-flags :lang-menu-open])
                                                style-topnav/li-right-div-blue
                                                style-topnav/li-right-div-white)))
                    {:on-click #(e! (fp-controller/->OpenLangMenu))})
        [:div {:style (merge {:transition "margin-top 300ms ease"}
                             {:margin-top "7px"})}
         (if (get-in app [:ote-service-flags :lang-menu-open])
           [ic/navigation-close {:style {:color "#fff" :height "24px" :width "30px" :top "5px"}}]
           [ic/action-language {:style {:color "#fff" :height "24px" :width "30px" :top "5px"}}])]
        [:span {:style {:color "#fff"}} (str/upper-case (name current-language))]]]

      [:li (stylefy/use-style style-topnav/li-right)
       [:div.header-general-menu (merge (stylefy/use-style (merge (if (get-in app [:ote-service-flags :header-open])
                                                                    style-topnav/li-right-div-blue
                                                                    style-topnav/li-right-div-white)))
                                        {:on-click #(e! (fp-controller/->OpenHeader))})
        [:div {:style (merge {:transition "margin-top 300ms ease"}
                             {:margin-top "7px"})}
         (if (get-in app [:ote-service-flags :header-open])
           [ic/navigation-close {:style {:color "#fff" :height "24px" :width "30px" :top "5px"}}]
           [ic/navigation-menu {:style {:color "#fff" :height "24px" :width "30px" :top "5px"}}])]
        [:span.hidden-xs {:style {:color "#fff"}} (tr [:common-texts :navigation-general-menu])]]]

      (when (user-logged-in? app)
        [:li (stylefy/use-style style-topnav/li-right)
         [:div.header-user-menu (merge (stylefy/use-style (merge (if (get-in app [:ote-service-flags :user-menu-open])
                                                                   style-topnav/li-right-div-blue
                                                                   style-topnav/li-right-div-white)))
                                       {:on-click #(e! (fp-controller/->OpenUserMenu))})
          [:div {:style (merge {:transition "margin-top 300ms ease"}
                               {:margin-top "7px"})}
           (if (get-in app [:ote-service-flags :user-menu-open])
             [ic/navigation-close {:style {:color "#fff" :height "24px" :width "3px0" :top "5px"}}]
             [ic/social-person {:style {:color "#fff" :height "2px4" :width "30px" :top "5px"}}])]
          [:span.hidden-xs {:style {:color "#fff"}}
           (text/maybe-shorten-text-to 30
                                       (if (clojure.string/blank? (:name user))
                                         (:email user)
                                         (:name user)))]]])

      (when (not (user-logged-in? app))
        [:li (stylefy/use-style style-topnav/li-right)
         [:div (merge (stylefy/use-style (merge style-topnav/li-right-div-white))
                      {:on-click #(e! (fp-controller/->ToggleRegistrationDialog))})
          [:div {:style (merge {:transition "margin-top 300ms ease"
                                :margin-top "0px"})}
           [:span.hidden-xs {:style {:color "#fff"}} (tr [:common-texts :navigation-register])]]]])

      (when (and (not (user-logged-in? app)) (flags/enabled? :ote-login))
        [:li (stylefy/use-style style-topnav/li-right)
         [:div (merge (stylefy/use-style (merge style-topnav/li-right-div-white))
                      {:on-click #(e! (login/->ShowLoginPage))})
          [:div {:style {:transition "margin-top 300ms ease"
                         :margin-top "0px"}}
           [:span.hidden-xs {:style {:color "#fff"}} (tr [:common-texts :navigation-login])]]]])]]))

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
       (for [{:keys [key label href target] :or [href "#"]} (filter some? entries)]
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

(defn bottombar-spacer
  "Horizontal spacing to give entries a bit of breathing room."
  []
  [:span {:style {:margin-right "1.2rem"}}])

(defn nap-bottombar [e! app]
  [:div (stylefy/use-style style-topnav/header-bottombar)
   ; left grouped entries
   [:span (stylefy/use-style {:display "flex"})

    ; TODO: Not sure where "tiedotteet" should be...
    #_[bottombar-dropdown e! app {:tag              :ajankohtaista
                                  :entries          []#_[[:tiedotteet "Tiedotteet"]]
                                  :label            "Ajankohtaista"
                                  :state-flag [:ote-service-flags :lang-TODO-open]  ; TODO
                                      :menu-click-handler identity
                                  :entry-click-handler identity}]

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
                                :state-flag          [:ote-service-flags :service-info-menu-open]  ; TODO
                                :menu-click-handler  #(e! (fp-controller/->OpenServiceInfoMenu))
                                :entry-click-handler identity}]

    ; TODO: I don't think we have "Tuen tarjonta" page yet...?
    #_[bottombar-spacer]
    #_[bottombar-dropdown e! app  {:tag                 :support
                                   :entries             [[:tuen-tarjonta "Tuen tarjonta"]]
                                   :label               "Tuki"
                                   :state-flag          [:ote-service-flags :lang-TODO-open]  ; TODO
                                   :menu-click-handler  identity
                                   :entry-click-handler identity}]

    [bottombar-spacer]

    (when (user-logged-in? app)
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
                                  :menu-click-handler  #(e! (fp-controller/->OpenMyServicesMenu))
                                  :entry-click-handler (fn [e entry]
                                                         (routes/navigate! (:key entry))
                                                         (e! (fp-controller/->OpenMyServicesMenu)))}])
   ]
   ; right aligned entries
   [:span (stylefy/use-style {:display "flex" :margin-left "auto"})
    ; TODO: alternate links when logged out, for logging in/registering
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
                                  :menu-click-handler  #(e! (fp-controller/->OpenUserMenu))
                                  :entry-click-handler (fn [e entry]
                                                         (when (= (:key entry) :Kirjaudu-ulos)
                                                           (.preventDefault e)
                                                           (e! (fp-controller/->OpenUserMenu))
                                                           (e! (login/->Logout))))}])

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
                                :menu-click-handler  #(e! (fp-controller/->OpenLangMenu))
                                :entry-click-handler (fn [e entry]
                                                       (.preventDefault e)
                                                       (e! (fp-controller/->OpenLangMenu))
                                                       (e! (fp-controller/->SetLanguage (:key entry))))}]]])

(defn nap-navbar [e! app desktop?]
  (let [lang-menu-open? (get-in app [:ote-service-flags :lang-menu-open])]
    [:div (stylefy/use-style style-topnav/header-bottombar)
     [:div (stylefy/use-style style-topnav/nap-navigation)
      [:div (stylefy/use-style style-topnav/nap-menu)
       [:ul (stylefy/use-style style-topnav/nap-menu-links)
        ^{:key "entry 1"}
        [:li (stylefy/use-style style-topnav/nap-menu-links-item)
         [:a (stylefy/use-style style-topnav/nap-menu-links-link) "entry 1"]]
        ^{:key "entry 2"}
        [:li (stylefy/use-style style-topnav/nap-menu-links-item)
         [:a (stylefy/use-style style-topnav/nap-menu-links-link) "entry 2"]]
        ^{:key "entry 3"}
        [:li (stylefy/use-style style-topnav/nap-menu-links-item)
         [:a (stylefy/use-style style-topnav/nap-menu-links-link) "entry 3"]]]]]

     [:div (stylefy/use-style style-topnav/nap-languages)
      [:button (merge (stylefy/use-style style-topnav/nap-languages-switcher-button)
                      {:on-click #(e! (fp-controller/->OpenLangMenu))}
                      #_{:on-click (fn [] (reset! menu-visible (not @menu-visible)))})
       [feather-icons/globe (stylefy/use-style (merge style-topnav/nap-languages-switcher-icon
                                                      {:margin-right ".5rem"}))]

       [:span (stylefy/use-style style-topnav/nap-languages-switcher-active)
        (get-lang-label @localization/selected-language)]

       [(if lang-menu-open?
          feather-icons/chevron-up
          feather-icons/chevron-down)
        (stylefy/use-style style-topnav/nap-languages-switcher-icon)]]
      [:ul#languages-menu (stylefy/use-style (merge style-topnav/nap-languages-switcher-menu
                                                    (when (not lang-menu-open?)
                                                      {:display "none"})))

       (doall
         (for [[lang flag] footer/selectable-languages]
           ^{:key (str "link_" (name lang) "_" flag)}
           [:li (stylefy/use-style style-topnav/nap-languages-switcher-item)
            [:a (merge (stylefy/use-style style-topnav/nap-languages-switcher-link)
                       {:key lang
                        :href "#"
                        :on-click #(do
                                     (.preventDefault %)
                                     (e! (fp-controller/->OpenLangMenu))
                                     (e! (fp-controller/->SetLanguage lang)))})
             flag]]))]]
     ]))

(def quicklink-urls
  {:fintraffic      {:url "https://www.fintraffic.fi/fi"                :langs {:fi "/fi" :sv "/sv" :en "/en"}}
   :liikennetilanne {:url "https://liikennetilanne.fintraffic.fi"       :langs {:fi "/fi" :sv "/sv" :en "/en"}}
   :palautevayla    {:url "https://palautevayla.fi/aspa?lang="          :langs {:fi "fi"  :sv "sv"  :en "en"}}
   :junalahdot      {:url "https://junalahdot.fi/junalahdot/main?lang=" :langs {:fi "1"   :sv "2"   :en "3"}}
   :skynavx         {:url "https://skynavx.fi/#/drone"                  :langs {}}
   :digitraffic     {:url "https://www.digitraffic.fi"                  :langs {:en "/en/"}}
   :digitransit     {:url "https://digitransit.fi"                      :langs {:en "/en/"}}
   :finap           {:url "https://finap.fi/#/"                         :langs {}}})

(defn- localized-quicklink-uri [quicklink]
  (let [current-language    (or (keyword @localization/selected-language) :fi)
        {:keys [url langs]} (get quicklink-urls quicklink)
        lang                (get langs current-language "")]
    (str url lang)))

(defn fintraffic-quick-links []
  [:ul (stylefy/use-style style-topnav/fintraffic-quick-links-menu)
     (doall
       (for [[href service] (map (juxt localized-quicklink-uri identity)
                                 [:liikennetilanne
                                  :palautevayla
                                  :junalahdot
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
  ; TODO: ::before height .5rem, width .5rem, display: block, position:absolute...
  [:div (stylefy/use-style style-topnav/header-topbar)
   [:a (merge (stylefy/use-style style-topnav/fintraffic-logo-link)
              {:href (localized-quicklink-uri :fintraffic)})
    [:img {:style style-topnav/fintraffic-logo
           :src "img/icons/Fintraffic_vaakalogo_valkoinen.svg"}]]
   [:nav {:style {:display "inline-flex"}}
    [fintraffic-quick-links]]])

(defn header [e! app desktop?]
  [:header
   [fintraffic-navbar]
   #_[nap-navbar e! app desktop?]
   [nap-bottombar e! app]  ; navbar-refaktorointi
   ; old bar's remnants
   [:div
    [:div (stylefy/use-style style-topnav/topnav-wrapper)
     [:div
      (stylefy/use-style style-topnav/topnav-desktop)
      [:div.container
       [top-nav-links e! app]]]

     [top-nav-drop-down-menu e! app]
     [user-menu e! app]
     [lang-menu e! app]]]

   [esc-press-listener e! app]
   [tos-notification e! app desktop?]])