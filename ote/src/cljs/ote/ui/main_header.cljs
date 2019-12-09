(ns ote.ui.main-header
  "Main header of the OTE app"
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
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
            [ote.views.transport-operator :as to]))

(defn header-scroll-sensor [is-scrolled? trigger-offset]
  (let [sensor-node (atom nil)
        check-scroll (fn []
                       (let [element-y (.-top (.getBoundingClientRect @sensor-node))]
                         (reset! is-scrolled? (< element-y trigger-offset))))]

    (r/create-class
      {:component-did-mount
       (fn [this]
         (reset! sensor-node (aget this "refs" "sensor"))
         (check-scroll)
         (.addEventListener js/window "scroll" check-scroll))
       :component-will-unmount
       (fn [this]
         (.removeEventListener js/window "scroll" check-scroll))
       :reagent-render
       (fn [_]
         [:span {:ref "sensor"}])})))

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

(defn- top-nav-drop-down-menu [e! app is-scrolled? pages]
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

(defn- top-nav-links [e! {:keys [user] :as app} is-scrolled?]
  (let [current-language @localization/selected-language]
    [:div.navbar (stylefy/use-style style-topnav/clear)
     [:ul (stylefy/use-style style-topnav/ul)
      [:li
       [:a
        {:style (merge
                  style-topnav/desktop-link
                  (if @is-scrolled?
                    {:padding-top "0px"}
                    {:padding-top "11px"}))
         :href "/#/"
         :on-click #(do
                      (e! (fp-controller/->CloseHeaderMenus))
                      (routes/navigate! :front-page))}
        [:img {:style (merge
                        style-topnav/logo
                        (when @is-scrolled?
                          style-topnav/logo-small))
               :src "img/icons/nap-logo.svg"}]]]

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
              (stylefy/use-style
                (merge style-topnav/desktop-link
                       (when @is-scrolled?
                         {:height "56px"})))
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
                                                style-topnav/li-right-div-white)
                                              (when @is-scrolled?
                                                {:height "56px"})))
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
                                                                    style-topnav/li-right-div-white)
                                                                  (when @is-scrolled?
                                                                    {:height "56px"})))
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
                                                                   style-topnav/li-right-div-white)
                                                                 (when @is-scrolled?
                                                                   {:height "56px"})))
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
         [:div (merge (stylefy/use-style (merge style-topnav/li-right-div-white
                                                (when @is-scrolled?
                                                  {:height "56px"})))
                      {:on-click #(e! (fp-controller/->ToggleRegistrationDialog))})
          [:div {:style (merge {:transition "margin-top 300ms ease"}
                               (if @is-scrolled?
                                 {:margin-top "0px"}
                                 {:margin-top "0px"}))}
           [:span.hidden-xs {:style {:color "#fff"}} (tr [:common-texts :navigation-register])]]]])

      (when (and (not (user-logged-in? app)) (flags/enabled? :ote-login))
        [:li (stylefy/use-style style-topnav/li-right)
         [:div (merge (stylefy/use-style (merge style-topnav/li-right-div-white
                                                (when @is-scrolled?
                                                  {:height "56px"})))
                      {:on-click #(e! (login/->ShowLoginPage))})
          [:div {:style (merge {:transition "margin-top 300ms ease"}
                               (if @is-scrolled?
                                 {:margin-top "0px"}
                                 {:margin-top "0px"}))}
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
         (linkify (tr [:common-texts :navigation-privacy-policy]) (tr [:common-texts :navigation-privacy-policy-text]) {:style (merge style-topnav/tos-toplink
                                                                                                                                      {:padding-right 0})
                                                                                                                        :target "_blank"})
         (tr [:common-texts :navigation-terms-and-cookies])]]
       [:div {:style (merge
                       {:width "20px" :float "right"}
                       (when-not desktop?
                         {:padding-top "10px"}))}
        [:span {:on-click #(do
                             (.preventDefault %)
                             (e! (fp-controller/->CloseTermsAndPrivacy user)))}
         [ic/navigation-close {:style {:color "#FFFFFF"}}]]]])))

(defn top-nav [e! app is-scrolled? desktop?]
  (let [user (:user app)
        page (:page app)
        user-logged-in? (not (nil? user))
        show-tos? (not (or (= page :register)
                           (and (not user-logged-in?) (= "true" (localstorage/get-item :tos-ok)))
                           (and user-logged-in? (= "true" (localstorage/get-item (keyword (str (:email user) "-tos-ok")))))))]
    [:div {:style (cond
                    (and (= false @is-scrolled?) show-tos?)
                    {:padding-bottom "3rem"}
                    (and @is-scrolled? show-tos?)
                    {:padding-bottom "2rem"}
                    (and (and (= false) @is-scrolled?) (= false show-tos?))
                    {:padding-bottom "0rem"}
                    :else
                    {:padding-bottom "0rem"})}
     [:div
      [header-scroll-sensor is-scrolled? -250]
      [esc-press-listener e! app]
      [:div (stylefy/use-style style-topnav/topnav-wrapper)
       [:div
        (stylefy/use-style (merge
                             style-topnav/topnav-desktop
                             (when @is-scrolled?
                               {:height "56px" :line-height "56px"})))
        [:div.container
         [top-nav-links e! app is-scrolled?]]]

       [top-nav-drop-down-menu e! app is-scrolled?]
       [user-menu e! app]
       [lang-menu e! app]
       [tos e! app desktop?]]]]))
