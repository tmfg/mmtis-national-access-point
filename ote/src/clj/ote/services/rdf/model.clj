(ns ote.services.rdf.model
  "RDF domain model layer - pure Clojure data structures representing RDF.
   This namespace contains all business logic for transforming service data into RDF data structures
   without any dependency on the Jena API."
  (:require [ote.localization :as localization :refer [tr]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification]
            [cheshire.core :as cheshire]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

;; ===== CONSTANTS =====

(defn operator-url [business-id base-url]
  (str base-url "service-search?operators=" (java.net.URLEncoder/encode (str business-id) "UTF-8")))

(def dcat "http://www.w3.org/ns/dcat#")
(def dct "http://purl.org/dc/terms/")
(def cnt "http://www.w3.org/2011/content#")
(def locn "http://www.w3.org/ns/locn#")
(def foaf "http://xmlns.com/foaf/0.1/")
(def mobility "https://w3id.org/mobilitydcat-ap#")
(def owl "http://www.w3.org/2002/07/owl#")
(def euauth "http://publications.europa.eu/resource/authority/")
(def xsd "http://www.w3.org/2001/XMLSchema#")
(def eudata "http://data.europa.eu/")

(def licence-url "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

;; ===== HELPER FUNCTIONS FOR RDF DATA STRUCTURES =====

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

(defn resource
  "Create a resource with properties.
   One-arity version creates a blank node (anonymous resource).
   Two-arity version creates a named resource with the given URI."
  ([properties]
   {:type :resource :uri nil :properties properties})
  ([uri properties]
   {:type :resource :uri uri :properties properties}))

(defn datetime
  "Create a typed literal for an xsd:dateTime value.
   Formats the timestamp to use 'T' separator for XSD dateTime compliance."
  [value]
  (let [formatted (-> (str value)
                      (str/replace #" " "T"))]
    (typed-literal formatted "http://www.w3.org/2001/XMLSchema#dateTime")))

;; ===== DOMAIN LOGIC FUNCTIONS =====

(defn localized-text-with-key
  "Usage: (localized-text-with-key \"fi\" [:email-templates :password-reset :subject])
   Catches markdown translation exceptions and returns the first argument as plain text."
  [language key]
  (try
    (localization/with-language language (tr key))
    (catch clojure.lang.ExceptionInfo e
      (if (= (.getMessage e) "Markdown formatted translations not supported.")
        ;; Return the first argument from the markdown translation as plain text
        (first (:args (ex-data e)))
        ;; Re-throw other ExceptionInfo instances unchanged
        (throw e)))))

(defn service->mobility-theme [service]
  (case (:ote.db.transport-service/sub-type service)
    :taxi "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-non-scheduled-transport"
    :request "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-non-scheduled-transport"
    :schedule "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-scheduled-transport"
    :terminal "https://w3id.org/mobilitydcat-ap/mobility-theme/other"
    :rentals "https://w3id.org/mobilitydcat-ap/mobility-theme/sharing-and-hiring-services"
    :parking "https://w3id.org/mobilitydcat-ap/mobility-theme/parking-service-and-rest-area-information"
    :brokerage "https://w3id.org/mobilitydcat-ap/mobility-theme/sharing-and-hiring-services"
    :other "https://w3id.org/mobilitydcat-ap/mobility-theme/other"
    ;; default case
    "https://w3id.org/mobilitydcat-ap/mobility-theme/other"))

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
    nil))

(defn service->transport-mode [service]
  (let [transport-type (first (::t-service/transport-type service))
        sub-type (:ote.db.transport-service/sub-type service)]
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

(defn interface->mobility-data-standard [interface]
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
    "https://w3id.org/mobilitydcat-ap/mobility-data-standard/other"))

(defn interface->format-extent [interface]
  (case (str (first (::t-service/format interface)))
    "GTFS" "http://publications.europa.eu/resource/authority/file-type/GTFS"
    "GTFS-RT" "http://publications.europa.eu/resource/authority/file-type/GTFS"
    "GBFS" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "Kalkati" "http://publications.europa.eu/resource/authority/file-type/XML"
    "NeTEx" "http://publications.europa.eu/resource/authority/file-type/XML"
    "Datex II" "http://publications.europa.eu/resource/authority/file-type/XML"

    "GeoJSON" "http://publications.europa.eu/resource/authority/file-type/GEOJSON"
    "JSON" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "CSV" "http://publications.europa.eu/resource/authority/file-type/CSV"
    "SIRI" "http://publications.europa.eu/resource/authority/file-type/XML"

    ;; probably not an actually-correct default but at least its technically correct
    "http://publications.europa.eu/resource/authority/file-type/BIN"))

(defn interface->rights-url [interface]
  (if (::t-service/license interface)
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided"
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/other"))

;; ===== GEOJSON-SPECIFIC FUNCTIONS =====

(defn geojson-dataset-uri
  "Generate dataset URI for a service (without operation area id)."
  [service base-url]
  (let [operator-id (:ote.db.transport-service/transport-operator-id service)
        service-id (:ote.db.transport-service/id service)]
    (str base-url "dataset/" operator-id "/" service-id)))

(defn geojson-access-url
  "Generate access URL for the GeoJSON export endpoint (service level)."
  [operator-id service-id base-url]
  (str base-url "export/geojson/" operator-id "/" service-id))

(defn geojson-download-url
  "Generate download URL for the GeoJSON export endpoint (service level)."
  [operator-id service-id base-url]
  (str base-url "export/geojson/" operator-id "/" service-id))

(def geojson-format "http://publications.europa.eu/resource/authority/file-type/GEOJSON")

(def geojson-license-url "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

(defn geojson-distribution-description [service]
  (str "Olennaiset liikennepalvelutiedot palvelusta " (::t-service/name service)))

(defn geojson-last-modified [service]
  (:ote.db.modification/modified service))

(def geojson-accrual-periodicity "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED")

(def fin-swe-eng-languages
  (mapv uri
        ["http://publications.europa.eu/resource/authority/language/FIN"
         "http://publications.europa.eu/resource/authority/language/SWE"
         "http://publications.europa.eu/resource/authority/language/ENG"]))

(def geojson-intended-information-service "https://w3id.org/mobilitydcat-ap/intended-information-service/other")

(defn operation-areas->geometry-collection
  "Create a GeometryCollection from operation areas without styling."
  [operation-areas]
  (when (seq operation-areas)
    (cheshire/generate-string
      {:type "GeometryCollection"
       :geometries (mapv 
                     (fn [area]
                       (cheshire/decode (:geojson area) keyword))
                     operation-areas)})))

;; ===== EXTERNAL INTERFACE-SPECIFIC FUNCTIONS =====

(defn interface-dataset-uri [service interface base-url]
  (let [operator-id (:ote.db.transport-service/transport-operator-id service)
        service-id (:ote.db.transport-service/id service)
        interface-id (::t-service/id interface)]
    (str base-url "dataset/" operator-id "/" service-id "/interface/" interface-id)))

(defn interface-access-url [interface]
  (some-> (get-in interface [::t-service/external-interface ::t-service/url])
          str/trim))

(defn interface-download-url [interface]
  (some-> (get-in interface [::t-service/external-interface ::t-service/url])
          str/trim))

(defn interface-format [interface]
  (interface->format-extent interface))

(def interface-license-url "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

(defn interface-distribution-description [interface]
  (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) ""))

(defn interface-last-modified [interface latest-conversion-status]
  (cond
    latest-conversion-status
    (:created latest-conversion-status)
    
    (::t-service/gtfs-imported interface)
    (::t-service/gtfs-imported interface)
    
    :else nil))

(def interface-accrual-periodicity "http://publications.europa.eu/resource/authority/frequency/UNKNOWN")

;; ===== COMMON HELPER FUNCTIONS =====

(defn compute-operator-uri [operator base-url]
  (operator-url (::t-operator/business-id operator) base-url))

(defn operator->agent
  "Create a standalone foaf:Agent resource for an operator."
  [operator base-url] 
  (let [operator-uri (compute-operator-uri operator base-url)
        operator-name (:ote.db.transport-operator/name operator)]
    (resource operator-uri
              {:rdf/type (uri :foaf/Agent)
               :foaf/name (literal operator-name)})))

(defn service->mobility-themes [service]
  (if-let [sub-theme (service->sub-theme service)]
    [(service->mobility-theme service) sub-theme]
    [(service->mobility-theme service)]))

(defn interface->intended-information-service [interface]
  (let [data-content (some-> interface ::t-service/data-content first name)]
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
      (do
        (log/warnf "Unknown datacontent %s" (pr-str data-content))
        "https://w3id.org/mobilitydcat-ap/intended-information-service/other"))))


;; ===== RDF DATA STRUCTURE CREATION =====

;; Common helpers
(defn mobility-data-standard-data [needs-blank-node? standard-uri]
  (if needs-blank-node?
    (resource {:rdf/type (uri :mobility/MobilityDataStandard)
               :owl/versionInfo (literal "GeoJSON rfc7946")
               :mobility/schema (uri "https://geojson.org/schema/GeoJSON.json")})
    (uri standard-uri)))

(defn isreferenced-and-related-data [geojson-dataset-id interface-dataset-id]
  [{:subject geojson-dataset-id
    :properties {:dct/relation (uri interface-dataset-id)}}
   {:subject interface-dataset-id
    :properties {:dct/isReferencedBy (uri geojson-dataset-id)}}])

;; ===== GEOJSON RDF GENERATION =====

(defn geojson->distribution
  "Create a distribution resource for the GeoJSON export endpoint."
  [service base-url]
  (let [service-id (:ote.db.transport-service/id service)
        operator-id (:ote.db.transport-service/transport-operator-id service)
        access-url (geojson-access-url operator-id service-id base-url)
        download-url (geojson-download-url operator-id service-id base-url)
        distribution-description (geojson-distribution-description service)
        rights-url "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided-free-of-charge"
        distribution-props {:rdf/type (uri :dcat/Distribution)
                            :dcat/accessURL (uri access-url)
                            :dcat/downloadURL (uri download-url)
                            :dct/format (uri geojson-format)
                            :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                                    :dct/identifier (uri geojson-license-url)})
                            :dct/rights (resource {:rdf/type (uri :dct/RightsStatement)
                                                   :dct/type (uri rights-url)})
                            :mobility/applicationLayerProtocol (uri "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https")
                            :mobility/description (lang-literal distribution-description "fi")
                            :mobility/communicationMethod (uri "https://w3id.org/mobilitydcat-ap/communication-method/pull")
                            :mobility/mobilityDataStandard (mobility-data-standard-data true nil)
                            :cnt/characterEncoding (literal "UTF-8")
                            :mobility/grammar (uri "https://w3id.org/mobilitydcat-ap/grammar/json-schema")}]
    (resource distribution-props)))

(defn geojson->dataset
  "Create a dataset resource for all operation areas of a service."
  [service operator operation-areas distribution base-url]
  (let [dataset-uri (geojson-dataset-uri service base-url)
        operator-uri (uri (compute-operator-uri operator base-url))
        municipality-uri (uri (:municipality service))
        last-modified (geojson-last-modified service)
        combined-geojson (operation-areas->geometry-collection operation-areas)
        spatial-data (if combined-geojson
                       [municipality-uri
                        (resource {:rdf/type (uri :dct/Location)
                                   :locn/geometry (typed-literal combined-geojson
                                                                 "https://www.iana.org/assignments/media-types/application/vnd.geo+json")})]
                       [municipality-uri])
        dataset-props (cond-> {:rdf/type (uri :dcat/Dataset)
                               :dct/title (literal (:ote.db.transport-service/name service))
                               :dct/description (literal (or (get-in service [::t-service/description 0 ::t-service/text]) ""))
                               :mobility/transportMode (uri (service->transport-mode service))
                               :dct/accrualPeriodicity (uri geojson-accrual-periodicity)
                               :mobility/mobilityTheme (mapv uri (service->mobility-themes service))
                               :dct/spatial spatial-data
                               :mobility/georeferencingMethod (uri "https://w3id.org/mobilitydcat-ap/georeferencing-method/geocoordinates")
                               :dct/theme (uri "http://publications.europa.eu/resource/authority/data-theme/TRAN")
                               :dct/identifier (uri dataset-uri)
                               :mobility/intendedInformationService (uri geojson-intended-information-service)
                               :dct/publisher operator-uri
                               :dct/rightsHolder operator-uri
                               :dcat/distribution [distribution]
                               :dct/conformsTo (uri "http://www.opengis.net/def/crs/EPSG/0/4326")
                               :dct/language fin-swe-eng-languages}
                        
                        last-modified
                        (assoc :dct/modified (datetime last-modified)))]
    (resource dataset-uri dataset-props)))

(defn geojson->catalog-record [service dataset-uri fintraffic-uri]
  (let [created (::modification/created service)
        modified (::modification/modified service)]
    (resource {:rdf/type (uri :dcat/CatalogRecord)
               :dct/created (datetime created)
               :dct/language fin-swe-eng-languages
               :foaf/primaryTopic (uri dataset-uri)
               :dct/modified (datetime modified)
               :dct/publisher (uri fintraffic-uri)})))

(defn merge-rdf-models [model1 model2]
  {:datasets (vec (concat (:datasets model1) (:datasets model2)))
   :catalog-records (vec (concat (:catalog-records model1) (:catalog-records model2)))})

(defn geojson->rdf
  "Create RDF data for a single GeoJSON dataset containing all operation areas."
  [service operation-areas operator fintraffic-uri base-url]
  (if (seq operation-areas)
    (let [distribution (geojson->distribution service base-url)
          dataset (geojson->dataset service operator operation-areas distribution base-url)
          dataset-uri (:uri dataset)
          catalog-record (geojson->catalog-record service dataset-uri fintraffic-uri)]
      {:datasets [dataset]
       :catalog-records [catalog-record]})
    ;; No operation areas - return empty model
    {:datasets []
     :catalog-records []}))

;; ===== EXTERNAL INTERFACE RDF GENERATION =====

(defn interface->distribution [service interface latest-conversion-status base-url]
  (let [access-url (interface-access-url interface)
        download-url (interface-download-url interface)
        format (interface-format interface)
        distribution-description (interface-distribution-description interface)
        mobility-data-standard-uri (interface->mobility-data-standard interface)
        rights-url (interface->rights-url interface)
        vaco-validation-timestamp (when latest-conversion-status
                                    (:tis_polling_completed latest-conversion-status))
        vaco-result-link (when latest-conversion-status
                           (:tis-magic-link latest-conversion-status))
        distribution-props (cond-> {:rdf/type (uri :dcat/Distribution)
                                    :dcat/accessURL (uri access-url)
                                    :dcat/downloadURL (uri download-url)
                                    :dct/format (uri format)
                                    :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                                            :dct/identifier (uri interface-license-url)})
                                    :dct/rights (resource {:rdf/type (uri :dct/RightsStatement)
                                                           :dct/type (uri rights-url)})
                                    :mobility/applicationLayerProtocol (uri "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https")
                                    :mobility/description (lang-literal distribution-description "fi")
                                    :mobility/mobilityDataStandard (uri mobility-data-standard-uri)}
                             
                             vaco-validation-timestamp
                             (assoc :dct/result (literal vaco-result-link)))]
    (resource distribution-props)))

(defn interface->dataset [service operator operation-areas interface latest-conversion-status distribution base-url]
  (let [dataset-uri (interface-dataset-uri service interface base-url)
        operator-uri (compute-operator-uri operator base-url)
        service-name (:ote.db.transport-service/name service)
        access-url (interface-access-url interface)
        dataset-title (str service-name " " access-url)
        service-description (or (get-in service [::t-service/description 0 ::t-service/text]) "")
        municipality (:municipality service)
        transport-mode (service->transport-mode service)
        mobility-themes (service->mobility-themes service)
        intended-info-service (interface->intended-information-service interface)
        last-modified (interface-last-modified interface latest-conversion-status)
        operation-area-geojson (:geojson (first operation-areas))
        mobility-themes-uris (vec (map uri mobility-themes))
        spatial-data (if operation-area-geojson
                       [(uri municipality)
                        (resource {:rdf/type (uri :dct/Location)
                                   :locn/geometry (typed-literal operation-area-geojson
                                                                 "https://www.iana.org/assignments/media-types/application/vnd.geo+json")})]
                       [(uri municipality)])
        dataset-props (cond-> {:rdf/type (uri :dcat/Dataset)
                               :dct/title (literal dataset-title)
                               :dct/description (literal service-description)
                               :mobility/transportMode (uri transport-mode)
                               :dct/accrualPeriodicity (uri interface-accrual-periodicity)
                               :mobility/mobilityTheme mobility-themes-uris
                               :dct/spatial spatial-data
                               :mobility/georeferencingMethod (uri "https://w3id.org/mobilitydcat-ap/georeferencing-method/geocoordinates")
                               :dct/theme (uri "http://publications.europa.eu/resource/authority/data-theme/TRAN")
                               :dct/identifier (uri dataset-uri)
                               :mobility/intendedInformationService (uri intended-info-service)
                               :dct/publisher (uri operator-uri)
                               :dct/rightsHolder (uri operator-uri)
                               :dcat/distribution [distribution]}

                        last-modified
                        (assoc :dct/modified (datetime last-modified)))]
    (resource dataset-uri dataset-props)))

(defn interface->catalog-record [service dataset-uri fintraffic-uri]
  (let [created (::modification/created service)
        modified (::modification/modified service)]
    (resource {:rdf/type (uri :dcat/CatalogRecord)
               :dct/created (datetime created)
               :dct/language fin-swe-eng-languages
               :foaf/primaryTopic (uri dataset-uri)
               :dct/modified (datetime modified)
               :dct/publisher (uri fintraffic-uri)})))

(defn interface->rdf [service operation-areas operator interface latest-conversion-status fintraffic-uri base-url]
  (let [distribution (interface->distribution service interface latest-conversion-status base-url)
        dataset (interface->dataset service operator operation-areas interface latest-conversion-status distribution base-url)
        dataset-uri (:uri dataset)
        catalog-record (interface->catalog-record service dataset-uri fintraffic-uri)]
    {:datasets [dataset]
     :catalog-records [catalog-record]}))

;; ===== CATALOG GENERATION =====

(defn domain->catalog [catalog-records dataset-uris latest-publication fintraffic-uri base-url]
  (let [catalog-uri (str base-url "catalog")]
    (resource catalog-uri
              {:rdf/type (uri :dcat/Catalog)
               :dct/title (lang-literal "Finap.fi - NAP - National Access Point" "en")
               :dct/description [(lang-literal (localized-text-with-key "fi" [:front-page :column-NAP]) "fi")
                                 (lang-literal (localized-text-with-key "sv" [:front-page :column-NAP]) "sv")
                                 (lang-literal (localized-text-with-key "en" [:front-page :column-NAP]) "en")]
               :foaf/homepage (uri "https://www.finap.fi/")
               :dct/spatial (resource {:rdf/type (uri :dct/Location)
                                       :dct/identifier (uri "http://publications.europa.eu/resource/authority/country/FIN")})
               :dct/language fin-swe-eng-languages
               :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                       :dct/identifier (uri licence-url)})
               :dct/issued (datetime "2018-01-01T00:00:01Z")
               :dct/themeTaxonomy (uri "https://w3id.org/mobilitydcat-ap/mobility-theme")
               :dct/modified (datetime latest-publication)
               :dct/identifier (literal catalog-uri)
               :dct/publisher (uri fintraffic-uri)
               :dcat/record (vec catalog-records)
               :dcat/dataset (vec (map uri dataset-uris))})))

(defn service-data->rdf
  "Create complete RDF data structure including catalog and fintraffic agent.
   Calls geojson->rdf for geojson and interface->rdf for all interfaces, merges results,
   and creates catalog. Returns a map with :catalog, :datasets, :distributions,
   :catalog-records, :relationships, :ns-prefixes, and :fintraffic-agent."
  [service-data base-url]
  (let [{:keys [service operation-areas operator validation-data latest-publication]} service-data
        external-interfaces (::t-service/external-interfaces service)
        fintraffic-uri "https://www.fintraffic.fi/en"
        fintraffic-agent (resource fintraffic-uri
                                   {:rdf/type (uri :foaf/Agent)
                                    :foaf/name (literal "Fintraffic Oy")})
        operator-agent (when operator (operator->agent operator base-url))
        result (if operation-areas
                 (let [;; Generate RDF data for GeoJSON dataset
                       geojson-rdf (geojson->rdf service operation-areas operator fintraffic-uri base-url)
                       
                       ;; Process interfaces and collect their RDF data
                       interface-rdf-models (doall
                                             (for [interface external-interfaces]
                                               (let [interface-id (::t-service/id interface)
                                                     validation-status (get validation-data interface-id)]
                                                 (interface->rdf service operation-areas operator interface validation-status fintraffic-uri base-url))))
                       
                       ;; Merge all RDF models
                       merged-rdf (reduce merge-rdf-models geojson-rdf interface-rdf-models)
                       
                       ;; Extract URIs for catalog and relationships
                       all-dataset-uris (map :uri (:datasets merged-rdf))
                       geojson-uris (map :uri (:datasets geojson-rdf))
                       interface-uris (map :uri (mapcat :datasets interface-rdf-models))
                       
                       ;; Create relationships between datasets (each GeoJSON dataset relates to each interface dataset)
                       relationships (vec (for [geojson-uri geojson-uris
                                               interface-uri interface-uris]
                                           (isreferenced-and-related-data geojson-uri interface-uri)))
                       relationships (vec (apply concat relationships))
                       
                       ;; Create catalog with embedded records and datasets
                       catalog-resource (domain->catalog (:catalog-records merged-rdf) all-dataset-uris latest-publication fintraffic-uri base-url)]
                   
                   (assoc merged-rdf 
                          :catalog catalog-resource
                          :relationships relationships))
                 
                 ;; No operation areas - create empty catalog
                 (let [catalog-resource (domain->catalog [] [] latest-publication fintraffic-uri base-url)]
                   {:catalog catalog-resource
                    :datasets []
                    :catalog-records []
                    :relationships []}))]
    (assoc result 
           :fintraffic-agent fintraffic-agent
           :operator-agent operator-agent
           :ns-prefixes [["dcat" dcat]
                         ["dct" dct]
                         ["foaf" foaf]
                         ["mobility" mobility]
                         ["cnt" cnt]
                         ["locn" locn]
                         ["owl" owl]
                         ["euauth" euauth]
                         ["xsd" xsd]
                         ["eudata" eudata]])))
