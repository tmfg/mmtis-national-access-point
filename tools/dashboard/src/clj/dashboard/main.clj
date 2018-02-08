(ns dashboard.main
  (:require [compojure.core :refer [GET POST routes]]
            [compojure.route :as route]
            [org.httpkit.server :as http-server]
            [org.httpkit.client :as http-client]
            [cognitect.transit :as t]
            [cheshire.core :as cheshire])
  (:import (java.util.concurrent Executors TimeUnit))
  (:gen-class))

(def server nil)

(def dashboard (atom {}))

(def executor (Executors/newScheduledThreadPool 4))

(defn dashboard-data [req]
  {:status 200
   :headers {"Content-Type" "application/json+transit"
             "Cache-Control" "no-cache, no-store"}
   :body (with-open [out (java.io.ByteArrayOutputStream.)]
           (t/write (t/writer out :json) @dashboard)
           (str out))})

(defn dashboard-routes []
  (routes
   (GET "/dashboard" req (dashboard-data req))
   (route/resources "/")))

(defn ^{:interval 20 ;; 900
        :key :published-services}
  finap-published-service-count! []
  (-> "https://finap.fi/ote/service-search?response_format=json"
      http-client/get deref :body (cheshire/decode keyword)
      :total-service-count))

(def tasks [#'finap-published-service-count!])

(defn start-tasks []
  (doseq [t tasks
          :let [{:keys [interval key name]} (meta t)]]
    (.scheduleAtFixedRate executor
                          (fn []
                            (try
                              (swap! dashboard assoc key (t))
                              (catch Exception e
                                (println "Failed to execute task, name=" name "; interval=" interval
                                         "; exception=" (.getName (type e)) (.getMessage e)))))
                          interval interval TimeUnit/SECONDS)))

(defn start []
  (alter-var-root
   #'server
   (fn [_]
     (start-tasks)
     (http-server/run-server (dashboard-routes) {:port 3001}))) (println "Jeejee"))

(defn -main []
  (start))
