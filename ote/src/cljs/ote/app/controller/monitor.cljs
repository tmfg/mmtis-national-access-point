(ns ote.app.controller.monitor
  (:require [ote.communication :as comm]
            [ote.ui.form :as form]
            [tuck.core :refer [define-event send-async! Event]]
            [ote.app.controller.common :refer [->ServerError]]))

;; Liikkumispalveluiden tuottamiseen osallistuvat yritykset 


(define-event QueryMonitorReport []
  {}
  (comm/post! "admin/monitor-report" {}
              {:on-failure (send-async! ->ServerError)
               :on-success (tuck/send-async! ->MonitorReportRespnse)})
  app)
