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

(defn select-mobility-theme [service]
  (case (:ote.db.transport-service/sub-type service)
    :taxi "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-non-scheduled-transport"
    :request "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-non-scheduled-transport"
    :schedule "https://w3id.org/mobilitydcat-ap/mobility-theme/public-transport-scheduled-transport"
    :terminal "https://w3id.org/mobilitydcat-ap/mobility-theme/other"
    :rentals "https://w3id.org/mobilitydcat-ap/mobility-theme/sharing-and-hiring-services"
    :parking "https://w3id.org/mobilitydcat-ap/mobility-theme/parking-service-and-rest-area-information"
    :brokerage "https://w3id.org/mobilitydcat-ap/mobility-theme/sharing-and-hiring-services"
    :other "https://w3id.org/mobilitydcat-ap/mobility-theme/other"))

(defn select-sub-theme [service]
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

(defn select-transport-mode [service]
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

(defn get-mobility-data-standard
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

(defn get-interface-format-extent [interface]
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

(defn get-rights-url [interface]
  ;; This is the best we can do with data that is available in the service.
  ;; By changing lincence as a mandatory data and by changing lisence to be selected from a list, we can ensure that this is correct.
  ;; But now this is the best we can do.
  (if (::t-service/license interface)
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided"
    "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/other"))


(defn periodOfTime [model available-from available-to created]
  (let [period (ResourceFactory/createResource)
        available-from (if available-from available-from created)]
    (.add model period RDF/type (ResourceFactory/createResource (str dcat "PeridOfTime")))
    (.add model period
          (ResourceFactory/createProperty (str dcat "startDate"))
          (ResourceFactory/createTypedLiteral (str available-from) (BaseDatatype. "http://www.w3.org/2001/XMLSchema#dateTime")))
    (when available-to
      (.add model period
            (ResourceFactory/createProperty (str dcat "endDate"))
            (ResourceFactory/createTypedLiteral (str available-to) (BaseDatatype. "http://www.w3.org/2001/XMLSchema#dateTime"))))
    period))


(defn add-isreferenced-and-related-to-dataset [geojson-dataset interface-dataset model]
  (.add model interface-dataset
        (ResourceFactory/createProperty (str dct "isReferencedBy"))
        geojson-dataset)
  (.add model geojson-dataset
        (ResourceFactory/createProperty (str dct "relation"))
        interface-dataset))

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

(defn create-catalog-model [db model catalog]
  (let [latest-publication (:published (first (latest-published-service db)))
        licence-resource (ResourceFactory/createResource)]
    ;; Catalog kuvaa mikä Finap on
    (.add model catalog RDF/type (ResourceFactory/createResource (str dcat "Catalog")))
    (.add model catalog
          (ResourceFactory/createProperty (str foaf "title"))
          (ResourceFactory/createStringLiteral "Finap.fi - NAP - National Access Point"))
    (.add model catalog
          (ResourceFactory/createProperty (str foaf "description"))
          ;; TODO: Hae kielimallin mukainen kuvaus. From file en.edn, fi.edn, sv.edn
          (ResourceFactory/createLangLiteral "NAP-liikkumispalvelukatalogi on avoin kansallinen yhteyspiste (National Access Point, NAP), johon liikkumispalvelun tuottajien on toimitettava tietoja digitaalisista olennaisten tietojen koneluettavista rajapinnoistaan. NAP-palvelu on osa kokonaisuutta, jonka tavoitteena on aikaansaada helppokäyttöisiä yhdistettyjä liikkumis- ja infopalveluita. NAP ei ole loppukäyttäjien ja matkustajien palvelu, vaan se on tarkoitettu liikkumispalveluiden tuottajille ja kehittäjille." "fi"))
    (.add model catalog
          (ResourceFactory/createProperty (str foaf "description"))
          ;; TODO: Hae kielimallin mukainen kuvaus
          (ResourceFactory/createLangLiteral "NAP-liikkumispalvelukatalogi on avoin kansallinen yhteyspiste (National Access Point, NAP), johon liikkumispalvelun tuottajien on toimitettava tietoja digitaalisista olennaisten tietojen koneluettavista rajapinnoistaan. NAP-palvelu on osa kokonaisuutta, jonka tavoitteena on aikaansaada helppokäyttöisiä yhdistettyjä liikkumis- ja infopalveluita. NAP ei ole loppukäyttäjien ja matkustajien palvelu, vaan se on tarkoitettu liikkumispalveluiden tuottajille ja kehittäjille." "sv"))
    (.add model catalog
          (ResourceFactory/createProperty (str foaf "homepage"))
          (ResourceFactory/createStringLiteral "https://www.finap.fi/"))

    ;; TODO: Ensure that this is ok by customer
    ;; Default is now Finland - http://data.europa.eu/nuts/code/FI
    ;; It means that catalog contains information only from Finland
    (.add model catalog (ResourceFactory/createProperty (str dct "spatial"))
          (ResourceFactory/createResource "http://data.europa.eu/nuts/code/FI"))

    ;; language
    (.add model catalog
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/FIN"))
    (.add model catalog
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/SWE"))
    (.add model catalog
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/ENG"))

    ;; license
    (.add model licence-resource RDF/type (ResourceFactory/createResource (str dct "LicenseDocument")))
    (.add model licence-resource
          (ResourceFactory/createProperty (str dct "identifier"))
          (ResourceFactory/createResource licence-url))
    (.add model catalog
          (ResourceFactory/createProperty (str dct "license"))
          licence-resource)

    ;; issued - published
    (.add model catalog
          (ResourceFactory/createProperty (str dct "issued"))
          (ResourceFactory/createTypedLiteral (str "2018-01-01T00:00:01Z") (BaseDatatype. "http://www.w3.org/2001/XMLSchema#dateTime")))

    ;; http://publications.europa.eu/resource/authority/data-theme/TRAN
    ;; themeTaxonomy
    (.add model catalog
          (ResourceFactory/createProperty (str dct "themeTaxonomy"))
          (ResourceFactory/createResource "https://w3id.org/mobilitydcat-ap/mobility-theme"))

    ;; modified -- Last recent modification date
    (.add model catalog
          (ResourceFactory/createProperty (str dct "modified"))
          (ResourceFactory/createTypedLiteral (str latest-publication) (BaseDatatype. "http://www.w3.org/2001/XMLSchema#dateTime")))

    ;; hasPart - Finap doesn't fetch other catalogs data.
    ;; isPartOf - As far as we know Finap is not part of any other catalog, so this is not used.

    ;; dct:identifier
    (.add model catalog
          (ResourceFactory/createProperty (str dct "identifier"))
          (ResourceFactory/createStringLiteral catalog-uri))

    ;; adms:identifier - As far as we know, Finap is not issued an EU-wide identificator yet.
    ))

(defn create-record [service model catalog interface dataset-resource fintraffic-agent]
  (let [id (:ote.db.transport-service/id service)
        record-uri (if (nil? interface)
                     (str dataset-base-uri id "/record")
                     (str (get-in interface [::t-service/external-interface ::t-service/url]) "/record"))
        record (ResourceFactory/createResource record-uri)
        created (:ote.db.modification/created service)
        modified (:ote.db.modification/modified service)]

    (.add model record RDF/type (ResourceFactory/createResource (str dcat "CatalogRecord")))
    ;; created
    (.add model record
          (ResourceFactory/createProperty (str dct "created"))
          (ResourceFactory/createStringLiteral (str created)))
    ;; language
    (.add model record
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/FIN"))
    (.add model record
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/SWE"))
    (.add model record
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/ENG"))
    ;; primaryTopic
    (.add model record
          (ResourceFactory/createProperty (str foaf "primaryTopic"))
          dataset-resource)
    ;; modified
    (.add model record
          (ResourceFactory/createProperty (str dct "modified"))
          (ResourceFactory/createStringLiteral (str modified)))

    ;; dct:publisher
    (.add model record
          (ResourceFactory/createProperty (str dct "publisher"))
          fintraffic-agent)

    ;; Add Record to Catalog
    (.add model catalog
          (ResourceFactory/createProperty (str dcat "record"))
          record)

    ;; source - Used when records are harvested from other portals and Finap doesn't harvest them.
    ))

(defn create-data-service
  "TODO: All external interfaces are not accessServices. Add this only for those that have API interfaces. ie. Finnair etc.
  We need to come up with a solution to distinguish between accessService and reqular dataset."
  [model interface]
  (let [accessService-resource (ResourceFactory/createResource)
        endpointUrl (get-in interface [::t-service/external-interface ::t-service/url])
        title (localized-text-with-key "fi" [:enums :ote.db.transport-service/interface-data-content :route-and-schedule])
        ;; First element is the finnish description, second is the swedish description and third is the english description.
        ;;TODO: Use correct language versions
        description (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) "")

        rights-resource (ResourceFactory/createResource)
        rights-url (get-rights-url interface)
        licence-resource (ResourceFactory/createResource)]

    (.add model accessService-resource RDF/type (ResourceFactory/createResource (str dct "DataService")))
    (.add model accessService-resource
          (ResourceFactory/createProperty (str dcat "endpointURL"))
          (ResourceFactory/createResource endpointUrl))
    (.add model accessService-resource
          (ResourceFactory/createProperty (str dct "title"))
          (ResourceFactory/createResource title))
    ;;TODO: endpointDescription is like link to swagger documentation or similar. Finap doesn't have this information
    ;; endpointDescription

    (.add model rights-resource RDF/type (ResourceFactory/createResource (str dct "RightsStatement")))
    (.add model rights-resource
          (ResourceFactory/createProperty (str dct "type"))
          (ResourceFactory/createResource rights-url))
    (.add model accessService-resource
          (ResourceFactory/createProperty (str dct "rights"))
          rights-resource)

    (.add model accessService-resource
          (ResourceFactory/createProperty (str dcat "description"))
          (ResourceFactory/createResource description))

    (.add model licence-resource RDF/type (ResourceFactory/createResource (str dct "LicenseDocument")))
    (.add model licence-resource
          (ResourceFactory/createProperty (str dct "identifier"))
          (ResourceFactory/createResource licence-url))
    (.add model accessService-resource
          (ResourceFactory/createProperty (str dct "license"))
          licence-resource)))

(defn create-dataset [db service model catalog service-id operator-id operation-areas operator interface]
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

        ;; See: https://github.com/mobilityDCAT-AP/mobilityDCAT-AP/issues/62
        mobilityDataStandard (if (nil? interface)
                               (let [moblityDataStandard-resource (ResourceFactory/createResource)]
                                 (doto model
                                   (.add moblityDataStandard-resource RDF/type (ResourceFactory/createResource (str mobility "MobilityDataStandard")))
                                   (.add moblityDataStandard-resource OWL/versionInfo (ResourceFactory/createPlainLiteral "GeoJSON rfc7946"))
                                   (.add moblityDataStandard-resource
                                         (ResourceFactory/createProperty (str mobility "schema"))
                                         (ResourceFactory/createResource "https://geojson.org/schema/GeoJSON.json")))
                                 moblityDataStandard-resource)
                               (ResourceFactory/createResource (get-mobility-data-standard interface)))

        format (if (nil? interface)
                 "http://publications.europa.eu/resource/authority/file-type/GEOJSON"
                 (get-interface-format-extent interface))

        rights-resource (ResourceFactory/createResource)
        rights-url (if (nil? interface)
                     "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided-free-of-charge"
                     (get-rights-url interface))

        licence-resource (ResourceFactory/createResource)
        licence-url (if (nil? interface)
                      "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0"
                      ;;TODO: Select suitable licence url from this list: http://publications.europa.eu/resource/authority/licence
                      "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")

        ;; We can use http/https protocol because Finap is doing automatic checks for all interfaces in http format when inteface is added to the service.
        applicationLayerProtocol "https://w3id.org/mobilitydcat-ap/application-layer-protocol/http-https"

        ;; TODO: Set the correct description for the default distribution.
        distribution-description (if (nil? interface)
                                   (ResourceFactory/createLangLiteral (str "Olennaiset liikennepalvelutiedot palvelusta " (::t-service/name service)) "fi")
                                   ;; TODO: Use correct language versions
                                   (ResourceFactory/createLangLiteral (or (get-in interface [::t-service/external-interface ::t-service/description 0 ::t-service/text]) "") "fi"))

        distribution-uri (if (nil? interface)
                           (str distribution-base-uri "/" id)
                           (str distribution-interface-base-uri "/" (::t-service/id interface)))
        operator-uri (operator-url (::t-operator/business-id operator))
        dataset (ResourceFactory/createResource dataset-uri)
        distribution (ResourceFactory/createResource distribution-uri)
        spatial-resource (ResourceFactory/createResource)
        operator-agent (ResourceFactory/createResource operator-uri)
        operator-name (:ote.db.transport-operator/name operator)
        assessment-resource (ResourceFactory/createResource)

        ;; TODO: This is not tested yet.
        ;; Ensure that this works as expected in stg and in prod.
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
                        (:ote.db.modification/modified service))]
    (.add model dataset RDF/type (ResourceFactory/createResource (str dcat "Dataset")))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "title"))
          (ResourceFactory/createStringLiteral (:ote.db.transport-service/name service)))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "description"))
          (ResourceFactory/createStringLiteral (or (get-in service [::t-service/description 0 ::t-service/text]) "")))

    (.add model dataset
          (ResourceFactory/createProperty (str mobility "transportMode"))
          (ResourceFactory/createStringLiteral (name (:ote.db.transport-service/sub-type service))))

    (.add model distribution RDF/type (ResourceFactory/createResource (str dcat "Distribution")))
    (.add model dataset
          (ResourceFactory/createProperty (str dcat "distribution"))
          distribution)

    ;; accessURL
    (.add model distribution
          (ResourceFactory/createProperty (str dcat "accessURL"))
          (ResourceFactory/createResource accessURL))

    ;; Download url is an url where data set data is located
    (.add model distribution
          (ResourceFactory/createProperty (str dcat "downloadURL"))
          (ResourceFactory/createResource downloadURL))

    ;; mobilityDataStandard
    (.add model distribution
          (ResourceFactory/createProperty (str mobility "mobilityDataStandard"))
          mobilityDataStandard)

    ;; format
    (.add model distribution
          (ResourceFactory/createProperty (str dct "format"))
          (ResourceFactory/createResource format))

    ;; rights
    (.add model rights-resource RDF/type (ResourceFactory/createResource (str dct "RightsStatement")))
    (.add model rights-resource
          (ResourceFactory/createProperty (str dct "type"))
          (ResourceFactory/createResource rights-url))
    (.add model distribution
          (ResourceFactory/createProperty (str dct "rights"))
          rights-resource)

    ;; applicationLayerProtocol
    (.add model distribution
          (ResourceFactory/createProperty (str mobility "applicationLayerProtocol"))
          (ResourceFactory/createResource applicationLayerProtocol))

    ;; description
    (.add model distribution
          (ResourceFactory/createProperty (str mobility "description"))
          distribution-description)

    ;; license
    (.add model licence-resource RDF/type (ResourceFactory/createResource (str dct "LicenseDocument")))
    (.add model licence-resource
          (ResourceFactory/createProperty (str dct "identifier"))
          (ResourceFactory/createResource licence-url))
    (.add model distribution
          (ResourceFactory/createProperty (str dct "license"))
          licence-resource)

    ;; accessService - TODO: Implement if data service is implemented in final product

    ;; characterEncoding - We donät know encondig of the external interface data. So add only if geojson distribution is created.
    (when (nil? interface)
      (.add model distribution
            (ResourceFactory/createProperty (str cnt "characterEncoding"))
            (ResourceFactory/createPlainLiteral "UTF-8")))

    ;; communicationMethod
    (.add model distribution
          (ResourceFactory/createProperty (str mobility "communicationMethod"))
          (ResourceFactory/createResource "https://w3id.org/mobilitydcat-ap/communication-method/pull"))

    ;; dataFormatNotes - No additional notes are available

    ;; grammar - We don't know grammar of the external interface data. So add only if geojson distribution is created.
    (when (nil? interface)
      (.add model distribution
            (ResourceFactory/createProperty (str mobility "grammar"))
            (ResourceFactory/createResource "https://w3id.org/mobilitydcat-ap/grammar/json-schema")))

    ;; sample - Finap or external interfaces doesn't provide sample data.
    ;; temporal - Finap doesn't provide temporal data.

    ;; title - Finap has only one distribution of each. No title needed.

    ;; accrualPeriodicity
    (.add model dataset
          (ResourceFactory/createProperty (str dct "accrualPeriodicity"))
          (ResourceFactory/createResource (if (nil? interface)
                                            "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED"
                                            "http://publications.europa.eu/resource/authority/frequency/UNKNOWN")))

    ;; mobilityTheme
    (.add model dataset
          (ResourceFactory/createProperty (str mobility "mobilityTheme"))
          (ResourceFactory/createResource (select-mobility-theme service)))

    ;; IF sub-theme is missing, do not stress
    (when (select-sub-theme service)
      (.add model dataset
            (ResourceFactory/createProperty (str mobility "mobilityTheme"))
            (ResourceFactory/createResource (select-sub-theme service))))

    ;; Get higher state from NUTS: http://publications.europa.eu/resource/dataset/nuts - e.g. Finland
    ;; and munivipality here: https://stirdata.github.io/data-specification/lau.ttl
    ;; Currently only polygon is provided because it is set for every service in Finap
    ;; TODO: Take municipality first and then geojson format according to primary and secondary areas
    (.add model spatial-resource RDF/type (ResourceFactory/createResource (str dct "Location")))
    (.add model dataset (ResourceFactory/createProperty (str dct "spatial"))
          (ResourceFactory/createResource "https://w3id.org/stirdata/resource/lau/item/FI_244")) ;; Hardcoded!

    (.add model spatial-resource (ResourceFactory/createProperty (str locn "geometry"))
          (ResourceFactory/createTypedLiteral (:geojson (first operation-areas)) (BaseDatatype. "https://www.iana.org/assignments/media-types/application/vnd.geo+json")))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "spatial"))
          spatial-resource)

    (.add model operator-agent RDF/type (ResourceFactory/createResource (str foaf "Organization")))
    (.add model operator-agent
          (ResourceFactory/createProperty (str foaf "name"))
          (ResourceFactory/createStringLiteral operator-name))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "publisher"))
          operator-agent)

    ;; georeferencingMethod - using lat/lon values can correct type to be provided
    (.add model dataset
          (ResourceFactory/createProperty (str mobility "georeferencingMethod"))
          (ResourceFactory/createResource "https://w3id.org/mobilitydcat-ap/georeferencing-method/geocoordinates"))

    (.add model operator-agent
          (ResourceFactory/createProperty (str foaf "name"))
          (ResourceFactory/createStringLiteral operator-name))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "publisher"))
          operator-agent)

    ;; contactPoint -- Jätetään pois, koska Kind tyyppiä, jolla pitää olla email ja nimi. Finapissa ei ole henkilötietoja eikä operaattorin emailia julkisena.
    ;; keyword - Jätetään pois -  Finapissa ei ole tageja.
    ;; networkCoverage - Jätetään pois - Yksiselitteistä verkkoa on vaikea päätellä. Esim Waterways ei toteudu kaikilla "meriliikenteen" toimijoilla, koska ne voivat toimia
    ;; esim suomenlahdella, mutta eivät sisämaan kanavissa.
    ;;

    ;; conformsTo - Only geojson data is in EPSG:4326 format. External interfaces can be in any format.
    (when (nil? interface)
      (.add model dataset
            (ResourceFactory/createProperty (str dct "conformsTo"))
            (ResourceFactory/createResource "https://www.opengis.net/def/crs/EPSG/0/4326")))

    ;; rightsHolder - Tämä on sama kuin operaattori. Oletuksena on, että operaattori on oikeuksien haltija.
    (.add model dataset
          (ResourceFactory/createProperty (str dct "rightsHolder"))
          operator-agent)

    ;; theme
    (.add model dataset
          (ResourceFactory/createProperty (str dct "theme"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/data-theme/TRAN"))

    ;;temporal
    (.add model dataset
          (ResourceFactory/createProperty (str dct "temporal"))
          (periodOfTime model available-from available-to created))

    ;; transportMode
    (.add model dataset
          (ResourceFactory/createProperty (str mobility "transportMode"))
          (ResourceFactory/createResource (select-transport-mode service)))

    ;; applicableLegislation - More or less impossible to determine which legislation applies to the dataset.

    ;; assessmentResult - Added VACO validation url
    (when (and interface vaco-validation-timestamp)
      (.add model assessment-resource RDF/type (ResourceFactory/createResource (str mobility "Assessment")))
      (.add model assessment-resource
            (ResourceFactory/createProperty (str dct "date"))
            (ResourceFactory/createTypedLiteral (str vaco-validation-timestamp) (BaseDatatype. "http://www.w3.org/2001/XMLSchema#dateTime")))
      (.add model distribution
            (ResourceFactory/createProperty (str dct "result"))
            (:tis-magic-link latest-conversion-status)))

    ;; hasVersion - Datasets in Finap are not versioned, so not added.

    ;; identifier
    (.add model dataset
          (ResourceFactory/createProperty (str mobility "identifier"))
          (ResourceFactory/createResource dataset-uri))

    ;; isReferencedBy AND related is implemented outside of this function.
    ;; isVersionOf - Datasets in Finap are not versioned, so not added.

    ;; intendedInformationService
    (.add model dataset
          (ResourceFactory/createProperty (str mobility "identifier"))
          (ResourceFactory/createResource (if interface
                                            (get-intended-information-service interface)
                                            "https://w3id.org/mobilitydcat-ap/intended-information-service/other")))

    ;; language
    (when-not interface
      (.add model dataset
            (ResourceFactory/createProperty (str dct "language"))
            (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/FIN"))
      (.add model dataset
            (ResourceFactory/createProperty (str dct "language"))
            (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/SWE"))
      (.add model dataset
            (ResourceFactory/createProperty (str dct "language"))
            (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/ENG")))

    ;; adms:identifier - As far as we know, Finap is not issued an EU-wide identificator yet.

    ;; issuded - Documentation says that this should not be used.

    ;; modified
    (when last-modified
      (.add model dataset
            (ResourceFactory/createProperty (str dct "modified"))
            (ResourceFactory/createTypedLiteral (str last-modified) (BaseDatatype. "http://www.w3.org/2001/XMLSchema#dateTime"))))


    ;; versionInfo - not supported in Finap, so not added.
    ;; versionNotes - not supported in Finap, so not added.
    ;; asQualityAnnotation -- not supported in Finap, so not added.

    ;; Add DataSet to catalog
    (.add model catalog
          (ResourceFactory/createProperty (str dcat "dataset"))
          dataset)

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
    (.add model fintraffic-agent RDF/type (ResourceFactory/createResource (str foaf "Organization")))
    (.add model fintraffic-agent
          (ResourceFactory/createProperty (str foaf "name"))
          (ResourceFactory/createStringLiteral "Fintraffic Oy"))
    (.add model catalog
          (ResourceFactory/createProperty (str dct "publisher"))
          fintraffic-agent)

    ;; Add catalog
    (create-catalog-model db model catalog)

    ;; Records and Dataset
    
    ;; 🤷‍♀️
    (when operation-areas
      (let [geojson-dataset-resource (create-dataset db service model catalog service-id operator-id operation-areas operator nil)
            _ (create-record service model catalog nil geojson-dataset-resource fintraffic-agent)]

        (doseq [interface external-interfaces]
          ;; TODO: is interface a dataservice or dataset?
          ;; If interface is not a dataservice, then create accessService
          (if is-dataservice?
            (create-data-service model interface)
            (let [interface-dataset-resource (create-dataset db service model catalog service-id operator-id operation-areas operator interface)
                  _ (create-record service model catalog interface interface-dataset-resource fintraffic-agent)
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
