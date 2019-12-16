(ns ote.views.terminal
  "Required datas for port, station and terminal service"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [ote.style.base :as style-base]
            [ote.app.controller.transport-service :as ts]
            [ote.views.transport-service-common :as ts-common]
            [ote.style.form :as style-form]
            [ote.ui.validation :as validation]))

(defn terminal-form-options [e! schemas in-validation? app]
  {:name->label (tr-key [:field-labels :terminal]
                        [:field-labels :transport-service-common]
                        [:field-labels :transport-service])
   :update!     #(e! (ts/->EditTransportService %))
   :footer-fn   (fn [data]
                  [ts-common/footer e! data schemas in-validation? app])})

(defn- indoor-map-group [in-validation?]
  (ts-common/service-url "indoor-map-url"
                         (tr [:field-labels :terminal ::t-service/indoor-map])
                         ::t-service/indoor-map
                         nil
                         in-validation?))

(defn- assistance-service-group [in-validation?]
  (form/group
    {:label (tr [:terminal-page :header-assistance])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:name ::t-service/assistance-description
     :type :localized-text
     :disabled? in-validation?
     :full-width      true
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :rows            1
     :full-width?     true
     :write           #(assoc-in %1 [::t-service/assistance ::t-service/description] %2)
     :read            (comp ::t-service/description ::t-service/assistance)}

    {:name ::t-service/assistance-place-description
     :type :localized-text
     :disabled? in-validation?
     :full-width true
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :rows 1
     :full-width? true
     :write #(assoc-in %1 [::t-service/assistance ::t-service/assistance-place-description] %2)
     :read (comp ::t-service/assistance-place-description ::t-service/assistance)}

    {:name ::t-service/assistance-by-reservation
     :type :checkbox
     :disabled? in-validation?
     :style style-form/padding-top
     :container-class "col-xs-12"
     :write #(assoc-in %1 [::t-service/assistance ::t-service/assistance-by-reservation-only] %2)
     :read (comp ::t-service/assistance-by-reservation-only ::t-service/assistance)
     }

    {:name ::t-service/hours-before
     :type :number ;; FIXME: When :interval type is ready, change to interval
     :disabled? in-validation?
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements  ::t-service/hours-before] %2)
     :read (comp ::t-service/hours-before  ::t-service/notification-requirements ::t-service/assistance)
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-3"}
    {:name ::t-service/telephone
     :type :string
     :disabled? in-validation?
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements ::t-service/telephone] %2)
     :read (comp ::t-service/telephone ::t-service/notification-requirements ::t-service/assistance)
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-3"}
    {:name ::t-service/email
     :type :string
     :disabled? in-validation?
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements ::t-service/email] %2)
     :read (comp ::t-service/email ::t-service/notification-requirements ::t-service/assistance)
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-3"
     :max-length 200}
    {:name ::t-service/assistance-url
     :type :string
     :disabled? in-validation?
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements ::t-service/url] %2)
     :read (comp ::t-service/url ::t-service/notification-requirements ::t-service/assistance)
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-3"}))


(defn- accessibility-and-other-services-group [in-validation?]
  (form/group
    {:label (tr [:terminal-page :header-accessibility])
     :columns 3
     :layout :row
     :card? false
     :top-border true}

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name ::t-service/accessibility-description
     :type :localized-text
     :disabled? in-validation?
     :full-width? true
     :rows 1}

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name ::t-service/accessibility-info-url
     :type :string
     :disabled? in-validation?
     :full-width? true
     :max-length 200}

    {:container-class "col-xs-12 col-sm-6 col-md-6"
     :name ::t-service/accessibility
     :label (tr [:terminal-page :header-checkboxlist-accessibility])
     :type :checkbox-group
     :disabled? in-validation?
     :full-width? true
     :show-option (tr-key [:enums ::t-service/accessibility])
     :options t-service/accessibility}

    {:name ::t-service/information-service-accessibility
     :type :checkbox-group
     :disabled? in-validation?
     :full-width? true
     :container-class "col-xs-12 col-sm-6 col-md-6"
     :show-option (tr-key [:enums ::t-service/information-service-accessibility])
     :options t-service/information-service-accessibility
     :container-style {:align-self "baseline"}}))

(defn terminal [e! {form-data ::t-service/terminal  :as service} app]
  (let [in-validation? (::t-service/validate form-data)
               groups [(ts-common/transport-type ::t-service/terminal in-validation?)
                       (ts-common/name-group (tr [:terminal-page :header-service-info]) in-validation?)
                       (ts-common/contact-info-group in-validation?)
                       (ts-common/place-search-group (ts-common/place-search-dirty-event e!) ::t-service/terminal in-validation?)
                       (ts-common/external-interfaces e!
                                                      (get service ::t-service/type)
                                                      (get service ::t-service/sub-type)
                                                      (get-in service [::t-service/passenger-transportation ::t-service/transport-type])
                                                      in-validation?)
                       (ts-common/service-hours-group "terminal" false in-validation?)
                       (indoor-map-group in-validation?)
                       (assistance-service-group in-validation?)
                       (accessibility-and-other-services-group in-validation?)]
               options (terminal-form-options e! groups in-validation? app)]
    [:div.row
      [form/form options groups form-data]]))
