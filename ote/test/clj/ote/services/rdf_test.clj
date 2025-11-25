(ns ote.services.rdf-test
  "Tests for RDF generation"
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.services.rdf.data :as rdf-data]
            [ote.services.rdf.model :as rdf-model]
            [ote.localization :as localization :refer [tr]]))

(def test-service
  {:service
   {:ote.db.transport-service/contact-email "tero.testinen@sahkoposti.fi",
    :ote.db.transport-service/sub-type :taxi,
    :ote.db.transport-service/id 123456789,
    :ote.db.modification/created
    #inst "2016-12-19T18:45:33.294000000-00:00",
    :ote.db.transport-service/notice-external-interfaces? false,
    :ote.db.transport-service/companies [],
    :municipality "https://w3id.org/stirdata/resource/lau/item/FI_092",
    :ote.db.transport-service/description
    [#:ote.db.transport-service{:lang "FI", :text "henkilöliikenne"}],
    :ote.db.transport-service/brokerage? false,
    :ote.db.transport-service/external-interfaces nil,
    :ote.db.transport-service/rentals {},
    :ote.db.transport-service/contact-address
    #:ote.db.common{:street "Testikatu 1",
                    :post_office "Vantaa",
                    :postal_code "01230"},
    :ote.db.modification/created-by
    "some_uuid",
    :ote.db.transport-service/name "Taksi",
    :ote.db.transport-service/type :passenger-transportation,
    :ote.db.transport-service/transport-operator-id 1,
    :ote.db.transport-service/contact-phone "0401234567",
    :ote.db.transport-service/commercial-traffic? true,
    :ote.db.transport-service/passenger-transportation
    #:ote.db.transport-service{:additional-services [],
                               :price-classes [],
                               :guaranteed-accessibility-tool [],
                               :booking-service
                               #:ote.db.transport-service{:description
                                                          []},
                               :guaranteed-info-service-accessibility [],
                               :guaranteed-accessibility-description [],
                               :limited-transportable-aid [],
                               :guaranteed-vehicle-accessibility [],
                               :pricing
                               #:ote.db.transport-service{:description
                                                          []},
                               :payment-methods [],
                               :limited-vehicle-accessibility [],
                               :real-time-information
                               #:ote.db.transport-service{:description
                                                          []},
                               :guaranteed-transportable-aid [],
                               :payment-method-description [],
                               :limited-accessibility-description [],
                               :service-exceptions [],
                               :limited-info-service-accessibility [],
                               :service-hours [],
                               :luggage-restrictions
                               [#:ote.db.transport-service{:lang "FI"}],
                               :limited-accessibility-tool []},
    :ote.db.transport-service/transport-type [:road]},
   :operation-areas
   [{:geojson "{\"type\":\"Polygon\",\"coordinates\":[]}",
     :primary? true,
     :feature-id "Vantaa"}],
   :operator
   #:ote.db.transport-operator{:gsm "0401234567",
                               :business-id "1234567-5",
                               :name "Tero Testinen",
                               :ckan-group-id
                               "some_uuid",
                               :id 123456789,
                               :billing-address {},
                               :visiting-address
                               #:ote.db.common{:street "Testikatu 1",
                                               :post_office "Vantaa",
                                               :postal_code "01230"},
                               :phone "0401234567",
                               :deleted? false,
                               :email "tero.testinen@example.com"},
   :validation-data {},
   :latest-publication #inst "2025-11-17T06:43:13.005000000-00:00"})

(deftest catalog
  (testing "Catalog"
    (testing "has all mandatory properties"
      (let [rdf-output (rdf-model/service-data->rdf test-service)
            catalog (:catalog rdf-output)
            catalog-props (:properties catalog)
            mandatory-properties [:dcat/dataset :dct/description :foaf/homepage 
                                 :dct/publisher :dcat/record :dct/spatial :foaf/title]]
        (doseq [prop mandatory-properties]
          (is (contains? catalog-props prop)
              (str "Catalog should have mandatory property " prop)))))
    
    (testing "dct:title is \"Finap.fi - NAP - National Access Point\""
      (let [rdf-output (rdf-model/service-data->rdf {})
            catalog (:catalog rdf-output)
            title (get-in catalog [:properties :foaf/title])]
        (is (= (:value title) "Finap.fi - NAP - National Access Point"))))
    
    (testing "dct:publisher is FINAP search URL to Fintraffic business id"
      (let [rdf-output (rdf-model/service-data->rdf {})
            catalog (:catalog rdf-output)
            publisher (get-in catalog [:properties :dct/publisher])]
        (is (= (:value publisher) "https://finap.fi/service-search?operators=2942108-7"))))
    
    (testing "dct:description is obtained from i18n files"
      (let [rdf-output (rdf-model/service-data->rdf {})
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
      (let [rdf-output (rdf-model/service-data->rdf {})
            catalog (:catalog rdf-output)
            spatial (get-in catalog [:properties :dct/spatial])]
        (is (= (:value spatial) "http://data.europa.eu/nuts/code/FI"))))
    
    (testing "dct:license is \"Creative Commons Nimeä 4.0 Kansainvälinen\""
      (let [rdf-output (rdf-model/service-data->rdf {})
            catalog (:catalog rdf-output)
            license (get-in catalog [:properties :dct/license])
            license-identifier (get-in license [:properties :dct/identifier])]
        (is (= (:value license-identifier) "http://publications.europa.eu/resource/authority/licence/CC_BY_4_0")
            "License should be CC BY 4.0 (Creative Commons Nimeä 4.0 Kansainvälinen)"))))

