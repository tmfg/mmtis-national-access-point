(ns ote.integration.export.csv
  "Export csv file."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [jeesql.core :refer [defqueries]]
            [compojure.core :refer [routes GET POST DELETE]]
            [clojure.data.csv :as csv]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [ring.util.io :as ring-io]))

(defqueries "ote/services/service_search.sql")

(defn- export-company-csv [output db service-id]
  (let [service-companies (participating-companies db {:id (Long/parseLong service-id)})
        columns [:y-tunnus :nimi]
        headers (mapv name columns)
        rows (mapv (juxt :y-tunnus :nimi) service-companies)
        data (concat [headers] rows)]

    ;; Output UTF-8 byte order mark before actual CSV contents
    (.write output (int 0xEF))
    (.write output (int 0xBB))
    (.write output (int 0xBF))

    (with-open [writer (io/writer output)]
      (csv/write-csv writer data))))

(defn- csv-export-routes
  "Routes that do not require authentication"
  [db]
  (routes
    (GET   "/export-company-csv/:service-id" [service-id]
      {:status  200
       :headers {"Content-Type"        "text/csv; charset=UTF-8"
                 "Content-Disposition" "attachment;"}
       :body    (ring-io/piped-input-stream
                  (fn [output]
                    (export-company-csv output db service-id)))})))

(defrecord CSVExport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
      [(http/publish! http {:authenticated? false} (csv-export-routes db))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
