(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :refer [linkify]]
            [ote.views.transport-operator :as to]
            [ote.views.front-page :as fp]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.transport-service :as t-service]
            [ote.views.passenger-transportation :as pt]
            [ote.views.terminal :as terminal]
            [ote.views.rental :as rental]
            [ote.views.parking :as parking]
            [ote.localization :refer [tr tr-key] :as localization]
            [ote.views.place-search :as place-search]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.topnav :as style-topnav]
            [ote.app.controller.transport-service :as ts]
            [ote.views.theme :refer [theme]]
            [ote.views.service-search :as service-search]
            [ote.ui.form :as form]
            [ote.app.controller.login :as login]
            [ote.app.controller.flags :as flags]
            [ote.ui.common :as common]
            [ote.ui.form-fields :as form-fields]
            [ote.views.admin :as admin]))

(defn logged-in? [app]
  (not-empty (get-in app [:user :username])))

(defn- is-topnav-active [give-page nav-page]
  (when (= give-page nav-page)
    "active"))

(defn- is-user-menu-active [app]
  (when (= true (get-in app [:ote-service-flags :user-menu-open]))
    "active"))

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
              :label [:common-texts :navigation-own-service-list]})

           (when (:admin? (:user app))
             {:page :admin
              :label [:common-texts :navigation-admin]})]))

(def selectable-languages [["fi" "suomi"]
                           ["sv" "svenska"]
                           #_["en" "english"]])

(defn- language-selection [e! style link-style show-label?]
  (let [current-language @localization/selected-language]
    [:div (stylefy/use-style style) (when show-label? (str (tr [:common-texts :language]) ": "))

     (doall
      (for [[lang flag] selectable-languages]
        [:a (merge
             (stylefy/use-style style-base/language-flag)
             {:key lang
              :href "#"
              :on-click #(e! (fp-controller/->SetLanguage lang))
              :style link-style})
         flag]))]))

(defn user-menu [e! name username]
  (when username
    [ui/drop-down-menu
    {:menu-style {}
     :underline-style {}
     :label-style {:color "#FFFFFF" :font-weight 700}
     :list-style {:background-color "#2D75B4"}
     :on-click #(e! (fp-controller/->OpenUserMenu))
     :anchor-origin {:horizontal "right" :vertical "bottom"}
     :target-origin {:horizontal "right" :vertical "top"}
     :selection-renderer (constantly name)}

     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :user-menu-summary])
                    :on-click #(do (.preventDefault %)
                                   (e! (fp-controller/->GoToUrl "/dashboard/datasets")))}]
     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :user-menu-profile])
                    :on-click #(do (.preventDefault %)
                                   (e! (fp-controller/->GoToUrl (str "/user/edit/" username))))}]
     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :navigation-give-feedback])
                    :on-click #(do (.preventDefault %)
                                   (e! (fp-controller/->OpenNewTab "http://bit.ly/nap-palaute")))}]
     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :user-menu-log-out])
                    :on-click #(do (.preventDefault %)
                                   (e! (fp-controller/->GoToUrl "/user/_logout")))} ]

     [ui/menu-item {:primary-text (r/as-element [language-selection e! style-base/language-selection-dropdown {:color "#fff"}])}]]))

(def own-services-pages #{:own-services :transport-service :new-service :edit-service :transport-operator :organizations})
(def services-pages #{:services})

(defn page-active?
"Return true if given current-page belongs to given page-group"
[page-group current-page]
  (cond
    (= page-group :own-services) (own-services-pages current-page)
    (= page-group :services) (services-pages current-page)
      :default false))

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
                 (if (page-active? page current-page)
                   (if desktop? style-topnav/desktop-active style-topnav/active)
                   (if desktop? style-topnav/desktop-link style-topnav/link)))
                {:href     "#"
                 :on-click #(do
                              (.preventDefault %)
                              (if url
                                (e! (fp-controller/->GoToUrl url))
                                (e! (fp-controller/->ChangePage page nil))))})
         (tr label)]]))
    [:div.user-menu {:class (is-user-menu-active app)
                     :style (when (> (:width app) style-base/mobile-width-px)
                              {:float "right"})}
     [user-menu e!
      (get-in app [:user :name])
      (get-in app [:user :username])]]

    (if (nil? (get-in app [:user :username]))
      [:ul (stylefy/use-style style-topnav/ul)
       [:li
        [linkify "/user/register" (tr [:common-texts :navigation-register])
         (merge (stylefy/use-style
                  (if desktop? style-topnav/desktop-link style-topnav/link))
                {:style {:float "right"}})]]
       [:li
        (if (flags/enabled? :ote-login)
          [linkify "#" (tr [:common-texts :navigation-login])
           (merge (stylefy/use-style
                   (if desktop? style-topnav/desktop-link style-topnav/link))
                  {:style {:float "right"}}
                  {:on-click #(do
                                (.preventDefault %)
                                (e! (login/->ShowLoginDialog)))})]
          [linkify "/user/login" (tr [:common-texts :navigation-login])
           (merge (stylefy/use-style
                   (if desktop? style-topnav/desktop-link style-topnav/link))
                  {:style {:float "right"}})])]])

    [:li (if desktop? nil (stylefy/use-style style-topnav/mobile-li))
     [linkify "https://s3.eu-central-1.amazonaws.com/ote-assets/nap-ohje.pdf" (tr [:common-texts :user-menu-nap-help])
      (merge (stylefy/use-style
               (if desktop? style-topnav/desktop-link style-topnav/link))
             {:target "_blank"
              :style (when (> (:width app) style-base/mobile-width-px)
                       {:float "right"})})]]]])

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


(defn- footer [e!]
  [:footer.site-footer
   [:div.container-fluid
    [:div.row
     [:div.col-md-2.footer-links
      [:a.logo {:href "#" }
       [:img {:src "/livi_logo_valkoinen.svg" :alt (tr [:common-texts :footer-livi-logo]) }]]]
     [:div.col-md-2.footer-links
      [:ul.unstyled
       [:li
        [linkify "https://www.liikennevirasto.fi/nap" (tr [:common-texts :footer-livi-url]) {:target "_blank"}]]
       [:li
        [linkify "https://s3.eu-central-1.amazonaws.com/ote-assets/nap-ohje.pdf" (tr [:common-texts :user-menu-nap-help]) {:target "_blank"}]]
       [:li
        [linkify "http://bit.ly/nap-palaute" (tr [:common-texts :navigation-give-feedback]) {:target "_blank"}]]
       [:li
        [language-selection e! style-base/language-selection-footer nil true]]]]]]])



(def grey-background-pages #{:edit-service :services :transport-operator :own-services :new-service})

(defn login-form [e! {:keys [credentials failed? error in-progress?] :as login}]
  [:div
   (when failed?
     [:div (stylefy/use-style style-base/error-element)
      (tr [:login :error error])])
   [form/form {:name->label (tr-key [:field-labels :login])
               :update! #(e! (login/->UpdateLoginCredentials %))
               :footer-fn (fn [data]
                            [ui/raised-button {:primary true
                                               :on-click #(e! (login/->Login))
                                               :label (tr [:login :login-button])}])}
    [(form/group
      {:label (tr [:login :label]) :expandable? false
       :columns 3}
      {:name :email
       :type :string}
      {:name :password
       :type :string :password? true})]
    credentials]])

(defn login-action-cards []
  [:div {:style {:flex-basis 0 :flex-grow 1}}
   [:div (stylefy/use-style (merge (style-base/flex-container "column")
                                   {:margin-left "1em"
                                    :padding-left "1em"}))
    [ui/card
     [ui/card-header {:title (tr [:login :no-account?])}]
     [ui/card-text
      [:div
       [:div (tr [:login :no-account-help])]
       [linkify "/user/register" (tr [:login :no-account-button])]]]]

    [ui/card
     [ui/card-header {:title (tr [:login :forgot-password?])}]
     [ui/card-text
      [:div
       [:div (tr [:login :forgot-password-help])]
       [linkify "/user/reset" (tr [:login :forgot-password-button])]]]]]])

(defn mobile-login-form [e! {:keys [credentials failed? error] :as login}]
  [:span
   [ui/card
    [ui/card-title {:title (tr [:login :label])}]
    [ui/card-text
     [:div (stylefy/use-style (merge (style-base/flex-container "column")
                                     {:justify-content "center"}))
      (when failed?
        [:div (stylefy/use-style style-base/error-element)
         (tr [:login :error error])])
      [form-fields/field {:label (tr [:field-labels :login :email])
                          :type :string :name :email
                          :style {:width "95%" :align-self "center"}
                          :update! #(e! (login/->UpdateLoginCredentials {:email %}))}
       (:email credentials)]
      [form-fields/field {:label (tr [:field-labels :login :password])
                          :type :string :name :password
                          :password? true
                          :style {:width "95%" :align-self "center"}
                          :update! #(e! (login/->UpdateLoginCredentials {:password %}))}
       (:password credentials)]
      [:div (stylefy/use-style (style-base/flex-container "row"))
       [ui/raised-button {:style {:flex-basis 0 :flex-grow 1}
                          :primary true
                          :on-click #(e! (login/->Login))
                          :label (tr [:login :login-button])}]
       [ui/raised-button {:style {:flex-basis 0 :flex-grow 1}
                          :secondary true
                          :on-click #(e! (login/->LoginCancel))
                          :label (tr [:buttons :cancel])}]]]]]
   [login-action-cards]])

(defn login-dialog [e! {:keys [credentials failed? error in-progress?] :as login}]
  [ui/dialog {:open true
              :on-request-close #(e! (login/->LoginCancel))}
   [:div (stylefy/use-style (merge (style-base/flex-container "row")))
    [:div {:style {:flex-basis 0 :flex-grow 1}}
     [login-form e! login]]
    [login-action-cards]]])

(defn document-title [page]
  (set! (.-title js/document)
        (case page
          :services (tr [:document-title :services])
          :operators (tr [:document-title :operators])
          :own-services (tr [:document-title :own-services])
          :admin (tr [:document-title :admin])

          ;; default title for other pages
          (tr [:document-title :default])))

  ;; Render as an empty span
  [:span])

(defn ote-application
  "OTE application main view"
  [e! app]

  ;; init - Get operator and service data from DB when refresh or on usage start
  (e! (fp-controller/->GetTransportOperatorData))

  (fn [e! {loaded? :transport-operator-data-loaded?
           login :login
           :as app}]
    [:div {:style (stylefy/use-style style-base/body)}
     [theme e! app
      [:div.ote-sovellus
       [top-nav e! app]

       (if (and (:show? login) common/mobile?)
         [mobile-login-form e! login]
         [:span
          (when (:show? login)
            [login-dialog e! login])

          (if (not loaded?)
            [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
            [:div.wrapper (when (grey-background-pages (:page app)) {:class "grey-wrapper"})
             [:div.container-fluid
              [document-title (:page app)]
              (case (:page app)
                :front-page [fp/own-services e! app]
                :own-services [fp/own-services e! app]
                :transport-service [t-service/select-service-type e! app]
                :transport-operator [to/operator e! app]

                ;; Routes for the service form, one for editing an existing one by id
                ;; and another when creating a new service
                :edit-service [t-service/edit-service-by-id e! app]
                :new-service [t-service/edit-new-service e! app]

                :services [service-search/service-search e! app (:service-search app)]

                :admin [admin/admin-panel e! app]
                [:div (tr [:common-texts :no-such-page]) (pr-str (:page app))])]])])

       [footer e!]]]]))
