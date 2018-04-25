(ns ote.integration.import.gtfs
  "GTFS file import functionality."
  (:require [amazonica.aws.s3 :as s3]
            [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.util.zip :refer [read-zip]]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]
            [clojure.java.io :as io])
  (:import (java.io File)))

(defn load-zip-from-url [url]
  (with-open [in (:body (http-client/get url {:as :stream}))]
    (read-zip in)))

(defn load-file-from-url [url file-name]
  (with-open [body (:body (http-client/get url {:as :stream}))]
    (io/copy body (java.io.File. file-name))
    (java.io.File. file-name)))

(defn load-gtfs [url]
  (http/transit-response
   (into {}
         (keep (fn [{:keys [name data]}]
                 (when-let [gtfs-file-type (gtfs-spec/name->keyword name)]
                   [gtfs-file-type (gtfs-parse/parse-gtfs-file gtfs-file-type data)])))
         (load-zip-from-url url))))

(defn gtfs-file-name [operator-id]
  (let [new-date (java.util.Date.)
        date (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") new-date)]
    (str date "_" operator-id "_gtfs.zip")))

(defn upload-gtfs->s3 [gtfs-config url operator-id]
  (let [file-name (gtfs-file-name operator-id)
        my-file (load-file-from-url url file-name)]
    (println "**** " file-name my-file)
    (s3/put-object (:bucket  gtfs-config) file-name my-file)
    (io/delete-file file-name)
    (http/transit-response "jee")))

(defrecord GTFSImport [gtfs-config]
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
           (http/publish! http {:authenticated? false}
                          (routes
                           (GET "/import/gtfs" {params :query-params}
                                (load-gtfs (get params "url")))
                           (GET "/import/gtfss3" {params :query-params}
                             (upload-gtfs->s3 gtfs-config (get params "url") (get params "operator-id")))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
