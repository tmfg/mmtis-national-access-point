(ns ote.app.controller.monitor
  (:require [ote.communication :as comm]
            [ote.ui.form :as form]
            [tuck.core :refer [define-event send-async! Event]]
            [ote.app.controller.common :refer [->ServerError]]))

;; Liikkumispalveluiden tuottamiseen osallistuvat yritykset 


(define-event MonitorReportResponse [response]
  {}
  (println "app monitor data:" (pr-str response))
  
  (assoc app
         :monitor-data response
         :monitor-loading? false))

(define-event QueryMonitorReport []
  {}
  (if-not (:monitor-loading? app)
    (do 
      (comm/get! "admin/reports/monitor-report"
                  {:on-failure (send-async! ->ServerError)
                   :on-success (send-async! ->MonitorReportResponse)})
      (assoc app :monitor-loading? true))
    app))
