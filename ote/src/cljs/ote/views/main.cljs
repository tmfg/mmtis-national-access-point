(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :refer [linkify ckan-iframe-dialog]]
            [ote.views.transport-operator :as to]
            [ote.views.front-page :as fp]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.transport-service :as t-service]
            [ote.views.footer :as footer]
            [ote.localization :refer [tr tr-key] :as localization]
            [ote.views.place-search :as place-search]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.topnav :as style-topnav]
            [ote.views.theme :refer [theme]]
            [ote.views.service-search :as service-search]
            [ote.ui.form :as form]
            [ote.app.controller.login :as login]
            [ote.app.controller.flags :as flags]
            [ote.ui.common :as common]
            [ote.ui.form-fields :as form-fields]
            [ote.views.admin :as admin]
            [ote.views.operators :as operators]
            [ote.views.route.route-list :as route-list]
            [ote.views.route :as route]
            [ote.views.gtfs-viewer :as gtfs-viewer]
            [ote.views.pre-notices.pre-notice :as notice]
            [ote.views.pre-notices.listing :as pre-notices-listing]
            [ote.views.pre-notices.authority-listing :as pre-notices-authority-listing]
            [ote.views.transit-visualization :as transit-visualization]
            [ote.views.transit-changes :as transit-changes]
            [ote.views.register :as register]
            [ote.ui.common :as common-ui]
            [ote.ui.main-header :refer [top-nav] :as main-header]))

(def pages {:own-services #{:own-services :transport-service :new-service :edit-service :transport-operator
                            :organizations}
            :services #{:services}
            :operators #{:operators}})

(defn login-form [e! {:keys [credentials failed? error in-progress?] :as login}]
  [:div.login-form
   (when failed?
     [:div (stylefy/use-style style-base/error-element)
      (tr [:login :error error])])
   [form/form {:name->label (tr-key [:field-labels :login])
               :update! #(e! (login/->UpdateLoginCredentials %))
               :footer-fn (fn [data]
                            [:span.login-dialog-footer
                             [ui/raised-button {:primary true
                                                :on-click #(e! (login/->Login))
                                                :label (tr [:login :login-button])}]])}
    [(form/group
      {:label (tr [:login :label]) :expandable? false
       :columns 3}
      {:name :email
       :label (tr [:field-labels :login :email-or-username])
       :type :string
       :autocomplete "email"
       :on-enter #(e! (login/->Login))}
      {:name :password
       :autocomplete "passoword"
       :type :string
       :password? true
       :on-enter #(e! (login/->Login))})]
    credentials]])

(defn login-action-cards [e!]
  [:div {:style {:flex-basis 0 :flex-grow 1}}
   [:div (stylefy/use-style (merge (style-base/flex-container "column")
                                   {:margin-left "1em"
                                    :padding-left "1em"}))
    [ui/card
     [ui/card-header {:title (tr [:login :no-account?])}]
     [ui/card-text
      [:div
       [:div (tr [:login :no-account-help])]
       [ui/flat-button {:on-click #(e! (fp-controller/->ToggleRegistrationDialog))
                        :label (tr [:login :no-account-button])
                        :primary true}]]]]

    [ui/card
     [ui/card-header {:title (tr [:login :forgot-password?])}]
     [ui/card-text
      [:div
       [:div (tr [:login :forgot-password-help])]
       [ui/flat-button {:on-click #(e! (fp-controller/->ToggleUserResetDialog))
                        :label (tr [:login :forgot-password-button])
                        :primary true}]]]]]])

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
      [form-fields/field {:label (tr [:field-labels :login :email-or-username])
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
   [login-action-cards e!]])

(defn login-dialog [e! {:keys [credentials failed? error in-progress?] :as login}]
  [ui/dialog {:open true
              :on-request-close #(e! (login/->LoginCancel))}
   [:div (stylefy/use-style (merge (style-base/flex-container "row")))
    [:div {:style {:flex-basis 0 :flex-grow 1}}
     [login-form e! login]]
    [login-action-cards e!]]])

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

(defn ckan-dialogs [e! {:keys [login show-register-dialog? show-reset-dialog?
                               show-user-edit-dialog? user]}]
  [:span
   (when (:show? login)
     ^{:key "login"}
     [login-dialog e! login])

   (when show-register-dialog?
     ^{:key "ckan-register"}
     [ckan-iframe-dialog
      (tr [:common-texts :navigation-register])
      "/user/register"
      ;; On modal close
      #(e! (fp-controller/->ToggleRegistrationDialog))
      ;; On ckan close i.e. user has been registered.
      #(e! (fp-controller/->ToggleRegistrationDialog))])

   (when show-reset-dialog?
            ^{:key "ckan-reset"}
     [ckan-iframe-dialog
      (tr [:login :forgot-password?])
      "/user/reset"
      #(e! (fp-controller/->ToggleUserResetDialog))
      #(e! (fp-controller/->UserResetRequested))])

   (when show-user-edit-dialog?
     ^{:key "ckan-user-edit"}
     [ckan-iframe-dialog
      (tr [:common-texts :user-menu-profile])
      (str "/user/edit/" (:username user))
      #(e! (fp-controller/->ToggleUserEditDialog))])])

(defn- scroll-to-page
  "Invisible component that scrolls to the top of the window whenever it is mounted."
  [page]
  (r/create-class
   {:component-did-mount #(.scrollTo js/window 0 0)
    :reagent-render
    (fn [page]
      [:span])}))

(defn ote-application
  "OTE application main view"
  [e! app]
  (let [is-scrolled? (r/atom false)]
    (fn [e! {loaded? :transport-operator-data-loaded?
             login :login
             :as app}]
      (let [desktop? (> (:width app) style-base/mobile-width-px)]
        [:div {:style (stylefy/use-style style-base/body)}
         [theme e! app
          [:div.ote-sovellus
           [top-nav e! app is-scrolled? desktop?]

           (if (and (:show? login) common/mobile?)
             [mobile-login-form e! login]
             [:span
              [ckan-dialogs e! app]
              (if (not loaded?)
                [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
                [:div.wrapper
                 (stylefy/use-style (merge
                                      {:transition "margin-top 300ms ease"}
                                      (if (or (not desktop?) @is-scrolled?)
                                        {:margin-top "56px"})))
                 [:div (if (= :front-page (:page app))
                         {:class "container-fluid"}
                         {:style {:padding-bottom "20px"}
                          :class "container"})
                  [document-title (:page app)]

                  ;; Ensure that a new scroll-to-page component is created when page changes
                  ^{:key (name (:page app))}
                  [scroll-to-page (:page app)]

                  (case (:page app)
                    :register [register/register e! (:register app) (:user app)]
                    :front-page [fp/front-page e! app]
                    :own-services [fp/own-services e! app]
                    :transport-service [t-service/select-service-type e! app]
                    :transport-operator [to/operator e! app]

                    ;; Routes for the service form, one for editing an existing one by id
                    ;; and another when creating a new service
                    :edit-service [t-service/edit-service-by-id e! app]
                    :new-service [t-service/create-new-service e! app]

                    :services [service-search/service-search e! app]

                    :admin [admin/admin-panel e! app]

                    :operators [operators/operators e! app]

                    :routes [route-list/routes e! app]
                    :new-route [route/new-route e! app]
                    :edit-route [route/edit-route-by-id e! app]

                    ;; 60days pre notice views
                    :new-notice [notice/new-pre-notice e! app]
                    :edit-pre-notice [notice/edit-pre-notice-by-id e! app]
                    :pre-notices [pre-notices-listing/pre-notices e! app]

                    :view-gtfs [gtfs-viewer/gtfs-viewer e! app]
                    :transit-visualization [transit-visualization/transit-visualization e! (:transit-visualization app)]

                    (:transit-changes :authority-pre-notices)
                    [transit-changes/transit-changes e! app]

                    [:div (tr [:common-texts :no-such-page]) (pr-str (:page app))])]])])
           [footer/footer e!]]]]))))
