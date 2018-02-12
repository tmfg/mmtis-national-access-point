(ns dashboard.main
  (:require [compojure.core :refer [GET POST routes]]
            [compojure.route :as route]
            [org.httpkit.server :as http-server]
            [cognitect.transit :as t]

            [dashboard.data.finap-services :as data-finap-services]
            [dashboard.data.jenkins :as data-jenkins]
            [dashboard.data.cloudwatch :as data-cloudwatch])
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

(def tasks [{:interval 900 :key :published-services
             :task #'data-finap-services/fetch-published-service-count}
            {:interval 10 :key :jenkins
             :task #'data-jenkins/jobs}
            {:interval 60 :key :db-load
             :task #'data-cloudwatch/finap-db-load}])

(defn start-tasks []
  (doseq [{:keys [interval key task]} tasks]
    (.scheduleAtFixedRate executor
                          (fn []
                            (try
                              (swap! dashboard assoc key (task))
                              (catch Exception e
                                (println "Failed to execute task"
                                         "; name=" (:name (meta task))
                                         "; interval=" interval
                                         "; exception=" (.getName (type e)) (.getMessage e)))))
                          0 interval TimeUnit/SECONDS)))

(defn start []
  (alter-var-root
   #'server
   (fn [_]
     (start-tasks)
     (http-server/run-server (dashboard-routes) {:port 3001})))
  (println "NAPOTE dashboard is up: http://localhost:3001/index.html"))

(defn -main []
  (start))
