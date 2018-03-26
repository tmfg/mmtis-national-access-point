(ns ote.app.controller.gtfs-viewer
  "GTFS viewer, visualizes data from a GTFS package."
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [ote.communication :as comm]
            [ote.gtfs.query :as gq]
            [clojure.string :as str]))

(declare ->LoadGTFSResponse ->LoadGTFSFailure)

(define-event StartViewer []
  {}
  (comm/get! (str "import/gtfs?url=" (js/encodeURIComponent (get-in app [:query :url])))
    {:on-success (tuck/send-async! ->LoadGTFSResponse)
     :on-failure (tuck/send-async! ->LoadGTFSFailure)})
  app)

(define-event LoadGTFSResponse [response]
  {:path [:gtfs-viewer]}
  response)

(define-event LoadGTFSFailure [response]
  {}
  (.log js/console "Load GTFS failed: " (pr-str response))
  (assoc app :flash-message-error "GTFS tiedoston lataus epäonnistui"))

(define-event SelectRoute [route]
  {:path [:gtfs-viewer]
   :app gtfs}
  (let [route-id (:gtfs/route-id route)
        trips (gq/route-trips gtfs route-id)
        stop-sequences (gq/stop-sequences-for-trips gtfs trips)]
    (assoc gtfs :selected-route
           {:route route
            :trips (gq/distinct-trips-times gtfs trips)
            :stop-sequences stop-sequences
            :stops (distinct (mapcat val stop-sequences))
            :lines (for [stop-sequence (gq/distinct-stop-sequences stop-sequences)]
                     {:positions (clj->js (map (juxt :gtfs/stop-lat :gtfs/stop-lon)
                                               stop-sequence))
                      :color (let [c (:gtfs/route-color route)]
                               (if (str/blank? c)
                                 "#000000"
                                 (str "#" c)))})})))

(defmethod routes/on-navigate-event :view-gtfs [_]
  (->StartViewer))
