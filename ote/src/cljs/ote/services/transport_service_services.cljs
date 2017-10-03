(ns ote.services.transport-service-services
  (:require [tuck.core :as t]))

(defrecord OpenPriceClassDialog [])
(defrecord ClosePriceClassDialog [])

(extend-protocol t/Event

  OpenPriceClassDialog
  (process-event [_ app]
    (assoc-in app [:transport-service :price-class-open] true))

  OpenPriceClassDialog
  (process-event [_ app]
    (assoc-in app [:transport-service :price-class-open] false)))


;ote.app.transport-service.events == määrittelee transport-servicen mahdolliset UI evneti
;ote.app.transport-service == määrittelee transport-servicen eventtie process-event implementaatiot