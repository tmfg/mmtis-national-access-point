(ns ote.app.state
  "Contains the global frontend application `app` database. The database is stored into localhost and accessed as ratom
   for interop. All frontend applications can access the shared global state. Everything that is in the current state of
   the frontend is in the app atom."
  (:require [alandipert.storage-atom :as store :refer [local-storage]]
            [cognitect.transit :as transit]
            [reagent.core :as r])
  (:import [goog.date UtcDateTime]))

; inject cljs-time/goog.date read+write support to storage-atom's transit serde
; Yes, this is quite a hack right now, so TODO: less hacky plz
(swap! store/transit-read-handlers assoc "m" (transit/read-handler (fn [s] (UtcDateTime.fromTimestamp s))))
(swap! store/transit-write-handlers assoc UtcDateTime (transit/write-handler
                                                  (constantly "m")
                                                  (fn [v] (.getTime v))
                                                  (fn [v] (str (.getTime v)))))

(defonce app (local-storage
               (r/atom {;; current page
                        ;; see ote.app.routes
                        :page :front-page
                        :params nil ; parameters from url route
                        :query nil ; query parameters from url (like "?foo=bar")

                         :user {} ;; No user data by default

                        ;; Currently selected / edited transport operator (company basic info)
                        :transport-operator nil

                        ;; Currently selected / edited transport service
                        :transport-service {}})
               :nap))

;; Separate app state for viewer mode
(defonce viewer (r/atom {:loading? true
                         :url nil
                         :geojson nil}))

(defn windowresize-handler
  "When window size changes set width and height to app state"
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
      (swap! app assoc :width w :height h)))

(.addEventListener js/window "resize" windowresize-handler)

(defn set-init-state!
  "May be called before app rendered to set initial state when starting up."
  [state-map]
  (swap! app merge state-map))
