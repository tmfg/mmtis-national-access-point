(ns ote.views.main
  "OTE application view composer"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.core :refer [color]]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [ote.style.base :as style-base]
            [ote.ui.circular_progress :as spinner]
            [ote.ui.main-header :refer [header]]
            [ote.ui.common :refer [linkify]]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.transport-operator :as to]
            [ote.views.front-page :as fp]
            [ote.views.transport-service.transport-service :as t-service]
            [ote.views.transport-service.service-type :as service-type]
            [ote.views.footer :as footer]
            [ote.views.theme :refer [theme]]
            [ote.views.service-search :as service-search]
            [ote.views.own-services :as os]
            [ote.views.operator-users :as ou]
            [ote.views.service-viewer :as sv]
            [ote.views.confirm-email :as ce]
            [ote.views.resend-confirmation :as rc]
            [ote.views.login :as login]
            [ote.views.user :as user]
            [ote.views.admin.user-edit :as user-edit]
            [ote.views.admin.admin :as admin]
            [ote.views.admin.detected-changes :as admin-detected-changes]
            [ote.views.email-notification-settings :as email-settings]
            [ote.views.route.route-list :as route-list]
            [ote.views.route :as route]
            [ote.views.gtfs-viewer :as gtfs-viewer]
            [ote.views.pre-notices.pre-notice :as notice]
            [ote.views.pre-notices.listing :as pre-notices-listing]
            [ote.views.transit-visualization.transit-visualization :as transit-visualization]
            [ote.views.transit-visualization.transit-changes :as transit-changes]
            [ote.views.register :as register]
            [ote.views.admin.monitor :as monitor]
            [ote.views.error.error-landing :as error-landing]))

(defn document-title [page]
  (set! (.-title js/document)
        (case page
          :services (tr [:document-title :services])
          :own-services (tr [:document-title :own-services])
          :admin (tr [:document-title :admin])

          ;; default title for other pages
          (tr [:document-title :default])))

  ;; Render as an empty span
  [:span])


(defn- scroll-to-page
  "Invisible component that scrolls to the top of the window whenever it is mounted."
  [page]
  (r/create-class
   {:component-did-mount #(.scrollTo js/window 0 0)
    :reagent-render
    (fn [page]
      [:span])}))

(def wide-pages #{:transit-visualization :transit-changes :authority-pre-notices
                  :own-services :admin :services :edit-route :new-route :routes
                  :new-notice :edit-pre-notice :edit-service :new-service
                  :view-gtfs})

(defn ote-application
  "OTE application main view"
  [e! app]
  (fn [e! {loaded? :transport-operator-data-loaded? :as app}]
    (let [desktop? (> (:width app) style-base/mobile-width-px)
          wide? (boolean (wide-pages (:page app)))]
      [:div {:style (stylefy/use-style style-base/body)}
       [theme e! app
        (if (nil? app)
          [spinner/circular-progress]
          [:div.ote-sovellus {:style {:display "flex"
                                      :flex-direction "column"
                                      :height "100vh"}}
           [header e! app desktop?]
           [:div (merge (stylefy/use-style style-base/sticky-footer)
                   {:on-click #(e! (fp-controller/->CloseHeaderMenus))})
            (if (not loaded?)
              [spinner/circular-progress]
              [(if wide? :div :div.wrapper)
               (if wide?
                 {}
                 (stylefy/use-style {:transition "margin-top 300ms ease"}))
               [:div (cond
                       (= :front-page (:page app))
                       {:class "container-fluid"}

                       wide?
                       {}

                       :else
                       {:style {:padding-bottom "20px"}
                        :class "container"})
                [document-title (:page app)]

                ;; Ensure that a new scroll-to-page component is created when page changes
                ^{:key (name (:page app))}
                [scroll-to-page (:page app)]

                (case (:page app)
                  :login [login/login e! (:login app)]
                  :error-landing [error-landing/error-landing-vw app]
                  :reset-password [login/reset-password e! app]
                  :register [register/register e! (:params app) (:register app) (:user app)]
                  :user [user/user e! (:user app)]
                  :user-edit [user-edit/edit-user e! app]
                  :front-page [fp/front-page e! app]
                  :own-services [os/own-services e! app]
                  :transport-service [service-type/select-service-type e! app]
                  :transport-operator [to/operator e! app]
                  :operator-users [ou/manage-access e! app]
                  ;; Routes for the service form, one for editing an existing one by id
                  ;; and another when creating a new service
                  :edit-service [t-service/edit-service-by-id e! app]
                  :new-service [t-service/create-new-service e! app]

                  ;; service catalog page
                  :services [service-search/service-search e! app]
                  :service-view [sv/service-view e! app]

                  :admin [admin/admin-panel e! app]
                  :admin-detected-changes [admin-detected-changes/configure-detected-changes e! (assoc-in app [:admin :transit-changes :tab] "admin-detected-changes")]
                  :admin-route-id [admin-detected-changes/configure-detected-changes e! (assoc-in app [:admin :transit-changes :tab] "admin-route-id")]
                  :admin-upload-gtfs [admin-detected-changes/configure-detected-changes e! (assoc-in app [:admin :transit-changes :tab] "admin-upload-gtfs")]
                  :admin-commercial-services [admin-detected-changes/configure-detected-changes e! (assoc-in app [:admin :transit-changes :tab] "admin-commercial-services")]
                  :admin-exception-days [admin-detected-changes/configure-detected-changes e! (assoc-in app [:admin :transit-changes :tab] "admin-exception-days")]

                  :email-settings [email-settings/email-notification-settings e! app]
                  :confirm-email [ce/confirm-email e! app]
                  :resend-confirmation [rc/email-confirmation-form e! app]

                  :routes [route-list/routes e! app]
                  :new-route [route/new-route e! app]
                  :edit-route [route/edit-route-by-id e! app]

                  :monitor [monitor/monitor-main e! app]

                  ;; 60days pre notice views
                  :new-notice [notice/new-pre-notice e! app]
                  :edit-pre-notice [notice/edit-pre-notice-by-id e! app]
                  :pre-notices [pre-notices-listing/pre-notices e! app]

                  :view-gtfs [gtfs-viewer/gtfs-viewer e! app]
                  :transit-visualization [transit-visualization/transit-visualization e! app]

                  (:transit-changes :authority-pre-notices)
                  [transit-changes/transit-changes e! app]

                  [:div (tr [:common-texts :no-such-page]) (pr-str (:page app))])]])
            [footer/footer e!]]])]])))