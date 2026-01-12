(ns ote.services.rdf-test
  "Tests for RDF generation"
  (:require [clojure.test :as t :refer [deftest testing is]]
            [clojure.string :as str]
            [ote.services.rdf.model :as rdf-model]
            [ote.services.rdf-test-utilities :as test-utils]))

(deftest catalog
  (testing "Catalog"
    (testing "has all mandatory properties"
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf test-utils/test-small-taxi-service "http://localhost:3000/")
                         "http://localhost:3000/")
            catalog (:catalog rdf-output)
            catalog-props (:properties catalog)
            mandatory-properties [:dcat/dataset :dct/description :foaf/homepage 
                                 :dct/publisher :dcat/record :dct/spatial :dct/title]]
        (doseq [prop mandatory-properties]
          (is (contains? catalog-props prop)
              (str "Catalog should have mandatory property " prop)))))
    
    (testing "dct:title is \"Finap.fi - NAP - National Access Point\""
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf {} "http://localhost:3000/")
                         "http://localhost:3000/")
            catalog (:catalog rdf-output)
            title (get-in catalog [:properties :dct/title])]
        (is (= (:value title) "Finap.fi - NAP - National Access Point"))))
    
    (testing "dct:publisher is URL to Fintraffic english site"
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf {} "http://localhost:3210/")
                         "http://localhost:3210/")
            catalog (:catalog rdf-output)
            publisher (get-in catalog [:properties :dct/publisher])]
        (is (= (:value publisher) "https://www.fintraffic.fi/en"))))
    
    (testing "dct:description is obtained from i18n files"
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf {} "http://localhost:3000/")
                         "http://localhost:3000/")
            catalog (:catalog rdf-output)
            descriptions (get-in catalog [:properties :dct/description])
            fi-expected (rdf-model/localized-text-with-key "fi" [:front-page :column-NAP])
            sv-expected (rdf-model/localized-text-with-key "sv" [:front-page :column-NAP])
            en-expected (rdf-model/localized-text-with-key "en" [:front-page :column-NAP])]
        (is (= (count descriptions) 3) "Should have 3 language variants")
        (is (some #(and (= (:lang %) "fi") 
                       (= (:value %) fi-expected)) 
                 descriptions)
            "Finnish description should be from i18n [:front-page :column-NAP]")
        (is (some #(and (= (:lang %) "sv") 
                       (= (:value %) sv-expected)) 
                 descriptions)
            "Swedish description should be from i18n [:front-page :column-NAP]")
        (is (some #(and (= (:lang %) "en") 
                       (= (:value %) en-expected)) 
                 descriptions)
            "English description should be from i18n [:front-page :column-NAP]"))))
    
    (testing "dct:spatial is the Finnish country code"
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf {} "http://localhost:3000/")
                         "http://localhost:3000/")
            catalog (:catalog rdf-output)
            spatial (get-in catalog [:properties :dct/spatial])
            spatial-identifier (get-in spatial [:properties :dct/identifier])]
        (is (= (:value spatial-identifier) "http://publications.europa.eu/resource/authority/country/FIN"))))
    
    (testing "dct:license is \"Creative Commons Nimeä 4.0 Kansainvälinen\""
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf {} "http://localhost:3000/")
                         "http://localhost:3000/")
            catalog (:catalog rdf-output)
            license (get-in catalog [:properties :dct/license])
            license-identifier (get-in license [:properties :dct/identifier])]
        (is (= (:value license-identifier) "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")
            "License should be CC BY 4.0 (Creative Commons Nimeä 4.0 Kansainvälinen)")))
    
    (testing "dcat:record contains as many elements as there are datasets"
      (let [rdf-output (rdf-model/add-catalog-to-service-rdf 
                         (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
                         "http://localhost:3000/")
            catalog (:catalog rdf-output)
            catalog-records (:catalog-records rdf-output)
            datasets (:datasets rdf-output)
            dataset-uris (set (map :uri datasets))
            record-primary-topics (set (map #(get-in % [:properties :foaf/primaryTopic :value]) catalog-records))
            catalog-record-links (get-in catalog [:properties :dcat/record])
            catalog-record-uris (set (map :value catalog-record-links))
            expected-record-uris (set (map :uri catalog-records))]
        (is (= (count catalog-records) (count datasets))
            "Should have one catalog record for each dataset")
        (is (= dataset-uris record-primary-topics)
            "Each catalog record's foaf:primaryTopic should match a dataset URI")
        (is (= catalog-record-uris expected-record-uris)
            "Catalog's dcat:record properties should link to all catalog records")))
    
    (testing "dataset URIs follow hierarchical pattern with operator context"
      ;; The web app uses URLs like /{operator-id}/{service-id}/ for services, and this is used as base for dataset URIs too.
      (let [operator-id 123
            service-id 456
            area-id 789
            interface-id 999
            test-data (-> test-utils/test-large-bus-service
                         (assoc-in [:service :ote.db.transport-service/id] service-id)
                         (assoc-in [:service :ote.db.transport-service/transport-operator-id] operator-id)
                         (assoc-in [:operation-areas 0 :id] area-id)
                         (assoc-in [:service :ote.db.transport-service/external-interfaces 0 :ote.db.transport-service/id] interface-id))
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            datasets (:datasets rdf-output)
            geojson-dataset (first (remove #(str/includes? (:uri %) "/interface/") datasets))
            interface-dataset (first (filter #(str/includes? (:uri %) "/interface/") datasets))]
        
        (testing "GeoJSON dataset URI includes operator-id and service-id (service-level, not per-area)"
          (is (not (nil? geojson-dataset)) "Should have at least one GeoJSON dataset")
          (when geojson-dataset
            (is (= (:uri geojson-dataset) 
                   (str "http://localhost:3000/dataset/" operator-id "/" service-id))
                "GeoJSON dataset URI should be {base-url}dataset/{operator-id}/{service-id}")))
        
        (testing "Interface dataset URI includes operator-id, service-id, and interface-id"
          (is (not (nil? interface-dataset)) "Should have at least one interface dataset")
          (when interface-dataset
            (is (= (:uri interface-dataset) 
                   (str "http://localhost:3000/dataset/" operator-id "/" service-id "/interface/" interface-id))
                "Interface dataset URI should be {base-url}dataset/{operator-id}/{service-id}/interface/{interface-id}"))))))

(deftest catalog-record
  (testing "CatalogRecord"
    (testing "has all mandatory properties"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-small-taxi-service "http://localhost:3000/")
            catalog-record (first (:catalog-records rdf-output))
            catalog-record-props (:properties catalog-record)
            mandatory-properties [:dct/created :dct/language :foaf/primaryTopic :dct/modified]]
        (doseq [prop mandatory-properties]
          (is (contains? catalog-record-props prop)
              (str "CatalogRecord should have mandatory property " prop)))))))

(deftest dataset
  (testing "Dataset"
    (testing "has all mandatory properties"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-small-taxi-service "http://localhost:3000/")
            dataset (first (:datasets rdf-output))
            dataset-props (:properties dataset)
            mandatory-properties [:dct/description :dcat/distribution :dct/accrualPeriodicity
                                 :mobility/mobilityTheme :dct/spatial :dct/title :dct/publisher]]
        (doseq [prop mandatory-properties]
          (is (contains? dataset-props prop)
              (str "Dataset should have mandatory property " prop)))))
    
    (testing "always has exactly one Distribution"
      ;; This is not a general property of mobility DCAT-AP, but a reusult of our data model.
      ;; There is no wayt to programmatically deduce whether two external interfaces are different
      ;; representations of the same dataset, so we create one dataset per interface with a single
      ;; distribution each.
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
            datasets (:datasets rdf-output)]
        (doseq [dataset datasets]
          (let [distributions (get-in dataset [:properties :dcat/distribution])]
            (is (= 1 (count distributions))
                (str "Dataset " (:uri dataset) " should have exactly one distribution"))
            (let [distribution (first distributions)]
              (is (= :resource (:type distribution))
                  "Distribution should be an embedded resource")
              (is (= :dcat/Distribution (get-in distribution [:properties :rdf/type :value]))
                  "Distribution should have type :dcat/Distribution"))))))
    
    (testing "dct:publisher is the search URL for the operator"
      (let [business-id "1234567-5"
            test-data (assoc-in test-utils/test-small-taxi-service [:operator :ote.db.transport-operator/business-id] business-id)
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            dataset (first (:datasets rdf-output))
            publisher (get-in dataset [:properties :dct/publisher])]
        (is (= (:value publisher) "http://localhost:3000/service-search?operators=1234567-5"))))
    
    (testing "dct:title for external interfaces includes service name and interface access URL"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
            datasets (:datasets rdf-output)
            ;; Find a dataset that has an interface (not the GeoJSON one)
            ;; Interface datasets have "/interface/" in their URI, GeoJSON datasets have "/area/"
            interface-dataset (first (filter #(str/includes? (:uri %) "/interface/") datasets))
            title (get-in interface-dataset [:properties :dct/title])]
        (is (not (nil? interface-dataset)) "Should have at least one interface dataset")
        (when interface-dataset
          (is (string? (:value title)) "Title should be a string")
          (is (str/starts-with? (:value title) 
                                          (get-in test-utils/test-large-bus-service [:service :ote.db.transport-service/name]))
              "Title should start with the service name")
          (is (str/includes? (:value title) "http") 
              "Title should include the interface access URL")))))
    
    (testing "is created once per service (not per operation area)"
      (let [helsinki-geojson "{\"type\":\"Point\",\"coordinates\":[24.9384,60.1695]}"
            espoo-geojson "{\"type\":\"Point\",\"coordinates\":[24.6559,60.2055]}"
            test-data (assoc test-utils/test-small-taxi-service
                            :operation-areas
                            [{:id 1
                              :geojson helsinki-geojson
                              :primary? true
                              :description [{:ote.db.transport-service/lang "FI"
                                           :ote.db.transport-service/text "Helsinki"}]}
                             {:id 2
                              :geojson espoo-geojson
                              :primary? false
                              :description [{:ote.db.transport-service/lang "FI"
                                           :ote.db.transport-service/text "Espoo"}]}])
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            datasets (:datasets rdf-output)
            geojson-datasets (remove #(str/includes? (:uri %) "/interface/") datasets)]
        (is (= 1 (count geojson-datasets)) "Should have exactly one GeoJSON dataset per service")
        ;; Check that title is just the service name (no area names)
        (let [geojson-dataset (first geojson-datasets)
              title (get-in geojson-dataset [:properties :dct/title :value])
              service-name (get-in test-data [:service :ote.db.transport-service/name])]
          (is (= title service-name)
              "GeoJSON dataset title should be the service name without area names")
          (is (not (str/includes? title "Helsinki"))
              "Title should not include area name Helsinki")
          (is (not (str/includes? title "Espoo"))
              "Title should not include area name Espoo"))
        ;; Check that spatial property contains both operation areas in a GeometryCollection
        (let [geojson-dataset (first geojson-datasets)
              spatial-locations (get-in geojson-dataset [:properties :dct/spatial])
              geometry-location (first (filter #(= :resource (:type %)) spatial-locations))
              geometry-literal (get-in geometry-location [:properties :locn/geometry :value])
              parsed-geometry (cheshire.core/parse-string geometry-literal keyword)]
          (is (not (nil? geometry-location)) "Should have a dct:Location with geometry")
          (is (= "GeometryCollection" (:type parsed-geometry))
              "Spatial geometry should be a GeometryCollection")
          (is (= 2 (count (:geometries parsed-geometry)))
              "GeometryCollection should contain 2 geometries (one per operation area)")
          (is (some #(= % {:type "Point" :coordinates [24.9384 60.1695]}) (:geometries parsed-geometry))
              "Should contain Helsinki coordinates")
          (is (some #(= % {:type "Point" :coordinates [24.6559 60.2055]}) (:geometries parsed-geometry))
              "Should contain Espoo coordinates")))))
    
    (testing "spatial is dct:Location with geometry when operation-areas exist"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-small-taxi-service "http://localhost:3000/")
            datasets (:datasets rdf-output)
            geojson-dataset (first (remove #(str/includes? (:uri %) "/interface/") datasets))
            spatial-locations (get-in geojson-dataset [:properties :dct/spatial])]
        (is (not (nil? geojson-dataset)) "Should have a GeoJSON dataset")
        (is (= 1 (count spatial-locations)) "Should have exactly one spatial entry")
        (let [spatial-entry (first spatial-locations)]
          (is (= :resource (:type spatial-entry)) "Spatial should be a resource (dct:Location)")
          (is (= :dct/Location (get-in spatial-entry [:properties :rdf/type :value]))
              "Spatial resource should have type dct:Location")
          (is (contains? (:properties spatial-entry) :locn/geometry)
              "dct:Location should have locn:geometry property"))))
    
    (testing "spatial is municipality URI when no operation-areas exist"
      (let [municipality-uri "http://publications.europa.eu/resource/authority/place/FIN_HEL"
            test-data (-> test-utils/test-small-taxi-service
                         (assoc :operation-areas [])
                         (assoc-in [:service :municipality] municipality-uri))
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            datasets (:datasets rdf-output)
            geojson-dataset (first (remove #(str/includes? (:uri %) "/interface/") datasets))]
        ;; When there are no operation areas, there should be no GeoJSON dataset
        (is (nil? geojson-dataset) 
            "Should not have a GeoJSON dataset when operation-areas is empty")))

(deftest distribution
  (testing "Distribution"
    (testing "has required properties"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
            datasets (:datasets rdf-output)
            mandatory-distribution-properties [:dcat/accessURL :mobility/mobilityDataStandard :dct/format :dct/rights]]
        (doseq [dataset datasets]
          (let [distribution (first (get-in dataset [:properties :dcat/distribution]))
                dist-props (set (keys (get distribution :properties)))]
            (doseq [prop mandatory-distribution-properties]
              (is (contains? dist-props prop)
                  (str "Distribution in dataset " (:uri dataset) " should have mandatory property " prop)))))))
    
    (testing "has correct dct:rights values"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
            datasets (:datasets rdf-output)
            geojson-datasets (filter #(not (str/includes? (:uri %) "/interface/")) datasets)
            interface-datasets (filter #(str/includes? (:uri %) "/interface/") datasets)]
        ;; GeoJSON distributions should have free-of-charge rights
        (doseq [dataset geojson-datasets]
          (let [distribution (first (get-in dataset [:properties :dcat/distribution]))
                rights (get-in distribution [:properties :dct/rights])
                rights-type (get-in rights [:properties :dct/type :value])]
            (is (= :resource (:type rights))
                (str "Rights in GeoJSON dataset " (:uri dataset) " should be a resource"))
            (is (= "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided-free-of-charge"
                   rights-type)
                (str "GeoJSON dataset " (:uri dataset) " should have free-of-charge rights"))))
        ;; Interface distributions should have appropriate rights based on license
        (doseq [dataset interface-datasets]
          (let [distribution (first (get-in dataset [:properties :dcat/distribution]))
                rights (get-in distribution [:properties :dct/rights])
                rights-type (get-in rights [:properties :dct/type :value])]
            (is (= :resource (:type rights))
                (str "Rights in interface dataset " (:uri dataset) " should be a resource"))
            (is (or (= "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/licence-provided" rights-type)
                    (= "https://w3id.org/mobilitydcat-ap/conditions-for-access-and-usage/other" rights-type))
                (str "Interface dataset " (:uri dataset) " should have valid rights URL"))))))))

(deftest relationship
  (testing "Relationship"
    (testing "is created between the GeoJSON dataset and each interface dataset"
      (let [test-data (-> test-utils/test-large-bus-service
                          (assoc :operation-areas
                                 [{:id 1
                                   :geojson "{\"type\":\"Polygon\",\"coordinates\":[]}"
                                   :primary? true
                                   :description [{:ote.db.transport-service/lang "FI"
                                                  :ote.db.transport-service/text "Helsinki"}]}
                                  {:id 2
                                   :geojson "{\"type\":\"Polygon\",\"coordinates\":[]}"
                                   :primary? false
                                   :description [{:ote.db.transport-service/lang "FI"
                                                  :ote.db.transport-service/text "Espoo"}]}]))
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            relationships (:relationships rdf-output)
            geojson-datasets (remove #(str/includes? (:uri %) "/interface/") (:datasets rdf-output))
            interface-datasets (filter #(str/includes? (:uri %) "/interface/") (:datasets rdf-output))
            num-geojson (count geojson-datasets)
            num-interfaces (count interface-datasets)
            expected-relationships (* num-geojson num-interfaces 2)] ; 2 relationships per pair (relation + isReferencedBy)
        (is (= 1 num-geojson) "Should have exactly 1 GeoJSON dataset (service-level, not per-area)")
        (is (= 2 num-interfaces) "Should have 2 interface datasets")
        (is (= expected-relationships (count relationships))
            (str "Should have " expected-relationships " relationships (1 GeoJSON dataset x 2 interfaces x 2 directions)"))))))

;; A snippet for service testing in REPL
#_(keep (fn [service-id]
          (try
            (when-let [service-data (ote.services.rdf.data/fetch-service-data db service-id)]
              (when (seq (get-in service-data [:service :ote.db.transport-service/external-interfaces]))
                (update service-data :operation-areas
                        (partial map #(assoc % :geojson "{\"type\":\"Polygon\",\"coordinates\":[]}")))))
            (catch Throwable e
              (println "Error processing service id" service-id ":" (.getMessage e))
              nil)))
        (range 1 1000))
