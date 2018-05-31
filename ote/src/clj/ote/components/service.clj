(ns ote.components.service
  "Helper for defining service components"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [routes]]))

(defmacro define-service-component
  "Define a service component that publishes HTTP routes."
  [component-name options & paths]
  (let [http (gensym "http")]
    `(defrecord ~component-name []
       component/Lifecycle
       (start [{~'db :db ~http :http :as this#}]
         (assoc this#
                :ote.components.service/stop-routes
                [~@(for [[unauthenticated? paths]
                         (group-by (comp boolean :unauthenticated meta)
                                   paths)]
                     `(http/publish!
                       ~http {:authenticated? ~(not unauthenticated?)}
                       (routes
                        ~@(for [p paths
                                :let [response-format (or (:format (meta p)) :raw)]]
                            (case response-format
                              :raw p
                              :transit (let [[method path bindings & body] p]
                                         `(~method ~path ~bindings
                                           (http/no-cache-transit-response
                                            (do ~@body)))))))))]))

       (stop [{stop-routes# :ote.components.service/stop-routes :as this#}]
         (doseq [stop-route# stop-routes#]
           (stop-route#))
         (dissoc this# :ote.components.service/stop-routes)))))
