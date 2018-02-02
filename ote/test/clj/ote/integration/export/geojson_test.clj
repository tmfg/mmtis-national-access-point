(ns ote.integration.export.geojson-test
  (:require [ote.integration.export.geojson :as geojson]
            [clojure.test :as t :refer [use-fixtures deftest is]]
            [ote.test :refer [system-fixture *ote* http-post http-get]]
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

(deftest export-geojson-with-interval
  (let [service (assoc (gen/generate (service-generators/service-type-generator :parking))
                       ::t-service/operation-area
                       [{::places/type "drawn"
                         ::places/namefin "Toriportti"
                         :geojson "{\"type\":\"Point\",\"coordinates\":[25.468116,65.012489]}"}])
        response (http-post "admin" "transport-service" service)
        id (get-in response [:transit ::t-service/id])]

    ;; Check that save is ok
    (is (pos? id))

    ;; Make it public so that GeoJSON export can return it
    (jdbc/execute! (:db *ote*) ["UPDATE \"transport-service\" SET \"published?\" = true where id = ?" id])

    ;; Fetch exported GeoJSON
    (let [geojson-url (str "export/geojson/" (::t-service/transport-operator-id service) "/" id)
          json-response (http-get "admin" geojson-url)]

      (is (= 200 (:status json-response)))

      ;; Check that maximum stay interval has the same fields in JSON and Clojure interval type
      (let [maximum-stay (get-in json-response [:json :features 0 :properties :transport-service
                                                :parking :maximum-stay])
            interval-fields (juxt :years :months :days :hours :minutes :seconds)]
        (is (= (interval-value maximum-stay)
               (interval-value (get-in service [::t-service/parking ::t-service/maximum-stay]))))))))
