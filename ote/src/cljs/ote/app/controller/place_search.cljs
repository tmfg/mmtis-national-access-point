(ns ote.app.controller.place-search
  "Controller for searching places on a map.
  Uses the backend openstreetmap-places service."
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.places :as places]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [ote.db.transport-service :as t-service]))

(defrecord SetPrimaryPlaceName [name])
(defrecord SetSecondaryPlaceName [name])
(defrecord SetDrawControl [show? primary?])
(defrecord AddPlace [id primary?])
(defrecord FetchPlaceResponse [response place])
(defrecord RemovePlaceById [id])
(defrecord PlaceCompletionsResponse [completions name primary?])

(defrecord SetMarker [event])

;; Events for adding and editing hand drawn geometries
(defrecord AddDrawnGeometry [geojson])
(defrecord EditDrawnGeometryName [id])
(defrecord SetDrawnGeometryName [id name])

(defn- add-place
  "Return app with a new place added."
  [app place geojson]
  (update-in app [:place-search :results]
             #(conj (or % [])
                    {:place place
                     :geojson geojson})))

(defn- update-place-by-id [app id update-fn & args]
  (update-in app [:place-search :results]
             (fn [results]
               (mapv #(if (= (get-in % [:place ::places/id]) id)
                        (update % :place
                                (fn [p]
                                  (apply update-fn p args)))
                        %)
                     results))))

(defn- search [{place-search :place-search :as app} primary?]
  (when-let [timeout (:timeout place-search)]
    (.clearTimeout js/window timeout))
  (let [name (if primary?
               (:primary-name place-search)
               (:secondary-name place-search))
        on-success (tuck/send-async! ->PlaceCompletionsResponse name primary?)]
    (if (>= (count name) 2)
      (assoc-in
       app [:place-search :timeout]
       (.setTimeout js/window
                    #(comm/get! (str "place-completions/" name)
                                {:on-success on-success})
                    500))
      app)))

(extend-protocol tuck/Event

  SetPrimaryPlaceName
  (process-event [{name :name} app]
    (search (assoc-in app [:place-search :primary-name] name) true))

  SetSecondaryPlaceName
  (process-event [{name :name} app]
    (search (assoc-in app [:place-search :secondary-name] name) false))

  SetDrawControl
  (process-event [{show? :show? primary? :primary?} app]
    (if (nil? primary?)
      (-> app
          (assoc-in [:place-search :show?] show?)
          (update-in [:place-search] dissoc :primary?))
      (-> app
          (assoc-in [:place-search :primary?] primary?)
          (assoc-in [:place-search :show?] show?))))

  PlaceCompletionsResponse
  (process-event [{:keys [completions name primary?]} app ]
    (if-not (= name (get-in app [:place-search (if primary?
                                                 :primary-name
                                                 :secondary-name)]))
      ;; Received stale completions (name is not what was searched for), ignore
      app
      (assoc-in app [:place-search :completions] completions)))

  AddPlace
  (process-event [{:keys [id primary?]} app]
    (if (some #(= id (::places/id (:place %)))
              (get-in app [:place-search :results]))
      ;; This name has already been added, don't do it again
      app
      (if-let [place (some #(when (= id (::places/id %)) %)
                           (get-in app [:place-search :completions]))]
        (do
          (comm/get! (str "place/" id)
                     {:on-success (tuck/send-async! ->FetchPlaceResponse (assoc place ::places/primary? primary?))})
          (-> app
              (assoc-in [:place-search (if primary?
                                                 :primary-name
                                                 :secondary-name)] "")
              (assoc-in [:place-search :completions] nil)))
        app)))

  FetchPlaceResponse
  (process-event [{:keys [response place]} app]
    (add-place app place response))

  RemovePlaceById
  (process-event [{id :id} app]
    (update-in app [:place-search :results]
               (fn [results]
                 (filterv  (comp (partial not= id) ::places/id :place) results))))


  SetMarker
  (process-event [{event :event} app]
    (let [lat (-> event .-latlng .-lat)
          lng (-> event .-latlng .-lng)]
                                        ;(.log js/console "SetMarker function " (-> event .-latlng .-lat) )
                                        ;(.log js/console "SetMarker function " (-> event .-latlng .-lng) )

      (assoc app :coordinates [ lat lng ] )))

  AddDrawnGeometry
  (process-event [{geojson :geojson} {:keys [drawn-geometry-idx] :as app}]
    (let [type (aget geojson "geometry" "type")]
      (-> app
          (update :drawn-geometry-idx (fnil inc 1))
          (add-place {::places/namefin (str type " " (or drawn-geometry-idx 1))
                      ::places/type "drawn"
                      ::places/id (str "drawn" (or drawn-geometry-idx 1))
                      ::places/primary? (get-in app [:place-search :primary?])}
                     geojson))))

  EditDrawnGeometryName
  (process-event [{id :id} app]
    (log/info "Edit name of " id)
    (update-place-by-id app id update :editing? not))

  SetDrawnGeometryName
  (process-event [{id :id name :name} app]
    (update-place-by-id app id assoc ::places/namefin name)))


(defn place-references
  "Gets a place search app model and returns place references from it.
  Place references are sent to the server instead of sending the geometries.
  Hand drawn geometries are sent with their geometry."
  [app]
  (mapv (fn [{:keys [geojson place]}]
          (case (::places/type place)
            ;; This is a hand drawn geometry, add GeoJSON geometry as string
            "drawn"
            (assoc place :geojson (js/JSON.stringify (aget geojson "geometry")))

            ;; This is a stored place (geometry already in database, but name may have changed)
            "stored"
            (dissoc place :geojson)

            ;; by default just return place
            place))
        (get-in app [:place-search :results])))

(defn operation-area-to-places
  "Turn an operation area from the backend to a format required by the UI."
  [operation-areas]
  {:place-search
   {:results
    (mapv (fn [{::t-service/keys [description id primary? location-geojson] :as operation-area}]
            {:place {::places/namefin (some #(when (= "FI" (::t-service/lang %))
                                               (::t-service/text %))
                                            description)
                     ::places/id id ;; FIXME: this needs to be a reference
                     ::places/type "stored"
                     ::places/primary? primary?}
             :geojson (js/JSON.parse location-geojson)})
          operation-areas)}})
