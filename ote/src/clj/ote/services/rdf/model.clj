(ns ote.services.rdf.model
  "RDF domain model layer - pure Clojure data structures representing RDF.
   This namespace contains all business logic for transforming service data into RDF data structures
   without any dependency on the Jena API."
  (:require [ote.localization :as localization :refer [tr]]
            [clojure.string :as str]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.db.modification :as modification]
            [taoensso.timbre :as log]))

;; ===== CONSTANTS =====

(def base-uri "http://localhost:3000/")
(def service-id 1)
(def business-id "2942108-7")

(defn fintraffic-url [business-id]
  (str "https://finap.fi/service-search?operators=" business-id))

(defn operator-url [business-id]
  (str "https://finap.fi/service-search?operators=" business-id))

(def dcat "http://www.w3.org/ns/dcat#")
(def dct "http://purl.org/dc/terms/")
#_(def cnt "http://www.w3.org/2011/content#")
#_(def locn "http://www.w3.org/ns/locn#")
(def foaf "http://xmlns.com/foaf/0.1/")
(def mobility "http://www.w3.org/ns/mobilitydcatap#")
#_(def owl "http://www.w3.org/2002/07/owl#")

(def catalog-uri (str base-uri "catalog"))
(def dataset-base-uri (str base-uri "rdf/" service-id))
(def distribution-base-uri (str dataset-base-uri "/distribution"))
(def distribution-interface-base-uri (str dataset-base-uri "/distribution/interface"))
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

(defn resource->uri
  "Convert a resource to a URI reference.
   Takes a resource map and returns a URI reference to that resource."
  [resource]
  (uri (:uri resource)))

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
    "GTFS" "http://publications.europa.eu/resource/authority/file-type/CSV"
    "GTFS-RT" "http://publications.europa.eu/resource/authority/file-type/CSV"
    "GBFS" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "Kalkati" "http://publications.europa.eu/resource/authority/file-type/XML"
    "NeTEx" "http://publications.europa.eu/resource/authority/file-type/XML"
    "GeoJSON" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "JSON" "http://publications.europa.eu/resource/authority/file-type/JSON"
    "CSV" "http://publications.europa.eu/resource/authority/file-type/CSV"
    "Datex II" "http://publications.europa.eu/resource/authority/file-type/XML"
    "SIRI" "http://publications.europa.eu/resource/authority/file-type/XML"
    nil))

(defn interface->rights-url [interface]
  (if (::t-service/license interface)
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided"
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/other"))

(defn compute-dataset-uri [service interface]
  (if (nil? interface)
    (str dataset-base-uri (:ote.db.transport-service/id service))
    (get-in interface [::t-service/external-interface ::t-service/url])))

(defn compute-distribution-uri [service interface]
  (if (nil? interface)
    (str distribution-base-uri "/" (:ote.db.transport-service/id service))
    (str distribution-interface-base-uri "/" (::t-service/id interface))))

(defn compute-operator-uri [operator]
  (operator-url (::t-operator/business-id operator)))

(defn compute-access-url [service operator-id service-id interface]
  (if (nil? interface)
    (str base-uri "export/geojson/" operator-id "/" service-id)
    (get-in interface [::t-service/external-interface ::t-service/url])))

(defn compute-download-url [service operator-id service-id interface]
  (if (nil? interface)
    (str base-uri "export/geojson/" operator-id "/" service-id)
    (get-in interface [::t-service/external-interface ::t-service/url])))

(defn interface->format [interface]
  (if (nil? interface)
    "http://publications.europa.eu/resource/authority/file-type/GEOJSON"
    (interface->format-extent interface)))

(defn interface->rights-url-computed [interface]
  (if (nil? interface)
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided-free-of-charge"
    (interface->rights-url interface)))

(defn interface->license-url [interface]
  "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

(defn compute-distribution-description [service interface]
  (if (nil? interface)
    (str "Olennaiset liikennepalvelutiedot palvelusta " (::t-service/name service))
    (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) "")))

(defn compute-last-modified [service interface latest-conversion-status]
  (cond
    (and interface latest-conversion-status)
    (:created latest-conversion-status)
    
    (and interface
         (nil? latest-conversion-status)
         (::t-service/gtfs-imported interface))
    (::t-service/gtfs-imported interface)
    
    (not interface)
    (:ote.db.modification/modified service)))

(defn interface->accrual-periodicity [interface]
  (if (nil? interface)
    "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED"
    (let [{:ote.db.transport-service/keys [format]} interface]
      (if (= (first format) "GeoJSON")
        "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED"
        "http://publications.europa.eu/resource/authority/frequency/UNKNOWN"))))

(defn service->mobility-themes [service]
  (if-let [sub-theme (service->sub-theme service)]
    [(service->mobility-theme service) sub-theme]
    [(service->mobility-theme service)]))

(defn interface->dataset-languages [interface]
  (when-not interface
    ["http://publications.europa.eu/resource/authority/language/FIN"
     "http://publications.europa.eu/resource/authority/language/SWE"
     "http://publications.europa.eu/resource/authority/language/ENG"]))

(defn interface->intended-information-service [interface]
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
      (do
        (log/warnf "Unknown datacontent %s" (pr-str data-content))
        "https://w3id.org/mobilitydcat-ap/intended-information-service/other"))))

(defn prepare-domain [service operator operation-areas interface latest-conversion-status]
  (let [service-id (:ote.db.transport-service/id service)
        operator-id (:ote.db.transport-service/transport-operator-id service)
        dataset-uri (compute-dataset-uri service interface)
        distribution-uri (compute-distribution-uri service interface)
        operator-uri (compute-operator-uri operator)
        access-url (compute-access-url service operator-id service-id interface)
        download-url (compute-download-url service operator-id service-id interface)
        format (interface->format interface)
        rights-url (interface->rights-url-computed interface)
        license-url (interface->license-url interface)
        distribution-description (compute-distribution-description service interface)
        last-modified (compute-last-modified service interface latest-conversion-status)
        accrual-periodicity (interface->accrual-periodicity interface)
        mobility-themes (service->mobility-themes service)
        dataset-languages (interface->dataset-languages interface)
        transport-mode (service->transport-mode service)
        intended-info-service (if interface
                                (interface->intended-information-service interface)
                                "https://w3id.org/mobilitydcat-ap/intended-information-service/other")
        vaco-validation-timestamp (when interface
                                    (:tis_polling_completed latest-conversion-status))
        record-uri (if (nil? interface)
                     (str dataset-base-uri service-id "/record")
                     (str (get-in interface [::t-service/external-interface ::t-service/url]) "/record"))]
    {:dataset-uri dataset-uri
     :distribution-uri distribution-uri
     :operator-uri operator-uri
     :operator-name (:ote.db.transport-operator/name operator)
     :service-id (:ote.db.transport-service/id service)
     :service-name (:ote.db.transport-service/name service)
     :service-description (or (get-in service [::t-service/description 0 ::t-service/text]) "")
     :available-from (:ote.db.transport-service/available-from service)
     :available-to (:ote.db.transport-service/available-to service)
     :created (::modification/created service)
     :modified (::modification/modified service)
     :last-modified last-modified
     :record-uri record-uri
     :access-url access-url
     :download-url download-url
     :format format
     :rights-url rights-url
     :license-url license-url
     :distribution-description distribution-description
     :accrual-periodicity accrual-periodicity
     :mobility-themes mobility-themes
     :dataset-languages dataset-languages
     :transport-mode transport-mode
     :intended-info-service intended-info-service
     :operation-area-geojson (:geojson (first operation-areas))
     :needs-mobility-data-standard-blank-node? (nil? interface)
     :mobility-data-standard-uri (when interface (interface->mobility-data-standard interface))
     :vaco-validation-timestamp vaco-validation-timestamp
     :vaco-result-link (when latest-conversion-status
                         (:tis-magic-link latest-conversion-status))
     :has-interface? (some? interface)
     :municipality (:municipality service)}))

;; ===== RDF DATA STRUCTURE CREATION =====

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

(defn domain->distribution [domain]
  (let [distribution-props (cond-> {:rdf/type (uri :dcat/Distribution)
                                    :dcat/accessURL (uri (:access-url domain))
                                    :dcat/downloadURL (uri (:download-url domain))
                                    :dct/format (uri (:format domain))
                                    :dct/rights (resource {:rdf/type (uri :dct/RightsStatement)
                                                           :dct/type (uri (:rights-url domain))})
                                    :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                                            :dct/identifier (uri (:license-url domain))})
                                    :mobility/applicationLayerProtocol (uri "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https")
                                    :mobility/description (lang-literal (:distribution-description domain) "fi")
                                    :mobility/communicationMethod (uri "https://w3id.org/mobilitydcat-ap/communication-method/pull")
                                    :mobility/mobilityDataStandard (mobility-data-standard-data 
                                                                     (:needs-mobility-data-standard-blank-node? domain)
                                                                     (:mobility-data-standard-uri domain))}
                             (not (:has-interface? domain))
                             (assoc :cnt/characterEncoding (literal "UTF-8")
                                    :mobility/grammar (uri "https://w3id.org/mobilitydcat-ap/grammar/json-schema"))
                             
                             (:vaco-validation-timestamp domain)
                             (assoc :dct/result (literal (:vaco-result-link domain))))]
    (resource (:distribution-uri domain) distribution-props)))

(defn domain->dataset [domain distributions]
  (let [mobility-themes (vec (map uri (:mobility-themes domain)))
        dataset-languages (when (:dataset-languages domain)
                           (vec (map uri (:dataset-languages domain))))
        operator (resource (:operator-uri domain)
                           {:rdf/type (uri :foaf/Organization)
                            :foaf/name (literal (:operator-name domain))})
        dataset-props (cond-> {:rdf/type (uri :dcat/Dataset)
                               :dct/title (literal (:service-name domain))
                               :dct/description (literal (:service-description domain))
                               :mobility/transportMode (uri (:transport-mode domain))
                               :dct/accrualPeriodicity (uri (:accrual-periodicity domain))
                               :mobility/mobilityTheme mobility-themes
                               :dct/spatial [(uri (:municipality domain))
                                             (resource {:rdf/type (uri :dct/Location)
                                                        :locn/geometry (typed-literal (:operation-area-geojson domain)
                                                                                      "https://www.iana.org/assignments/media-types/application/vnd.geo+json")})]
                               :mobility/georeferencingMethod (uri "https://w3id.org/mobilitydcat-ap/georeferencing-method/geocoordinates")
                               :dct/theme (uri "http://publications.europa.eu/resource/authority/data-theme/TRAN")
                               :mobility/identifier (uri (:dataset-uri domain))
                               :mobility/intendedInformationService (uri (:intended-info-service domain))
                               :dct/publisher operator
                               :dct/rightsHolder operator
                               :dcat/distribution (map resource->uri distributions)}
                        (not (:has-interface? domain))
                        (assoc :dct/conformsTo (uri "https://www.opengis.net/def/crs/EPSG/0/4326"))

                        dataset-languages
                        (assoc :dct/language dataset-languages)

                        (:last-modified domain)
                        (assoc :dct/modified (typed-literal (str (:last-modified domain))
                                                            "http://www.w3.org/2001/XMLSchema#dateTime")))]
    (resource (:dataset-uri domain) dataset-props)))

(defn domain->assessment [domain]
  (when (:vaco-validation-timestamp domain)
    (resource {:rdf/type (uri :mobility/Assessment)
               :dct/date (typed-literal (str (:vaco-validation-timestamp domain))
                                        "http://www.w3.org/2001/XMLSchema#dateTime")})))

(defn domain->catalog-record [domain dataset-uri fintraffic-uri]
  (resource (:record-uri domain)
            {:rdf/type (uri :dcat/CatalogRecord)
             :dct/created (literal (str (:created domain)))
             :dct/language [(uri "http://publications.europa.eu/resource/authority/language/FIN")
                            (uri "http://publications.europa.eu/resource/authority/language/SWE")
                            (uri "http://publications.europa.eu/resource/authority/language/ENG")]
             :foaf/primaryTopic (uri dataset-uri)
             :dct/modified (literal (str (:modified domain)))
             :dct/publisher (uri fintraffic-uri)}))

(defn domain->catalog [catalog-records dataset-uris latest-publication fintraffic-uri]
  (let [catalog-record-uris (map :uri catalog-records)]
    (resource catalog-uri
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
               :dct/license (resource {:rdf/type (uri :dct/LicenseDocument)
                                       :dct/identifier (uri licence-url)})
               :dct/issued (typed-literal "2018-01-01T00:00:01Z" "http://www.w3.org/2001/XMLSchema#dateTime")
               :dct/themeTaxonomy (uri "https://w3id.org/mobilitydcat-ap/mobility-theme")
               :dct/modified (typed-literal (str latest-publication) "http://www.w3.org/2001/XMLSchema#dateTime")
               :dct/identifier (literal catalog-uri)
               :dct/publisher (uri fintraffic-uri)
               :dcat/record (vec (map uri catalog-record-uris))
               :dcat/dataset (vec (map uri dataset-uris))})))

(defn domain->rdf-for-service [service operation-areas operator interface latest-conversion-status fintraffic-uri]
  (let [domain (prepare-domain service operator operation-areas interface 
                               latest-conversion-status)
        distributions [(domain->distribution domain)]
        dataset (domain->dataset domain distributions)
        dataset-uri (:uri dataset)
        catalog-record (domain->catalog-record domain dataset-uri fintraffic-uri)]
    
    {:distributions distributions
     :assessments (if-let [assessment (domain->assessment domain)] [assessment] [])
     :datasets [dataset]
     :catalog-records [catalog-record]}))

(defn merge-rdf-models [model1 model2]
  {:distributions (vec (concat (:distributions model1) (:distributions model2)))
   :assessments (vec (concat (:assessments model1) (:assessments model2)))
   :datasets (vec (concat (:datasets model1) (:datasets model2)))
   :catalog-records (vec (concat (:catalog-records model1) (:catalog-records model2)))})

(defn service-data->rdf
  "Create complete RDF data structure including catalog and fintraffic agent.
   Calls domain->rdf-for-service for geojson and all interfaces, merges results,
   and creates catalog. Returns a map with :catalog, :datasets, :distributions,
   :assessments, :catalog-records, :data-services, :relationships, :ns-prefixes, and :fintraffic-agent."
  [service-data]
  (let [{:keys [service operation-areas operator validation-data latest-publication]} service-data
        external-interfaces (::t-service/external-interfaces service)
        ;; TODO this is probably not what we want for Fintraffic.
        fintraffic-uri (fintraffic-url business-id)
        fintraffic-agent (resource fintraffic-uri
                                   {:rdf/type (uri :foaf/Organization)
                                    :foaf/name (literal "Fintraffic Oy")})
        ;; TODO this is probably not correct
        is-dataservice? false
        ;; Create data services for interfaces that need them
        data-services (if is-dataservice?
                       (vec (map domain->data-service external-interfaces))
                       [])
        result (if operation-areas
                 (let [;; Generate RDF data for GeoJSON dataset
                       geojson-rdf (domain->rdf-for-service service operation-areas operator nil nil fintraffic-uri)
                       
                       ;; Process interfaces and collect their RDF data
                       interface-rdf-models (doall
                                             (for [interface external-interfaces]
                                               (let [interface-id (::t-service/id interface)
                                                     validation-status (get validation-data interface-id)]
                                                 (domain->rdf-for-service service operation-areas operator interface validation-status fintraffic-uri))))
                       
                       ;; Merge all RDF models
                       merged-rdf (reduce merge-rdf-models geojson-rdf interface-rdf-models)
                       
                       ;; Extract URIs for catalog and relationships
                       all-dataset-uris (map :uri (:datasets merged-rdf))
                       geojson-uri (first all-dataset-uris)
                       interface-uris (rest all-dataset-uris)
                       
                       ;; Create relationships between datasets
                       relationships (vec (mapcat #(isreferenced-and-related-data geojson-uri %)
                                                  interface-uris))
                       
                       ;; Create catalog with embedded records and datasets
                       catalog-resource (domain->catalog (:catalog-records merged-rdf) all-dataset-uris latest-publication fintraffic-uri)]
                   
                   (assoc merged-rdf 
                          :catalog catalog-resource
                          :relationships relationships))
                 
                 ;; No operation areas - create empty catalog
                 (let [catalog-resource (domain->catalog [] [] latest-publication fintraffic-uri)]
                   {:catalog catalog-resource
                    :distributions []
                    :assessments []
                    :datasets []
                    :catalog-records []
                    :relationships []}))]
    (assoc result 
           :fintraffic-agent fintraffic-agent
           :data-services data-services
           :ns-prefixes [["dcat" dcat]
                         ["dct" dct]
                         ["foaf" foaf]
                         ["mobility" mobility]])))
