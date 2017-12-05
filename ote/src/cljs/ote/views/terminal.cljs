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
            [ote.style.form :as style-form]))

(defn terminal-form-options [e!]
  {:name->label (tr-key [:field-labels :terminal] [:field-labels :transport-service-common])
   :update!     #(e! (ts/->EditTransportService %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [ts-common/footer e! data])})

(defn name-group [e!]
  (form/group
    {:label (tr [:terminal-page :header-service-info])
     :columns 3
     :layout :row}

    {:name ::t-service/name
     :container-class "col-md-6"
     :full-width? true
     :type :string
     :required? true}))

(defn- indoor-map-group []
  (ts-common/service-url
   (tr [:field-labels :terminal ::t-service/indoor-map])
   ::t-service/indoor-map))

(defn- assistance-service-group []
  (form/group
    {:label (tr [:terminal-page :header-assistance])
     :columns 3
     :layout :row}


    {:name ::t-service/assistance-description
     :type :localized-text
     :full-width true
     :container-class "col-md-12"
     :rows 5
     :max-rows 10
     :full-width? true
     :write #(assoc-in %1 [::t-service/assistance ::t-service/description] %2)
     :read (comp ::t-service/description ::t-service/assistance)}

    {:name ::t-service/hours-before
     :type :number ;; FIXME: When :interval type is ready, change to interval
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements  ::t-service/hours-before] %2)
     :read (comp ::t-service/hours-before  ::t-service/notification-requirements ::t-service/assistance)}
    {:name ::t-service/telephone
     :type :string
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements ::t-service/telephone] %2)
     :read (comp ::t-service/telephone ::t-service/notification-requirements ::t-service/assistance)}
    {:name ::t-service/email
     :type :string
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements ::t-service/email] %2)
     :read (comp ::t-service/email ::t-service/notification-requirements ::t-service/assistance)}
    {:name ::t-service/assistance-url
     :type :string
     :write #(assoc-in %1 [::t-service/assistance ::t-service/notification-requirements ::t-service/url] %2)
     :read (comp ::t-service/url ::t-service/notification-requirements ::t-service/assistance)}

    ))


(defn- accessibility-and-other-services-group []
  (form/group
   {:label (tr [:terminal-page :header-accessibility])
    :columns 3
    :layout :row}

   {:container-class "col-md-6"
    :name ::t-service/accessibility-description
    :type :localized-text
    :full-width? true
    :rows 5 :max-rows 10}

   {:container-class "col-md-5"
    :name ::t-service/accessibility-info-url
    :type :string
    :full-width? true}

   {:container-class "col-md-6"
    :name        ::t-service/accessibility
    :label       (tr [:terminal-page :header-checkboxlist-accessibility])
    :type        :checkbox-group
    :full-width? true
    :show-option (tr-key [:enums ::t-service/accessibility])
    :options     t-service/accessibility}

   {:name        ::t-service/information-service-accessibility
    :type        :checkbox-group
    :full-width? true
    :container-class "col-md-5"
    :show-option (tr-key [:enums ::t-service/information-service-accessibility])
    :options     t-service/information-service-accessibility}





   ))

(defn terminal [e! {form-data ::t-service/terminal :as state}]
  (r/with-let [options (terminal-form-options e!)
               groups [(name-group e!)
                       (ts-common/contact-info-group)
                       (ts-common/place-search-group e! ::t-service/terminal)
                       (ts-common/external-interfaces)
                       (indoor-map-group)
                       (assistance-service-group)
                       (accessibility-and-other-services-group)]]
    [:div.row
     [:div {:class "col-lg-12"}
      [:div
       (if (nil? (get state ::t-service/id))
         [:h1 (tr [:terminal-page :header-add-new-terminal])]
         [:h1 (tr [:terminal-page :header-edit-terminal])])]
      [form/form options groups form-data]]]))
