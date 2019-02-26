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

(define-event HashRecalculationsResponse [response]
  {}
  (assoc-in app [:admin :transit-changes :hash-recalculations] response))

(define-event LoadHashRecalculations []
  {}
  (comm/get! "transit-changes/hash-recalculations"
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
  (->LoadHashRecalculations ))

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

(define-event CalculateDateHasheshResponse [response]
  {}
  (.log js/console "Laskenta valmis" (pr-str response))
  app)

(define-event ForceMonthlyDayHashCalculation []
  {}
  (comm/get! "transit-changes/force-monthly-day-hash-calculation/false"
             {:on-success (tuck/send-async! ->CalculateDateHasheshResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event ForceMonthlyFutureDayHashCalculation []
  {}
  (comm/get! "transit-changes/force-monthly-day-hash-calculation/true"
             {:on-success (tuck/send-async! ->CalculateDateHasheshResponse)
              :on-failure (tuck/send-async! ->ServerError)})

  app)

(define-event ForceDayHashCalculation []
  {}
  (comm/get! "transit-changes/force-all-day-hash-calculation/false"
             {:on-success (tuck/send-async! ->CalculateDateHasheshResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event ForceFutureDayHashCalculation []
  {}
  (comm/get! "transit-changes/force-all-day-hash-calculation/true"
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

(defn ^:export force-detect-transit-changes []
  (->ForceDetectTransitChanges))

(defn ^:export force-interface-import []
  (->ForceInterfaceImport))
