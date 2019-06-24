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
            [ring.middleware.gzip :as gzip]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as session-cookie]
            [ote.localization :as localization]
            [ring.middleware.cookies :as cookies]
            [cheshire.core :as cheshire]
            [ote.util.fn :as ote-fn]))

(defn- serve-request [handlers req]
  (some #(% req) handlers))


(defn wrap-language [handler]
  (fn [{cookies :cookies :as req}]
    (let [cookie-lang (get-in cookies ["finap_lang" :value])
          lang (or (and (localization/supported-languages cookie-lang)
                        (keyword cookie-lang))
                   :fi)]
      (localization/with-language
        lang (handler req)))))

(defn wrap-strip-prefix [strip-prefix handler]
  (fn [{uri :uri :as req}]
    (handler (if (str/starts-with? uri strip-prefix)
                (assoc req :uri (subs uri (count strip-prefix)))
                req))))

(defn wrap-middleware [strip-prefix handler & extra-middleware]
  (gzip/wrap-gzip
   (params/wrap-params
    (cookies/wrap-cookies
     (wrap-language
      (reduce (fn [handler middleware]
                (middleware handler))
              (wrap-strip-prefix strip-prefix handler)
              (remove nil? extra-middleware)))))))


(defn wrap-security-exception [handler]
  (fn [req]
    (try
      (handler req)
      (catch SecurityException se
        (log/warn se "Security exception in " (:uri req) ", user: " (:user req))
        {:status 403
         :headers {}
         :body ""}))))

(defn wrap-session [{session :session} handler]
  (if-not session
    ;; No session config, return handler as is
    handler

    ;; Session config defined, add session and anti CSRF
    (session/wrap-session
     (anti-forgery/wrap-anti-forgery handler)
     {:store (session-cookie/cookie-store {:key (:key session)})
      :cookie-name "ote-session"
      :cookie-attrs {:http-only true}})))


(defrecord HttpServer [config handlers public-handlers]
  component/Lifecycle
  (start [{db :db :as this}]
    (let [strip-prefix (or (:strip-prefix config) "")

          ;; Handler for static resources
          resources
          (wrap-middleware strip-prefix (route/resources "/"))

          ;; Handler for routes that don't require authenticated user
          public-handler
          (wrap-middleware strip-prefix #(serve-request @public-handlers %)
                           (partial nap-users/wrap-user-info {:db db :allow-unauthenticated? true})
                           (partial nap-cookie/wrap-check-cookie (assoc (:auth-tkt config)
                                                                        :allow-unauthenticated? true))
                           (partial wrap-session config))

          ;; Handler for routes that require authentication
          handler
          (wrap-middleware strip-prefix #(serve-request @handlers %)
                           wrap-security-exception
                           (partial nap-users/wrap-user-info {:db db :allow-unauthenticated? false})
                           (partial nap-cookie/wrap-check-cookie (assoc (:auth-tkt config)
                                                                        :allow-unauthenticated? false))
                           (partial wrap-session config))]
      (assoc this ::stop
             (server/run-server
              (fn [req]
                (or (resources req)
                    (public-handler req)
                    (handler req)))
              (merge config
                     {:error-logger (fn [msg exception]
                                      (log/error exception msg))})))))
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

(def no-cache-headers  {"Cache-Control" "no-cache, no-store"})

(defn with-no-cache-headers [response]
  (update response :headers merge no-cache-headers))

(defn transit-response
  "Return the given Clojure `data` as a Transit response"
  ([data]
   (transit-response data 200))
  ([data status]
   {:status status
    :headers {"Content-Type" "application/json+transit"}
    :body (transit/clj->transit data)}))

(defn no-cache-transit-response
  "Return the given Clojure `data` as a Transit response with status code 200 and no-cache headers."
  [data]
  (with-no-cache-headers (transit-response data)))

(defn json-response [data]
  {:status 200
   :headers {"Content-Type" "application/json; charset=UTF-8"}
   :body (cheshire/encode data {:key-fn name})})

(defn geojson-response [json-data]
  {:status 200
   :headers {"Content-Type" "application/vnd.geo+json"}
   :body json-data})

(defn api-response
  "Helper for API responses that are used both by OTE app and public.
  If \"response_format\" query parameter is \"json\", returns a JSON response.
  Otherwise returns a transit response."
  [{{format "response_format"} :query-params} data]
  (let [format (and format (str/lower-case format))]
    (if (= format "json")
      (json-response data)
      (transit-response data))))

(defn transit-request
  "Parse HTTP POST body as Transit data."
  [in]
  (transit/transit->clj in))

;; Input: spec=spec which validates params, params=map to validate against spec
;; Output: http response if params are not valid, nil if params are valid.
(defn response-bad-args [spec params]
  (when-let [error-str (ote-fn/form-validation-error-str spec params)]
    (transit-response
      (str "Invalid argument(s): " error-str)
      400)))

(defrecord SslUpgrade [ip port url]
  component/Lifecycle
  (start [this]
    (assoc this ::stop
           (server/run-server
            (constantly {:status 301
                         :headers {"Location" url}})
            {:port port
             :ip (or ip "0.0.0.0")})))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
