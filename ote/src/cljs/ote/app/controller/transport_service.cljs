(ns ote.app.controller.transport-service
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.db.transport-service :as transport-service]
            [ote.ui.form :as form]
            [ote.app.controller.passenger-transportation :as pt]))

(defrecord AddPriceClassRow [])
(defrecord AddServiceHourRow [])
(defrecord RemovePriceClassRow [])
(defrecord SelectTransportServiceType [data])



(extend-protocol t/Event

  AddPriceClassRow
  (process-event [_ app]
    (update-in app [:transport-service ::transport-service/passenger-transportation ::transport-service/price-classes]
               #(conj (or % []) {::transport-service/currency "EUR"})))

  AddServiceHourRow
  (process-event [_ app]
    (update-in app [:transport-service ::transport-service/passenger-transportation ::transport-service/service-hours]
               #(conj (or % []) {::transport-service/from "08:00"})))             

  RemovePriceClassRow
  (process-event [_ app]
    (assoc-in app [:transport-service :price-class-open] false))

  SelectTransportServiceType
  (process-event [{data :data} app]
    (assoc app :page (get data ::transport-service/service-type)))

)