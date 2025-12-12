(ns ote.services.rdf.serialization-test
  "Tests for RDF serialization utilities, focusing on add-resource-to-model! function"
  (:require [clojure.test :refer [deftest testing is]]
            [ote.services.rdf.serialization :as serialization]
            [ote.services.rdf.model :as model])
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]))

(defn- get-statement
  "Get a specific statement from model by subject URI and predicate URI"
  [model subject-uri predicate-uri]
  (let [subject (ResourceFactory/createResource subject-uri)
        predicate (ResourceFactory/createProperty predicate-uri)
        iter (.listStatements model subject predicate nil)]
    (when (.hasNext iter)
      (.next iter))))

(defn- count-statements
  "Count statements in the model"
  [model]
  (.size model))

(deftest test-add-resource-with-uri-value
  (testing "Add resource with URI property value (type :uri with keyword)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/resource1"
                                        {:rdf/type (model/uri :dcat/Catalog)})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain exactly 1 statement")

      (let [stmt (get-statement model
                                "http://example.org/resource1"
                                "http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
            object (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isResource object) "Object should be a resource")
        (is (= "http://www.w3.org/ns/dcat#Catalog"
               (.toString (.asResource object)))
            "Object should be dcat:Catalog")))))

(deftest test-add-resource-with-uri-string-value
  (testing "Add resource with URI property value (type :uri with string)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/resource2"
                                        {:dct/publisher (model/uri "https://www.example.com")})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain exactly 1 statement")

      (let [stmt (get-statement model
                                "http://example.org/resource2"
                                "http://purl.org/dc/terms/publisher")
            object (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isResource object) "Object should be a resource")
        (is (= "https://www.example.com"
               (.toString (.asResource object)))
            "Object URI should match")))))

(deftest test-add-resource-with-literal-value
  (testing "Add resource with plain string literal (type :literal)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/resource3"
                                        {:dct/title (model/literal "Test Title")})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain exactly 1 statement")

      (let [stmt (get-statement model
                                "http://example.org/resource3"
                                "http://purl.org/dc/terms/title")
            object (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isLiteral object) "Object should be a literal")
        (is (= "Test Title" (.getString (.asLiteral object)))
            "Literal value should match")))))

(deftest test-add-resource-with-typed-literal
  (testing "Add resource with typed literal (type :typed-literal)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/resource4"
                                        {:dcat/startDate (model/typed-literal "2023-01-01"
                                                                              "http://www.w3.org/2001/XMLSchema#date")})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain exactly 1 statement")

      (let [stmt (get-statement model
                                "http://example.org/resource4"
                                "http://www.w3.org/ns/dcat#startDate")
            object (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isLiteral object) "Object should be a literal")
        (is (= "2023-01-01" (.getString (.asLiteral object)))
            "Literal value should match")
        (is (= "http://www.w3.org/2001/XMLSchema#date"
               (.toString (.getDatatypeURI (.asLiteral object))))
            "Datatype should match")))))

(deftest test-add-resource-with-lang-literal
  (testing "Add resource with language-tagged literal (type :lang-literal)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/resource5"
                                        {:dct/description (model/lang-literal "Finnish description" "fi")})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain exactly 1 statement")

      (let [stmt (get-statement model
                                "http://example.org/resource5"
                                "http://purl.org/dc/terms/description")
            object (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isLiteral object) "Object should be a literal")
        (is (= "Finnish description" (.getString (.asLiteral object)))
            "Literal value should match")
        (is (= "fi" (.getLanguage (.asLiteral object)))
            "Language tag should match")))))

(deftest test-add-resource-with-blank-node
  (testing "Add resource with nested resource (blank node)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/person"
                                        {:foaf/name (model/resource {:foaf/title (model/literal "John")
                                                                     :dct/description (model/literal "Smith")})})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 3 (count-statements model))
          "Model should contain 3 statements (1 for foaf:name link + 2 for nested properties)")

      ;; Get the statement linking person to the name resource
      (let [stmt (get-statement model
                                "http://example.org/person"
                                "http://xmlns.com/foaf/0.1/name")
            name-resource (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isResource name-resource) "Object should be a resource")
        (is (.isAnon (.asResource name-resource)) "Nested resource should be a blank node")

        ;; Check the nested resource has its properties in the model
        (let [name-res (.asResource name-resource)
              ;; Need to search in the model, not call getProperty on detached resource
              iter (.listStatements model name-res nil nil)
              props (loop [ps []]
                      (if (.hasNext iter)
                        (let [s (.next iter)]
                          (recur (conj ps {:predicate (.toString (.getPredicate s))
                                           :object (.toString (.getObject s))})))
                        ps))]
          (is (= 2 (count props)) "Nested resource should have 2 properties")
          (is (some #(and (= (:predicate %) "http://xmlns.com/foaf/0.1/title")
                          (.contains (:object %) "John")) props)
              "Should have title property with value John")
          (is (some #(and (= (:predicate %) "http://purl.org/dc/terms/description")
                          (.contains (:object %) "Smith")) props)
              "Should have description property with value Smith"))))))

(deftest test-add-resource-with-named-nested-resource
  (testing "Add resource with named nested resource (has URI)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/catalog"
                                        {:dct/spatial (model/resource "http://example.org/location"
                                                                      {:dct/identifier (model/literal "FIN")})})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 2 (count-statements model))
          "Model should contain 2 statements")

      (let [stmt (get-statement model
                                "http://example.org/catalog"
                                "http://purl.org/dc/terms/spatial")
            location-resource (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isResource location-resource) "Object should be a resource")
        (is (= "http://example.org/location"
               (.toString (.asResource location-resource)))
            "Nested resource should have correct URI")
        (is (not (.isAnon (.asResource location-resource))) "Nested resource should not be a blank node")

        ;; Check the nested resource has its property
        (let [id-stmt (get-statement model
                                     "http://example.org/location"
                                     "http://purl.org/dc/terms/identifier")]
          (is (not (nil? id-stmt)) "Nested resource should have identifier")
          (is (= "FIN" (.getString (.getObject id-stmt)))
              "Identifier value should match"))))))

(deftest test-add-resource-with-multi-valued-property
  (testing "Add resource with multi-valued property (vector of values)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/dataset"
                                        {:dct/description [(model/lang-literal "English desc" "en")
                                                           (model/lang-literal "Finnish desc" "fi")
                                                           (model/lang-literal "Swedish desc" "sv")]})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 3 (count-statements model))
          "Model should contain 3 statements (one per language)")

      (let [iter (.listStatements model
                                  (ResourceFactory/createResource "http://example.org/dataset")
                                  (serialization/property :dct/description)
                                  nil)
            descriptions (loop [descs []]
                           (if (.hasNext iter)
                             (let [stmt (.next iter)
                                   literal (.asLiteral (.getObject stmt))]
                               (recur (conj descs
                                            {:value (.getString literal)
                                             :lang (.getLanguage literal)})))
                             descs))]
        (is (= 3 (count descriptions)) "Should have 3 descriptions")
        (is (some #(and (= (:value %) "English desc") (= (:lang %) "en")) descriptions)
            "Should have English description")
        (is (some #(and (= (:value %) "Finnish desc") (= (:lang %) "fi")) descriptions)
            "Should have Finnish description")
        (is (some #(and (= (:value %) "Swedish desc") (= (:lang %) "sv")) descriptions)
            "Should have Swedish description")))))

(deftest test-add-resource-with-rdf-type
  (testing "Add resource with rdf:type property"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/catalog"
                                        {:rdf/type (model/uri :dcat/Catalog)})
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain 1 statement")

      ;; Check using RDF/type constant
      (let [stmt (.getProperty model
                               (ResourceFactory/createResource "http://example.org/catalog")
                               RDF/type)
            object (.getObject stmt)]
        (is (not (nil? stmt)) "rdf:type statement should exist")
        (is (.isResource object) "Object should be a resource")
        (is (= "http://www.w3.org/ns/dcat#Catalog"
               (.toString (.asResource object)))
            "Type should be dcat:Catalog")))))

(deftest test-add-resource-with-existing-jena-resource
  (testing "Add resource referencing an existing Jena Resource object"
    (let [model (ModelFactory/createDefaultModel)
          ;; Create a separate resource first
          existing-resource (ResourceFactory/createResource "http://example.org/existing")
          _ (.add model existing-resource RDF/type
                  (ResourceFactory/createResource "http://www.w3.org/ns/dcat#Dataset"))
          ;; Now reference it from another resource
          resource-data {:uri "http://example.org/catalog"
                         :properties {:dcat/dataset {:type :resource :value existing-resource}}}
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 2 (count-statements model))
          "Model should contain 2 statements")

      (let [stmt (get-statement model
                                "http://example.org/catalog"
                                "http://www.w3.org/ns/dcat#dataset")
            object (.getObject stmt)]
        (is (not (nil? stmt)) "Statement should exist")
        (is (.isResource object) "Object should be a resource")
        (is (= "http://example.org/existing"
               (.toString (.asResource object)))
            "Should reference the existing resource")))))

(deftest test-add-resource-with-blank-subject
  (testing "Add resource without URI (blank node as subject)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource {:dct/title (model/literal "Blank Node Title")})
          result-resource (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain 1 statement")

      ;; The resource should be a blank node
      (is (.isAnon result-resource) "Result should be a blank node")

      ;; Query the model to find the statement (can't use getProperty on detached resource)
      (let [iter (.listStatements model result-resource nil nil)
            stmt (.next iter)]
        (is (not (nil? stmt)) "Blank node should have a statement")
        (is (= "http://purl.org/dc/terms/title" (.toString (.getPredicate stmt)))
            "Statement should have dct:title predicate")
        (is (= "Blank Node Title" (.getString (.getObject stmt)))
            "Title value should match")))))

(deftest test-add-resource-with-relationship-subject
  (testing "Add resource using :subject instead of :uri (for relationships)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data {:subject "http://example.org/relationship"
                         :properties {:dct/relation {:type :uri :value "http://example.org/target"}}}
          _ (serialization/add-resource-to-model! model resource-data)]

      (is (= 1 (count-statements model))
          "Model should contain 1 statement")

      (let [stmt (get-statement model
                                "http://example.org/relationship"
                                "http://purl.org/dc/terms/relation")]
        (is (not (nil? stmt)) "Statement should exist")
        (is (= "http://example.org/target"
               (.toString (.asResource (.getObject stmt))))
            "Relation target should match")))))

(deftest test-add-resource-three-arity-version
  (testing "Three-arity version: add properties to existing Jena resource"
    (let [model (ModelFactory/createDefaultModel)
          resource (ResourceFactory/createResource "http://example.org/dataset")
          properties {:dct/title (model/literal "Dataset Title")
                      :dct/description (model/lang-literal "Description" "en")}
          result (serialization/add-resource-to-model! model resource properties)]

      (is (= 2 (count-statements model))
          "Model should contain 2 statements")

      (is (= resource result) "Should return the same resource object")

      ;; Query the model instead of calling getProperty on resource
      (let [title-stmt (get-statement model
                                      "http://example.org/dataset"
                                      "http://purl.org/dc/terms/title")
            desc-stmt (get-statement model
                                     "http://example.org/dataset"
                                     "http://purl.org/dc/terms/description")]
        (is (not (nil? title-stmt)) "Should have title statement")
        (is (= "Dataset Title" (.getString (.getObject title-stmt)))
            "Title should match")
        (is (not (nil? desc-stmt)) "Should have description statement")
        (is (= "Description" (.getString (.getObject desc-stmt)))
            "Description should match")
        (is (= "en" (.getLanguage (.asLiteral (.getObject desc-stmt))))
            "Language should match")))))

(deftest test-add-resource-filters-nil-values
  (testing "Add resource filters out nil values (values without :value key)"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/resource"
                                        {:dct/title (model/literal "Valid Title")
                                         :dct/description (model/literal nil)
                                         :dct/identifier nil})
          _ (serialization/add-resource-to-model! model resource-data)]

      ;; Should only add the statement with non-nil value
      (is (= 1 (count-statements model))
          "Model should contain only 1 statement (nil values filtered)")

      (let [stmt (get-statement model
                                "http://example.org/resource"
                                "http://purl.org/dc/terms/title")]
        (is (not (nil? stmt)) "Title statement should exist")
        (is (= "Valid Title" (.getString (.getObject stmt)))
            "Title value should match")))))

(deftest test-complex-nested-structure
  (testing "Complex nested structure with multiple levels"
    (let [model (ModelFactory/createDefaultModel)
          resource-data (model/resource "http://example.org/catalog"
                                        {:rdf/type (model/uri :dcat/Catalog)
                                         :dct/title (model/literal "Test Catalog")
                                         :dcat/dataset (model/resource "http://example.org/dataset1"
                                                                       {:rdf/type (model/uri :dcat/Dataset)
                                                                        :dct/title [(model/lang-literal "Dataset FI" "fi")
                                                                                    (model/lang-literal "Dataset EN" "en")]
                                                                        :dcat/distribution (model/resource
                                                                                            {:dcat/accessURL (model/uri "http://example.org/access")})})})
          _ (serialization/add-resource-to-model! model resource-data)]

      ;; Count: 2 (catalog) + 1 (dataset link) + 3 (dataset) + 2 (distribution link + property) = 8
      (is (= 8 (count-statements model))
          "Model should contain all statements in the hierarchy")

      ;; Verify catalog
      (let [catalog-type (get-statement model
                                        "http://example.org/catalog"
                                        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type")]
        (is (not (nil? catalog-type)) "Catalog should have type"))

      ;; Verify dataset
      (let [dataset-type (get-statement model
                                        "http://example.org/dataset1"
                                        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type")]
        (is (not (nil? dataset-type)) "Dataset should have type")

        ;; Verify multi-language titles
        (let [iter (.listStatements model
                                    (ResourceFactory/createResource "http://example.org/dataset1")
                                    (serialization/property :dct/title)
                                    nil)
              titles (loop [ts []]
                       (if (.hasNext iter)
                         (let [stmt (.next iter)
                               literal (.asLiteral (.getObject stmt))]
                           (recur (conj ts {:value (.getString literal)
                                            :lang (.getLanguage literal)})))
                         ts))]
          (is (= 2 (count titles)) "Should have 2 language variants")
          (is (some #(and (= (:value %) "Dataset FI") (= (:lang %) "fi")) titles)
              "Should have Finnish title")
          (is (some #(and (= (:value %) "Dataset EN") (= (:lang %) "en")) titles)
              "Should have English title"))))))

(deftest test-three-level-nested-anonymous-resources
  (testing "Three-level nesting: named resource with two levels of anonymous nested resources"
    (let [model (ModelFactory/createDefaultModel)
          ;; Create a structure like:
          ;; <http://example.org/service> dcat:distribution [ 
          ;;   dcat:accessURL "..." ;
          ;;   dct:license [ 
          ;;     dct:identifier <...> ;
          ;;     dct:title "License title"
          ;;   ]
          ;; ]
          resource-data (model/resource "http://example.org/service"
                                        {:dct/title (model/literal "Main Service")
                                         :dcat/distribution (model/resource
                                                             {:dcat/accessURL (model/uri "http://example.org/api")
                                                              :dct/format (model/uri "http://example.org/format/json")
                                                              :dct/license (model/resource
                                                                            {:dct/identifier (model/uri "http://example.org/license/cc-by-4.0")
                                                                             :dct/title (model/literal "Creative Commons BY 4.0")})})})
          _ (serialization/add-resource-to-model! model resource-data)]

      ;; Count statements:
      ;; 1. service -> dct:title
      ;; 2. service -> dcat:distribution
      ;; 3. distribution (blank) -> dcat:accessURL
      ;; 4. distribution (blank) -> dct:format
      ;; 5. distribution (blank) -> dct:license
      ;; 6. license (blank) -> dct:identifier
      ;; 7. license (blank) -> dct:title
      (is (= 7 (count-statements model))
          "Model should contain 7 statements across 3 nesting levels")

      ;; Verify the main service resource
      (let [service-title-stmt (get-statement model
                                              "http://example.org/service"
                                              "http://purl.org/dc/terms/title")
            service-dist-stmt (get-statement model
                                             "http://example.org/service"
                                             "http://www.w3.org/ns/dcat#distribution")]
        (is (not (nil? service-title-stmt)) "Service should have title")
        (is (= "Main Service" (.getString (.getObject service-title-stmt)))
            "Service title should match")
        
        (is (not (nil? service-dist-stmt)) "Service should have distribution")
        
        ;; Get the distribution blank node
        (let [distribution-node (.asResource (.getObject service-dist-stmt))]
          (is (.isAnon distribution-node) "Distribution should be a blank node")
          
          ;; Check distribution properties
          (let [dist-stmts (.listStatements model distribution-node nil nil)
                dist-props (loop [ps []]
                             (if (.hasNext dist-stmts)
                               (let [s (.next dist-stmts)]
                                 (recur (conj ps {:predicate (.toString (.getPredicate s))
                                                  :object (.getObject s)})))
                               ps))]
            (is (= 3 (count dist-props)) "Distribution should have 3 properties")
            
            ;; Find the license property
            (let [license-prop (first (filter #(= (:predicate %) "http://purl.org/dc/terms/license")
                                              dist-props))
                  license-node (.asResource (:object license-prop))]
              (is (not (nil? license-prop)) "Distribution should have license")
              (is (.isAnon license-node) "License should be a blank node (2nd level nesting)")
              
              ;; Check license properties (3rd level)
              (let [license-stmts (.listStatements model license-node nil nil)
                    license-props (loop [ps []]
                                    (if (.hasNext license-stmts)
                                      (let [s (.next license-stmts)]
                                        (recur (conj ps {:predicate (.toString (.getPredicate s))
                                                         :object (.toString (.getObject s))})))
                                      ps))]
                (is (= 2 (count license-props)) "License should have 2 properties")
                (is (some #(and (= (:predicate %) "http://purl.org/dc/terms/identifier")
                                (.contains (:object %) "cc-by-4.0")) license-props)
                    "License should have identifier")
                (is (some #(and (= (:predicate %) "http://purl.org/dc/terms/title")
                                (.contains (:object %) "Creative Commons BY 4.0")) license-props)
                    "License should have title")))))))))
