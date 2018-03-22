(ns ote.app.controller.gtfs-viewer
  "GTFS viewer, visualizes data from a GTFS package."
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [ote.communication :as comm]
            [ote.gtfs.query :as gq]
            [clojure.string :as str]))

(defrecord StartViewer [])
(defrecord LoadGTFSResponse [response])
(defrecord LoadGTFSFailure [response])

(define-event SelectRoute [route]
  {:path [:gtfs-viewer]
   :app gtfs}
  (println "ROUTE" (pr-str route))
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

(extend-protocol tuck/Event
  StartViewer
  (process-event [_ app]
    (println "STARTING GTFS VIEW")
    (comm/get! (str "import/gtfs?url=" (js/encodeURIComponent (get-in app [:query :url])))
               {:on-success (tuck/send-async! ->LoadGTFSResponse)
                :on-failure (tuck/send-async! ->LoadGTFSFailure)})
    app)

  LoadGTFSResponse
  (process-event [{response :response} app]
    #_(.log js/console "GOT RESPONSE:" (pr-str response))
    (assoc app :gtfs-viewer response))

  LoadGTFSFailure
  (process-event [{response :response} app]
    (.log js/console "Load GTFS failed: " (pr-str response))
    app))

(define-event FetchStopTimesForStop [stop-id]
  {:path [:gtfs-viewer]
   :app gtfs}
  (assoc-in gtfs [:selected-route :stop-times-for-stop stop-id]
            "FOFOFOFOFOF"))
