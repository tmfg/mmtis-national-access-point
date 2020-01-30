(ns ote.app.controller.admin
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [testdouble.cljs.csv :as csv]
            [clojure.string :as str]
            cljsjs.filesaverjs
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.localization :refer [tr]]
            [ote.util.text :as text]
            [ote.time :as time]
            [ote.app.routes :as routes]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.controller.admin-validation :as admin-validation]))

(defn- update-service-by-id [app id update-fn & args]
  (update-in app [:service-search :results]
             (fn [results]
               (map #(if (= (::t-service/id %) id)
                       (apply update-fn % args)
                       %)
                    results))))

(defrecord UpdateUserFilter [user-filter])
(defrecord UpdateServiceFilter [service-filter])
(defrecord UpdateServiceOperatorFilter [operator-filter])
(defrecord UpdateOperatorFilter [operator-filter])
(defrecord UpdatePublishedFilter [published-filter])

;; User tab
(defrecord SearchUsers [])
(defrecord SearchUsersResponse [response])
(defrecord OpenDeleteUserModal [id])
(defrecord OpenDeleteUserModalResponse [response id])
(defrecord CancelDeleteUser [id])
(defrecord ConfirmDeleteUser [id])
(defrecord ConfirmDeleteUserResponse [response])
(defrecord ConfirmDeleteUserResponseFailure [response])
(defrecord EnsureUserId [id ensured-id])
(defrecord OpenEditUserDialog [id])
(defrecord CloseEditUserDialog [id])

(defrecord SearchServices [])
(defrecord SearchServicesByOperator [])
(defrecord SearchOperators [])
(defrecord SearchServicesResponse [response])
(defrecord SearchOperatorResponse [response])
(defrecord GetBusinessIdReport [])
(defrecord GetBusinessIdReportResponse [response])
(defrecord UpdateBusinessIdFilter [business-id-filter])
(defrecord DeleteTransportService [id])
(defrecord CancelDeleteTransportService [id])
(defrecord ConfirmDeleteTransportService [id])
(defrecord DeleteTransportServiceResponse [response])
(defrecord FailedDeleteTransportServiceResponse [response])
(defrecord ChangeAdminTab [tab])
(defrecord ChangeRedirectTo [new-page])

;; Interface tab
(defrecord UpdateInterfaceFilters [filter])
(defrecord UpdateInterfaceRadioFilter [radio-filter])
(defrecord SearchInterfaces [])
(defrecord SearchInterfacesResponse [response])
(defrecord OpenInterfaceErrorModal [interface])
(defrecord CloseInterfaceErrorModal [])
(defrecord OpenOperatorModal [interface])
(defrecord CloseOperatorModal [])
(defrecord GetInterfaceDownloads [interface-id])
(defrecord GetInterfaceDownloadsResponse [response interface-id])
(defrecord CloseDownloadList [])

;; Sea route tab
(defrecord UpdateSeaRouteFilters [filter])
(defrecord SearchSeaRoutes [])
(defrecord SearchSeaRoutesResponse [response])

;; Netex tab
(defrecord UpdateNetexFilters [filter])
(defrecord SearchNetexConversions [])
(defrecord SearchNetexConversionsResponse [response])

;; Company csv tab
(defrecord FetchCompanyCsvs [])
(defrecord FetchCompanyCsvsResponse [response])
(defrecord OpenValidationWarningModal [warning])
(defrecord CloseValidationWarningModal [])

;; Delete Transport Operator
(defrecord OpenDeleteOperatorModal [id])
(defrecord CancelDeleteOperator [id])
(defrecord ConfirmDeleteOperator [id])
(defrecord DeleteOperatorResponse [response])
(defrecord DeleteOperatorResponseFailed [response])
(defrecord EnsureServiceOperatorId [id ensured-id])
(defrecord EditTransportOperator [business-id])
(defrecord EditTransportOperatorResponse [response])

(defrecord ToggleAddMemberDialog [id])
(defrecord ChangeTab [tab-value])

(defn- update-operator-by-id [app id update-fn & args]
  (update-in app [:admin :operator-list :results]
             (fn [operators]
               (map #(if (= (::t-operator/id %) id)
                       (apply update-fn % args)
                       %)
                    operators))))

(defn- update-user-by-id [app id update-fn & args]
  (update-in app [:admin :user-listing :results]
             (fn [users]
               (map #(if (= (:id %) id)
                       (apply update-fn % args)
                       %)
                    users))))

(defn- get-search-result-operator-by-id [app id]
  (some
    #(when (= (::t-operator/id %) id) %)
    (get-in app [:admin :operator-list :results])))

(defn- get-user-by-id [app id]
  (some
    #(when (= (:id %) id) %)
    (get-in app [:admin :user-listing :results])))

(extend-protocol tuck/Event

  UpdateUserFilter
  (process-event [{f :user-filter} app]
    (update-in app [:admin :user-listing] assoc :user-filter f))

  UpdateServiceFilter
  (process-event [{f :service-filter} app]
    (update-in app [:admin :service-listing] assoc :service-filter f))

  UpdateServiceOperatorFilter
  (process-event [{f :operator-filter} app]
    (update-in app [:admin :service-listing] assoc :operator-filter f))

  UpdateOperatorFilter
  (process-event [{f :operator-filter} app]
    (update-in app [:admin :operator-list] assoc :operator-filter f))

  UpdateBusinessIdFilter
  (process-event [{f :business-id-filter} app]
    (update-in app [:admin :business-id-report] assoc :business-id-filter f))

  UpdatePublishedFilter
  (process-event [{f :published-filter} app]
    (update-in app [:admin :service-listing] assoc :published-filter f))

  SearchUsers
  (process-event [_ app]
    (let [filter (get-in app [:admin :user-listing :user-filter])]
      (comm/get! (str "admin/user"
                      (when filter (str "?type=any&search=" filter)))
                 {:on-success (tuck/send-async! ->SearchUsersResponse)
                  :on-failure (tuck/send-async! ->SearchUsersResponse)}))
    (assoc-in app [:admin :user-listing :loading?] true))

  ConfirmDeleteUser
  (process-event [{id :id} app]
    (if (= id (:ensured-id (get-user-by-id app id)))
      (comm/delete! (str "admin/user/" id)
                    nil
                    {:on-success (tuck/send-async! ->ConfirmDeleteUserResponse)
                     :on-failure (tuck/send-async! ->ConfirmDeleteUserResponseFailure)})
      (.log js/console "Could not delete user! Check given id:" id))
    app)

  ConfirmDeleteUserResponse
  (process-event [{response :response} app]
    (let [filtered-map (filter #(not= (:id %) response) (get-in app [:admin :user-listing :results]))]
      (-> app
          (assoc-in [:admin :user-listing :results] filtered-map)
          (assoc :flash-message "Käyttäjä poistettu onnistuneesti."))))

  ConfirmDeleteUserResponseFailure
  (process-event [{response :response} app]
    (assoc app :flash-message-error "Käyttäjän poistaminen epäonnistui"))

  SearchUsersResponse
  (process-event [{response :response} app]
    (update-in app [:admin :user-listing] assoc
               :loading? false
               :results (if (vector? response)              ;; :response contains data in vector on success, otherwise http error in a map
                          response
                          [])))

  OpenDeleteUserModal
  (process-event [{id :id} app]
    (comm/get! (str "admin/member?userid=" id)
               {:on-success (tuck/send-async! ->OpenDeleteUserModalResponse id)})
    app)

  OpenDeleteUserModalResponse
  (process-event [{response :response id :id} app]
    (-> app
        (update-user-by-id
          id
          assoc :show-delete-modal? true)
        (update-user-by-id
          id
          assoc :other-members response)))

  CancelDeleteUser
  (process-event [{id :id} app]
    (update-user-by-id
      app id
      dissoc :show-delete-modal?))

  EnsureUserId
  (process-event [{id :id ensured-id :ensured-id} app]
    (update-user-by-id
      app id
      assoc :ensured-id ensured-id))

  OpenEditUserDialog
  (process-event [{id :id} app]
    (update-user-by-id
      app id
      assoc :show-edit-dialog? true))

  CloseEditUserDialog
  (process-event [{id :id} app]
    (update-user-by-id
      app id
      assoc :show-edit-dialog? false))

  GetBusinessIdReport
  (process-event [_ app]
    (comm/post! "admin/business-id-report" {:business-id-filter (get-in app [:admin :business-id-report :business-id-filter])}
                {:on-success (tuck/send-async! ->GetBusinessIdReportResponse)})
    (assoc-in app [:admin :business-id-report :loading?] true))

  GetBusinessIdReportResponse
  (process-event [{response :response} app]
    (update-in app [:admin :business-id-report] assoc
               :loading? false
               :results response))

  SearchServices
  (process-event [_ app]
    (comm/post! "admin/transport-services"
                {:query (get-in app [:admin :service-listing :service-filter])
                 :published-type (get-in app [:admin :service-listing :published-filter])}
                {:on-success (tuck/send-async! ->SearchServicesResponse)})
    (assoc-in app [:admin :service-listing :loading?] true))

  SearchServicesByOperator
  (process-event [_ app]
    (comm/post! "admin/transport-services-by-operator"
                {:query (get-in app [:admin :service-listing :operator-filter])
                 :published-type (get-in app [:admin :service-listing :published-filter])}
                {:on-success (tuck/send-async! ->SearchServicesResponse)})
    (assoc-in app [:admin :service-listing :loading?] true))

  SearchServicesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :service-listing] assoc
               :loading? false
               :results response))

  SearchOperators
  (process-event [_ app]
    (comm/post! "admin/transport-operators"
                {:query (get-in app [:admin :operator-list :operator-filter])}
                {:on-success (tuck/send-async! ->SearchOperatorResponse)})
    (assoc-in app [:admin :operator-list :loading?] true))

  SearchOperatorResponse
  (process-event [{response :response} app]
    (update-in app [:admin :operator-list] assoc
               :loading? false
               :results response))

  UpdateInterfaceFilters
  (process-event [{filter :filter} app]
    (update-in app [:admin :interface-list :filters] merge filter))

  SearchInterfaces
  (process-event [_ app]
    (comm/post! "admin/interfaces" (form/without-form-metadata
                                     (get-in app [:admin :interface-list :filters]))
                {:on-success (tuck/send-async! ->SearchInterfacesResponse)})
    (assoc-in app [:admin :interface-list :loading?] true))

  SearchInterfacesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :interface-list] assoc
               :loading? false
               :results response))

  UpdateInterfaceRadioFilter
  (process-event [{f :radio-filter} app]
    (case f
      :all (update-in app [:admin :interface-list :filters] assoc
                      :import-error false
                      :db-error false
                      :no-interface false)
      :no-interface (update-in app [:admin :interface-list :filters] assoc
                               :import-error false
                               :db-error false
                               :no-interface true)
      :db-error (update-in app [:admin :interface-list :filters] assoc
                           :import-error false
                           :db-error true
                           :no-interface false)
      :import-error (update-in app [:admin :interface-list :filters] assoc
                               :import-error true
                               :db-error false
                               :no-interface false)
      ;; default
      app))

  GetInterfaceDownloadsResponse
  (process-event [{response :response interface-id :interface-id} app]
    (-> app
        ;; Mark selected interface as open
        (assoc-in [:admin :interface-list :selected-interface-id] interface-id)
        ;; Loading ended
        (assoc-in [:admin :interface-list :loading-tab?] false)
        ;; Delete old data
        (update-in [:admin :interface-list :results] (fn [results]
                                                       (filter
                                                         #(not= interface-id (:interface-id %))
                                                         results)))
        ;; Add response data
        (update-in [:admin :interface-list :results] #(concat % response))))

  ;; Open interface list and fetches all download data from the server
  GetInterfaceDownloads
  (process-event [{interface-id :interface-id} app]
    (comm/get! (str "admin/list-interface-downloads/" interface-id)
               {:on-success (tuck/send-async! ->GetInterfaceDownloadsResponse interface-id)
                :on-failure (tuck/send-async! ->ServerError)})
    (-> app
        (assoc-in [:admin :interface-list :selected-interface-id] interface-id)
        (assoc-in [:admin :sea-routes :loading-tab?] true)))

  CloseDownloadList
  (process-event [_ app]
    (assoc-in app [:admin :interface-list :selected-interface-id] nil))

  OpenInterfaceErrorModal
  (process-event [{interface :interface} app]
    (assoc-in app [:admin :interface-list :error-modal] interface))

  CloseInterfaceErrorModal
  (process-event [_ app]
    (assoc-in app [:admin :interface-list :error-modal] nil))

  OpenOperatorModal
  (process-event [{interface :interface} app]
    (assoc-in app [:admin :interface-list :operator-modal] interface))

  CloseOperatorModal
  (process-event [_ app]
    (assoc-in app [:admin :interface-list :operator-modal] nil))

  UpdateSeaRouteFilters
  (process-event [{filter :filter} app]
    (update-in app [:admin :sea-routes] assoc :filters filter))

  SearchSeaRoutes
  (process-event [_ app]
    (comm/post! "admin/sea-routes" (get-in app [:admin :sea-routes :filters])
                {:on-success (tuck/send-async! ->SearchSeaRoutesResponse)})
    (assoc-in app [:admin :sea-routes :loading?] true))

  SearchSeaRoutesResponse
  (process-event [{response :response} app]
    (update-in app [:admin :sea-routes] assoc
               :loading? false
               :results response))

  UpdateNetexFilters
  (process-event [{filter :filter} app]
    (update-in app [:admin :netex] assoc :filters filter))

  SearchNetexConversions
  (process-event [_ app]
    (comm/post! "admin/netex" (get-in app [:admin :netex :filters])
                {:on-success (tuck/send-async! ->SearchNetexConversionsResponse)
                 :on-failure (tuck/send-async! ->ServerError)})
    (assoc-in app [:admin :netex :loading?] true))

  SearchNetexConversionsResponse
  (process-event [{response :response} app]
    (update-in app [:admin :netex] assoc
               :loading? false
               :results response))

  FetchCompanyCsvsResponse
  (process-event [{response :response} app]
    (update-in app [:admin :company-csv] assoc
               :loading? false
               :results response))

  FetchCompanyCsvs
  (process-event [_ app]
    (comm/get! "admin/company-csvs"
               {:on-success (tuck/send-async! ->FetchCompanyCsvsResponse)
                :on-failure (tuck/send-async! ->ServerError)})
    (assoc-in app [:admin :company-csv :loading?] true))

  OpenValidationWarningModal
  (process-event [{warning :warning} app]
    (-> app
      (assoc-in [:admin :company-csv :validation-warning] warning)
      (assoc-in [:admin :company-csv :open-validation-warning?] true)))

  CloseValidationWarningModal
  (process-event [_ app]
    (-> app
      (assoc-in [:admin :company-csv :validation-warning] nil)
      (assoc-in [:admin :company-csv :open-validation-warning?] true)))

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
          (assoc :flash-message (tr [:common-texts :delete-service-success])
                 :services-changed? true))))

  FailedDeleteTransportServiceResponse
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :delete-service-error])))

  ChangeAdminTab
  (process-event [{tab :tab} app]
    (assoc-in app [:admin :tab :admin-page] tab))

  ChangeRedirectTo
  (process-event [{new-page :new-page} app]
    (assoc app :redirect-to new-page))

  OpenDeleteOperatorModal
  (process-event [{id :id} app]
    (update-operator-by-id
      app id
      assoc :show-delete-modal? true
      :ensure-id nil))

  CancelDeleteOperator
  (process-event [{id :id} app]
    (update-operator-by-id
      app id
      dissoc :show-delete-modal?))

  ConfirmDeleteOperator
  (process-event [{id :id} app]
    (when (= id (int (:ensured-id (get-search-result-operator-by-id app id))))
      (comm/post! "admin/transport-operator/delete" {:id id}
                  {:on-success (tuck/send-async! ->DeleteOperatorResponse)
                   :on-failure (tuck/send-async! ->DeleteOperatorResponseFailed)}))
    app)

  DeleteOperatorResponse
  (process-event [{response :response} app]
    (let [filtered-map (filter #(not= (::t-operator/id %) (int response)) (get-in app [:admin :operator-list :results]))]
      (-> app
          (assoc-in [:admin :operator-list :results] filtered-map)
          (assoc :flash-message "Palveluntuottaja poistettu onnistuneesti."
                 :operators-changed? true))))

  DeleteOperatorResponseFailed
  (process-event [{response :response} app]
    (assoc app :flash-message-error "Palveluntuottajan poistaminen epäonnistui"))

  EnsureServiceOperatorId
  (process-event [{id :id ensured-id :ensured-id} app]
    (update-operator-by-id
      app id
      assoc :ensured-id ensured-id))

  EditTransportOperator
  (process-event [{business-id :business-id} app]
    (comm/get! (str "admin/user-operators-by-business-id/" business-id)
               {:on-success (tuck/send-async! ->EditTransportOperatorResponse)
                :on-failure (tuck/send-async! ->ServerError)})
    app)

  EditTransportOperatorResponse
  (process-event [{response :response} app]
    (routes/navigate! :transport-operator {:id (::t-operator/id (:transport-operator (first response)))})
    (-> app
        ;; Clean up admins own services to enable operator deletion.
        (dissoc :transport-service-vector)
        (assoc :admin-transport-operators response)))

  ToggleAddMemberDialog
  (process-event [{id :id} app]
    (let [show? (:show-add-member-dialog? (get-search-result-operator-by-id app id))]
      (update-operator-by-id
        app id
        assoc :show-add-member-dialog? (not show?))))

  ChangeTab
  (process-event [{tab-value :tab-value} app]
    (assoc-in app [:admin :tab :admin-page] tab-value)))

(defmethod routes/on-navigate-event :admin [{params :params}]
  (admin-validation/->LoadValidationServices))

(defn format-interface-content-values [value-array]
  (let [data-content-value #(tr [:enums ::t-service/interface-data-content %])
        value-str (str/join ", " (map #(data-content-value (keyword %)) value-array))
        return-value (text/maybe-shorten-text-to 45 value-str)]
    return-value))


(defn download-csv [filename content]
  (let [mime-type (str "text/csv;charset=" (.-characterSet js/document))
        blob (new js/Blob
                  (clj->js [content])
                  (clj->js {:type mime-type}))]
    (js/saveAs blob filename)))

(define-event DownloadInterfacesCSV []
  {:path [:admin :interface-list :results]}
  (->> (concat
         [["Palveluntuottaja" "Sisältö" "Tyyppi" "Rajapinta" "Viimeisin käsittely"]]
         (map (juxt :operator-name
                    (comp #(str "\"" (format-interface-content-values %) "\"") :data-content)
                    (comp #(str "\"" (str/join "," %) "\"") :format)
                    :url
                    (comp time/format-timestamp-for-ui :imported))
              app))
       csv/write-csv
       (download-csv "rajapinnat.csv"))
  app)
