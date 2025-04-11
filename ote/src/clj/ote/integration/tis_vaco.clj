(ns ote.integration.tis-vaco
  "Integration to Travel Information Service's Validation-Conversion REST API"
  (:require [camel-snake-kebab.core :as csk]
            [cheshire.core :as cheshire]
            [clj-http.client :as http-client]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [java-time :as jt]
            [ote.db.transport-operator :as t-operator]
            [ote.util.throttle :as throttle]
            [specql.core :as specql]
            [taoensso.timbre :as log]))

(def ^:private auth-data (atom {}))

(defn ^:private get-token [tenant-id client-id scope client-secret]
  {:url    (str "https://login.microsoftonline.com/" tenant-id "/oauth2/v2.0/token")
   :params {"grant_type"    "client_credentials"
            "client_id"     client-id
            "scope"         scope
            "client_secret" client-secret}})

(defn ^:private token-expired?
  "Checks if the given token timestamp is about to expire. There is some leeway to account for potential network lag and
  other in-transit latencies."
  [ts]
  (or (nil? ts)
      (jt/before?
        ts
        (jt/minus (jt/local-date-time) (jt/minutes 1)))))

(defn ^:private update-expired-token
  "Handles access token caching to avoid nuking Azure AD endpoints. Uses token expiration as refresh hint. See also
  [[token-expired?]]"
  [auth-data {:keys [tenant-id client-id scope client-secret]}]
  (if (token-expired? (:expires-in auth-data))
    (let [{:keys [url params]}          (get-token tenant-id client-id scope client-secret)
          {:keys [status headers body]} (http-client/post url {:form-params params})]
      (-> (cheshire/parse-string body csk/->kebab-case-keyword)
          (update :expires-in (fn [offset] (jt/plus (jt/local-date-time) (jt/seconds (- offset 10)))))))
    auth-data))

(defn ^:private fetch-business-id [db operator-id]
  ; TODO
  (-> (specql/fetch db ::t-operator/transport-operator
                      #{::t-operator/business-id}
                      {::t-operator/id operator-id})
        first
        ::t-operator/business-id))

(defn ^:private find-package [db interface-id package-id]
  (when interface-id
    (first
      (specql/fetch db :gtfs/package
                    (specql/columns :gtfs/package)
                    {:gtfs/id                                package-id
                     :gtfs/external-interface-description-id interface-id
                     :gtfs/deleted?                          false}
                    {::specql/order-by        :gtfs/created
                     ::specql/order-direction :descending
                     ::specql/limit           1}))))

(def ^:private rate-limiter-hit-delay
  "Returns in milliseconds the wait time for single call based on rate limiter configuration."
  (let [timespan         (* 60 60 1000)  ; total timespan for calls is 60 minutes
        allowed-requests 500           ; how many requests allowed per timespan
        delay-per-call   (/ timespan allowed-requests)]
    delay-per-call))

(defn ^:private api-call
  "Adds common headers, handles authentication etc. for TIS VACO API calls. Returns nil on failure to allow punning."
  ([config call url body params] (api-call config call url body params nil nil))
  ([config call url body params db package-id]
   (throttle/with-throttle-ms
     rate-limiter-hit-delay
     (let [endpoint (if (str/starts-with? url (:api-base-url config))
                      url
                      (str (:api-base-url config) url))]
       (try
         (let [{:keys [access-token]} (swap! auth-data update-expired-token config)
               {:keys [status headers body]} (call endpoint
                                                   (merge
                                                     {:headers {"User-Agent" "Fintraffic FINAP / 0.1"
                                                                "Authorization" (str "Bearer " access-token)}}
                                                     ; this is an actual API which uses HTTP statuses for a reason,
                                                     ; so allow all non 5xx to be handled properly
                                                     (merge {:unexceptional-status #(<= 200 % 499)} params)
                                                     (when body {:body (when body (cheshire/generate-string body))})))]
           (log/info (str "API call to " endpoint " returned " status))
           body)
         (catch Exception e
           (log/warn e (str "Failed API call " endpoint))
           (when (and db package-id)
             ;; If database and package-id is provided, log the error to the database
             (specql/update! db :gtfs/package {:gtfs/tis_polling_error e} {:gtfs/id package-id}))
           nil))))))

(defn api-queue-create
  ([config payload] (api-queue-create config payload nil nil))
  ([config payload db package-id]
   (some-> (api-call config http-client/post "/api/queue" payload {:content-type :json} db package-id)
           (cheshire/parse-string))))

(defn api-fetch-entry
  [config entry-id]
  (or (some-> (api-call config http-client/get (str "/api/queue/" entry-id) nil {:content-type :json})
              (cheshire/parse-string))
      (do
        (log/info (str "No fetch-entry result available for " entry-id))
        {})))

(defn read-to-memory [is]
  (let [baos (java.io.ByteArrayOutputStream.)]
    (io/copy is baos)
    (java.io.ByteArrayInputStream. (.toByteArray baos))))

(defn api-download-file
  [config href]
  (let [{:keys [access-token]} (swap! auth-data update-expired-token config)
        body (-> (http-client/get href {:headers {"User-Agent"    "Fintraffic FINAP / 0.1"
                                                  "Authorization" (str "Bearer " access-token)}
                                        :as      :stream})
                 :body
                 (read-to-memory))]
    body))

(defn queue-entry
  [db
   ; :tis-vaco config root from config.edn
   config
   ; interface
   {:keys [url
           operator-id
           operator-name
           ts-id
           last-import-date
           license
           id
           data-content]}
   ; conversion-meta
   {:keys [gtfs-file
           gtfs-filename
           gtfs-basename
           external-interface-description-id
           external-interface-data-content
           service-id
           package-id
           operator-name
           contact-email]}
   ; payload
   {:keys [format validations conversions]}]
  (let [context   (str "FINAP (" operator-id "/" service-id "/" external-interface-description-id ")")
        package   (find-package db id package-id)
        new-entry (api-queue-create config {:url         url
                                            :format      format
                                            :businessId  (fetch-business-id db operator-id)
                                            :etag        (when package (:gtfs/etag package))
                                            :name        (str operator-name " / GTFS / " context)
                                            :validations (or validations [])
                                            :conversions (or conversions [])
                                            :context     (str operator-id "/" service-id "/" id)
                                            :metadata    (merge {:caller        "FINAP"
                                                                 :operator-id   operator-id
                                                                 :operator-name operator-name
                                                                 :service-id    service-id
                                                                 :interface-id  id
                                                                 :package-id    package-id}
                                                                (when contact-email {:contact-email contact-email}))}
                                    db package-id)
        ;; Temporary logging
        _ (log/info (str "API call returned new-entry: " (pr-str new-entry)))]
    (when new-entry
      (try
        ;; Add vaco information to the GTFS package when query is made.
        (specql/update! db :gtfs/package
                        {:gtfs/tis-entry-public-id (get-in new-entry ["data" "publicId"])
                         :gtfs/tis-magic-link (get-in new-entry ["links" "refs" "magic" "href"])
                         :gtfs/tis_submit_completed (java.sql.Timestamp. (System/currentTimeMillis))}
                        {:gtfs/id (:gtfs/id package)})
        new-entry
        (catch Exception e
          (log/warn e "Failed to update GTFS package with TIS VACO entry reference"))))))

(defn fetch-public-data
  "Returns publicly available data about the NeTEx `interface` if any. Data should be used for rendering
  badges/inter-service links and such.

  Returns `nil` if public data isn't available for any reason."
  [db config interface-id package-id]
  (when-let [package (find-package db interface-id package-id)]
    (-> package
        (select-keys [:gtfs/tis-entry-public-id :gtfs/tis-success :gtfs/tis-complete :gtfs/tis-magic-link])
        (assoc :api-base-url (get-in config [:tis-vaco :api-base-url])))))