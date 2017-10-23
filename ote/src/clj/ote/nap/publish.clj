(ns ote.nap.publish
  "Publish transport-service as CKAN dataset."
  (:require [ote.nap.ckan :as ckan]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.operation-area :as operation-area]
            [specql.core :refer [fetch] :as specql]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(defn- fetch-service-operation-area-description
  "Fetch the operation area as a comma separated list (for SOLR facet search).
  Takes all operation areas for the transport service and joins their description
  texts into a single string."
  [db service-id]
  (->> (fetch db ::operation-area/operation-area
              #{::operation-area/description}
              {::operation-area/transport-service-id service-id})
       (mapcat ::operation-area/description)
       (map ::t-service/text)
       (str/join ", ")))

(defn- ckan-dataset-description
  "Create a CKAN dataset description that can be used with the CKAN API to
  create a dataset. The owner organization is the user's organization."
  [db user {service-id ::t-service/id :as ts}]
  {:ckan/name (str "org-" (::t-service/transport-operator-id ts)
                   "-service-" service-id)
   :ckan/title (::t-service/name ts)
   :ckan/owner-org (get-in user [:group :name])
   :ckan/transport-service-type (name (::t-service/type ts))
   :ckan/operation-area (fetch-service-operation-area-description db service-id)})

(defmulti interface-description ::t-service/type)

(defmethod interface-description :passenger-transportation [ts]
  {:ckan/url (str "/ote/export/geojson/"
                  (::t-service/transport-operator-id ts) "/"
                  (::t-service/id ts))
   :ckan/name (str (::t-service/name ts) " GeoJSON")})

(defn- ckan-resource-description
  "Create a CKAN resource description that can be used with the CKAN API to
  create a resource."
  [export-base-url ts {:ckan/keys [id name] :as ckan-dataset}]
  (println "CKAN-DATASET: " (pr-str ckan-dataset))
  (merge {:ckan/package-id id}
         (update (interface-description ts)
                 :ckan/url #(str export-base-url %))))

(defn- verify-ckan-response
  "Check that CKAN API call response was successful and return the result.
  If the API call failed, log an error and throw an exception."
  [{:ckan/keys [success result] :as response}]
  (if-not success
    (do
      (log/error "CKAN API call did not succeed: " response)
      (throw (ex-info "CKAN API call failed" response)))
    result))

(def transport-operator-descriptor-columns
  "Columns we need to fetch for a transport service when creating the dataset
  descriptor."
  #{::t-service/id ::t-service/transport-operator-id
    ::t-service/name ::t-service/type})

(defn- fetch-transport-service [db id]
  (first
   (fetch db ::t-service/transport-service transport-operator-descriptor-columns
          {::t-service/id id})))

(defn publish-service-to-ckan!
  "Use CKAN API to creata a dataset (package) and resource for the given transport service id."
  [{:keys [api export-base-url] :as nap-config} db user transport-service-id]
  (let [c (ckan/->CKAN api (get-in user [:user :apikey]))
        ts (fetch-transport-service db transport-service-id)]
    (->> ts
         (ckan-dataset-description db user)
         (ckan/create-dataset c)
         verify-ckan-response
         (ckan-resource-description export-base-url ts)
         (ckan/add-dataset-resource c))))




;; defs for repl experimenting (PENDING: remove)
#_(def db (:db ote.main/ote))
#_(def user {:user
             {:id "401139db-8f3e-4371-8233-5d51d4c4c8b6",
              :username "admin",
              :name "Admin Adminson",
              :apikey "d7c6dccf-6541-4443-a9b4-7ab7c36735bc",
              :email "admin@napoteadmin123.com"},
             :group
             {:id "79046442-ad25-4865-a174-ec199a4b39c4",
              :name "taksiyritys-testinen-oy",
              :title "Taksiyritys Testinen Oy"},
             :groups
             [{:id "79046442-ad25-4865-a174-ec199a4b39c4",
               :name "taksiyritys-testinen-oy",
               :title "Taksiyritys Testinen Oy"}]})

#_ (def publish-result
     (publish-service-to-ckan! {:api "http://localhost:8080/api/"
                                :export-base-url "http://localhost:8080"} db user 3))
