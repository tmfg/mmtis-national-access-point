(ns ote.components.service
  "Helper for defining service components"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [routes]]
            [ring.util.io :as ring-io]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn export-csv [output data]
  ;; Output UTF-8 byte order mark before actual CSV contents
  (.write output (int 0xEF))
  (.write output (int 0xBB))
  (.write output (int 0xBF))

  (with-open [writer (io/writer output)]
    (csv/write-csv writer data)))

(defmacro define-service-component
  "Define a service component that publishes HTTP routes."
  [component-name options & paths]
  (let [http (gensym "http")]
    `(defrecord ~component-name [~@(:fields options)]
       component/Lifecycle
       (start [{~'db :db ~http :http :as this#}]
         (let [~(or (:dependencies options) (gensym "_")) this#]
           (assoc this#
                  :ote.components.service/stop-routes
                  [~@(for [[unauthenticated? paths]
                           (group-by (comp boolean :unauthenticated meta)
                                     paths)]
                       `(http/publish!
                         ~http {:authenticated? ~(not unauthenticated?)}
                         (routes
                          ~@(for [p paths
                                  :let [response-format (or (:format (meta p)) :raw)
                                        filename (:filename (meta p))]]
                              (case response-format
                                :raw p
                                :transit (let [[method path bindings & body] p]
                                           `(~method ~path ~bindings
                                              (http/no-cache-transit-response
                                                (do ~@body))))
                                :csv (let [[method path bindings & body] p]
                                       `(~method ~path ~bindings
                                          {:status 200
                                           :headers {"Content-Type" "text/csv; charset=UTF-8"
                                                     "Content-Disposition" (str "attachment;" ~(when filename `(str " filename=" ~filename)))}
                                           :body (ring-io/piped-input-stream
                                                   (fn [output#]
                                                     (export-csv output#
                                                                 (do ~@body))))})))))))])))

       (stop [{stop-routes# :ote.components.service/stop-routes :as this#}]
         (doseq [stop-route# stop-routes#]
           (stop-route#))
         (dissoc this# :ote.components.service/stop-routes)))))
