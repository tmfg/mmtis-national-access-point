(ns ote.services.rdf
  "Use Jena to create Mobility DCAT-AP RDF from the database."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [ote.localization :as localization :refer [tr]]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            [specql.core :as specql]
            [taoensso.timbre :as log]
            [compojure.core :refer [routes GET]]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [clojure.string :as str]
            )
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]
           [java.time Instant])
  )

(defqueries "ote/integration/export/geojson.sql")

;; URI-prefiksit DCAT-AP:lle ja Mobility DCAT-AP:lle
(def base-uri "http://localhost:3000/")
(def service-id 1)
;(def operator-id 1)
(def business-id "2942108-7")                              ;; Tämä on testibusiness-id, jota käytetään Finap.fi:n testisivuilla
(defn fintraffic-url [business-id]
  (str "https://finap.fi/service-search?operators=" business-id))
(defn operator-url [business-id]
  (str "https://finap.fi/service-search?operators=" business-id))

(def dcat "http://www.w3.org/ns/dcat#")
(def dct "http://purl.org/dc/terms/")
(def locn "http://www.w3.org/ns/locn#")
(def foaf "http://xmlns.com/foaf/0.1/")
(def mobility "http://www.w3.org/ns/mobilitydcatap#")
(def catalog-uri (str base-uri "catalog"))
(def dataset-base-uri (str base-uri "rdf/" service-id))
(def distribution-base-uri (str dataset-base-uri "/distribution"))

(defqueries "ote/services/service_search.sql")

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

;; Luo Mobility DCAT-AP RDF-malli

(defn create-catalog-model [model catalog fintraffic-agent]
  ;; ;; Catalog kuvaa mikä Finap on
  (.add model catalog RDF/type (ResourceFactory/createResource (str dcat "Catalog")))
  (.add model catalog
        (ResourceFactory/createProperty (str foaf "title"))
        (ResourceFactory/createStringLiteral "Finap.fi - NAP - National Access Point"))
  (.add model catalog
        (ResourceFactory/createProperty (str foaf "description"))
        ;; TODO: Hae kielimallin mukainen kuvaus
        (ResourceFactory/createLangLiteral "NAP-liikkumispalvelukatalogi on avoin kansallinen yhteyspiste (National Access Point, NAP), johon liikkumispalvelun tuottajien on toimitettava tietoja digitaalisista olennaisten tietojen koneluettavista rajapinnoistaan. NAP-palvelu on osa kokonaisuutta, jonka tavoitteena on aikaansaada helppokäyttöisiä yhdistettyjä liikkumis- ja infopalveluita. NAP ei ole loppukäyttäjien ja matkustajien palvelu, vaan se on tarkoitettu liikkumispalveluiden tuottajille ja kehittäjille." "FIN"))
  (.add model catalog
        (ResourceFactory/createProperty (str foaf "description"))
        ;; TODO: Hae kielimallin mukainen kuvaus
        (ResourceFactory/createLangLiteral "NAP-liikkumispalvelukatalogi on avoin kansallinen yhteyspiste (National Access Point, NAP), johon liikkumispalvelun tuottajien on toimitettava tietoja digitaalisista olennaisten tietojen koneluettavista rajapinnoistaan. NAP-palvelu on osa kokonaisuutta, jonka tavoitteena on aikaansaada helppokäyttöisiä yhdistettyjä liikkumis- ja infopalveluita. NAP ei ole loppukäyttäjien ja matkustajien palvelu, vaan se on tarkoitettu liikkumispalveluiden tuottajille ja kehittäjille." "SWE"))
  (.add model catalog
        (ResourceFactory/createProperty (str foaf "homepage"))
        (ResourceFactory/createStringLiteral "https://www.finap.fi/"))
  (.add model fintraffic-agent RDF/type (ResourceFactory/createResource (str foaf "Agent")))
  (.add model fintraffic-agent
        (ResourceFactory/createProperty (str foaf "name"))
        (ResourceFactory/createStringLiteral "Fintraffic Oy"))
  (.add model catalog
        (ResourceFactory/createProperty (str dct "publisher"))
        fintraffic-agent)
  )

(defn create-record [service model]
  (let [id (:ote.db.transport-service/id service)
        record-uri ""
        dataset-uri (str dataset-base-uri id)
        record (ResourceFactory/createResource record-uri)
        created (:ote.db.modification/created service)
        modified (:ote.db.modification/modified service)]
    (.add model record RDF/type (ResourceFactory/createResource (str dcat "CatalogRecord")))
    (.add model record
          (ResourceFactory/createProperty (str dct "created"))
          (ResourceFactory/createStringLiteral (str created)))
    (.add model record
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/FIN"))
    (.add model record
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/SWE"))
    (.add model record
          (ResourceFactory/createProperty (str dct "language"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/language/ENG"))
    (.add model record
          (ResourceFactory/createProperty (str foaf "primaryTopic"))
          (ResourceFactory/createResource (str dataset-uri)))
    (.add model record
          (ResourceFactory/createProperty (str dct "modified"))
          (ResourceFactory/createStringLiteral (str modified)))))

(defn create-dataset [service model catalog service-id operator-id operation-areas operator]
  (let [id (:ote.db.transport-service/id service)
        dataset-uri (str dataset-base-uri id)
        distribution-uri (str distribution-base-uri "/" id)
        operator-uri (operator-url (::t-operator/business-id operator))
        dataset (ResourceFactory/createResource dataset-uri)
        distribution (ResourceFactory/createResource distribution-uri)
        spatial-resource (ResourceFactory/createResource)
        operator-agent (ResourceFactory/createResource operator-uri)
        operator-name (:ote.db.transport-operator/name operator)]
    (.add model dataset RDF/type (ResourceFactory/createResource (str dcat "Dataset")))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "title"))
          (ResourceFactory/createStringLiteral (:ote.db.transport-service/name service)))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "description"))
          (ResourceFactory/createStringLiteral (or (::t-service/description service) "")))

    (.add model catalog
          (ResourceFactory/createProperty (str dcat "dataset"))
          dataset)

    (.add model dataset
          (ResourceFactory/createProperty (str mobility "transportMode"))
          (ResourceFactory/createStringLiteral (name (:ote.db.transport-service/sub-type service))))

    (.add model distribution RDF/type (ResourceFactory/createResource (str dcat "Distribution")))
    (.add model dataset
          (ResourceFactory/createProperty (str dcat "distribution"))
          distribution)

    ;;
    (.add model distribution
          (ResourceFactory/createProperty (str dcat "accessURL"))
          (ResourceFactory/createResource (str base-uri "export/geojson/" operator-id "/" service-id)))
    ;; Download urliin tulee osote, josta se data on ladattavissa
    (.add model distribution
          (ResourceFactory/createProperty (str dcat "downloadURL"))
          (ResourceFactory/createResource (str base-uri "export/geojson/" operator-id "/" service-id)))

    (.add model dataset
          (ResourceFactory/createProperty (str dct "accrualPeriodicity"))
          (ResourceFactory/createResource "http://publications.europa.eu/resource/authority/frequency/AS_NEEDED"))

    (.add model dataset
          (ResourceFactory/createProperty (str mobility "mobilityTheme"))
          (ResourceFactory/createResource (select-mobility-theme service)))

    ;; Jos sub-themeä ei ole, niin ei lisätä sitä
    (when (select-sub-theme service)
      (.add model dataset
            (ResourceFactory/createProperty (str mobility "mobilityTheme"))
            (ResourceFactory/createResource (select-sub-theme service))))

    ;; HAe NUTS aineistosta korkeampi taso: http://publications.europa.eu/resource/dataset/nuts - hae täältä Esim suomi Finland
    ;; Ja täältä esim kunta: https://stirdata.github.io/data-specification/lau.ttl
    ;; Tässä vaiheessa on annettu pelkästään polygon, joka on kaikilla Finapin palveluilla
    (.add model spatial-resource (ResourceFactory/createProperty (str locn "geometry"))
          (ResourceFactory/createStringLiteral (:geojson (first operation-areas)))
          #_ (ResourceFactory/createTypedLiteral "https://www.iana.org/assignments/media-types/application/vnd.geo+json" (:geojson (first operation-areas))))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "spatial"))
          spatial-resource)

    (.add model operator-agent RDF/type (ResourceFactory/createResource (str foaf "Agent")))
    (.add model operator-agent
          (ResourceFactory/createProperty (str foaf "name"))
          (ResourceFactory/createStringLiteral operator-name))
    (.add model dataset
          (ResourceFactory/createProperty (str dct "publisher"))
          operator-agent)

    ))

(defn create-dcat-ap-model [service operation-areas operator]
  (let [service-id (:ote.db.transport-service/id service)
        operator-id (:ote.db.transport-service/transport-operator-id service)
        model (ModelFactory/createDefaultModel)
        catalog (ResourceFactory/createResource catalog-uri)
        datasets [service]
        #_ (println "create-dcat-ap-model :: datasets " datasets)
        _ (println "create-dcat-ap-model :: service " service)
        ;; Tälläinen organisaatiosivu pitäisi tehdä, joka listaa organisaation palvelut
        fintraffic-agent (ResourceFactory/createResource (fintraffic-url business-id))
        ;linquistic-resource (ResourceFactory/createResource fintraffic-url)

        records datasets]
    (.setNsPrefix model "dcat" dcat)
    (.setNsPrefix model "dct" dct)
    (.setNsPrefix model "foaf" foaf)
    (.setNsPrefix model "mobility" mobility)

    ;; Lisää catalogi
    (create-catalog-model model catalog fintraffic-agent)

    ;; Records
    (doseq [service records]
      (create-record service model))

    ;; Datasetit - KAikki datasetit, mitä tietokannasta löytyy
    (doseq [service datasets]
      (create-dataset service model catalog service-id operator-id operation-areas operator))
    model))

;; Sarjallista RDF/XML
(defn serialize-model [model]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (.write model out "TURTLE" #_ "RDF/XML")
    (.toString out)))

(defn create-rdf [db service-id]
  (let [service-id (if (string? service-id)
                     (Long/parseLong service-id)
                     service-id)
        #_ (println "create-rds :: service-id " service-id)
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
        _ (println "create-rds :: operator " operator)
        #_ (println "create-rds :: operation-areas " operation-areas)
        #_ (println "create-rds :: service " service)

        model (create-dcat-ap-model service operation-areas operator)
        ;rdf-xml (serialize-model model)
        rdf-turtle (serialize-model model)
        turtle-format (-> (response/response rdf-turtle)
                  (response/content-type "text/turtle; charset=UTF-8"))
        #_ (println "create-rds :: juttu " juttu)]
    turtle-format))

(defn- rds-routes [config db]
  (routes
    (GET ["/rdf/:service-id", :service-id #".+"] {{service-id :service-id} :params :as req}
      #_ (http/api-response req (create-rdf db service-id))
      {:status  200
       :headers {"Content-Type"        "text/turtle; charset=UTF-8"
                 "Content-Disposition" "attachment;"}
       :body    (create-rdf db service-id)})))

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
