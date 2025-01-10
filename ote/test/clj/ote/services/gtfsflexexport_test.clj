(ns ote.services.gtfsflexexport-test
  (:require [clojure.test :as t :refer [deftest is testing]]
            [com.stuartsierra.component :as component]
            [clojure.test.check.generators :as gen]
            [ote.db.service-generators :as s-generators]
            [ote.services.service-search :as sut]
            [ote.test-tools :refer :all]
            [ote.test :refer :all]
            [ote.integration.export.gtfs-flex :as gtfs-flex-service]
            [ote.services.transport :as transport-service]))

(t/use-fixtures :each
                (system-fixture
                  :transport-service
                  (component/using (transport-service/->TransportService nil) [:http :db])

                  :service-search
                  (component/using (sut/->ServiceSearch (slurp "config.edn")) [:http :db])))

;; Ensure that static-routes are working
(deftest static-routes-test
  (let [route-id 1
        transport-operator-id 1
        operator-name "Secret Santa"
        service-name "Santa's Sleigh"
        light-rail (gtfs-flex-service/->static-routes route-id :light-rail transport-operator-id operator-name service-name)
        subway (gtfs-flex-service/->static-routes route-id :subway transport-operator-id operator-name service-name)
        rail (gtfs-flex-service/->static-routes route-id :rail transport-operator-id operator-name service-name)
        bus (gtfs-flex-service/->static-routes route-id :bus transport-operator-id operator-name service-name)
        ferry (gtfs-flex-service/->static-routes route-id :ferry transport-operator-id operator-name service-name)
        cable-car (gtfs-flex-service/->static-routes route-id :cable-car transport-operator-id operator-name service-name)
        gondola (gtfs-flex-service/->static-routes route-id :gondola transport-operator-id operator-name service-name)
        funicular (gtfs-flex-service/->static-routes route-id :funicular transport-operator-id operator-name service-name)
        trolleybus (gtfs-flex-service/->static-routes route-id :trolleybus transport-operator-id operator-name service-name)
        monorail (gtfs-flex-service/->static-routes route-id :monorail transport-operator-id operator-name service-name)
        taxi (gtfs-flex-service/->static-routes route-id :taxi transport-operator-id operator-name service-name)]
    (is (= (:gtfs/route-type light-rail) "0"))
    (is (= (:gtfs/route-type subway) "1"))
    (is (= (:gtfs/route-type rail) "2"))
    (is (= (:gtfs/route-type bus) "3"))
    (is (= (:gtfs/route-type ferry) "4"))
    (is (= (:gtfs/route-type cable-car) "5"))
    (is (= (:gtfs/route-type gondola) "6"))
    (is (= (:gtfs/route-type funicular) "7"))
    (is (= (:gtfs/route-type trolleybus) "11"))
    (is (= (:gtfs/route-type monorail) "12"))
    (is (= (:gtfs/route-type taxi) "1501"))))


(deftest export-gtfs-flex-test-status-ok
  (let [db (:db *ote*)
        config nil
        ;; Get the transport operator id
        transport-operator-id (:id (get-transport-opertor-by-name "Terminaali Oy"))
        ;; Generate transport-service
        transport-service (gen/generate (s-generators/service-type-generator :passenger-transportation))
        transport-service (-> transport-service
                              (dissoc :ote.db.transport-operator/id)
                              (assoc :ote.db.transport-service/transport-operator-id transport-operator-id))

        ;; Save generated transport-service to database
        response (http-post (:user-id-admin @ote.test/user-db-ids-atom)
                            "transport-service"
                            transport-service)

        transport-service-id (-> response :transit :ote.db.transport-service/id)
        r (gtfs-flex-service/export-gtfs-flex db config transport-operator-id transport-service-id )]
    (is (= (:status r) 200))))
