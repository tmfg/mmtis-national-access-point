(ns ote.services.rdf.model
  "RDF domain model layer - pure Clojure data structures representing RDF.
   This namespace contains all business logic for transforming service data into RDF data structures
   without any dependency on the Jena API."
  (:require [ote.localization :as localization :refer [tr]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification]
            [taoensso.timbre :as log]))

;; ===== CONSTANTS =====

(def fintraffic-business-id "2942108-7")

(defn operator-url [business-id base-url]
  (str base-url "service-search?operators=" business-id))

(def dcat "http://www.w3.org/ns/dcat#")
(def dct "http://purl.org/dc/terms/")
(def cnt "http://www.w3.org/2011/content#")
(def locn "http://www.w3.org/ns/locn#")
(def foaf "http://xmlns.com/foaf/0.1/")
(def mobility "http://w3id.org/mobilitydcat-ap#")
(def owl "http://www.w3.org/2002/07/owl#")
(def euauth "https://publications.europa.eu/resource/authority/")
(def xsd "http://www.w3.org/2001/XMLSchema#")
(def eudata "http://data.europa.eu/")

(def licence-url "https://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

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

(defn resource->uri
  "Convert a resource to a URI reference.
   Takes a resource map and returns a URI reference to that resource."
  [resource]
  (uri (:uri resource)))

(defn datetime
  "Create a typed literal for an xsd:dateTime value."
  [value]
  (typed-literal (str value) "http://www.w3.org/2001/XMLSchema#dateTime"))

;; ===== DOMAIN LOGIC FUNCTIONS =====

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
    "GTFS" "https://publications.europa.eu/resource/authority/file-type/GTFS"
    "GTFS-RT" "https://publications.europa.eu/resource/authority/file-type/GTFS"
    "GBFS" "https://publications.europa.eu/resource/authority/file-type/JSON"
    "Kalkati" "https://publications.europa.eu/resource/authority/file-type/XML"
    "NeTEx" "https://publications.europa.eu/resource/authority/file-type/XML"
    "Datex II" "https://publications.europa.eu/resource/authority/file-type/XML"
    
    "GeoJSON" "https://publications.europa.eu/resource/authority/file-type/JSON"
    "JSON" "https://publications.europa.eu/resource/authority/file-type/JSON"
    "CSV" "https://publications.europa.eu/resource/authority/file-type/CSV"
    "SIRI" "https://publications.europa.eu/resource/authority/file-type/XML"

    ;; probably not an actually-correct default but at least its technically correct
    "https://publications.europa.eu/resource/authority/file-type/BIN"))

(defn interface->rights-url [interface]
  (if (::t-service/license interface)
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided"
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/other"))

;; ===== GEOJSON-SPECIFIC FUNCTIONS =====

;; TODO placeholder URIs
(defn geojson-dataset-uri [service operation-area base-url]
  (str base-url "rdf/" (:ote.db.transport-service/id service) "/area/" (:id operation-area)))

(defn geojson-distribution-uri [service operation-area base-url]
  (str base-url "rdf/" (:ote.db.transport-service/id service) "/distribution/area/" (:id operation-area)))

(defn geojson-access-url [operator-id service-id operation-area-id base-url]
  (str base-url "export/geojson/" operator-id "/" service-id "/area/" operation-area-id))

(defn geojson-download-url [operator-id service-id operation-area-id base-url]
  (str base-url "export/geojson/" operator-id "/" service-id "/area/" operation-area-id))

(def geojson-format "https://publications.europa.eu/resource/authority/file-type/GEOJSON")

(def geojson-rights-url "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided-free-of-charge")

(def geojson-license-url "https://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

(defn geojson-distribution-description [service]
  (str "Olennaiset liikennepalvelutiedot palvelusta " (::t-service/name service)))

(defn geojson-last-modified [service]
  (:ote.db.modification/modified service))

(def geojson-accrual-periodicity "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED")

;; TODO Why don't we need LinguisticSystem here like in the catalog record? Does this not end up in a SHACL validation?
(def geojson-dataset-languages
  ["https://publications.europa.eu/resource/authority/language/FIN"
   "https://publications.europa.eu/resource/authority/language/SWE"
   "https://publications.europa.eu/resource/authority/language/ENG"])

(def geojson-intended-information-service "https://w3id.org/mobilitydcat-ap/intended-information-service/other")

;; ===== EXTERNAL INTERFACE-SPECIFIC FUNCTIONS =====

(defn interface-dataset-uri [interface]
  (get-in interface [::t-service/external-interface ::t-service/url]))

(defn interface-distribution-uri [service interface base-url]
  (str base-url "rdf/" (:ote.db.transport-service/id service) "/distribution/interface/" (::t-service/id interface)))

(defn interface-access-url [interface]
  (get-in interface [::t-service/external-interface ::t-service/url]))

(defn interface-download-url [interface]
  (get-in interface [::t-service/external-interface ::t-service/url]))

(defn interface-format [interface]
  (interface->format-extent interface))

(def interface-license-url "https://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

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

(defn domain->data-service [interface]
  (let [endpointUrl (get-in interface [::t-service/external-interface ::t-service/url])
        title (localized-text-with-key "fi" [:enums :ote.db.transport-service/interface-data-content :route-and-schedule])
        description (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) "")
        rights-url (interface->rights-url interface)]
    (resource {:rdf/type (uri :dct/DataService)
               :dcat/endpointURL (uri endpointUrl)
               :dct/title (uri title)
               :dct/description (uri description)
               :dct/rights (resource {:rdf/type (uri :dct/RightsStatement)
                                      :dct/type (uri rights-url)})
               :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                       :dct/identifier (uri licence-url)})})))

;; ===== GEOJSON RDF GENERATION =====

(defn geojson->distribution [service operation-area base-url]
  (let [service-id (:ote.db.transport-service/id service)
        operator-id (:ote.db.transport-service/transport-operator-id service)
        operation-area-id (:id operation-area)
        distribution-uri (geojson-distribution-uri service operation-area base-url)
        access-url (geojson-access-url operator-id service-id operation-area-id base-url)
        download-url (geojson-download-url operator-id service-id operation-area-id base-url)
        distribution-description (geojson-distribution-description service)
        distribution-props {:rdf/type (uri :dcat/Distribution)
                            :dcat/accessURL (uri access-url)
                            :dcat/downloadURL (uri download-url)
                            :dct/format (uri geojson-format)
                            :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                                    :dct/identifier (uri geojson-license-url)})
                            :mobility/applicationLayerProtocol (uri "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https")
                            :mobility/description (lang-literal distribution-description "fi")
                            :mobility/communicationMethod (uri "https://w3id.org/mobilitydcat-ap/communication-method/pull")
                            :mobility/mobilityDataStandard (mobility-data-standard-data true nil)
                            :cnt/characterEncoding (literal "UTF-8")
                            :mobility/grammar (uri "https://w3id.org/mobilitydcat-ap/grammar/json-schema")}]
    (resource distribution-uri distribution-props)))

(defn geojson->dataset [service operator operation-area distribution base-url]
  (let [dataset-uri (geojson-dataset-uri service operation-area base-url)
        operator-uri (compute-operator-uri operator base-url)
        service-name (:ote.db.transport-service/name service)
        area-name (get-in operation-area [:description 0 ::t-service/text] "")
        dataset-title (if (seq area-name)
                        (str service-name " - " area-name)
                        service-name)
        service-description (or (get-in service [::t-service/description 0 ::t-service/text]) "")
        municipality (:municipality service)
        transport-mode (service->transport-mode service)
        mobility-themes (service->mobility-themes service)
        last-modified (geojson-last-modified service)
        operation-area-geojson (:geojson operation-area)
        mobility-themes-uris (vec (map uri mobility-themes))
        dataset-languages-uris (vec (map uri geojson-dataset-languages))
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
                               :dct/accrualPeriodicity (uri geojson-accrual-periodicity)
                               :mobility/mobilityTheme mobility-themes-uris
                               :dct/spatial spatial-data
                               :mobility/georeferencingMethod (uri "https://w3id.org/mobilitydcat-ap/georeferencing-method/geocoordinates")
                               :dct/theme (uri "https://publications.europa.eu/resource/authority/data-theme/TRAN")
                               :mobility/identifier (uri dataset-uri)
                               :mobility/intendedInformationService (uri geojson-intended-information-service)
                               :dct/publisher (uri operator-uri)
                               :dct/rightsHolder (uri operator-uri)
                               :dcat/distribution [(resource->uri distribution)]
                               :dct/conformsTo (uri "http://www.opengis.net/def/crs/EPSG/0/4326")
                               :dct/language dataset-languages-uris}
                        
                        last-modified
                        (assoc :dct/modified (datetime last-modified)))]
    (resource dataset-uri dataset-props)))

(defn geojson->catalog-record [service dataset-uri fintraffic-uri]
  (let [created (::modification/created service)
        modified (::modification/modified service)]
    (resource {:rdf/type (uri :dcat/CatalogRecord)
               :dct/created (datetime created)
               :dct/language [(uri "https://publications.europa.eu/resource/authority/language/FIN")
                              (uri "https://publications.europa.eu/resource/authority/language/SWE")
                              (uri "https://publications.europa.eu/resource/authority/language/ENG")]
               :foaf/primaryTopic (uri dataset-uri)
               :dct/modified (datetime modified)
               :dct/publisher (uri fintraffic-uri)})))

(defn merge-rdf-models [model1 model2]
  {:distributions (vec (concat (:distributions model1) (:distributions model2)))
   :assessments (vec (concat (:assessments model1) (:assessments model2)))
   :datasets (vec (concat (:datasets model1) (:datasets model2)))
   :catalog-records (vec (concat (:catalog-records model1) (:catalog-records model2)))})

(defn geojson->rdf [service operation-areas operator fintraffic-uri base-url]
  (if (seq operation-areas)
    (let [area-models (for [operation-area operation-areas]
                        (let [distribution (geojson->distribution service operation-area base-url)
                              dataset (geojson->dataset service operator operation-area distribution base-url)
                              dataset-uri (:uri dataset)
                              catalog-record (geojson->catalog-record service dataset-uri fintraffic-uri)]
                          {:distributions [distribution]
                           :assessments []
                           :datasets [dataset]
                           :catalog-records [catalog-record]}))
          merged (reduce merge-rdf-models 
                        {:distributions [] :assessments [] :datasets [] :catalog-records []}
                        area-models)]
      merged)
    ;; No operation areas - return empty model
    {:distributions []
     :assessments []
     :datasets []
     :catalog-records []}))

;; ===== EXTERNAL INTERFACE RDF GENERATION =====

(defn interface->distribution [service interface latest-conversion-status base-url]
  (let [distribution-uri (interface-distribution-uri service interface base-url)
        access-url (interface-access-url interface)
        download-url (interface-download-url interface)
        format (interface-format interface)
        distribution-description (interface-distribution-description interface)
        mobility-data-standard-uri (interface->mobility-data-standard interface)
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
                                    :mobility/applicationLayerProtocol (uri "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https")
                                    :mobility/description (lang-literal distribution-description "fi")
                                    :mobility/communicationMethod (uri "https://w3id.org/mobilitydcat-ap/communication-method/pull")
                                    :mobility/mobilityDataStandard (uri mobility-data-standard-uri)}
                             
                             vaco-validation-timestamp
                             (assoc :dct/result (literal vaco-result-link)))]
    (resource distribution-uri distribution-props)))

(defn interface->dataset [service operator operation-areas interface latest-conversion-status distribution base-url]
  (let [dataset-uri (interface-dataset-uri interface)
        operator-uri (compute-operator-uri operator base-url)
        operator-name (:ote.db.transport-operator/name operator)
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
                               :dct/theme (uri "https://publications.europa.eu/resource/authority/data-theme/TRAN")
                               :mobility/identifier (uri dataset-uri)
                               :mobility/intendedInformationService (uri intended-info-service)
                               :dct/publisher (uri operator-uri)
                               :dct/rightsHolder (uri operator-uri)
                               :dcat/distribution [(resource->uri distribution)]}

                        last-modified
                        (assoc :dct/modified (datetime last-modified)))]
    (resource dataset-uri dataset-props)))

(defn interface->assessment [latest-conversion-status]
  (when latest-conversion-status
    (let [vaco-validation-timestamp (:tis_polling_completed latest-conversion-status)]
      (when vaco-validation-timestamp
        (resource {:rdf/type (uri :mobility/Assessment)
                   :dct/date (datetime vaco-validation-timestamp)})))))

(defn interface->catalog-record [service dataset-uri fintraffic-uri]
  (let [created (::modification/created service)
        modified (::modification/modified service)]
    (resource {:rdf/type (uri :dcat/CatalogRecord)
               :dct/created (datetime created)
               :dct/language [(uri "https://publications.europa.eu/resource/authority/language/FIN")
                              (uri "https://publications.europa.eu/resource/authority/language/SWE")
                              (uri "https://publications.europa.eu/resource/authority/language/ENG")]
               :foaf/primaryTopic (uri dataset-uri)
               :dct/modified (datetime modified)
               :dct/publisher (uri fintraffic-uri)})))

(defn interface->rdf [service operation-areas operator interface latest-conversion-status fintraffic-uri base-url]
  (let [distribution (interface->distribution service interface latest-conversion-status base-url)
        dataset (interface->dataset service operator operation-areas interface latest-conversion-status distribution base-url)
        dataset-uri (:uri dataset)
        catalog-record (interface->catalog-record service dataset-uri fintraffic-uri)]
    {:distributions [distribution]
     :assessments (if-let [assessment (interface->assessment latest-conversion-status)] [assessment] [])
     :datasets [dataset]
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
               :dct/language [(uri "https://publications.europa.eu/resource/authority/language/FIN")
                              (uri "https://publications.europa.eu/resource/authority/language/SWE")
                              (uri "https://publications.europa.eu/resource/authority/language/ENG")]
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
   :assessments, :catalog-records, :data-services, :relationships, :ns-prefixes, and :fintraffic-agent."
  [service-data base-url]
  (let [{:keys [service operation-areas operator validation-data latest-publication]} service-data
        external-interfaces (::t-service/external-interfaces service)
        fintraffic-uri "https://www.fintraffic.fi/en"
        fintraffic-agent (resource fintraffic-uri
                                   {:rdf/type (uri :foaf/Agent)
                                    :foaf/name (literal "Fintraffic Oy")})
        operator-agent (when operator (operator->agent operator base-url))
        ;; TODO this is probably not correct
        is-dataservice? false
        ;; Create data services for interfaces that need them
        data-services (if is-dataservice?
                       (vec (map domain->data-service external-interfaces))
                       [])
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
                    :distributions []
                    :assessments []
                    :datasets []
                    :catalog-records []
                    :relationships []}))]
    (assoc result 
           :fintraffic-agent fintraffic-agent
           :operator-agent operator-agent
           :data-services data-services
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
