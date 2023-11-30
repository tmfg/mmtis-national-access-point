(ns ote.integration.tis-vaco
  "Integration to Travel Information Service's Validation-Conversion REST API"
  (:require [camel-snake-kebab.core :as csk]
            [cheshire.core :as cheshire]
            [clj-http.client :as http-client]
            [java-time :as jt]
            [ote.db.transport-operator :as t-operator]
            [specql.core :as specql]
            [taoensso.timbre :as log]))

(def ^:private auth-data (atom {}))

(defn ^:private get-token [tenant-id client-id client-secret]
  {:url (str "https://login.microsoftonline.com/" tenant-id "/oauth2/v2.0/token")
   :params {"grant_type" "client_credentials"
            "client_id" client-id
            "scope" (str client-id "/.default")
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
  [auth-data {:keys [tenant-id client-id client-secret]}]
  (if (token-expired? (:expires-in auth-data))
    (let [{:keys [url params]}          (get-token tenant-id client-id client-secret)
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

(defn ^:private api-call
  "Adds common headers, handles authentication etc. for TIS VACO API calls. Returns nil on failure to allow punning."
  [config call url body]
  (try
    (let [{:keys [access-token]}        (swap! auth-data update-expired-token config)
          rest-endpoint                 (str (:api-base-url config) url)
          {:keys [status headers body]} (call rest-endpoint
                                              (merge
                                                {:headers      {"User-Agent"    "Fintraffic FINAP / 0.1"
                                                                "Authorization" (str "Bearer " access-token)}
                                                 :content-type :json}
                                                (when body {:body (when body (cheshire/generate-string body))})))]
      (log/debug (str "API call to " rest-endpoint " returned " status))
      (cheshire/parse-string body))
    (catch Exception e
      ; clj-http throws exceptions for just about everything which is stupid, so the exception needs to be swallowed
      (let [status (some-> e ex-data :status)]
        (when (< 4 (int (/ status 100)))
          (log/warn (str "Failed API call " (str (:api-base-url config) url) " / " status))))
      nil)))

(defn api-queue-create
  [config payload]
  (api-call config http-client/post "/api/queue" payload))

(defn api-fetch-entry
  [config entry-id]
  (or (api-call config http-client/get (str "/api/queue/" entry-id) nil)
      {}))

(defn queue-entry
  [db
   ; :tis-vaco config root from config.edn
   config
   ; interface
   {:keys [url operator-id operator-name ts-id last-import-date license id data-content]}
   ; conversion-meta
   {:keys [gtfs-file gtfs-filename gtfs-basename external-interface-description-id external-interface-data-content service-id package-id operator-name]}]
  (let [package   (find-package db id package-id)
        new-entry (api-queue-create config {:url         url
                                            :format      "gtfs"
                                            :businessId  (fetch-business-id db operator-id)
                                            :etag        (when package (:gtfs/etag package))
                                            :validations [{:name   "gtfs.canonical.v4_1_0"
                                                           :config {}}]
                                            :conversions [{:name "gtfs2netex.fintraffic.v1_0_0"
                                                           :config {}}]
                                            :metadata    {:caller        "FINAP"
                                                          :operator-id   operator-id
                                                          :operator-name operator-name
                                                          :service-id    service-id
                                                          :interface-id  id
                                                          :package-id    package-id}})]
    (when new-entry
      (try
        (specql/update! db :gtfs/package
                        {:gtfs/tis-entry-public-id (get-in new-entry ["data" "publicId"])}
                        {:gtfs/id (:gtfs/id package)})
        new-entry
        (catch Exception e
          (log/warn e "Failed to update GTFS package with TIS VACO entry reference"))))))