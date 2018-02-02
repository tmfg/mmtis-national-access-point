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

(defrecord UpdateUserFilter [user-filter])
(defrecord UpdateServiceFilter [service-filter])
(defrecord UpdateOperatorFilter [operator-filter])
(defrecord SearchUsers [])
(defrecord SearchServices [])
(defrecord SearchServicesByOperator [])
(defrecord SearchUsersResponse [response])
(defrecord SearchServicesResponse [response])
(defrecord GetBusinessIdReport [])
(defrecord GetBusinessIdReportResponse [response])
(defrecord DeleteTransportService [id])
(defrecord CancelDeleteTransportService [id])
(defrecord ConfirmDeleteTransportService [id])
(defrecord DeleteTransportServiceResponse [response])
(defrecord FailedDeleteTransportServiceResponse [response])
(defrecord ChangeAdminTab [tab])

(extend-protocol tuck/Event

  UpdateUserFilter
  (process-event [{f :user-filter} app]
    (update-in app [:admin :user-listing] assoc :user-filter f))

  UpdateServiceFilter
  (process-event [{f :service-filter} app]
    (update-in app [:admin :service-listing] assoc :service-filter f))

  UpdateOperatorFilter
  (process-event [{f :operator-filter} app]
    (update-in app [:admin :service-listing] assoc :operator-filter f))

  SearchUsers
  (process-event [_ app]
    (comm/post! "admin/users" (get-in app [:admin :user-listing :user-filter])
                {:on-success (tuck/send-async! ->SearchUsersResponse)})
    (assoc-in app [:admin :user-listing :loading?] true))

  SearchUsersResponse
  (process-event [{response :response} app]
    (update-in app [:admin :user-listing] assoc
               :loading? false
               :results response))

  GetBusinessIdReport
  (process-event [_ app]
    (comm/post! "admin/business-id-report" {}
                {:on-success (tuck/send-async! ->GetBusinessIdReportResponse)})
    (assoc-in app [:admin :business-id-report :loading?] true))

  GetBusinessIdReportResponse
  (process-event [{response :response} app]
    (update-in app [:admin :business-id-report] assoc
               :loading? false
               :results response))

  SearchServices
  (process-event [_ app]
    (comm/post! "admin/transport-services" (get-in app [:admin :service-listing :service-filter])
                {:on-success (tuck/send-async! ->SearchServicesResponse)})
    (assoc-in app [:admin :service-listing :loading?] true))

  SearchServicesByOperator
  (process-event [_ app]
    (comm/post! "admin/transport-services-by-operator" (get-in app [:admin :service-listing :operator-filter])
                {:on-success (tuck/send-async! ->SearchServicesResponse)})
    (assoc-in app [:admin :service-listing :loading?] true))

  SearchServicesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :service-listing] assoc
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

  ChangeAdminTab
    (process-event [{tab :tab} app]
      (assoc-in app [:params :admin-page] tab)))

