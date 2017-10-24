(ns ote.services.viewer
  "Proxy service to load service data from URL"
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [GET routes]]
            [org.httpkit.client :as client]
            [org.httpkit.server :refer [with-channel] :as http-server]
            [taoensso.timbre :as log]))

(defn- load-resource [{params :query-params :as req}]
  (with-channel req response-ch
    (client/get (params "url")
                (fn [{:keys [status body headers] :as response}]
                  (spit "response" (pr-str response))
                  (http-server/send! response-ch
                                     {:status status
                                      :body body
                                      :headers {"Content-Type" (:content-type headers)}}
                                     true)))))


(defrecord Viewer []
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop (http/publish! http {:authenticated? false}
                                      (routes
                                       (GET "/viewer" req
                                            (load-resource req))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
