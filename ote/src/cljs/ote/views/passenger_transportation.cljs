(ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as service]
            [ote.ui.debug :as debug]
            [ote.db.transport-service :as transport-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]))

(defn passenger-transportation-info [e! status]
  [:div.row
   [:div {:class "col-lg-4"}
    [:div
     [:h3 "Vaihe 2: Täydennä henkilökuljetukseen liittyvät tiedot."]]
    [form/form
     {:name->label (tr-key [:field-labels])
      :update! #(e! (service/->EditTransportService %))
      :name #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! (service/->SavePassengerTransportData))
                                    :disabled (form/disable-save? data)}
                    "Tallenna"])}

     [{
       :name    ::transport-service/luggage-restrictions
       :type  :localized-text
       :rows   5}


     #_ {
       :label "Alue"
       :name ::transport-service/description
       :type  :localized-text
       :rows   3
       :read (comp ::transport-service/description ::transport-service/main-operation-area)
       :write (fn [data desc]
                (assoc-in data [::ts-definitionsmain-operation-area ::ts-definitionsdescription] desc))}

      #_ {
       :label "Lokaatio"
       :name ::transport-service/location
       :type  :string
       :read (comp ::transport-service/location ::ts-definitionsmain-operation-area)
       :write (fn [data location]
                (assoc-in data [::ts-definitionsmain-operation-area ::ts-definitionslocation] location))}


      (form/group "Real time information"
      {
       :name ::transport-service/url
       :type :string
       :read (comp ::transport-service/url ::transport-service/real-time-information)
       :write (fn [data url]
                (assoc-in data [::transport-service/real-time-information ::transport-service/url] url))}
      {
       :name ::transport-service/description
       :type  :localized-text
       :rows   3
       :read (comp ::transport-service/description ::transport-service/real-time-information)
       :write (fn [data desc]
                (assoc-in data [::transport-service/real-time-information ::transport-service/description] desc))})

      (form/group "Varauspalvelun tiedot"
      {
       :name ::transport-service/url
       :type :string
       :read (comp ::transport-service/url ::transport-service/booking-service)
       :write (fn [data url]
                (assoc-in data [::transport-service/booking-service ::transport-service/url] url))}
      {
       :name ::transport-service/description
       :type  :localized-text
       :rows   3
       :read (comp ::transport-service/description ::transport-service/booking-service)
       :write (fn [data desc]
                (assoc-in data [::transport-service/booking-service ::transport-service/description] desc))})
      ]

     status]

    [debug/debug status]]])
