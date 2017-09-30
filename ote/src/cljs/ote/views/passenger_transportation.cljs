(ns ote.views.passenger-transportation
  "Required datas for passenger transportation provider"
  (:require [ote.ui.form :as form]
            [ote.ui.napit :as napit]
            [ote.tiedot.palvelu :as service]
            [ote.ui.debug :as debug]
            [ote.domain.liikkumispalvelu :as t]
            [ote.localization :refer [tr tr-key]]))

(defn passenger-transportation-info [e! status]
  [:div.row
   [:div {:class "col-lg-4"}
    [:div
     [:h3 "Vaihe 2: Täydennä henkilökuljetukseen liittyvät tiedot."]]
    [form/form
     {:name->label (tr-key [:olennaiset-tiedot :otsikot])
      :update! #(e! (service/->EditTransportService %))
      :name #(tr [:olennaiset-tiedot :otsikot %])
      :footer-fn (fn [data]
                   [napit/tallenna {:on-click #(e! (service/->SavePassengerTransportData))
                                    :disabled (form/disable-save? data)}
                    "Tallenna"])}

     [{
       :name    ::t/luggage-restrictions
       :type  :localized-text
       :rows   5}
      
      {
       :name ::t/url
       :type :string
       :read (comp ::t/url ::t/real-time-information)
       :write (fn [data url]
                (assoc-in data [::t/real-time-information ::t/url] url))}
      {
       :name ::t/description
       :type  :localized-text
       :rows   3
       :read (comp ::t/description ::t/real-time-information)
       :write (fn [data desc]
                (assoc-in data [::t/real-time-information ::t/description] desc))}

      {
       :name ::t/url
       :type :string
       :read (comp ::t/url ::t/booking-service)
       :write (fn [data url]
                (assoc-in data [::t/booking-service ::t/url] url))}
      {
       :name ::t/description
       :type  :localized-text
       :rows   3
       :read (comp ::t/description ::t/booking-service)
       :write (fn [data desc]
                (assoc-in data [::t/booking-service ::t/description] desc))}
      ]

     status]

    [debug/debug status]]])
