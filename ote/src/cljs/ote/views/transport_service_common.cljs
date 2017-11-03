(ns ote.views.transport-service-common
  "View parts that are common to all transport service forms."
  (:require [tuck.core :as tuck]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.form :as form]
            [ote.db.common :as common]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.transport-service :as ts]
            [ote.views.place-search :as place-search]))

(defn service-url
  "Creates a form group for service url hat creates two form elements url and localized text area"
  [label service-url-field]
  (form/group
    {:label label
    :layout :row
    :columns 3}
    {:class "set-bottom"
    :name   ::t-service/url
    :type   :string
    :read   (comp ::t-service/url service-url-field)
    :write  (fn [data url]
             (assoc-in data [service-url-field ::t-service/url] url))}
    {:name ::t-service/description
    :type  :localized-text
    :rows  1 :rows-max 3
    :read  (comp ::t-service/description service-url-field)
    :write (fn [data desc]
             (assoc-in data [service-url-field ::t-service/description] desc))}))



(defn external-interfaces
  "Creates a form group for external services."
  []
  (form/group
   {:label  (tr [:field-labels :transport-service-common ::t-service/external-interfaces])
    :columns 3}

   (form/info (tr [:form-help :external-interfaces]))
   {:name ::t-service/external-interfaces
    :type :table
    :table-fields [{:name ::t-service/external-service-description :type :localized-text :width "40%"
                    :read (comp ::t-service/description ::t-service/external-interface)
                    :write #(assoc-in %1 [::t-service/external-interface ::t-service/description] %2)}
                   {:name ::t-service/external-service-url :type :string :width "40%"
                    :read (comp ::t-service/url ::t-service/external-interface)
                    :write #(assoc-in %1 [::t-service/external-interface ::t-service/url] %2)}
                   {:name ::t-service/format :type :string :width "20%"}]
    :delete? true
    :add-label (tr [:buttons :add-external-interface])}))

(defn contact-info-group []
  (form/group
   {:label  (tr [:passenger-transportation-page :header-contact-details])
    :columns 3
    :layout :row}
   {:name        ::common/street
    :type        :string
    :read (comp ::common/street ::t-service/contact-address)
    :write (fn [data street]
             (assoc-in data [::t-service/contact-address ::common/street] street))
    :label (tr [:field-labels ::common/street])
    :required? true}

   {:name        ::common/postal_code
    :type        :string
    :read (comp ::common/postal_code ::t-service/contact-address)
    :write (fn [data postal-code]
             (assoc-in data [::t-service/contact-address ::common/postal_code] postal-code))
    :label (tr [:field-labels ::common/postal_code])
    :required? true
    :validate [[:postal-code]]}

   {:name        ::common/post_office
    :type        :string
    :read (comp ::common/post_office ::t-service/contact-address)
    :write (fn [data post-office]
             (assoc-in data [::t-service/contact-address ::common/post_office] post-office))
    :label (tr [:field-labels ::common/post_office])
    :required? true}

   {:name        ::t-service/contact-phone
    :type        :string}

   {:name        ::t-service/contact-email
    :type        :string}

   {:name        ::t-service/homepage
    :type        :string}))

(defn footer [e! {published? ::t-service/published? :as data}]
  [:div.row
   (if published?
     [buttons/save {:on-click #(e! (ts/->SaveTransportService true))
                      :disabled (form/disable-save? data)}
      (tr [:buttons :save-updated])]
     [:span
      [buttons/save {:on-click #(e! (ts/->SaveTransportService true))
                       :disabled (form/disable-save? data)}
       (tr [:buttons :save-and-publish])]
      [buttons/save  {:on-click #(e! (ts/->SaveTransportService false))
                        :disabled  (form/disable-save? data)}
       (tr [:buttons :save-as-draft])]])
   [buttons/cancel {:on-click #(e! (ts/->CancelTransportServiceForm))}
    (tr [:buttons :discard])]])

(defn place-search-group [e! key]
  (place-search/place-search-form-group
   (tuck/wrap-path e! :transport-service key ::t-service/operation-area)
   (tr [:field-labels :transport-service-common ::t-service/operation-area])
   ::t-service/operation-area))
