(ns ote.app.controller.gtfs-viewer
  "GTFS viewer, visualizes data from a GTFS package."
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [ote.communication :as comm]
            [ote.gtfs.query :as gq]
            [clojure.string :as str]
            [ote.util.fn :refer [flip]]
            [ote.localization :refer [tr]]))

(declare ->LoadGTFSResponse ->LoadGTFSFailure)

(define-event StartViewer []
  {}
  (let [{url :url type :type} (get-in app [:query])
        import-api (if (= type "kalkati") "import/kalkati" "import/gtfs")]
    (comm/get! (str import-api "?url=" (js/encodeURIComponent url))
               {:on-success (tuck/send-async! ->LoadGTFSResponse)
                :on-failure (tuck/send-async! ->LoadGTFSFailure)})
    (assoc app :gtfs-viewer {})))

(define-event LoadGTFSResponse [response]
  {:path [:gtfs-viewer]}
  (update response :gtfs/routes-txt
          (flip sort-by) (juxt :gtfs/route-short-name
                               :gtfs/route-long-name)))

(define-event LoadGTFSFailure [response]
  {:path [:gtfs-viewer :error-message]}
  (tr [:gtfs-viewer :gtfs-load-failed]))

(define-event SelectRoute [route]
  {:path [:gtfs-viewer]
   :app gtfs}
  (let [route-id (:gtfs/route-id route)
        trips (gq/route-trips gtfs route-id)
        shape-ids (into #{} (keep :gtfs/shape-id) trips)
        stop-sequences (gq/stop-sequences-for-trips gtfs trips)
        color (if (str/blank? (:gtfs/route-color route))
                "#000000"
                (str "#" (:gtfs/route-color route)))

        shapes-by-id (gq/shapes-for-ids gtfs shape-ids)

        lines
        (doall
         (if (seq shape-ids)
           ;; We have shapes, use them
           (for [[_ shapes] shapes-by-id]
             (map (juxt :gtfs/shape-pt-lat :gtfs/shape-pt-lon) shapes))

           ;; No shapes, draw line through stop sequence
           (for [stop-sequence (gq/distinct-stop-sequences stop-sequences)]
             (map (juxt :gtfs/stop-lat :gtfs/stop-lon)
                  stop-sequence))))]
    (assoc gtfs :selected-route
           {:route route
            :trips (gq/distinct-trips-times gtfs trips)
            :stop-sequences stop-sequences
            :stops (distinct (mapcat val stop-sequences))
            :lines (doall
                    (for [positions lines]
                      {:positions (clj->js positions)
                       :color color}))
            :bearing-markers (mapcat #(gq/bearing-markers % 25)
                                     (vals shapes-by-id))})))

(defmethod routes/on-navigate-event :view-gtfs [_]
  (->StartViewer))
