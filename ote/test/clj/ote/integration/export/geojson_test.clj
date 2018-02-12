(ns ote.integration.export.geojson-test
  (:require [ote.integration.export.geojson :as geojson]
            [clojure.test :as t :refer [use-fixtures deftest is testing]]
            [ote.test :refer [system-fixture *ote* http-post http-get sql-execute!]]
            [clojure.java.jdbc :as jdbc]
            [com.stuartsierra.component :as component]
            [ote.services.transport :as transport-service]
            [ote.db.service-generators :as service-generators]
            [clojure.test.check.generators :as gen]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]))

(use-fixtures :each
  (system-fixture
   :transport (component/using (transport-service/->Transport nil) [:http :db])
   :export-geojson (component/using (geojson/->GeoJSONExport) [:db :http])))

(defn interval-value [{:keys [years months days hours minutes seconds]}]
  [years months
   (+ seconds
      (* 60 minutes)
      (* 60 60 hours)
      (* 60 60 24 days))])

(defn- export-geojson [{::t-service/keys [id transport-operator-id]}]
  (is id "saved service has id")
  (is transport-operator-id "saved service has a transport-operator id")
  ;; Make it public so that GeoJSON export can return it
  (jdbc/execute! (:db *ote*) ["UPDATE \"transport-service\" SET \"published?\" = true where id = ?" id])
  (let [geojson-url (str "export/geojson/" transport-operator-id "/" id)
        _ (println "URL: " geojson-url)
        json-response (http-get "admin" geojson-url)]
    (is (= 200 (:status json-response)))
    (is (:json json-response))
    (:json json-response)))

(def test-operation-area [{::places/type "drawn"
                           ::places/namefin "Toriportti"
                           :geojson "{\"type\":\"Point\",\"coordinates\":[25.468116,65.012489]}"}])

(deftest export-geojson-with-interval
  (let [service (assoc (gen/generate (service-generators/service-type-generator :parking))
                       ::t-service/operation-area test-operation-area)
        response (http-post "admin" "transport-service" service)
        id (get-in response [:transit ::t-service/id])]

    ;; Check that save is ok
    (is (pos? id))

    ;; Fetch exported GeoJSON
    (let [geojson (export-geojson (:transit response))]
      ;; Check that maximum stay interval has the same fields in JSON and Clojure interval type
      (let [maximum-stay (get-in geojson [:features 0 :properties :transport-service
                                          :parking :maximum-stay])
            interval-fields (juxt :years :months :days :hours :minutes :seconds)]
        (is (= (interval-value maximum-stay)
               (interval-value (get-in service [::t-service/parking ::t-service/maximum-stay]))))))))

(def week-day-number {"MON" 0
                      "TUE" 1
                      "WED" 2
                      "THU" 3
                      "FRI" 4
                      "SAT" 5
                      "SUN" 6})

(deftest service-hours-export
  (testing "Service hours are exported properly and weekdays are sorted"
    (doseq [[type path] [[:passenger-transportation [::t-service/passenger-transportation ::t-service/service-hours]]
                         [:parking [::t-service/parking ::t-service/service-hours]]]
            :let [service (assoc (gen/generate (service-generators/service-type-generator type))
                                 ::t-service/operation-area test-operation-area)
                  saved-service (:transit (http-post "admin" "transport-service" service))
                  geojson (export-geojson saved-service)]]

      (let [generated-values (get-in service path)
            geojson-values (get-in geojson (into [:features 0 :properties :transport-service]
                                                 (map (comp keyword name) path)))]
        (is (= (count generated-values) (count geojson-values))
            "generated and exported has the same amount of service hours entries")
        (doall
         (map (fn [generated-value geojson-value]
                (is (= (into #{} (map name) (::t-service/week-days generated-value))
                       (into #{} (:week-days geojson-value)))
                    "generated and exported have the same week days")
                (is (apply < (map week-day-number (:week-days geojson-value)))
                    "weekdays are in order (monday first, sunday last)"))
              generated-values
              geojson-values))))))
