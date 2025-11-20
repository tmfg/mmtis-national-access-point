(ns ote.services.rdf
  "Use Jena to create Mobility DCAT-AP RDF from the database."
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [ote.components.http :as http]
            [ote.localization :as localization :refer [tr]]
            [ring.util.response :as response]
            [specql.core :as specql]
            [compojure.core :refer [routes GET]]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification])
  (:import (org.apache.jena.datatypes.xsd XSDDateTime)
           [org.apache.jena.rdf.model Model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF OWL]
           [org.apache.jena.datatypes BaseDatatype]
           [org.apache.jena.datatypes.xsd.impl XSDDateTimeType]
           [java.time Instant]))

(defqueries "ote/integration/export/geojson.sql")
(defqueries "ote/services/service_search.sql")
(defqueries "ote/tasks/gtfs.sql")

(declare fetch-operation-area-for-service latest-published-service fetch-latest-gtfs-vaco-status)

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

(def namespace-map
  {:dcat dcat
   :dct dct
   :cnt cnt
   :locn locn
   :foaf foaf
   :mobility mobility})

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
                   :mobility/intendedInformationService]]
    (into {} (map (fn [kw]
                    [kw (ResourceFactory/createProperty (kw->uri kw))])
                  prop-keys))))

(defn property
  "Get a pre-created RDF property from a namespaced keyword."
  [kw]
  (or (get properties kw)
      (throw (ex-info (str "Unknown property: " kw) {:keyword kw}))))

(def catalog-uri (str base-uri "catalog"))
(def dataset-base-uri (str base-uri "rdf/" service-id))
(def distribution-base-uri (str dataset-base-uri "/distribution"))
(def distribution-interface-base-uri (str dataset-base-uri "/distribution/interface"))
;;TODO: Select suitable licence url from this list: http://publications.europa.eu/resource/authority/licence
(def licence-url "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")


(defn localized-text-with-key
  "Usage: (localized-text-with-key \"fi\" [:email-templates :password-reset :subject])"
  [language key]
  (localization/with-language language (tr key)))

(defn service->mobility-theme [service]
  (case (:ote.db.transport-service/sub-type service)
    :taxi "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-non-scheduled-transport"
    :request "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-non-scheduled-transport"
    :schedule "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-scheduled-transport"
    :terminal "https://w3id.org/mobilitydcat-ap/mobility-theme/other"
    :rentals "https://w3id.org/mobilitydcat-ap/mobility-theme/sharing-and-hiring-services"
    :parking "https://w3id.org/mobilitydcat-ap/mobility-theme/parking-service-and-rest-area-information"
    :brokerage "https://w3id.org/mobilitydcat-ap/mobility-theme/sharing-and-hiring-services"
    :other "https://w3id.org/mobilitydcat-ap/mobility-theme/other"))

(defn service->sub-theme [service]
  (case (:ote.db.transport-service/sub-type service)
    :taxi nil
    :request nil
    :schedule nil
    :terminal "https://w3id.org/mobilitydcat-ap/mobility-theme/connection-links"
    :rentals nil
    :parking nil
    :brokerage nil
    :other nil
    :else nil))

(defn service->transport-mode [service]
  (let [transport-type (first (::t-service/transport-type service))
        sub-type (:ote.db.transport-service/sub-type service)]

    ;; Valitse allaolevista linkeistä yksi tai useampi
    ;; Esim. aviation on aina /air
    ;; Ja jos on Säännöllistä aikataulun mukaista liikennetta ja tranport-type :road, niin silloin /bus
    ;; Jos on taksiliikenne, niin silloin /taxi
    ;; Kaikki transport-type :sea on aina /maritime

    ;; Ihan tarkasti ei voida aina valita, koska esim Voi vuokraa e-scootereita, mutta sitä ei voi päätellä Finapissa olevista tiedoista suoraan.

    (cond
      (= transport-type :aviation)
      "https://w3id.org/mobilitydcat-ap/transport-mode/air"
      (= transport-type :sea)
      "https://w3id.org/mobilitydcat-ap/transport-mode/maritime"
      (= transport-type :rail)
      "https://w3id.org/mobilitydcat-ap/transport-mode/long-distance-rail"
      (and (= transport-type :road) (= :taxi sub-type))
      "https://w3id.org/mobilitydcat-ap/transport-mode/taxi"
      (and (= transport-type :road) (= :schedule sub-type))
      "https://w3id.org/mobilitydcat-ap/transport-mode/bus"
      :else "https://w3id.org/mobilitydcat-ap/transport-mode/bus")))

(defn interface->mobility-data-standard
  "When implementing final version of this function, ensure that is this ok."
  [interface]
  (case (str (first (::t-service/format interface)))
    "GTFS" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/gtfs"
    "GTFS-RT" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/gtfs-rt"
    "GBFS" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/gbfs"
    "Kalkati" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"
    "NeTEx" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"
    "GeoJSON" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"
    "JSON" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"
    "CSV" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"
    "Datex II" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/datex-II"
    "SIRI" "https://w3id.org/mobilitydcat-ap/mobility-data-standard/siri"
    ;; :else
    "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"))

(defn interface->format-extent [interface]
  (case (str (first (::t-service/format interface)))
    "GTFS" "http://publications.europa.eu/resource/authority/file-type/CSV"
    "GTFS-RT" "http://publications.europa.eu/resource/authority/file-type/CSV"
    "GBFS" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "Kalkati" "http://publications.europa.eu/resource/authority/file-type/XML"
    "NeTEx" "http://publications.europa.eu/resource/authority/file-type/XML"
    "GeoJSON" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "JSON" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "CSV" "http://publications.europa.eu/resource/authority/file-type/CSV"
    ;; TODO: Ensure correct format somehow - This is mandatory information so the correct format has to be given.
    ;; DATEX II is primary in XML format, but can also be in JSON format.
    "Datex II" "http://publications.europa.eu/resource/authority/file-type/XML"
    "SIRI" "http://publications.europa.eu/resource/authority/file-type/XML"
    ;; TODO: In cases where format is not known, we need to get the corredt format from somewhere due to DCAT-AP requirements. This is mandatory field.
    ;; This might be a big problem in the future. To ensure the correct format, the data from the interface must be downloaded and checked.
    ;; Correct format might be written in the first bytes in the downloaded file.
    
    ;; :else
    nil))

(defn interface->rights-url [interface]
  ;; This is the best we can do with data that is available in the service.
  ;; By changing lincence as a mandatory data and by changing lisence to be selected from a list, we can ensure that this is correct.
  ;; But now this is the best we can do.
  (if (::t-service/license interface)
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided"
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/other"))


;; Helper functions to create self-describing RDF values
(defn uri
  "Create a URI/resource reference."
  [value]
  {:type :uri :value value})

(defn literal
  "Create a plain literal string."
  [value]
  {:type :literal :value value})

(defn typed-literal
  "Create a typed literal with datatype."
  [value datatype]
  {:type :typed-literal :value value :datatype datatype})

(defn lang-literal
  "Create a language-tagged literal."
  [value lang]
  {:type :lang-literal :value value :lang lang})

;; Phase 1: Create Clojure data representation
(defn period-of-time-data
  "Create a Clojure map representing a dcat:PeriodOfTime."
  [available-from available-to created]
  (let [start (or available-from created)]
    (cond-> {:rdf/type (uri :dcat/PeridOfTime)  ;; NOTE: Typo in original, keeping for compatibility
             :dcat/startDate (typed-literal (str start) "http://www.w3.org/2001/XMLSchema#dateTime")}
      available-to
      (assoc :dcat/endDate (typed-literal (str available-to) "http://www.w3.org/2001/XMLSchema#dateTime")))))

;; Phase 2: Convert Clojure data to Jena model
(defn add-resource-to-model!
  "Add a resource and its properties to a Jena model.
   properties-map is a map of property keywords to value descriptors.
   Value descriptors are maps with :type and :value keys:
   - {:type :uri :value <keyword-or-string>} - Resource reference
   - {:type :literal :value <string>} - Plain string literal
   - {:type :typed-literal :value <string> :datatype <uri-string>} - Typed literal
   - {:type :lang-literal :value <string> :lang <lang-code>} - Language-tagged literal
   Values can also be vectors of the above for multi-valued properties."
  [model resource properties-map]
  (doseq [[prop-kw val] properties-map]
    (let [vals (if (vector? val) val [val])]
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
                       (:value v)
                       
                       (throw (ex-info "Unknown value type" {:value v})))]
          (if (= prop-kw :rdf/type)
            (.add model resource RDF/type object)
            (.add model resource (property prop-kw) object))))))
  resource)

;; Compatibility wrapper (uses both phases)
(defn period-of-time [model available-from available-to created]
  (let [data (period-of-time-data available-from available-to created)
        resource (ResourceFactory/createResource)]
    (add-resource-to-model! model resource data)))


;; Phase 1: Create relationship data
(defn isreferenced-and-related-data
  "Create data representing bidirectional reference relationships between datasets."
  [geojson-dataset-id interface-dataset-id]
  [{:subject geojson-dataset-id
    :properties {:dct/relation (uri interface-dataset-id)}}
   {:subject interface-dataset-id
    :properties {:dct/isReferencedBy (uri geojson-dataset-id)}}])

;; Phase 2: Add relationships to model
(defn add-relationships-to-model!
  "Add multiple subject-property-object relationships to a Jena model.
   relationships is a seq of maps with :subject (resource) and :properties (map)."
  [model relationships]
  (doseq [{:keys [subject properties]} relationships]
    (add-resource-to-model! model subject properties)))

;; Compatibility wrapper
(defn add-isreferenced-and-related-to-dataset [geojson-dataset interface-dataset model]
  (let [relationships (isreferenced-and-related-data geojson-dataset interface-dataset)]
    (add-relationships-to-model! model relationships)))

(defn get-intended-information-service
  "Päättele tyyppi interface typestä"
  [interface]
  (let [data-content (some-> interface ::t-service/data-content first)]
    (case data-content
      "route-and-schedule" "https://w3id.org/mobilitydcat-ap/intended-information-service/trip-plans"
      "luggage-restrictions" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "realtime-interface" "https://w3id.org/mobilitydcat-ap/intended-information-service/dynamic-passing-times-trip-plans-and-auxiliary-information"
      "booking-interface" "https://w3id.org/mobilitydcat-ap/intended-information-service/dynamic-availability-check"
      "accessibility-services" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "other-services" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "pricing" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "service-hours" "https://w3id.org/mobilitydcat-ap/intended-information-service/dynamic-availability-check"
      "disruptions" "https://w3id.org/mobilitydcat-ap/intended-information-service/dynamic-information-service"
      "payment-interface" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "other" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "map-and-location" "https://w3id.org/mobilitydcat-ap/intended-information-service/location-search"
      "on-behalf-errand" "https://w3id.org/mobilitydcat-ap/intended-information-service/other"
      "customer-account-info" "https://w3id.org/mobilitydcat-ap/intended-information-service/information-service"
      ;; :else
      (do
        (log/warnf "Unknown datacontent %s" (pr-str data-content))
        "https://w3id.org/mobilitydcat-ap/intended-information-service/other"))))

;; Luo Mobility DCAT-AP RDF-malli

;; Phase 1: Create catalog data
(defn catalog-data
  "Create a Clojure map representing a dcat:Catalog."
  [db]
  (let [latest-publication (:published (first (latest-published-service db)))]
    {:catalog
     {:rdf/type (uri :dcat/Catalog)
      :foaf/title (literal "Finap.fi - NAP - National Access Point")
      :dct/description [(lang-literal (localized-text-with-key "fi" [:front-page :column-NAP]) "fi")
                        (lang-literal (localized-text-with-key "sv" [:front-page :column-NAP]) "sv")
                        (lang-literal (localized-text-with-key "en" [:front-page :column-NAP]) "en")]
      :foaf/homepage (literal "https://www.finap.fi/")
      :dct/spatial (uri "http://data.europa.eu/nuts/code/FI")
      :dct/language [(uri "http://publications.europa.eu/resource/authority/language/FIN")
                     (uri "http://publications.europa.eu/resource/authority/language/SWE")
                     (uri "http://publications.europa.eu/resource/authority/language/ENG")]
      :dct/license {:type :blank-node
                    :properties {:rdf/type (uri :dct/LicenseDocument)
                                :dct/identifier (uri licence-url)}}
      :dct/issued (typed-literal "2018-01-01T00:00:01Z" "http://www.w3.org/2001/XMLSchema#dateTime")
      :dct/themeTaxonomy (uri "https://w3id.org/mobilitydcat-ap/mobility-theme")
      :dct/modified (typed-literal (str latest-publication) "http://www.w3.org/2001/XMLSchema#dateTime")
      :dct/identifier (literal catalog-uri)}}))

;; Compatibility wrapper
(defn create-catalog-model [db model catalog]
  (let [data (catalog-data db)
        catalog-props (get-in data [:catalog])
        license-data (get catalog-props :dct/license)
        license-resource (ResourceFactory/createResource)
        catalog-props-without-license (dissoc catalog-props :dct/license)]
    ;; Add license properties
    (when (= (:type license-data) :blank-node)
      (add-resource-to-model! model license-resource (:properties license-data)))
    ;; Add catalog properties with license as a resource reference
    (add-resource-to-model! model catalog (assoc catalog-props-without-license 
                                                 :dct/license {:type :resource :value license-resource}))))

(defn catalog-record-data [service interface dataset-resource fintraffic-agent]
  (let [id (:ote.db.transport-service/id service)
        record-uri (if (nil? interface)
                     (str dataset-base-uri id "/record")
                     (str (get-in interface [::t-service/external-interface ::t-service/url]) "/record"))
        created (:ote.db.modification/created service)
        modified (:ote.db.modification/modified service)]
    {:uri record-uri
     :properties {:rdf/type (uri :dcat/CatalogRecord)
                  :dct/created (literal (str created))
                  :dct/language [(uri "http://publications.europa.eu/resource/authority/language/FIN")
                                 (uri "http://publications.europa.eu/resource/authority/language/SWE")
                                 (uri "http://publications.europa.eu/resource/authority/language/ENG")]
                  :foaf/primaryTopic {:type :resource :value dataset-resource}
                  :dct/modified (literal (str modified))
                  :dct/publisher {:type :resource :value fintraffic-agent}}}))

(defn create-catalog-record [service model catalog interface dataset-resource fintraffic-agent]
  (let [record-data (catalog-record-data service interface dataset-resource fintraffic-agent)
        record (ResourceFactory/createResource (:uri record-data))]
    (add-resource-to-model! model record (:properties record-data))
    ;; Add Record to Catalog
    (.add model catalog
          (property :dcat/record)
          record)
    ;; source - Used when records are harvested from other portals and Finap doesn't harvest them.
    ))

(defn data-service-data
  "Create a Clojure map representing a dcat:DataService (accessService)."
  [interface]
  (let [endpointUrl (get-in interface [::t-service/external-interface ::t-service/url])
        title (localized-text-with-key "fi" [:enums :ote.db.transport-service/interface-data-content :route-and-schedule])
        ;; First element is the finnish description, second is the swedish description and third is the english description.
        ;;TODO: Use correct language versions
        description (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) "")
        rights-url (interface->rights-url interface)]
    {:access-service
     {:properties {:rdf/type (uri :dct/DataService)
                   :dcat/endpointURL (uri endpointUrl)
                   :dct/title (uri title)
                   :dct/description (uri description)
                   :dct/rights {:type :blank-node
                                :properties {:rdf/type (uri :dct/RightsStatement)
                                            :dct/type (uri rights-url)}}
                   :dct/license {:type :blank-node
                                 :properties {:rdf/type (uri :dct/LicenseDocument)
                                             :dct/identifier (uri licence-url)}}}}}))

(defn create-data-service
  "TODO: All external interfaces are not accessServices. Add this only for those that have API interfaces. ie. Finnair etc.
  We need to come up with a solution to distinguish between accessService and reqular dataset."
  [model interface]
  (let [data (data-service-data interface)
        accessService-resource (ResourceFactory/createResource)
        access-service-props (get-in data [:access-service :properties])
        rights-data (get access-service-props :dct/rights)
        rights-resource (ResourceFactory/createResource)
        license-data (get access-service-props :dct/license)
        license-resource (ResourceFactory/createResource)
        access-service-props-clean (-> access-service-props
                                        (dissoc :dct/rights)
                                        (dissoc :dct/license))]
    ;; Add rights properties
    (add-resource-to-model! model rights-resource (get rights-data :properties))
    ;; Add license properties
    (add-resource-to-model! model license-resource (get license-data :properties))
    ;; Add access service properties with resource references
    (add-resource-to-model! model accessService-resource
                           (assoc access-service-props-clean
                                  :dct/rights {:type :resource :value rights-resource}
                                  :dct/license {:type :resource :value license-resource}))
    accessService-resource))

(defn dataset-data [db service service-id operator-id operation-areas operator interface]
  (let [id (:ote.db.transport-service/id service)
        available-from (:ote.db.transport-service/available-from service)
        available-to (:ote.db.transport-service/available-to service)
        created (::modification/created service)
        dataset-uri (if (nil? interface)
                      (str dataset-base-uri id)
                      (get-in interface [::t-service/external-interface ::t-service/url]))
        accessURL (if (nil? interface)
                    (str base-uri "export/geojson/" operator-id "/" service-id)
                    (get-in interface [::t-service/external-interface ::t-service/url]))
        downloadURL (if (nil? interface)
                      (str base-uri "export/geojson/" operator-id "/" service-id)
                      (get-in interface [::t-service/external-interface ::t-service/url]))
        
        format (if (nil? interface)
                 "http://publications.europa.eu/resource/authority/file-type/GEOJSON"
                 (interface->format-extent interface))

        rights-url (if (nil? interface)
                     "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided-free-of-charge"
                     (interface->rights-url interface))

        licence-url (if (nil? interface)
                      "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0"
                      "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

        applicationLayerProtocol "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https"

        distribution-description (if (nil? interface)
                                   (str "Olennaiset liikennepalvelutiedot palvelusta " (::t-service/name service))
                                   (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) ""))

        distribution-uri (if (nil? interface)
                           (str distribution-base-uri "/" id)
                           (str distribution-interface-base-uri "/" (::t-service/id interface)))
        operator-uri (operator-url (::t-operator/business-id operator))
        operator-name (:ote.db.transport-operator/name operator)

        latest-conversion-status (when interface
                                   (first (fetch-latest-gtfs-vaco-status db {:service-id service-id
                                                                             :interface-id (::t-service/id interface)})))
        vaco-validation-timestamp (when interface
                                    (:tis_polling_completed latest-conversion-status))

        last-modified (cond
                        (and interface latest-conversion-status)
                        (:created latest-conversion-status)
                        (and interface
                             (nil? latest-conversion-status)
                             (::t-service/gtfs-imported interface))
                        (::t-service/gtfs-imported interface)
                        (not interface)
                        (:ote.db.modification/modified service))
        
        accrual-periodicity (if (nil? interface)
                              "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED"
                              (let [{:ote.db.transport-service/keys [format]} interface]
                                (if (= (first format) "GeoJSON")
                                  "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED"
                                  "http://publications.europa.eu/resource/authority/frequency/UNKNOWN")))
        
        mobility-themes (if-let [sub-theme (service->sub-theme service)]
                          [(uri (service->mobility-theme service))
                           (uri sub-theme)]
                          [(uri (service->mobility-theme service))])
        
        dataset-languages (when-not interface
                            [(uri "http://publications.europa.eu/resource/authority/language/FIN")
                             (uri "http://publications.europa.eu/resource/authority/language/SWE")
                             (uri "http://publications.europa.eu/resource/authority/language/ENG")])]
    
    {:dataset-uri dataset-uri
     :distribution-uri distribution-uri
     :operator-uri operator-uri
     :needs-mobility-data-standard-blank-node? (nil? interface)
     :mobility-data-standard-uri (when interface (interface->mobility-data-standard interface))
     :operator
     {:properties {:rdf/type (uri :foaf/Organization)
                   :foaf/name (literal operator-name)}}
     :spatial
     {:properties {:rdf/type (uri :dct/Location)
                   :locn/geometry (typed-literal (:geojson (first operation-areas)) 
                                                 "https://www.iana.org/assignments/media-types/application/vnd.geo+json")}}
     :rights
     {:properties {:rdf/type (uri :dct/RightsStatement)
                   :dct/type (uri rights-url)}}
     :license
     {:properties {:rdf/type (uri :dct/LicenseDocument)
                   :dct/identifier (uri licence-url)}}
     :assessment (when (and interface vaco-validation-timestamp)
                   {:properties {:rdf/type (uri :mobility/Assessment)
                                :dct/date (typed-literal (str vaco-validation-timestamp) 
                                                        "http://www.w3.org/2001/XMLSchema#dateTime")}
                    :result-link (:tis-magic-link latest-conversion-status)})
     :distribution
     {:properties (cond-> {:rdf/type (uri :dcat/Distribution)
                           :dcat/accessURL (uri accessURL)
                           :dcat/downloadURL (uri downloadURL)
                           :dct/format (uri format)
                           :mobility/applicationLayerProtocol (uri applicationLayerProtocol)
                           :mobility/description (lang-literal distribution-description "fi")
                           :mobility/communicationMethod (uri "https://w3id.org/mobilitydcat-ap/communication-method/pull")}
                    (nil? interface)
                    (assoc :cnt/characterEncoding (literal "UTF-8")
                           :mobility/grammar (uri "https://w3id.org/mobilitydcat-ap/grammar/json-schema")))}
     :dataset
     {:properties (cond-> {:rdf/type (uri :dcat/Dataset)
                           :dct/title (literal (:ote.db.transport-service/name service))
                           :dct/description (literal (or (get-in service [::t-service/description 0 ::t-service/text]) ""))
                           :mobility/transportMode (uri (service->transport-mode service))
                           :dct/accrualPeriodicity (uri accrual-periodicity)
                           :mobility/mobilityTheme mobility-themes
                           :dct/spatial [(uri "https://w3id.org/stirdata/resource/lau/item/FI_244")]
                           :mobility/georeferencingMethod (uri "https://w3id.org/mobilitydcat-ap/georeferencing-method/geocoordinates")
                           :dct/theme (uri "http://publications.europa.eu/resource/authority/data-theme/TRAN")
                           :dct/temporal {:type :blank-node
                                         :properties (period-of-time-data available-from available-to created)}
                           :mobility/identifier (uri dataset-uri)
                           :mobility/intendedInformationService (uri (if interface
                                                                        (get-intended-information-service interface)
                                                                        "https://w3id.org/mobilitydcat-ap/intended-information-service/other"))}
                    (nil? interface)
                    (assoc :dct/conformsTo (uri "https://www.opengis.net/def/crs/EPSG/0/4326"))
                    
                    dataset-languages
                    (assoc :dct/language dataset-languages)
                    
                    last-modified
                    (assoc :dct/modified (typed-literal (str last-modified) 
                                                       "http://www.w3.org/2001/XMLSchema#dateTime")))}}))

(defn create-dataset [db service model catalog service-id operator-id operation-areas operator interface]
  (let [data (dataset-data db service service-id operator-id operation-areas operator interface)
        dataset (ResourceFactory/createResource (:dataset-uri data))
        distribution (ResourceFactory/createResource (:distribution-uri data))
        operator-agent (ResourceFactory/createResource (:operator-uri data))
        spatial-resource (ResourceFactory/createResource)
        rights-resource (ResourceFactory/createResource)
        license-resource (ResourceFactory/createResource)
        assessment-resource (when (:assessment data) (ResourceFactory/createResource))
        temporal-resource (ResourceFactory/createResource)
        
        ;; Special case: mobility data standard for GeoJSON
        mobilityDataStandard (if (:needs-mobility-data-standard-blank-node? data)
                               (let [mds-resource (ResourceFactory/createResource)]
                                 (doto model
                                   (.add mds-resource RDF/type (ResourceFactory/createResource (kw->uri :mobility/MobilityDataStandard)))
                                   (.add mds-resource OWL/versionInfo (ResourceFactory/createPlainLiteral "GeoJSON rfc7946"))
                                   (.add mds-resource
                                         (property :mobility/schema)
                                         (ResourceFactory/createResource "https://geojson.org/schema/GeoJSON.json")))
                                 mds-resource)
                               (ResourceFactory/createResource (:mobility-data-standard-uri data)))]
    
    ;; Add operator properties
    (add-resource-to-model! model operator-agent (get-in data [:operator :properties]))
    
    ;; Add spatial properties
    (add-resource-to-model! model spatial-resource (get-in data [:spatial :properties]))
    
    ;; Add rights properties
    (add-resource-to-model! model rights-resource (get-in data [:rights :properties]))
    
    ;; Add license properties
    (add-resource-to-model! model license-resource (get-in data [:license :properties]))
    
    ;; Add temporal properties
    (add-resource-to-model! model temporal-resource (get-in data [:dataset :properties :dct/temporal :properties]))
    
    ;; Add assessment if exists
    (when assessment-resource
      (add-resource-to-model! model assessment-resource (get-in data [:assessment :properties])))
    
    ;; Add distribution properties
    (let [dist-props (get-in data [:distribution :properties])
          dist-props-with-refs (assoc dist-props
                                     :dct/rights {:type :resource :value rights-resource}
                                     :dct/license {:type :resource :value license-resource}
                                     :mobility/mobilityDataStandard {:type :resource :value mobilityDataStandard})
          dist-props-final (if assessment-resource
                             (assoc dist-props-with-refs 
                                   :dct/result (literal (get-in data [:assessment :result-link])))
                             dist-props-with-refs)]
      (add-resource-to-model! model distribution dist-props-final))
    
    ;; Add dataset properties
    (let [dataset-props (get-in data [:dataset :properties])
          dataset-props-clean (dissoc dataset-props :dct/temporal)
          dataset-props-with-refs (assoc dataset-props-clean
                                        :dcat/distribution {:type :resource :value distribution}
                                        :dct/spatial [{ :type :resource :value (ResourceFactory/createResource "https://w3id.org/stirdata/resource/lau/item/FI_244")}
                                                      {:type :resource :value spatial-resource}]
                                        :dct/publisher {:type :resource :value operator-agent}
                                        :dct/rightsHolder {:type :resource :value operator-agent}
                                        :dct/temporal {:type :resource :value temporal-resource})]
      (add-resource-to-model! model dataset dataset-props-with-refs))
    
    ;; Add dataset to catalog
    (.add model catalog (property :dcat/dataset) dataset)
    
    ;; Return the dataset resource
    dataset))

(defn create-dcat-ap-model
  "https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/#properties-for-dataset"
  [db service operation-areas operator]
  (let [service-id (:ote.db.transport-service/id service)
        operator-id (:ote.db.transport-service/transport-operator-id service)
        model (ModelFactory/createDefaultModel)
        catalog (ResourceFactory/createResource catalog-uri)
        external-interfaces (::t-service/external-interfaces service)
        is-dataservice? false

        ;; TODO: Better Organization page should be added to Finap
        fintraffic-agent (ResourceFactory/createResource (fintraffic-url business-id))]
    (.setNsPrefix model "dcat" dcat)
    (.setNsPrefix model "dct" dct)
    (.setNsPrefix model "foaf" foaf)
    (.setNsPrefix model "mobility" mobility)

    ;; Fintraffic agent
    (.add model fintraffic-agent RDF/type (ResourceFactory/createResource (kw->uri :foaf/Organization)))
    (.add model fintraffic-agent
          (property :foaf/name)
          (ResourceFactory/createStringLiteral "Fintraffic Oy"))
    (.add model catalog
          (property :dct/publisher)
          fintraffic-agent)

    ;; Add catalog
    (create-catalog-model db model catalog)

    ;; Records and Dataset
    
    ;; 🤷‍♀️
    (when operation-areas
      (let [geojson-dataset-resource (create-dataset db service model catalog service-id operator-id operation-areas operator nil)
            _ (create-catalog-record service model catalog nil geojson-dataset-resource fintraffic-agent)]

        (doseq [interface external-interfaces]
          ;; TODO: is interface a dataservice or dataset?
          ;; If interface is not a dataservice, then create accessService
          (if is-dataservice?
            (create-data-service model interface)
            (let [interface-dataset-resource (create-dataset db service model catalog service-id operator-id operation-areas operator interface)
                  _ (create-catalog-record service model catalog interface interface-dataset-resource fintraffic-agent)
                  _ (add-isreferenced-and-related-to-dataset geojson-dataset-resource
                                                             interface-dataset-resource
                                                             model)])))))

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
                       (let [service (first (specql/fetch db ::t-service/transport-service
                                                          (conj (specql/columns ::t-service/transport-service)
                                                                ;; join external interfaces
                                                                [::t-service/external-interfaces
                                                                 (specql/columns ::t-service/external-interface-description)])
                                                          {::t-service/id service-id}))
                             operation-areas (seq (fetch-operation-area-for-service db {:transport-service-id service-id}))
                             operator-id (:ote.db.transport-service/transport-operator-id service)
                             operator (first (specql/fetch db ::t-operator/transport-operator
                                                           (specql/columns ::t-operator/transport-operator)
                                                           {::t-operator/id operator-id}))]
                         (create-dcat-ap-model db service operation-areas operator)))
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
  (let [service-id (if (string? service-id)
                     (Long/parseLong service-id)
                     service-id)
        service (first (specql/fetch db ::t-service/transport-service
                                     (conj (specql/columns ::t-service/transport-service)
                                           ;; join external interfaces
                                           [::t-service/external-interfaces
                                            (specql/columns ::t-service/external-interface-description)])
                                     {::t-service/id service-id}))
        operation-areas (seq (fetch-operation-area-for-service db {:transport-service-id service-id}))
        operator-id (:ote.db.transport-service/transport-operator-id service)
        operator (first (specql/fetch db ::t-operator/transport-operator
                                      (specql/columns ::t-operator/transport-operator)
                                      {::t-operator/id operator-id}))

        model (create-dcat-ap-model db service operation-areas operator)

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
