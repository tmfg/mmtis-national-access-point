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
    (str "transport-operator/" (url-util/encode-url-component (get-in app [:params :ckan-group-id])) "/users")
    {:on-success (tuck/send-async! ->GetUsersSuccess)
     :on-failure (tuck/send-async! #(fp/->ChangePage :front-page nil))})
  (assoc-in app [:manage-access :loaded?] false))

(defn GetTransportOperator [app]
  (comm/get!
    (str "transport-operator/" (url-util/encode-url-component (get-in app [:params :ckan-group-id])))
    {:on-success (tuck/send-async! ->GetOperatorSuccess)
     :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event NewUserSuccess [result]
  {}
  (let [users (vec (get-in app [:manage-access :users]))
        users-long (sort-by :pending? (conj users result))]
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

(define-event PostNewUser [email ckan-group-id]
  {}
  (comm/post!
    (str "transport-operator/" (url-util/encode-url-component ckan-group-id) "/users")
    {:email email}
    {:on-success (tuck/send-async! ->NewUserSuccess)
     :on-failure (tuck/send-async! ->NewUserFailure)})
  (assoc-in app [:manage-access :new-member-loading?] true))

(define-event RemoveMemberSuccess [result member]
  {}
  (let [users (get-in app [:manage-access :users])
        new-users (filterv
                    #(not= (:id member) (:id %))
                    users)]
    (-> app
      (assoc-in [:manage-access :confirmation :open?] false)
      (assoc-in [:manage-access :users] new-users))))

(define-event RemoveTokenSuccess [result token]
  {}
  (let [users (get-in app [:manage-access :users])
        new-users (filterv
                    #(not= (:token token) (:token %))
                    users)]
    (-> app
      (assoc-in [:manage-access :confirmation :open?] false)
      (assoc-in [:manage-access :users] new-users))))

(define-event RemoveMemberError []
  {}
  (-> app
    (assoc :flash-message-error (tr [:transport-users-page :removing-last-user]))
    (assoc-in [:manage-access :confirmation :open?] false)))

(define-event RemoveMember [member operator-id]
  {}
  (comm/delete!
    (str "transport-operator/" (url-util/encode-url-component operator-id) "/users")
    member
    {:on-success (tuck/send-async! ->RemoveMemberSuccess member)
     :on-failure (tuck/send-async! ->RemoveMemberError)})
  app)

(define-event RemoveToken [token operator-id]
  {}
  (comm/delete!
    (str "transport-operator/" (url-util/encode-url-component operator-id) "/token")
    token
    {:on-success (tuck/send-async! ->RemoveTokenSuccess token)
     :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event OpenConfirmationDialog [member operator-id]
  {}
  (assoc-in app [:manage-access :confirmation] {:open? true :member member :operator-id operator-id}))

(define-event CloseConfirmationDialog []
  {}
  (assoc-in app [:manage-access :confirmation :open?] false))

(define-event InitTransportUserView []
  {}
  (GetOperatorUsers (GetTransportOperator app)))

(defmethod routes/on-navigate-event :operator-users [_]
  (->InitTransportUserView))
