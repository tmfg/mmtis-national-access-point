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

(defmethod routes/on-navigate-event :email-settings [_ app]
  (->LoadData))

(defn load-email-notifications-from-server! []
  (comm/get! "pre-notices/regions"
             {:on-success (tuck/send-async! ->RegionsResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(defn load-regions-from-server! []
  (comm/get! "settings/email-notifications"
             {:on-success (tuck/send-async! ->UserNotificationsResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(tuck/define-event LoadData []
                   {:path [:email-settings]}
                   (load-regions-from-server!)
                   (load-email-notifications-from-server!)
                   (-> app
                       (assoc :regions-loading true)
                       (assoc :user-notifications-loading true)))

;; Region id's are strings in db. Change them to keywords
(defn str->keyword [x]
  (map #(keyword %) x))

;; Create new route
(defrecord RegionsResponse [response])
(defrecord UserNotificationsResponse [response])
(defrecord SaveEmailNotificationSettings [])
(defrecord UpdateSettings [form-data])
(defrecord SaveEmailSettingsResponse [response])
(defrecord SaveEmailSettingsResponseFailure [response])

(extend-protocol tuck/Event

  RegionsResponse
  (process-event [{response :response} app]
    (-> app
        (assoc-in [:email-settings :regions] response)
        (assoc-in [:email-settings :regions-loading] false)))


  UserNotificationsResponse
  (process-event [{response :response} app]
    (-> app
        (assoc-in [:email-settings :user-notifications] response)
        (update-in [:email-settings :user-notifications :ote.db.user-notifications/finnish-regions] str->keyword)
        (assoc-in [:email-settings :user-notifications-loading] false)))

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
    (routes/navigate! :front-page)
    (-> app
        (dissoc :email-settings
                :before-unload-message)
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
