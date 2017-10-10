(ns ote.app.controller.place-search
  "Controller for searching places on a map.
  Uses the backend openstreetmap-places service."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.places :as places]))

(defrecord LoadPlaces [])
(defrecord LoadPlacesResponse [places])
(defrecord SetPlaceName [name])
(defrecord AddPlace [name])
(defrecord AddPlaceResponse [geojson place])
(defrecord RemovePlaceById [id])


(extend-protocol tuck/Event

  LoadPlaces
  (process-event [_ app]
    (comm/get! "place-list"
               {:on-success (tuck/send-async! ->LoadPlacesResponse)})
    app)

  LoadPlacesResponse
  (process-event [{places :places} app]
    (update app :place-search assoc
            :place-names (into [] (map ::places/name) places)
            :places (into {}
                          (map (juxt ::places/name identity) places))))

  SetPlaceName
  (process-event [{name :name} app]
    (assoc-in app [:place-search :name] name))

  AddPlace
  (process-event [{name :name} app]
    (if (some #(= name (::places/name (:place %)))
              (get-in app [:place-search :results]))
      ;; This name has already been added, don't do it again
      app
      (if-let [place (get-in app [:place-search :places name])]
        (do
          (comm/get! (str "places/" name)
                     {:on-success (tuck/send-async! ->AddPlaceResponse place)})
          (update app :place-search
                  #(assoc %
                          :name ""
                          :search-in-progress? true)))
        app)))

  AddPlaceResponse
  (process-event [{:keys [place geojson]} app]
    (update app :place-search
            #(-> %
                 (assoc %
                        :search-in-progress? false
                        :name "")
                 (update :results
                         (fn [results]
                           (conj (or results [])
                                 {:place place :geojson geojson}))))))

  RemovePlaceById
  (process-event [{id :id} app]
    (update-in app [:place-search :results]
               (fn [results]
                 (filterv  (comp (partial not= id) ::places/id :place) results)))))

(defn place-references
  "Gets a place search app model and returns place references from it.
  Place references are sent to the server instead of sending the geometries."
  [app]
  (mapv :place (get-in app [:place-search :results])))
