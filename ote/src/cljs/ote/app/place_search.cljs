(ns ote.app.place-search
  "Controller for searching places on a map.
  Uses the backend openstreetmap-places service."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]))

(defrecord SetPlaceName [name])
(defrecord SearchPlaces [])
(defrecord SearchPlacesResponse [places])

(extend-protocol tuck/Event

  SetPlaceName
  (process-event [{name :name} app]
    (assoc-in app [:place-search :name] name))

  SearchPlaces
  (process-event [_ app]
    (comm/get! (str "/openstreetmap-places/" (get-in app [:place-search :name]))
               {:on-success (tuck/send-async! ->SearchPlacesResponse)})
    (update app :place-search
            #(assoc %
                    :search-in-progress? true
                    :results nil)))

  SearchPlacesResponse
  (process-event [{places :places} app]
    (update app :place-search
            #(assoc %
                    :search-in-progress? false
                    :results places))))
