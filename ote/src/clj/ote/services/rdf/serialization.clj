(ns ote.services.rdf.serialization
  "RDF model serialization to Turtle format.
   Encapsulates all Jena library interactions."
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]
           [org.apache.jena.datatypes BaseDatatype]))

;; Namespace URI mappings for RDF prefixes
(def dcat "http://www.w3.org/ns/dcat#")
(def dct "http://purl.org/dc/terms/")
(def cnt "http://www.w3.org/2011/content#")
(def locn "http://www.w3.org/ns/locn#")
(def foaf "http://xmlns.com/foaf/0.1/")
(def mobility "http://w3id.org/mobilitydcat-ap#")
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

(defn add-resource-to-model!
  "Add a resource and its properties to a Jena RDF model.
   
   Two-arity version: [model resource-data]
   Takes a resource data structure and adds it to the model. 
   The resource-data should have:
   - :uri (string) - The resource URI, or
   - :subject (string) - For relationship objects
   - :properties (map) - Property map as described below
   
   Three-arity version: [model resource properties-map]
   Takes a Jena Resource object and a properties-map.
   
   The properties-map is a map of property keywords to value descriptors.
   Value descriptors are maps with :type and :value keys:
   - {:type :uri :value <keyword-or-string>} - Resource reference (keyword is resolved via kw->uri)
   - {:type :literal :value <string>} - Plain string literal
   - {:type :typed-literal :value <string> :datatype <uri-string>} - Typed literal with datatype
   - {:type :lang-literal :value <string> :lang <lang-code>} - Language-tagged literal (e.g., \"en\", \"fi\")
   - {:type :resource :properties {...}} - Nested resource (blank node or named resource)
   - {:type :resource :value <Jena-Resource>} - Reference to an existing Jena resource object
   
   Values can be single maps or vectors/sequences of maps for multi-valued properties.
   Special handling: :rdf/type is mapped to RDF/type constant."
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

(defn flatten-rdf
  "Flatten RDF data structure into a single sequence of resources in the order they should be added to the model.
   Order: fintraffic-agent, operator-agent, assessments, datasets, relationships, data-services, catalog.
   Distributions and catalog records are now embedded as blank nodes within their parent resources.
   Note: relationships are maps with :subject and :properties that need special handling."
  [rdf-data]
  (concat [(:fintraffic-agent rdf-data)]
          (when-let [op-agent (:operator-agent rdf-data)] [op-agent])
          (:assessments rdf-data)
          (:datasets rdf-data)
          (:relationships rdf-data)
          (:data-services rdf-data)
          [(:catalog rdf-data)]))

(defn create-dcat-ap-model
  "Create a Jena RDF model from RDF data structure.
   https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/#properties-for-dataset"
  [rdf-data]
  (let [model (ModelFactory/createDefaultModel)]
    ;; Set namespace prefixes from data
    (doseq [[prefix uri] (:ns-prefixes rdf-data)]
      (.setNsPrefix model prefix uri))

    ;; Add all resources to Jena model in order
    (doseq [resource (flatten-rdf rdf-data)]
      (add-resource-to-model! model resource))

    ;; Return model
    model))

(defn rdf-data->turtle
  "Convert RDF data structure(s) to Turtle format string.
   Takes either a single RDF data map or a sequence of RDF data maps.
   If given a sequence, merges all models before serialization.

  Writes the resulting ttl into the out-outputstream"
  [out rdf-data-or-seq]
  (let [model (if (sequential? rdf-data-or-seq)
                (let [models (map create-dcat-ap-model rdf-data-or-seq)]
                  (reduce (fn [acc m] (doto acc (.add m))) models))
                (create-dcat-ap-model rdf-data-or-seq))]
    (.write model out "TURTLE")
    nil))
