(ns ote.app.controller.pre-notices
  "Controller for 60 day pre notices"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [ote.ui.form :as form]
            [ote.localization :refer [tr]]))



(defn valid-notice? [notice]
  true)

(declare ->LoadPreNoticesResponse ->LoadPreNoticesFailure)

;; Load the pre-notices that are available
(tuck/define-event LoadOrganizationPreNotices []
  {:path [:pre-notices]}
  (comm/get! "pre-notices/list"
             {:on-success (tuck/send-async! ->LoadPreNoticesResponse)
              :on-failure (tuck/send-async! ->LoadPreNoticesFailure)})
  :loading)

(tuck/define-event LoadAuthorityPreNotices []
  {:path [:pre-notices]}
  (comm/get! "pre-notices/authority-list"
             {:on-success (tuck/send-async! ->LoadPreNoticesResponse)
              :on-failure (tuck/send-async! ->LoadPreNoticesFailure)})
  :loading)

(defmethod routes/on-navigate-event :pre-notices [_]
  (->LoadOrganizationPreNotices))

(defmethod routes/on-navigate-event :authority-pre-notices [_]
  (->LoadAuthorityPreNotices))

(tuck/define-event LoadPreNoticesResponse [response]
  {:path [:pre-notices]}
  response)

(tuck/define-event LoadPreNoticesFailure [response]
  {}
  (assoc app :flash-message-error (tr [:common-texts :server-error])))

;; Create new route
(defrecord CreateNewPreNotice [])
(defrecord SelectOperatorForNotice [data])
(defrecord EditForm [form-data])
(defrecord SaveToDb [published?])
(defrecord SaveNoticeResponse [response])
(defrecord SaveNoticeFailure [response])
(defrecord CancelNotice [])

(defrecord DeleteEffectiveDate [index])

(extend-protocol tuck/Event

  CreateNewPreNotice
  (process-event [_ app]
    (routes/navigate! :new-notice)
    (-> app
        (dissoc :pre-notice)
        (assoc-in [:pre-notice ::t-operator/id] (get-in app [:transport-operator ::t-operator/id]))))

  SelectOperatorForNotice
  (process-event [{data :data} app]
    (let [id (get data ::t-operator/id)
          selected-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                     %)
                                  (:transport-operators-with-services app))]
      (-> app
          (assoc-in [:pre-notice ::t-operator/id] id)
          (assoc
            :transport-operator (:transport-operator selected-operator)
            :transport-service-vector (:transport-service-vector selected-operator)))))

  EditForm
  (process-event [{form-data :form-data} app]
    (-> app
        (update :pre-notice merge form-data)))


  SaveToDb
  (process-event [{published? :published?} app]
    (let [n (:pre-notice app)
          notice (form/without-form-metadata n)]
      (comm/post! "pre-notice" notice
                  {:on-success (tuck/send-async! ->SaveNoticeResponse)
                   :on-failure (tuck/send-async! ->SaveNoticeFailure)})
      (-> app
          (dissoc :before-unload-message)
          ;(set-saved-transfer-operator notice)
          )))
  SaveNoticeResponse
  (process-event [{response :response} app]
    (routes/navigate! :pre-notices)
    ;; TODO: when published? true, use save-success-send
    (-> app
        (assoc :flash-message (tr [:pre-notice-page :save-success]))
        (dissoc :pre-notice)
        (assoc :page :pre-notices)))

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



  DeleteEffectiveDate
  (process-event [{id :id} app]
    (.log js/console " DeleteEffectiveDate id " id)
    app)
  )
