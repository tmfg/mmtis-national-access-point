(ns ote.app.controller.place-search
  "Controller for searching places on a map.
  Uses the backend openstreetmap-places service."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.places :as places]
            [clojure.string :as str]))

(defrecord SetPlaceName [name])
(defrecord AddPlace [id])
(defrecord FetchPlaceResponse [response place])
(defrecord RemovePlaceById [id])
(defrecord PlaceCompletionsResponse [completions name])

(extend-protocol tuck/Event

  SetPlaceName
  (process-event [{name :name} app]
    (let [app (assoc-in app [:place-search :name] name)]
      (when (>= (count name) 2)
        (comm/get! (str "place-completions/" name)
                   {:on-success (tuck/send-async! ->PlaceCompletionsResponse name)}))
      app))

  PlaceCompletionsResponse
  (process-event [{:keys [completions name]} app]
    (if-not (= name (get-in app [:place-search :name]))
      ;; Received stale completions (name is not what was searched for), ignore
      app
      (assoc-in app [:place-search :completions]
                (let [name-lower (str/lower-case name)]
                  (sort-by #(str/index-of (str/lower-case (::places/namefin %))
                                          name-lower)
                           completions)))))

  AddPlace
  (process-event [{id :id} app]
    (.log js/console "ADD PLACE:" id)
    (if (some #(= id (::places/id (:place %)))
              (get-in app [:place-search :results]))
      ;; This name has already been added, don't do it again
      app
      (if-let [place (some #(when (= id (::places/id %)) %)
                           (get-in app [:place-search :completions]))]
        (do
          (comm/get! (str "place/" id)
                     {:on-success (tuck/send-async! ->FetchPlaceResponse place)})
          (-> app
              (assoc-in [:place-search :name] "")
              (assoc-in [:place-search :completions] nil)))
        app)))

  FetchPlaceResponse
  (process-event [{:keys [response place]} app]
    (update-in app [:place-search :results]
               #(conj (or % [])
                      {:place place
                       :geojson response})))
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
