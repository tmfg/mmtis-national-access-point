(ns ote.config.netex-config
  "Configuration values for the feature")

(defn config []
  {:chouette {:work-dir "work/"
              :input-report-file "inputReport.json"
              :output-report-file "outputReport.json"
              :validation-report-file "validation_report.json"}})
