(ns ote.app.controller.admin
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [testdouble.cljs.csv :as csv]
            [ote.localization :refer [tr]]
            [clojure.string :as str]
            [ote.util.text :as text]
            [ote.time :as time]
            [ote.app.controller.common :refer [->ServerError]]
            cljsjs.filesaverjs
            [ote.app.routes :as routes]))

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
(defrecord SearchInterfaces [])
(defrecord SearchInterfacesResponse [response])
(defrecord OpenInterfaceErrorModal [id])
(defrecord CloseInterfaceErrorModal [id])
(defrecord OpenOperatorModal [id])
(defrecord CloseOperatorModal [id])

;; Sea route tab
(defrecord UpdateSeaRouteFilters [filter])
(defrecord SearchSeaRoutes [])
(defrecord SearchSeaRoutesResponse [response])

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
(defrecord ChangeDetectionTab [tab-value])

(defn- update-operator-by-id [app id update-fn & args]
  (update-in app [:admin :operator-list :results]
             (fn [operators]
               (map #(if (= (::t-operator/id %) id)
                       (apply update-fn % args)
                       %)
                    operators))))

(defn- update-interface-by-id [app id update-fn & args]
  (update-in app [:admin :interface-list :results]
             (fn [operators]
               (map #(if (= (:interface-id %) id)
                       (apply update-fn % args)
                       %)
                    operators))))

(defn- update-user-by-id [app id update-fn & args]
  (update-in app [:admin :user-listing :results]
             (fn [operators]
               (map #(if (= (:id %) id)
                       (apply update-fn % args)
                       %)
                    operators))))


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
    (comm/post! "admin/users" (get-in app [:admin :user-listing :user-filter])
                {:on-success (tuck/send-async! ->SearchUsersResponse)})
    (assoc-in app [:admin :user-listing :loading?] true))

  ConfirmDeleteUser
  (process-event [{id :id} app]
    (if (= id (:ensured-id (get-user-by-id app id)))
      (comm/post! "admin/delete-user" {:id id}
                  {:on-success (tuck/send-async! ->ConfirmDeleteUserResponse)
                   :on-failure (tuck/send-async! ->ConfirmDeleteUserResponseFailure)})
      (.log js/console "Could not delete user! Check given id."))
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
               :results response))

  OpenDeleteUserModal
  (process-event [{id :id} app]
    (comm/post! "admin/user-operator-members" {:id id}
                {:on-success (tuck/send-async! ->OpenDeleteUserModalResponse id)})
    app)

  OpenDeleteUserModalResponse
  (process-event [{response :response id :id } app]
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
    (comm/post! "admin/business-id-report"  {:business-id-filter (get-in app [:admin :business-id-report :business-id-filter])}
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

  OpenInterfaceErrorModal
  (process-event [{id :id} app]
    (update-interface-by-id
      app id
      assoc :show-error-modal? true))

  CloseInterfaceErrorModal
  (process-event [{id :id} app]
    (update-interface-by-id
      app id
      dissoc :show-error-modal?))

  OpenOperatorModal
  (process-event [{id :id} app]
    (update-interface-by-id
      app id
      assoc :show-operator-modal? true))

  CloseOperatorModal
  (process-event [{id :id} app]
    (update-interface-by-id
      app id
      dissoc :show-operator-modal?))

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
  (process-event [{response :response} app ]
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
    (assoc-in app [:admin :tab :admin-page] tab-value))

  ChangeDetectionTab
  (process-event [{tab-value :tab-value} app]
               (assoc-in app [:admin :transit-changes :tab] tab-value)))

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

;; admin tools
(define-event UpdateHashCalculationValues [values]
  {}
  (update-in app [:admin :transit-changes :daily-hash-values] merge values))

(define-event UpdateRouteHashCalculationValues [values]
  {}
  (update-in app [:admin :transit-changes :route-hash-values] merge values))

(define-event UpdateUploadValues [values]
  {}
  (update-in app [:admin :transit-changes :upload-gtfs] merge values))

(define-event ForceDetectTransitChanges []
  {}
  (comm/post! "/transit-changes/force-detect" nil
              {:on-success #(.log js/console %)})
  app)

(define-event ForceInterfaceImport []
  {}
  (comm/post! "/transit-changes/force-interface-import" nil
              {:on-success #(.log js/console %)})
  app)

(define-event ForceHashCalculationForService []
  {}
  (comm/get! (str "/transit-changes/force-calculate-hashes/"
                  (get-in app [:admin :transit-changes :daily-hash-values :service-id]) "/"
                  (get-in app [:admin :transit-changes :daily-hash-values :package-count]))
              {:on-success #(.log js/console %)})
  app)

(define-event ForceRouteHashCalculationForService []
  {}
  (comm/get! (str "/transit-changes/force-calculate-route-hash-id/"
                  (get-in app [:admin :transit-changes :route-hash-values :service-id]) "/"
                  (get-in app [:admin :transit-changes :route-hash-values :package-count]) "/"
                  (get-in app [:admin :transit-changes :route-hash-values :route-id-type]))
             {:on-success #(.log js/console %)})
  app)

(define-event UploadResponse [response]
  {}
  (assoc app :flash-message "Paketti ladattu"))

(define-event UploadAttachment [input-html-element]
  {}
  (let [filename (.-name (first (array-seq (.-files input-html-element))))
        service-id (get-in app [:admin :transit-changes :upload-gtfs :service-id])
        date (get-in app [:admin :transit-changes :upload-gtfs :date])]
    (if (re-matches #".*\.(zip)" filename)
      (do
        (comm/upload! (str "transit-changes/upload-gtfs/" service-id "/" date) input-html-element
                      {:on-success (tuck/send-async! ->UploadResponse)
                       :on-failure (tuck/send-async! ->ServerError)})
        app)
      (->
        app
        (update-in [:admin :transit-changes :upload-gtfs]
                   #(conj (or (vec (butlast %)) [])
                          {:error (str (tr [:common-texts :invalid-file-type]) ": " filename)}))
        (assoc :flash-message-error (str (tr [:common-texts :invalid-file-type]) ": " filename))))))



(define-event LoadRouteHashServicesResponse [response]
  {}
  (assoc-in app [:admin :transit-changes :route-hash-services] response))

(define-event LoadRouteHashServices []
  {}
  (comm/get! "transit-changes/load-services-with-route-hash-id"
             {:on-success (tuck/send-async! ->LoadRouteHashServicesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event CalculateDateHasheshResponse [response]
  {}
  (.log js/console "Laskenta valmis" (pr-str response))
  app)

(define-event ForceMonthlyDayHashCalculation []
  {}
  (comm/get! "transit-changes/force-monthly-day-hash-calculation"
             {:on-success (tuck/send-async! ->CalculateDateHasheshResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event ForceDayHashCalculation []
  {}
  (comm/get! "transit-changes/force-all-day-hash-calculation"
             {:on-success (tuck/send-async! ->CalculateDateHasheshResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(defn ^:export force-detect-transit-changes []
  (->ForceDetectTransitChanges))

(defn ^:export force-interface-import []
  (->ForceInterfaceImport))
