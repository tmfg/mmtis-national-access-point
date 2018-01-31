(ns ote.app.controller.admin
  (:require [tuck.core :as tuck]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [ote.communication :as comm]))

(defn- update-service-by-id [app id update-fn & args]
  (update-in app [:service-search :results]
          (fn [results]
            (map #(if (= (::t-service/id %) id)
                    (apply update-fn % args)
                    %)
                 results))))

(defrecord UpdateUserFilter [filter])
(defrecord SearchUsers [])
(defrecord SearchUsersResponse [response])
(defrecord DeleteTransportService [id])
(defrecord CancelDeleteTransportService [id])
(defrecord ConfirmDeleteTransportService [id])
(defrecord DeleteTransportServiceResponse [response])
(defrecord FailedDeleteTransportServiceResponse [response])

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
               :results response))

  DeleteTransportService
  (process-event [{id :id} app]
    (update-service-by-id
      app id
      assoc :show-delete-modal? true))

  CancelDeleteTransportService
  (process-event [{id :id} app]
    (update-service-by-id
      app id
      dissoc :show-delete-modal?))

  ConfirmDeleteTransportService
  (process-event [{id :id} app]
    (comm/post! "admin/transport-service/delete"
                {:id id}
                {:on-success (tuck/send-async! ->DeleteTransportServiceResponse)
                 :on-failure (tuck/send-async! ->FailedDeleteTransportServiceResponse)})
    app)


  DeleteTransportServiceResponse
  (process-event [{response :response} app]
    (let [filtered-map (filter #(not= (:ote.db.transport-service/id %) (int response)) (get-in app [:service-search :results]))]
      (-> app
          (assoc-in [:service-search :results] filtered-map)
          (assoc :page :services
                 :flash-message (tr [:common-texts :delete-service-success])
                 :services-changed? true))))

  FailedDeleteTransportServiceResponse
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :delete-service-error])))

  )
