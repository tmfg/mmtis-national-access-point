(ns ote.app.controller.login
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]))

(defrecord ShowLoginDialog [])
(defrecord UpdateLoginCredentials [credentials])
(defrecord Login [])
(defrecord LoginResponse [response])
(defrecord LoginFailed [response])
(defrecord LoginCancel [])

(extend-protocol tuck/Event

  ShowLoginDialog
  (process-event [_ app]
    (assoc app :login {:show? true}))

  UpdateLoginCredentials
  (process-event [{credentials :credentials} app]
    (update-in app [:login :credentials] merge credentials))

  Login
  (process-event [_ app]
    (comm/post! "login"
                (select-keys (get-in app [:login :credentials]) #{:email :password})
                {:on-success (tuck/send-async! ->LoginResponse)
                 :on-failure (tuck/send-async! ->LoginFailed)})
    (update app :login
            #(-> %
                 (dissoc :failed? :error)
                 (assoc :in-progress? true))))

  LoginResponse
  (process-event [{response :response} app]
    (if (:success? response)
      (-> app
          (assoc :user (:user response))
          (dissoc :login)
          (assoc :flash-message "loggasit sisään, jee!"))
      (assoc-in app [:login :failed?] true)))

  LoginFailed
  (process-event [{response :response} app]
    ;; The login request itself failed
    (assoc-in app [:login :error] response))

  LoginCancel
  (process-event [_ app]
    (dissoc app :login)))
