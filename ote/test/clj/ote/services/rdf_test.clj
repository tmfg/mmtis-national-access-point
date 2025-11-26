(ns ote.services.rdf-test
  "Tests for RDF generation"
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.services.rdf.data :as rdf-data]
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
                                 :dct/publisher :dcat/record :dct/spatial :foaf/title]]
        (doseq [prop mandatory-properties]
          (is (contains? catalog-props prop)
              (str "Catalog should have mandatory property " prop)))))
    
    (testing "dct:title is \"Finap.fi - NAP - National Access Point\""
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3000/")
            catalog (:catalog rdf-output)
            title (get-in catalog [:properties :foaf/title])]
        (is (= (:value title) "Finap.fi - NAP - National Access Point"))))
    
    (testing "dct:publisher is FINAP search URL to Fintraffic business id"
      (let [rdf-output (rdf-model/service-data->rdf {} "http://localhost:3210/")
            catalog (:catalog rdf-output)
            publisher (get-in catalog [:properties :dct/publisher])]
        (is (= (:value publisher) "http://localhost:3210/service-search?operators=2942108-7"))))
    
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
            spatial (get-in catalog [:properties :dct/spatial])]
        (is (= (:value spatial) "http://data.europa.eu/nuts/code/FI"))))
    
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
    
    (testing "dct:publisher is the search URL for the operator"
      (let [business-id "1234567-5"
            test-data (assoc-in test-utils/test-small-taxi-service [:operator :ote.db.transport-operator/business-id] business-id)
            rdf-output (rdf-model/service-data->rdf test-data "http://localhost:3000/")
            dataset (first (:datasets rdf-output))
            publisher (get-in dataset [:properties :dct/publisher])]
        (is (= (:uri publisher) "http://localhost:3000/service-search?operators=1234567-5"))))))

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