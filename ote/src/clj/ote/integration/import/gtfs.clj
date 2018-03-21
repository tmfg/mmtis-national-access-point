(ns ote.integration.import.gtfs
  "GTFS file import functionality."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clj-http.client :as http-client]
            [ote.util.zip :refer [read-zip]]
            [ote.gtfs.spec :as gtfs-spec]
            [ote.gtfs.parse :as gtfs-parse]))

(defn load-zip-from-url [url]
  (with-open [in (:body (http-client/get url {:as :stream}))]
    (read-zip in)))

(defn load-gtfs [url]
  (http/transit-response
   (into {}
         (keep (fn [{:keys [name data]}]
                 (println "name:" name)
                 (when-let [gtfs-file-type (gtfs-spec/name->keyword name)]
                   (println "name: " name ", file type: " gtfs-file-type)
                   [gtfs-file-type (gtfs-parse/parse-gtfs-file gtfs-file-type data)])))
         (load-zip-from-url url))))

(defrecord GTFSImport []
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
           (http/publish! http {:authenticated? false}
                          (routes
                           (GET "/import/gtfs" {params :query-params}
                                (println "PARAMS: " params)
                                (#'load-gtfs (get params "url")))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
