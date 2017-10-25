(ns ote.components.http
  "HTTP-kit server"
  (:require [org.httpkit.server :as server]
            [com.stuartsierra.component :as component]
            [compojure.route :as route]
            [ote.transit :as transit]
            [ote.nap.cookie :as nap-cookie]
            [ote.nap.users :as nap-users]
            [taoensso.timbre :as log]
            [clojure.string :as str]
            [ring.middleware.params :as params]))

(defn- serve-request [handlers req]
  (some #(% req) handlers))

(defn wrap-strip-prefix [strip-prefix handler]
  (fn [{uri :uri :as req}]
    (handler (if (str/starts-with? uri strip-prefix)
                (assoc req :uri (subs uri (count strip-prefix)))
                req))))

(defrecord HttpServer [config handlers public-handlers]
  component/Lifecycle
  (start [{db :db :as this}]
    (let [strip-prefix (or (:strip-prefix config) "")

          ;; Handler for static resources
          resources
          (params/wrap-params
           (wrap-strip-prefix
            strip-prefix
            (route/resources "/")))

          ;; Handler for routes that don't require authenticated user
          public-handler
          (params/wrap-params
           (wrap-strip-prefix strip-prefix #(serve-request @public-handlers %)))

          ;; Handler for routes that require authentication
          handler #(serve-request @handlers %)
          handler
          (if-let [auth-tkt (:auth-tkt config)]
            (nap-cookie/wrap-check-cookie
             auth-tkt
             (nap-users/wrap-user-info
              db
              (wrap-strip-prefix strip-prefix handler)))
            handler)]
      (assoc this ::stop
             (server/run-server
              (fn [req]
                (or (resources req)
                    (public-handler req)
                    (handler req)))
              config))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn http-server
  "Create an HTTP server component with the given `config`."
  [config]
  (->HttpServer config (atom []) (atom [])))

(defn publish!
  "Publish a new Ring `handler` to the HTTP-server.
  Requests are handled by trying each published handler in the order they are published until
  one of the handlers returns a response.

  Handlers must return `nil` for requests they aren't prepared to handle.

  Returns a 0-arity function that will remove this `handler` when called.

  An optional options map can be passed as the second argument.
  The following options are supported:

  :authenticated?   If user must be authenticated to be able to access this
                    handler. Defaults to true. Set to false for publicly
                    accessible services that don't require user info."
  ([http handler]
   (publish! http {:authenticated? true} handler))
  ([http {:keys [authenticated?] :as options} handler]
   (let [handlers (if authenticated?
                    (:handlers http)
                    (:public-handlers http))]
     (swap! handlers conj handler)
     #(swap! handlers
             (fn [handlers]
               (filterv (partial not= handler) handlers))))))

(defn transit-response
  "Return the given Clojure `data` as a Transit response with status code 200."
  [data]
  {:status 200
   :headers {"Content-Type" "application/json+transit"}
   :body (transit/clj->transit data)})

(defn transit-request
  "Parse HTTP POST body as Transit data."
  [in]
  (transit/transit->clj in))
