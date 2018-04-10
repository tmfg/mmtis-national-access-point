(ns ote.app.controller.pre-notice
  "60 days notice controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.time :as time]
            [clojure.string :as str]
            [ote.app.controller.route.gtfs :as route-gtfs]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.ui.form :as form]
            [ote.app.routes :as routes]
            [ote.util.fn :refer [flip]]
            [clojure.set :as set]
            [ote.localization :refer [tr tr-key]]
            [taoensso.timbre :as log]
            [ote.util.collections :as collections]
            [clojure.set :as set]))

(defrecord SaveToDb [])
(defrecord CancelNotice [])
(defrecord SelectOperatorForNotice [data])


(extend-protocol tuck/Event
  SaveToDb
  (process-event [_ app]
    (.log js/console " Savetetaan "
          app))


  CancelNotice
  (process-event [_ app]
    (.log js/console " Canceloidaan "
          app)
    )

  SelectOperatorForNotice
  (process-event [{data :data} app]
    (let [id (get data ::t-operator/id)
          selected-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                     %)
                                  (:transport-operators-with-services app))]
      (assoc app
        :transport-operator (:transport-operator selected-operator)
        :transport-service-vector (:transport-service-vector selected-operator)))))

(defn valid-notice? [notice]
  true)