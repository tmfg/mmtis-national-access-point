(ns ote.services.rdf
  "Use Jena to create Mobility DCAT-AP RDF from the database."
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ote.components.http :as http]
            [ring.util.response :as response]
            [specql.core :as specql]
            [compojure.core :refer [routes GET]]
            [ote.db.transport-service :as t-service]
            [ote.services.rdf.data :as rdf-data]
            [ote.services.rdf.model :as rdf-model])
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]
           [org.apache.jena.datatypes BaseDatatype]))

;; URI-prefiksit DCAT-AP:lle ja Mobility DCAT-AP:lle
(def base-uri "http://localhost:3000/")
(def service-id 1)
(def business-id "2942108-7")                               ;; Tämä on testibusiness-id, jota käytetään Finap.fi:n testisivuilla
(defn fintraffic-url [business-id]
  (str "https://finap.fi/service-search?operators=" business-id))
(defn operator-url [business-id]
  (str "https://finap.fi/service-search?operators=" business-id))

(def dcat "http://www.w3.org/ns/dcat#")
(def dct "http://purl.org/dc/terms/")
(def cnt "http://www.w3.org/2011/content#")
(def locn "http://www.w3.org/ns/locn#")
(def foaf "http://xmlns.com/foaf/0.1/")
(def mobility "http://www.w3.org/ns/mobilitydcatap#")
(def owl "http://www.w3.org/2002/07/owl#")

(def namespace-map
  {:dcat dcat
   :dct dct
   :cnt cnt
   :locn locn
   :foaf foaf
   :mobility mobility
   :owl owl})

(defn kw->uri
  "Convert a namespaced keyword like :dcat/startDate to a URI string."
  [kw]
  (let [ns (namespace kw)
        name (name kw)
        base-uri (get namespace-map (keyword ns))]
    (when-not base-uri
      (throw (ex-info (str "Unknown namespace: " ns) {:keyword kw})))
    (str base-uri name)))

(def properties
  "Map of property keywords to pre-created RDF properties."
  (let [prop-keys [:dcat/startDate
                   :dcat/endDate
                   :dcat/record
                   :dcat/distribution
                   :dcat/accessURL
                   :dcat/downloadURL
                   :dcat/endpointURL
                   :dcat/description
                   :dcat/dataset
                   :dct/isReferencedBy
                   :dct/relation
                   :dct/description
                   :dct/spatial
                   :dct/language
                   :dct/license
                   :dct/identifier
                   :dct/issued
                   :dct/themeTaxonomy
                   :dct/modified
                   :dct/created
                   :dct/publisher
                   :dct/title
                   :dct/rights
                   :dct/type
                   :dct/format
                   :dct/accrualPeriodicity
                   :dct/conformsTo
                   :dct/rightsHolder
                   :dct/theme
                   :dct/temporal
                   :dct/date
                   :dct/result
                   :foaf/title
                   :foaf/homepage
                   :foaf/primaryTopic
                   :foaf/name
                   :locn/geometry
                   :cnt/characterEncoding
                   :mobility/schema
                   :mobility/transportMode
                   :mobility/mobilityDataStandard
                   :mobility/applicationLayerProtocol
                   :mobility/description
                   :mobility/communicationMethod
                   :mobility/grammar
                   :mobility/mobilityTheme
                   :mobility/georeferencingMethod
                   :mobility/identifier
                   :mobility/intendedInformationService
                   :owl/versionInfo]]
    (into {} (map (fn [kw]
                    [kw (ResourceFactory/createProperty (kw->uri kw))])
                  prop-keys))))

(defn property
  "Get a pre-created RDF property from a namespaced keyword."
  [kw]
  (or (get properties kw)
      (throw (ex-info (str "Unknown property: " kw) {:keyword kw}))))

(def dataset-base-uri (str base-uri "rdf/" service-id))


;; ===== RDF CONVERSION LAYER: Jena Model Creation =====
;; Functions that convert RDF data structures to Jena models
;; These are the only functions that interact with the Jena API

(defn add-resource-to-model!
  "Add a resource and its properties to a Jena model.
   
   Two-arity version: [model resource-data]
   Takes a resource data structure (created with the `resource` helper function)
   and adds it to the model. The resource-data should have :uri and :properties keys,
   or :subject and :properties keys (for relationships).
   
   Three-arity version: [model resource properties-map]
   Takes a Jena Resource object and a properties-map.
   properties-map is a map of property keywords to value descriptors.
   Value descriptors are maps with :type and :value keys:
   - {:type :uri :value <keyword-or-string>} - Resource reference
   - {:type :literal :value <string>} - Plain string literal
   - {:type :typed-literal :value <string> :datatype <uri-string>} - Typed literal
   - {:type :lang-literal :value <string> :lang <lang-code>} - Language-tagged literal
   - {:type :blank-node :properties {...}} - Blank node with nested properties
   Values can also be vectors of the above for multi-valued properties."
  ([model resource-data]
   (let [;; Handle both regular resources and relationship objects
         uri-or-subject (or (:uri resource-data) (:subject resource-data))
         res (if uri-or-subject
              (ResourceFactory/createResource uri-or-subject)
              (ResourceFactory/createResource))]
     (add-resource-to-model! model res (:properties resource-data))))
  ([model resource properties-map]
   (doseq [[prop-kw val] properties-map]
     (let [vals (cond
                  (vector? val) val
                  (seq? val) val
                  (map? val) [val]
                  :else [val])]
       (doseq [v vals]
         (let [object (case (:type v)
                        :uri
                        (let [uri-val (:value v)]
                          (if (keyword? uri-val)
                            (ResourceFactory/createResource (kw->uri uri-val))
                            (ResourceFactory/createResource uri-val)))
                        
                        :literal
                        (ResourceFactory/createStringLiteral (:value v))
                        
                        :typed-literal
                        (ResourceFactory/createTypedLiteral 
                          (:value v) 
                          (BaseDatatype. (:datatype v)))
                        
                        :lang-literal
                        (ResourceFactory/createLangLiteral (:value v) (:lang v))
                        
                        :resource
                        (if (:properties v)
                          ;; Resource with properties (blank node or named resource)
                          (add-resource-to-model! model v)
                          ;; Reference to existing resource
                          (:value v))
                        
                        (throw (ex-info "Unknown value type" {:value v :property prop-kw :all-properties (keys properties-map)})))]
           (if (= prop-kw :rdf/type)
             (.add model resource RDF/type object)
             (.add model resource (property prop-kw) object))))))
   resource))

;; ===== RDF CONVERSION LAYER: Jena Model Creation =====
;; Functions that convert RDF data structures to Jena models
;; These are the only functions that interact with the Jena API

(defn create-dcat-ap-model
  "https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/#properties-for-dataset"
  [service-data]
  (let [model (ModelFactory/createDefaultModel)
        
        ;; Create all RDF data structures using pure functions
        rdf-data (rdf-model/service-data->rdf service-data)]
    ;; Set namespace prefixes from data
    (doseq [[prefix uri] (:ns-prefixes rdf-data)]
      (.setNsPrefix model prefix uri))

    ;; Add all resources to Jena model in order
    (doseq [resource (rdf-model/flatten-rdf rdf-data)]
      (add-resource-to-model! model resource))

    ;; Return model
    model))

;; Sarjallista RDF/XML
(defn serialize-model [model]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (.write model out "TURTLE" #_"RDF/XML")
    (.toString out)))

(defn create-rdf
  ([db]
   (let [service-ids (map :ote.db.transport-service/id (specql/fetch db ::t-service/transport-service
                                   #{:ote.db.transport-service/id}
                                   {}))
         _ (assert (seq service-ids))
         models (map (fn [service-id]
                       (let [service-data (rdf-data/fetch-service-data db service-id)]
                         (create-dcat-ap-model service-data)))
                     service-ids)
         final-model (reduce (fn [acc model]
                               (doto acc
                                 (.add model)))
                             models)
         rdf-turtle (serialize-model final-model)]
   (-> (response/response rdf-turtle)
       (response/content-type "text/turtle; charset=UTF-8")
       (response/header "Content-Disposition" "attachment;"))))
  
  ([db service-id]
  ;; Use data layer functions for fetching
  (let [service-id (if (string? service-id)
                     (Long/parseLong service-id)
                     service-id)
        service-data (rdf-data/fetch-service-data db service-id)
        model (create-dcat-ap-model service-data)
        rdf-turtle (serialize-model model)
        turtle-format (-> (response/response rdf-turtle)
                          (response/content-type "text/turtle; charset=UTF-8")
                          (response/header "Content-Disposition" "attachment;"))]
    turtle-format)))

(defn- rds-routes [config db]
  (routes
   (GET "/rdf" {:as req}
     (create-rdf db))
   (GET ["/rdf/:service-id", :service-id #".+"] {{service-id :service-id} :params :as req}
     ;; create-rdf returns a complete response
     ;; and is probably a lot easier to redefine, as compojure's/ring's handlers are somewhat repl-hostile to redefine
     (create-rdf db service-id))))

(defrecord RDS [config]
  component/Lifecycle
  (start [{db :db
           http :http
           :as this}]
    (assoc this ::stop
                (http/publish! http {:authenticated? false}
                               (rds-routes config db))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
