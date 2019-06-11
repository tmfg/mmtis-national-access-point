(ns ote.app.controller.operator-users
  "Organization access management controller"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr]]
            [ote.communication :as comm]
            [ote.app.controller.front-page :as fp]
            [ote.util.url :as url-util]
            [ote.app.routes :as routes]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.db.transport-operator :as t-operator]))

(define-event EmailFieldOnChange [input]
  {}
  (update-in app [:manage-access] assoc :new-member-email input))

(define-event GetUsersSuccess [result]
  {}
  (-> app
      (assoc-in [:manage-access :users] result)
      (assoc-in [:manage-access :loaded?] true)))

(define-event GetOperatorSuccess [result]
  {}
  (assoc-in app [:manage-access :operator-name] (::t-operator/name result)))

(defn GetOperatorUsers [app]
  (comm/get!
    (str "transport-operator/" (url-util/encode-url-component (get-in app [:params :operator-id])) "/users")
    {:on-success (tuck/send-async! ->GetUsersSuccess)
     :on-failure (tuck/send-async! #(fp/->ChangePage :front-page nil))})
  (assoc-in app [:manage-access :loaded?] false))

(defn GetTransportOperator [app]
  (comm/get!
    (str "t-operator/" (url-util/encode-url-component (get-in app [:params :operator-id])))
    {:on-success (tuck/send-async! ->GetOperatorSuccess)
     :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event NewUserSuccess [result]
  {}
  (let [users (vec (get-in app [:manage-access :users]))
        users-long (conj users result)]
    (-> app
        (assoc :flash-message (tr [:transport-users-page :invite-sent-success]))
        (assoc-in [:manage-access :users] users-long)
        (assoc-in [:manage-access :new-member-email] "")
        (assoc-in [:manage-access :new-member-loading?] false))))

(define-event NewUserFailure [response]
  {}
  (-> app
      (assoc :flash-message-error (tr [:transport-users-page (get-in response [:response :error])]))
      (assoc-in [:manage-access :new-member-email] "")
      (assoc-in [:manage-access :new-member-loading?] false)))

(define-event PostNewUser [email operator-id]
  {}
  (comm/post!
    (str "transport-operator/" (url-util/encode-url-component operator-id) "/users")
    {:email email}
    {:on-success (tuck/send-async! ->NewUserSuccess)
     :on-failure (tuck/send-async! ->NewUserFailure)})
  (assoc-in app [:manage-access :new-member-loading?] true))

;; TODO: remove removed user from app-state
(define-event RemoveMemberSuccess [result]
  {}
  app)

(define-event RemoveMember [member operator-id]
  {}
  (comm/delete!
    (str "transport-operator/" (url-util/encode-url-component operator-id) "/users/" (:email member))
    {}
    {:on-success (tuck/send-async! ->RemoveMemberSuccess)
     :on-failure (tuck/send-async! ->ServerError)})
  app)                                                      ;;TODO: Remove removed user from app-state

(define-event InitTransportUserView []
  {}
  (GetOperatorUsers (GetTransportOperator app)))

(defmethod routes/on-navigate-event :access-management [_]
  (->InitTransportUserView))
