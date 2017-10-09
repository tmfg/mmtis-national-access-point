(ns ote.app.place-search
  "Controller for searching places on a map.
  Uses the backend openstreetmap-places service."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.places :as places]))

(defrecord SetPlaceName [name])
(defrecord SearchPlaces [])
(defrecord SearchPlacesResponse [places])
(defrecord RemovePlaceById [id])

(extend-protocol tuck/Event

  SetPlaceName
  (process-event [{name :name} app]
    (assoc-in app [:place-search :name] name))

  SearchPlaces
  (process-event [_ app]
    (comm/get! (str "places/" (get-in app [:place-search :name]))
               {:on-success (tuck/send-async! ->SearchPlacesResponse)})
    (update app :place-search
            #(assoc %
                    :search-in-progress? true)))

  SearchPlacesResponse
  (process-event [{places :places} app]
    (update app :place-search
            #(-> %
                 (assoc %
                        :search-in-progress? false
                        :name "")
                 (update :results concat places))))

  RemovePlaceById
  (process-event [{id :id} app]
    (update-in app [:place-search :results]
               (fn [results]
                 (filterv  (comp (partial not= id) ::places/id) results)))))
