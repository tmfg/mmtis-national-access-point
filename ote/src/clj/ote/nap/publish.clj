(ns ote.nap.publish
  "Publish transport-service as CKAN dataset."
  (:require [ote.nap.ckan :as ckan]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.db.modification :as modification]
            [ote.db.operation-area :as operation-area]
            [specql.core :refer [fetch] :as specql]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [ote.components.db :as db]
            [ote.time :as time]))

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
  [db user {ds-id ::t-service/ckan-dataset-id service-id ::t-service/id :as ts}]
  (let [operator (first (fetch db ::t-operator/transport-operator
                                      #{::t-operator/ckan-group-id}
                                      {::t-operator/id (::t-service/transport-operator-id ts)}))]

    (merge
      {:ckan/name                   (str "org-" (::t-service/transport-operator-id ts)
                                         "-service-" service-id)
       :ckan/title                  (::t-service/name ts)
       :ckan/owner-org              (::t-operator/ckan-group-id operator)
       :ckan/transport-service-type (name (::t-service/type ts))
       :ckan/operation-area         (fetch-service-operation-area-description db service-id)}

      ;; If dataset has already been created, add its id
      (when ds-id
        {:ckan/id ds-id}))))

(defmulti interface-description ::t-service/type)

(defmethod interface-description :default [ts]
  (merge
    {:ckan/url    (str "/ote/export/geojson/"
                       (::t-service/transport-operator-id ts) "/"
                       (::t-service/id ts))
     :ckan/name   (str (::t-service/name ts) " GeoJSON")
     :ckan/format "GeoJSON"}

    (when (::t-service/ckan-resource-id ts)
      {:ckan/id (::t-service/ckan-resource-id ts)})

    (when (::modification/created ts)
      {:ckan/created (time/pgtimestamp->ckan-timestring (::modification/created ts))})

    (when (::modification/modified ts)
      {:ckan/last-modified (time/pgtimestamp->ckan-timestring (::modification/modified ts))})))


(defn- ckan-resource-description
  "Create a CKAN resource description that can be used with the CKAN API to
  create a resource."
  [export-base-url ts {:ckan/keys [id name] :as ckan-dataset}]
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
    ::t-service/name ::t-service/type
    ::t-service/ckan-dataset-id ::t-service/ckan-resource-id
    ::modification/created ::modification/modified})

(defn- fetch-transport-service [db id]
  (first
   (fetch db ::t-service/transport-service transport-operator-descriptor-columns
          {::t-service/id id})))

(defn fetch-transport-service-external-interfaces [db id]
  (fetch db ::t-service/external-interface-description
         #{::t-service/external-interface ::t-service/data-content ::t-service/format
           ::t-service/license ::t-service/ckan-resource-id ::t-service/id}
         {::t-service/transport-service-id id}))

(defn delete-resources-from-published-service!
  [{:keys [api export-base-url] :as nap-config} user removed-resources]
  (let [ckan (ckan/->CKAN api (get-in user [:user :apikey]))]
    (doseq [{ckan-resource-id ::t-service/ckan-resource-id} removed-resources]
      (ckan/delete-dataset-resource! ckan ckan-resource-id))))

(defn publish-service-to-ckan!
  "Use CKAN API to creata a dataset (package) and resource for the given transport service id."
  [{:keys [api export-base-url] :as nap-config} db user transport-service-id]
  (let [c (ckan/->CKAN api (get-in user [:user :apikey]))
        ts (fetch-transport-service db transport-service-id)
        dataset (->> ts
                     (ckan-dataset-description db user)
                     (ckan/create-or-update-dataset! c)
                     verify-ckan-response)
        resource (->> dataset
                      (ckan-resource-description export-base-url ts)
                      (ckan/add-or-update-dataset-resource! c)
                      verify-ckan-response)

        external-interfaces (fetch-transport-service-external-interfaces db transport-service-id)
        external-resources
        (mapv (fn [{external-interface ::t-service/external-interface
                    data-content       ::t-service/interface-data-content
                    fmt                ::t-service/format
                    lic                ::t-service/license
                    resource-id        ::t-service/ckan-resource-id}]
                (verify-ckan-response
                  (ckan/add-or-update-dataset-resource!
                    c (merge
                        {:ckan/package-id (:ckan/id dataset)
                         :ckan/name       (if (not (nil? (-> external-interface ::t-service/description first ::t-service/text)))
                                            (-> external-interface ::t-service/description first ::t-service/text)
                                            "Rajapinta")
                         :ckan/url        (if (not (nil? (::t-service/url external-interface)))
                                            (::t-service/url external-interface)
                                            "Osoite puuttuu")
                         :ckan/format     (if (nil? fmt) "" fmt)
                         :ckan/license    lic}

                        (when resource-id
                          {:ckan/id resource-id})

                        (when (::modification/modified ts)
                          {:ckan/last-modified (time/pgtimestamp->ckan-timestring (::modification/modified ts))})))))
              external-interfaces)]

    ;; Update CKAN resource ids for all external interfaces
    (doall
     (map (fn [{id ::t-service/id} {ckan-resource-id :ckan/id}]
            (specql/update! db ::t-service/external-interface-description
                            {::t-service/ckan-resource-id ckan-resource-id}
                            {::t-service/id id}))
          external-interfaces external-resources))

    ;; Update CKAN dataset and resource ids
    (specql/update! db ::t-service/transport-service
                    {::t-service/ckan-dataset-id (:ckan/id dataset)
                     ::t-service/ckan-resource-id (:ckan/id resource)}
                    {::t-service/id transport-service-id})

    {:dataset dataset
     :resource resource
     :external-resources external-resources}))

(defn delete-published-service!
  [{:keys [api export-base-url] :as nap-config} db user transport-service-id]
  (let [c (ckan/->CKAN api (get-in user [:user :apikey]))
        dataset-id (-> db
                       (specql/fetch ::t-service/transport-service
                                     #{::t-service/ckan-dataset-id}
                                     {::t-service/id transport-service-id})
                       first
                       ::t-service/ckan-dataset-id)]
    (when-not dataset-id
      (throw (ex-info "Can't find CKAN dataset-id for service"
                      {::t-service/id transport-service-id})))

    (ckan/delete-dataset! c dataset-id)))
