(ns ote.app.controller.transit-visualization
  (:require [ote.communication :as comm]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.app.routes :as routes]
            [ote.time :as time]
            [taoensso.timbre :as log]))

(def hash-colors
  ["#52ef99" "#c82565" "#8fec2f" "#8033cb" "#5c922f" "#fe74fe" "#02531d"
   "#ec8fb5" "#23dbe1" "#a4515b" "#169294" "#fd5925" "#3d4e92" "#f4d403"
   "#66a1e5" "#d07d09" "#9382e9" "#b9cf84" "#544437" "#f2cdb9"])

(define-event LoadOperatorDatesResponse [dates]
  {:path [:transit-visualization]}
  (-> app
      (assoc :hash->color (zipmap (distinct (map :hash dates))
                            (cycle hash-colors
                                    ;; FIXME: after all colors are consumed, add some pattern style
                                   ))
             :date->hash (into {}
                               (map (juxt (comp time/format-date :date)
                                          :hash))
                               dates))
      (dissoc :loading)))

(define-event LoadOperatorDates [operator-id]
  {:path [:transit-visualization :loading]}
  (comm/get! (str "transit-visualization/dates/" operator-id)
             {:on-success (tuck/send-async! ->LoadOperatorDatesResponse)})
  true)

(defmethod routes/on-navigate-event :transit-visualization [_]
  (->LoadOperatorDates 1))

(define-event HighlightHash [hash]
  {:path [:transit-visualization :highlight]}
  hash)
