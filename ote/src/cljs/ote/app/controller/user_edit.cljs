(ns ote.app.controller.user-edit
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.localization :as localization :refer [tr]]
            [ote.app.routes :as routes]))

(defn handle-user-save-error [app key response]
  (let [{:keys [username-taken email-taken password-incorrect?]} response]
    (-> app
      (update-in [key :username-taken]
        #(if username-taken
           (conj (or % #{}) username-taken)
           %))
      (update-in [key :email-taken]
        #(if email-taken
           (conj (or % #{}) email-taken)
           %))
      (assoc-in [key :password-incorrect?] password-incorrect?)
      (assoc :flash-message-error
             (if (or username-taken email-taken password-incorrect?)
               ;; Expected form errors, don't show snackbar
               nil

               ;; Unexpected failure, show server error message
               (tr [:common-texts :server-error]))))))

(define-event UpdateForm [form-data]
  {}
  (-> app
    (assoc-in [:user-edit :form-data] form-data)))

(define-event UserFetchSuccess [result]
  {}
  (-> app
    (assoc :user-edit result)))

(define-event UserFetchFailure [result]
  {}
  (routes/navigate! :admin)
  (assoc app :flash-message-error (tr [:common-texts :server-error])))

(define-event UserSaveSuccess [result]
  {}
  (routes/navigate! :admin)
  (-> app
    (update-in [:admin :user-listing] dissoc :results)
    (assoc :flash-message "Käyttäjän tiedot tallennettu onnistuneesti")))


(define-event CancelEdit []
  {}
  (routes/navigate! :admin)
  (-> app
    (update-in [:admin :user-listing] dissoc :results)))

(define-event UserSaveFailure [response]
  {}
  (handle-user-save-error app :user-edit (:response response)))

(define-event SaveUserInfo [form-data user-id]
  {}
  (comm/post!
    (str "user/" user-id)
    form-data
    {:on-success (tuck/send-async! ->UserSaveSuccess)
     :on-failure (tuck/send-async! ->UserSaveFailure)})
  app)

(define-event FetchUserInformation [params]
  {}
  (comm/get!
    (str "user/" (:id params))
    {:on-success (tuck/send-async! ->UserFetchSuccess)
     :on-failure (tuck/send-async! ->UserFetchFailure)})
  app)

(defmethod routes/on-navigate-event :user-edit [{params :params}]
  (->FetchUserInformation params))
