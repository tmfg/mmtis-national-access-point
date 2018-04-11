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

(defrecord SaveToDb [published?])
(defrecord SaveNoticeResponse [response])
(defrecord SaveNoticeFailure [response])
(defrecord CancelNotice [])
(defrecord SelectOperatorForNotice [data])
(defrecord EditForm [form-data])
(defrecord DeleteEffectiveDate [index])


(extend-protocol tuck/Event

  EditForm
  (process-event [{form-data :form-data} app]
    (.log js/console "Tadaa, ja saatiin formi " (clj->js form-data))
    (-> app
        (update :pre-notice merge form-data)))


  SaveToDb
  (process-event [{published? :published?} app]
    (let [n (:pre-notice app)
          notice (form/without-form-metadata n)]
      (.log js/console " Tata " (clj->js notice))
      (comm/post! "pre-notice" notice
                {:on-success (tuck/send-async! ->SaveNoticeResponse)
                 :on-failure (tuck/send-async! ->SaveNoticeFailure)})
    (-> app
        (dissoc :before-unload-message)
        ;(set-saved-transfer-operator notice)
        )))
  SaveNoticeResponse
  (process-event [{response :response} app]
    (routes/navigate! :new-notice)
    ;; TODO: when published? true, use save-success-send
    (-> app
        (assoc :flash-message (tr [:pre-notice-page :save-success]))
        (dissoc :pre-notice)
        (assoc :page :new-notice)))

  SaveNoticeFailure
  (process-event [{response :response} app]
    (.error js/console "Save notice failed:" (pr-str response))
    ;; TODO: when published? true, use save-failure-send
    (assoc app
      :flash-message-error (tr [:pre-notice-page :save-failure])))


  CancelNotice
  (process-event [_ app]
    (.log js/console " Canceloidaan ")
    app)

  SelectOperatorForNotice
  (process-event [{data :data} app]
    (let [id (get data ::t-operator/id)
          selected-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                     %)
                                  (:transport-operators-with-services app))]
      (.log js/console "selected operator " (clj->js selected-operator) " ja id " id)
      (-> app
          (assoc-in [:pre-notice ::transit/transport-operator-id] id)
          (assoc
        :transport-operator (:transport-operator selected-operator)
        :transport-service-vector (:transport-service-vector selected-operator)))))

  DeleteEffectiveDate
  (process-event [{id :id} app]
    (.log js/console " DeleteEffectiveDate id " id)
    app)
  )

(defn valid-notice? [notice]
  true)