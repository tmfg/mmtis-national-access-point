(ns ote.communication
  "Communication with OTE server. Wrappers for AJAX calls and helpers for URL paths."
  (:require [ajax.core :as ajax :refer [GET POST DELETE]]
            [cognitect.transit :as t]
            [ote.ui.nprogress :as progress]
            [ote.transit :as transit]))

(def query-counter (atom 0))

(defonce base-url (atom ""))

(defn set-base-url! [url]
  (reset! base-url url))

(defn transit-request-format []
  (ajax/transit-request-format transit/write-options))

(defn transit-response-format []
  (ajax/transit-response-format {:reader (t/reader :json transit/read-options)
                                 :raw    true}))

(defn- check-progress! []
  (when (zero? @query-counter)
    (progress/done)))

(defn- response-handler! [handler]
  (fn [& args]
    (progress/inc)
    (swap! query-counter dec)
    (check-progress!)
    (when handler
      (apply handler args))))

(defn- request-url [url]
  (str @base-url url))

(defn- anti-csrf-token-header []
  {"X-CSRF-Token" (.getAttribute js/document.body "data-anti-csrf-token")})

(defn get!
  "Make a GET request to the given URL.
  URL parameters can be given with the `:params` key.
  Callbacks for successful and failure are provided with `:on-success` and `:on-failure`
  keys respectively"
  [url {:keys [params on-success on-failure response-format]}]

  (progress/start)
  (swap! query-counter inc)
  (GET (request-url url)
       {:headers (anti-csrf-token-header)
        :params          params
        :cache           false
        :handler         (response-handler! on-success)
        :error-handler   (response-handler! on-failure)
        :response-format (or response-format (transit-response-format))}))

(defn post!
  "Make a POST request to the given URL.
  URL parameters can be given with the `:body` key.
  Callbacks for successful and failure are provided with `:on-success` and `:on-failure`
  keys respectively"
  [url body {:keys [on-success on-failure response-format timeout]}]

  (progress/start)
  (swap! query-counter inc)
  (POST (request-url url)
        {:headers (anti-csrf-token-header)
         :params          body
         :timeout         (or timeout 30000)
         :handler         (response-handler! on-success)
         :error-handler   (response-handler! on-failure)
         :format          (transit-request-format)
         :response-format (or response-format (transit-response-format))}))

(defn delete!
  "Make a DELETE request to the given URL.
  URL parameters can be given with the `:body` key.
  Callbacks for successful and failure are provided with `:on-success` and `:on-failure`
  keys respectively"
  [url body {:keys [on-success on-failure response-format]}]

  (progress/start)
  (swap! query-counter inc)
  (DELETE (request-url url)
        {:headers (anti-csrf-token-header)
         :params          body
         :handler         (response-handler! on-success)
         :error-handler   (response-handler! on-failure)
         :format          (transit-request-format)
         :response-format (or response-format (transit-response-format))}))

(defn upload! [url input {:keys [on-success on-failure on-progress]}]
  (let [fd (js/FormData.)
        files (.-files input)
        name (.-name input)]
    (.append fd "file" (aget files 0))
    (progress/start)
    (swap! query-counter inc)
    (POST (request-url url)
          {:headers (anti-csrf-token-header)
           :body fd
           :handler (response-handler! on-success)
           :error-handler (response-handler! on-failure)
           :response-format (transit-response-format)})))