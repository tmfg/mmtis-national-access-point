(ns ote.nap.ckan
  "CKAN-integration tasks. Functions to invoke CKAN API calls from OTE."
  (:require [org.httpkit.client :as http]
            [clojure.spec.alpha :as s]
            [cheshire.core :as cheshire]
            [clojure.string :as str]))

(defrecord CKAN [url api-key])


(s/def :ckan/dataset (s/keys :req [:ckan/name :ckan/owner-org]
                             :opt [:ckan/notes :ckan/title :ckan/private
                                   :ckan/author :ckan/author-email
                                   :ckan/maintainer :ckan/maintainer-email
                                   :ckan/license-id :ckan/url :ckan/version
                                   :ckan/state :ckan/type :ckan/extras]))

(s/def :ckan/name string?)
(s/def :ckan/owner-org string?) ;; name or id of owner organization
(s/def :ckan/notes string?) ;; free text description
(s/def :ckan/title string?) ;; title (default: same as name)
(s/def :ckan/private boolean?) ;; private (default true)
(s/def :ckan/author string?) ;; author name
(s/def :ckan/author-email string?) ;; author's email
(s/def :ckan/maintainer string?) ;; name of the dataset’s maintainer (optional)
(s/def :ckan/maintainer-email string?) ;; the email address of the dataset’s maintainer (optional)
(s/def :ckan/license-id string?) ;; the id of the dataset’s license, see license_list() for available values (optional)

(s/def :ckan/url string?) ;; a URL for the dataset’s source (optional)
(s/def :ckan/version string?) ;; (string, no longer than 100 characters) – (optional)
(s/def :ckan/state #{"active" "deleted"}) ;; the current state of the dataset

(s/def :ckan/type string?) ;; the type of the dataset (optional), IDatasetForm plugins associate themselves with different dataset types and provide custom dataset handling behaviour for these types

;; FIXME: tags (list of tag dictionaries) – the dataset’s tags, see tag_create() for the format of tag dictionaries (optional)

(s/def :ckan/extras (s/coll-of :ckan/extra)) ;;  the dataset’s extras (optional), extras are arbitrary (key: value) metadata items that can be added to datasets, each extra dictionary should have keys 'key' (a string), 'value' (a string)

(s/def :ckan/extra (s/keys :req [:ckan/key :ckan/value]))
(s/def :ckan/key string?)
(s/def :ckan/value string?)

;; NOTE: Not all package attributes are defined here.
;; See: http://docs.ckan.org/en/latest/api/index.html#ckan.logic.action.create.package_create


(s/def :ckan/resource (s/keys :req [:ckan/package-id :ckan/url]
                              :opt [:ckan/revision-id :ckan/description
                                    :ckan/format :ckan/hash :ckan/name
                                    :ckan/resource-type :ckan/mimetype
                                    :ckan/mimetype-inner :ckan/cache-url
                                    :ckan/size :ckan/created :ckan/last-modified
                                    :ckan/cache-last-updated]))

(s/def :ckan/revision-id string?)
(s/def :ckan/description string?)
(s/def :ckan/format string?)
(s/def :ckan/hash string?)
(s/def :ckan/resource-type string?)
(s/def :ckan/mimetype string?)
(s/def :ckan/mimetype-inner string?)
(s/def :ckan/cache-url string?)
(s/def :ckan/size int?)
(s/def :ckan/created  string?)  ;; iso date
(s/def :ckan/last-modified  string?)  ;; iso date
(s/def :ckan/cache-last-updated string?)  ;; iso date





;; Convert JSON keys to keywords in the :ckan namespace
;; Also convert underscores to dashes.

(defn keyword->ckan-key [kw]
  (-> kw name (str/replace #"-" "_")))

(defn ckan-key->keyword [key]
  (keyword "ckan" (str/replace key #"_" "-")))


(defn- ckan-post [{:keys [url api-key]} path payload]
  (let [response
        @(http/post (str url path)
                    {:body (cheshire/encode payload
                                            {:key-fn keyword->ckan-key})
                     :headers {"Content-Type" "application/json"
                               "Authorization" api-key}})]
    (when-not (= 200 (:status response))
      (throw (ex-info (str "CKAN API call " path " failed.")
                      {:response response
                       :payload payload})))
    (-> response :body (cheshire/decode ckan-key->keyword))))

(defn- ckan-dataset-action! [ckan action dataset]
  (when-not (s/valid? :ckan/dataset dataset)
    (throw (ex-info "Invalid CKAN dataset map"
                    (s/explain-data :ckan/dataset dataset))))
  (ckan-post ckan action dataset))

(defn create-dataset! [ckan dataset]
  (ckan-dataset-action! ckan "action/package_create" dataset))

(defn update-dataset! [ckan dataset]
  (ckan-dataset-action! ckan "action/package_update" dataset))

(defn create-or-update-dataset! [ckan dataset]
  (let [action (if (:ckan/id dataset)
                 update-dataset!
                 create-dataset!)]
    (action ckan dataset)))

(defn- ckan-resource-action! [ckan action resource]
  (when-not (s/valid? :ckan/resource resource)
    (throw (ex-info "Invalid CKAN dataset resource map"
                    (s/explain-data :ckan/resource resource))))
  (ckan-post ckan action resource))

(defn add-dataset-resource! [ckan resource]
  (ckan-resource-action! ckan "action/resource_create" resource))

(defn update-dataset-resource! [ckan resource]
  (ckan-resource-action! ckan "action/resource_update" resource))

(defn add-or-update-dataset-resource! [ckan resource]
  (let [action (if (:ckan/id resource)
                 update-dataset-resource!
                 add-dataset-resource!)]
    (action ckan resource)))
