(ns ote.app.controller.email-notification-settings
  "Controller for email notification settings"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.db.transit :as transit]
            [ote.ui.form :as form]
            [ote.localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]))

(declare ->LoadData ->RegionsResponse ->UserNotificationsResponse ->UpdateSettings)

;; On Navigate to :email-settings -> load data
(defmethod routes/on-navigate-event :email-settings [_ app]
  (->LoadData))

(defn fill-selected-regions
  "Fill selected regions if no settings saved"
  [response]
    (if (get-in response [:email-settings :user-notifications :ote.db.user-notifications/created-by])
      ;; User have saved settings - so no need to do anything
      response
      ;; No settings in db, so act like all regions are selected
      (assoc-in response [:email-settings :user-notifications :ote.db.user-notifications/finnish-regions]
                      (map #(:id %) (get-in response [:email-settings :regions])))))

(defn load-email-notifications-from-server! []
  (comm/get! "settings/email-notifications"
             {:on-success (tuck/send-async! ->UserNotificationsResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(tuck/define-event LoadData []
                   {:path [:email-settings]}
                   (load-email-notifications-from-server!)
                   (-> app
                       (assoc :regions-loading true)
                       (assoc :user-notifications-loading true)))

;; Region id's are strings in db. Change them to keywords
(defn str->keyword [x]
  (map #(keyword %) x))

;; Create new route
(defrecord UserNotificationsResponse [response])
(defrecord SaveEmailNotificationSettings [])
(defrecord UpdateSettings [form-data])
(defrecord SaveEmailSettingsResponse [response])
(defrecord SaveEmailSettingsResponseFailure [response])

(extend-protocol tuck/Event

  UserNotificationsResponse
  (process-event [{response :response} app]
    (let [r (fill-selected-regions response)]
      (-> app
        (assoc-in [:email-settings :regions] (get-in r [:email-settings :regions]))
        (assoc-in [:email-settings :user-notifications] (get-in r [:email-settings :user-notifications]))
        (assoc-in [:email-settings :regions-loading] false))))

  SaveEmailNotificationSettings
  (process-event [_ app]

    (let [settings (as-> (get-in app [:email-settings :user-notifications]) n
                         (form/without-form-metadata n)
                         (assoc n :ote.db.user-notifications/finnish-regions
                                  (into [] (map name (:ote.db.user-notifications/finnish-regions n)))))] ;; Change keywords to strings
      (comm/post! "settings/email-notifications" settings
                  {:on-success (tuck/send-async! ->SaveEmailSettingsResponse)
                   :on-failure (tuck/send-async! ->SaveEmailSettingsResponseFailure)})
      app))

  SaveEmailSettingsResponse
  (process-event [{response :response} app]
    (routes/navigate! :email-settings)
    (-> app
        (dissoc :before-unload-message)
        (assoc :flash-message
                 (tr [:email-notification-settings-page :save-success]))))

  SaveEmailSettingsResponseFailure
  (process-event [{response :response} app]
    (.error js/console "Save settings failed:" (pr-str response))
    (assoc app
      :flash-message-error
      (tr [:email-notification-settings-page :save-failure])))

  UpdateSettings
  (process-event [{form-data :form-data} app]
    (-> app
        (update-in [:email-settings :user-notifications] merge form-data))))
