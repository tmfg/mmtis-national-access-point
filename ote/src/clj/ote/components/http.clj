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
            [ring.middleware.params :as params]
            [ring.middleware.gzip :as gzip]))

(defn- serve-request [handlers req]
  (some #(% req) handlers))

(defn- parse-accept-language [accept-language]
  ;; Parse accept language and return a vector of language codes in the order they appear.
  ;; Doesn't take weights into account and removes other locale information (like 'en-US' vs 'en-GB').
  (vec
   (distinct
    (for [language (str/split accept-language #",")
          :when (>= (count language) 2)
          :let [lang (subs language 0 2)]]
      lang))))

(defn wrap-accept-language [handler]
  (fn [{{accept-language "accept-language"} :headers :as req}]
    (handler
     (if-not accept-language
       ;; No "Accept-Language" header specified, return request as is
       req
       ;; Parse header and assoc a :accept-language key to the request
       (assoc req :accept-language (parse-accept-language accept-language))))))

(defn wrap-strip-prefix [strip-prefix handler]
  (fn [{uri :uri :as req}]
    (handler (if (str/starts-with? uri strip-prefix)
                (assoc req :uri (subs uri (count strip-prefix)))
                req))))

(defn wrap-middleware [strip-prefix handler & extra-middleware]
  (gzip/wrap-gzip
   (params/wrap-params
    (wrap-accept-language
     (reduce (fn [handler middleware]
               (middleware handler))
             (wrap-strip-prefix strip-prefix handler)
             extra-middleware)))))

(defrecord HttpServer [config handlers public-handlers]
  component/Lifecycle
  (start [{db :db :as this}]
    (let [strip-prefix (or (:strip-prefix config) "")

          ;; Handler for static resources
          resources
          (wrap-middleware strip-prefix (route/resources "/"))

          ;; Handler for routes that don't require authenticated user
          public-handler
          (wrap-middleware strip-prefix #(serve-request @public-handlers %))

          ;; Handler for routes that require authentication
          handler
          (wrap-middleware strip-prefix #(serve-request @handlers %)
                           (partial nap-users/wrap-user-info db)
                           (partial nap-cookie/wrap-check-cookie (:auth-tkt config)))]
      (assoc this ::stop
             (server/run-server
              (fn [req]
                (or (public-handler req)
                    (resources req)
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

(defn no-cache-transit-response
  "Return the given Clojure `data` as a Transit response with status code 200 and no-cache headers."
  [data]
  (update (transit-response data) :headers merge {"Cache-Control" "no-cache, no-store"}))

(defn transit-request
  "Parse HTTP POST body as Transit data."
  [in]
  (transit/transit->clj in))
