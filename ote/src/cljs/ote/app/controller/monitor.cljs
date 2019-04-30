(ns ote.app.controller.monitor
  (:require [ote.communication :as comm]
            [ote.ui.form :as form]
            [tuck.core :refer [define-event send-async! Event]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.routes :as routes]))

(define-event MonitorReportResponse [response]
  {}
  (-> app
      (assoc-in [:admin :monitor :monitor-data] response)
      (assoc-in [:admin :monitor :monitor-loading?] false)
      (assoc-in [:admin :monitor :report-type] :tertile)))


(define-event QueryMonitorReport []
  {}
  (if-not (get-in app [:admin :monitor :monitor-loading?])
    (do 
      (comm/get! "admin/reports/monitor-report"
                  {:on-failure (send-async! ->ServerError)
                   :on-success (send-async! ->MonitorReportResponse)})
      (assoc-in app [:admin :monitor :monitor-loading?] true))
    app))

(define-event ChangeReportType [report-type]
  {}
  (assoc-in app [:admin :monitor :report-type] report-type))

(defmethod routes/on-navigate-event :monitor [_]
  (->QueryMonitorReport))