(ns ote.services.rdf-test
  "Tests for RDF generation"
  (:require [clojure.test :as t :refer [deftest testing is]]
            [clojure.string :as str]
            [ote.services.rdf.model :as rdf-model]
            [ote.services.rdf-test-utilities :as test-utils]
            [ote.localization :as localization :refer [tr]]))

(deftest catalog
  (testing "Catalog"
    (testing "has all mandatory properties"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-small-taxi-service "http://localhost:3000/")
            catalog (:catalog rdf-output)
            catalog-props (:properties catalog)
            mandatory-properties [:dcat/dataset :dct/description :foaf/homepage 
                                 :dct/publisher :dcat/record :dct/spatial :dct/title]]
        (doseq [prop mandatory-properties]
          (is (contains? catalog-props prop)
              (str "Catalog should have mandatory property " prop)))))
    
    (testing "dct:title is \"Finap.fi - NAP - National Access Point\""
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3000/")
            catalog (:catalog rdf-output)
            title (get-in catalog [:properties :dct/title])]
        (is (= (:value title) "Finap.fi - NAP - National Access Point"))))
    
    (testing "dct:publisher is URL to Fintraffic english site"
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3210/")
            catalog (:catalog rdf-output)
            publisher (get-in catalog [:properties :dct/publisher])]
        (is (= (:value publisher) "https://www.fintraffic.fi/en"))))
    
    (testing "dct:description is obtained from i18n files"
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3000/")
            catalog (:catalog rdf-output)
            descriptions (get-in catalog [:properties :dct/description])
            fi-expected (localization/with-language "fi" (tr [:front-page :column-NAP]))
            sv-expected (localization/with-language "sv" (tr [:front-page :column-NAP]))
            en-expected (localization/with-language "en" (tr [:front-page :column-NAP]))]
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
    
    (testing "dct:spatial is the Finnish NUTS code"
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3000/")
            catalog (:catalog rdf-output)
            spatial (get-in catalog [:properties :dct/spatial])
            spatial-identifier (get-in spatial [:properties :dct/identifier])]
        (is (= (:value spatial-identifier) "http://data.europa.eu/nuts/code/FI"))))
    
    (testing "dct:license is \"Creative Commons Nimeä 4.0 Kansainvälinen\""
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3000/")
            catalog (:catalog rdf-output)
            license (get-in catalog [:properties :dct/license])
            license-identifier (get-in license [:properties :dct/identifier])]
        (is (= (:value license-identifier) "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")
            "License should be CC BY 4.0 (Creative Commons Nimeä 4.0 Kansainvälinen)")))
    
    (testing "dcat:record contains as many elements as there are datasets"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
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
            "Catalog's dcat:record properties should link to all catalog records"))))

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
                (str "Dataset " (:uri dataset) " should have exactly one distribution"))))))
    
    (testing "dct:publisher is the search URL for the operator"
      (let [business-id "1234567-5"
            test-data (assoc-in test-utils/test-small-taxi-service [:operator :ote.db.transport-operator/business-id] business-id)
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            dataset (first (:datasets rdf-output))
            publisher (get-in dataset [:properties :dct/publisher])]
        (is (= (:uri publisher) "http://localhost:3000/service-search?operators=1234567-5"))))
    
    (testing "dct:title for external interfaces includes service name and interface access URL"
      (let [rdf-output (rdf-model/service-data->rdf test-utils/test-large-bus-service "http://localhost:3000/")
            datasets (:datasets rdf-output)
            ;; Find a dataset that has an interface (not the GeoJSON one)
            ;; TODO need a better way to identify interface datasets
            interface-dataset (first (filter #(not (str/includes? (:uri %) "/rdf/")) datasets))
            title (get-in interface-dataset [:properties :dct/title])]
        (is (not (nil? interface-dataset)) "Should have at least one interface dataset")
        (when interface-dataset
          (is (string? (:value title)) "Title should be a string")
          (is (str/starts-with? (:value title) 
                                          (get-in test-utils/test-large-bus-service [:service :ote.db.transport-service/name]))
              "Title should start with the service name")
          (is (str/includes? (:value title) "http") 
              "Title should include the interface access URL")))))
    
    (testing "is created for each operation area"
      (let [test-data (assoc test-utils/test-small-taxi-service
                            :operation-areas
                            [{:id 1
                              :geojson "{\"type\":\"Polygon\",\"coordinates\":[]}"
                              :primary? true
                              :description [{:ote.db.transport-service/lang "FI"
                                           :ote.db.transport-service/text "Helsinki"}]}
                             {:id 2
                              :geojson "{\"type\":\"Polygon\",\"coordinates\":[]}"
                              :primary? false
                              :description [{:ote.db.transport-service/lang "FI"
                                           :ote.db.transport-service/text "Espoo"}]}])
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            datasets (:datasets rdf-output)
            geojson-datasets (filter #(str/includes? (:uri %) "/area/") datasets)]
        (is (= 2 (count geojson-datasets)) "Should have one dataset per operation area")
        (is (some #(str/includes? (:uri %) "/area/1") geojson-datasets)
            "Should have dataset for operation area 1")
        (is (some #(str/includes? (:uri %) "/area/2") geojson-datasets)
            "Should have dataset for operation area 2")
        ;; Check that titles include area names
        (let [titles (map #(get-in % [:properties :dct/title :value]) geojson-datasets)]
          (is (some #(str/includes? % "Helsinki") titles)
              "Should have dataset title with Helsinki")
          (is (some #(str/includes? % "Espoo") titles)
              "Should have dataset title with Espoo")))))

(deftest relationship
  (testing "Relationship"
    ;; TODO need to check why it is we do this, and does this generalize as we moved from including one GeoJSON to multiple
    (testing "is created between each GeoJSON dataset and interface dataset"
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
            geojson-datasets (filter #(str/includes? (:uri %) "/area/") (:datasets rdf-output))
            interface-datasets (remove #(str/includes? (:uri %) "/area/") (:datasets rdf-output))
            num-geojson (count geojson-datasets)
            num-interfaces (count interface-datasets)
            expected-relationships (* num-geojson num-interfaces 2)] ; 2 relationships per pair (relation + isReferencedBy)
        (is (= 2 num-geojson) "Should have 2 GeoJSON datasets")
        (is (= 2 num-interfaces) "Should have 2 interface datasets")
        (is (= expected-relationships (count relationships))
            (str "Should have " expected-relationships " relationships (2 operation areas x 2 interfaces x 2 directions)"))))))

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
