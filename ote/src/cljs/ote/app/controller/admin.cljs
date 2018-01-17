(ns ote.app.controller.admin
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]))

(defrecord UpdateUserFilter [filter])
(defrecord SearchUsers [])
(defrecord SearchUsersResponse [response])


(extend-protocol tuck/Event

  UpdateUserFilter
  (process-event [{f :filter} app]
    (update-in app [:admin :user-listing] assoc :filter f))

  SearchUsers
  (process-event [_ app]
    (comm/post! "admin/users" (get-in app [:admin :user-listing :filter])
                {:on-success (tuck/send-async! ->SearchUsersResponse)})
    (assoc-in app [:admin :user-listing :loading?] true))

  SearchUsersResponse
  (process-event [{response :response} app]
    (update-in app [:admin :user-listing] assoc
               :loading? false
               :results response)))
