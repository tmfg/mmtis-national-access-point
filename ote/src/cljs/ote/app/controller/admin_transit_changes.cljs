(ns ote.app.controller.admin-transit-changes
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

(define-event InitAdminDetectedChanges []
  {}
  (update-in app
             [:admin :pre-notice]
             dissoc
             :pre-notice-notify-send))

(define-event HashRecalculationsResponse [response]
  {}
  (assoc-in app [:admin :transit-changes :hash-recalculations] response))

(define-event LoadHashRecalculations []
  {}
  (comm/get! "transit-changes/hash-calculation/"
             {:on-success (tuck/send-async! ->HashRecalculationsResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event LoadCommercialServicesResponse [response]
  {}
  (assoc-in app [:admin :transit-changes :commercial-services] response))

(define-event LoadCommercialServices []
  {}
  (comm/get! "admin/commercial-services"
             {:on-success (tuck/send-async! ->LoadCommercialServicesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event LoadRouteHashServicesResponse [response]
  {}
  (assoc-in app [:admin :transit-changes :route-hash-services] response))

(define-event LoadRouteHashServices []
  {}
  (comm/get! "transit-changes/load-services-with-route-hash-id"
             {:on-success (tuck/send-async! ->LoadRouteHashServicesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(defmethod routes/on-navigate-event :admin-detected-changes [{params :params}]
  (->LoadHashRecalculations))

(defmethod routes/on-navigate-event :admin-commercial-services [_]
  (->LoadCommercialServices))

(defmethod routes/on-navigate-event :admin-route-id [_]
  (->LoadRouteHashServices))

(define-event ChangeDetectionTab [tab-value]
  {}
  (routes/navigate! (keyword tab-value))
  (assoc-in app [:admin :transit-changes :tab] tab-value))

;; admin tools
(define-event UpdateHashCalculationValues [values]
  {}
  (update-in app [:admin :transit-changes :daily-hash-values] merge values))

(define-event UpdateRouteHashCalculationValues [values]
  {}
  (update-in app [:admin :transit-changes :route-hash-values] merge values))

(define-event ServiceInterfacesResponse [response]
  {}
  (assoc-in app [:admin :transit-changes :upload-gtfs :interfaces] response))

(define-event GetServiceInterfaces []
  {}
  ;; If user gives service-id we need to fetch interfaces related to that service
  (comm/get! (str "admin/service-interfaces/" (get-in app [:admin :transit-changes :upload-gtfs :service-id]))
             {:on-success (tuck/send-async! ->ServiceInterfacesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event UpdateInterfaceServiceId [service-id]
  {}
  (assoc-in app [:admin :transit-changes :upload-gtfs :service-id] service-id))

(define-event UpdateUploadValues [values]
  {}
  (update-in app [:admin :transit-changes :upload-gtfs] merge values))

(define-event ForceDetectTransitChanges []
  {}
  (comm/post! "/transit-changes/force-detect/" nil
              {:timeout (* 60000 7)                         ;; Set timeout to 7 minutes to prevent mystical errors with large gtfs packages
               :on-success #(.log js/console %)})
  app)

(define-event SetSingleDetectionServiceId [service-id]
  {}
  (assoc-in app [:admin :transit-changes :single-detection-service-id] service-id))

(define-event SetSingleDownloadGtfsServiceId [service-id]
  {}
  (assoc-in app [:admin :transit-changes :single-download-gtfs-service-id] service-id))

(define-event DetectChangesForGivenService []
  {}
  (let [service-id (get-in app [:admin :transit-changes :single-detection-service-id])]
    ;; When service-id is not given, do not try to start detection
    (when service-id
      (comm/post! (str "transit-changes/force-detect/" service-id) nil
                  {:timeout (* 60000 7)                     ;; Set timeout to 7 minutes to prevent mystical errors with large gtfs packages
                   :on-success (tuck/send-async! ->SetSingleDetectionServiceId service-id)}))
    app))

(define-event ForceInterfaceImportForGivenServiceSuccess [response]
  {}
  (-> app
      (assoc-in [:admin :transit-changes :single-download-gtfs-service-response] nil)
      (assoc :flash-message (str "GTFS Paketin lataus onnistui " response))))

(define-event ForceInterfaceImportForGivenServiceFailure [response]
  {}
  (-> app
      (assoc-in [:admin :transit-changes :single-download-gtfs-service-response]
                {:status :error
                 :msg response})
      (assoc :flash-message-error (:response response))))

(define-event ForceInterfaceImportForGivenService []
  {}
  (let [service-id (get-in app [:admin :transit-changes :single-download-gtfs-service-id])]
    (comm/post! (str "/transit-changes/force-interface-import/" service-id) nil
                {:timeout (* 60000 7)                       ;; Set timeout to 7 minutes to prevent mystical errors with large gtfs packages
                 :on-success (tuck/send-async! ->ForceInterfaceImportForGivenServiceSuccess)
                 :on-failure (tuck/send-async! ->ForceInterfaceImportForGivenServiceFailure)})
    app))

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
        interface-id (::t-service/id (get-in app [:admin :transit-changes :upload-gtfs :interface-id]))
        date (get-in app [:admin :transit-changes :upload-gtfs :date])]
    (if (re-matches #".*\.(zip)" filename)
      (do
        (comm/upload! (str "transit-changes/upload-gtfs/" service-id "/" interface-id "/" date) input-html-element
                      {:on-success (tuck/send-async! ->UploadResponse)
                       :on-failure (tuck/send-async! ->ServerError)})
        app)
      (->
        app
        (update-in [:admin :transit-changes :upload-gtfs]
                   #(conj (or (vec (butlast %)) [])
                          {:error (str (tr [:common-texts :invalid-file-type]) ": " filename)}))
        (assoc :flash-message-error (str (tr [:common-texts :invalid-file-type]) ": " filename))))))

(define-event CalculateDateHasheshResponse [response]
  {}
  (.log js/console "Laskenta valmis" (pr-str response))
  app)

(define-event CalculateDayHash [scope future]
  {}
  (comm/get! (str "transit-changes/hash-calculation/" scope "/" future)
             {:on-success (tuck/send-async! ->CalculateDateHasheshResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event ToggleCommercialTrafficResponse [response]
  {}
  ;; Do nothing on success
  (.log js/console "Response " (pr-str response))
  app)

(define-event ToggleCommercialTraffic [service-id commercial?]
  {}
  ;; Post changed commercial? status to backend
  (comm/post! "admin/toggle-commercial-services"
              {:id service-id
               :commercial? (not commercial?)}
              {:on-success (tuck/send-async! ->ToggleCommercialTrafficResponse)
               :on-failure (tuck/send-async! ->ServerError)})
  ;; Update front end by default. If server doesn't respond correctly front end is still updated... which is nice
  (update-in app [:admin :transit-changes :commercial-services]
             (fn [services]
               services
               (map (fn [service]
                      (if (= (:service-id service) service-id)
                        (assoc service :commercial? (not commercial?))
                        service))
                    services))))

(define-event ResetHashRecalculations []
  {}
  (comm/delete! (str "transit-changes/hash-calculation") {}
                {:on-success (tuck/send-async! ->LoadHashRecalculations)})
  app)


(define-event CSVLoadSuccess [response]
  {}
  (-> app
      (update-in [:admin :exceptions-from-csv] dissoc :error)
      (assoc-in [:admin :exceptions-from-csv :exceptions] response)
      (assoc-in [:admin :exceptions-from-csv :status] 200)))

(define-event CSVLoadFailure [response]
  {}
  (-> app
      (update-in [:admin :exceptions-from-csv] dissoc :exceptions)
      (assoc-in [:admin :exceptions-from-csv :error] response)
      (assoc-in [:admin :exceptions-from-csv :status] 500)))

(define-event StartCSVLoad []
  {}
  (comm/get! "admin/csv-fetch"
             {:on-success (tuck/send-async! ->CSVLoadSuccess)
              :on-failure (tuck/send-async! ->CSVLoadFailure)})
  app)

(define-event SendPreNoticeSuccess [response]
  {}
  (assoc-in app [:admin :pre-notice :pre-notice-notify-send] :success))

(define-event SendPreNoticeFailure [response]
  {}
  (assoc-in app [:admin :pre-notice :pre-notice-notify-send] response))

(define-event SendPreNotices []
  {}
  (comm/get! "/admin/pre-notices/notify"
             {
              :on-success (tuck/send-async! ->SendPreNoticeSuccess)
              :on-failure (tuck/send-async! ->SendPreNoticeFailure)})
  app)

(define-event GeneralTroubleshootingLog []
  {}
  (comm/get! "admin/general-troubleshooting-log"
             {:on-success #(.log js/console "response = " %)
              :on-failure #(.log js/console "response = " %)})
  app)

(defn ^:export force-detect-transit-changes []
  (->ForceDetectTransitChanges))
