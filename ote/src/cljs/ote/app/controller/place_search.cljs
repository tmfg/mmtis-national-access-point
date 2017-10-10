(ns ote.app.controller.place-search
  "Controller for searching places on a map.
  Uses the backend openstreetmap-places service."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.places :as places]))

(defrecord LoadPlaceNames [])
(defrecord LoadPlaceNamesResponse [names])
(defrecord SetPlaceName [name])
(defrecord AddPlace [name])
(defrecord AddPlaceResponse [places])
(defrecord RemovePlaceById [id])

(extend-protocol tuck/Event

  LoadPlaceNames
  (process-event [_ app]
    (comm/get! "place-names"
               {:on-success (tuck/send-async! ->LoadPlaceNamesResponse)})
    app)

  LoadPlaceNamesResponse
  (process-event [{names :names} app]
    (assoc-in app [:place-search :names] names))

  SetPlaceName
  (process-event [{name :name} app]
    (assoc-in app [:place-search :name] name))

  AddPlace
  (process-event [{name :name} app]
    (comm/get! (str "places/" name)
               {:on-success (tuck/send-async! ->AddPlaceResponse)})
    (update app :place-search
            #(assoc %
                    :name ""
                    :search-in-progress? true)))

  AddPlaceResponse
  (process-event [{places :places} app]
    (update app :place-search
            #(-> %
                 (assoc %
                        :search-in-progress? false
                        :name "")
                 (update :results
                         (fn [results]
                           (into (or results []) places))))))

  RemovePlaceById
  (process-event [{id :id} app]
    (update-in app [:place-search :results]
               (fn [results]
                 (filterv  (comp (partial not= id) ::places/id) results)))))
