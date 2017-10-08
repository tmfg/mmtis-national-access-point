(ns ote.integration.export.geojson
  "Integration service that serves GeoJSON documents for published
  transport services."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [GET]]
            [specql.core :as specql]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]))

(declare export-geojson)

(defrecord GeoJSONExport []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc this ::stop
           (http/publish!
            http {:authenticated? false}
            (GET "/export/geojson/:transport-operator-id{[0-9]+}/:transport-service-id{[0-9]+}"
                 [transport-operator-id transport-service-id]
                 (export-geojson db
                                 (Long/parseLong transport-operator-id)
                                 (Long/parseLong transport-service-id))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn- export-geojson [db transport-operator-id transport-service-id]
  (let [service (first (specql/fetch db ::t-service/transport-service
                                     (specql/columns ::t-service/transport-service)
                                     {::t-service/transport-operator-id transport-operator-id
                                      ::t-service/id transport-service-id}))]
    (if service
      (str service)
      {:status 404
       :body "GeoJSON for service not found."})))

(comment
  {:ote.db.transport-service/type :passenger-transportation,
   :ote.db.transport-service/transport-operator-id 36,
   :ote.db.transport-service/passenger-transportation
   {:ote.db.transport-service/accessibility-tool #{:wheelchair :walkingstick},
    :ote.db.transport-service/additional-services #{:child-seat},
    :ote.db.transport-service/price-classes
    [{:ote.db.transport-service/currency "EUR", :ote.db.transport-service/name "perusmaksu", :ote.db.transport-service/price-per-unit 7M, :ote.db.transport-service/unit "matkan alkaessa"}
     {:ote.db.transport-service/currency "EUR", :ote.db.transport-service/name "perustaksa", :ote.db.transport-service/price-per-unit 4.9M, :ote.db.transport-service/unit "km"}],
    :ote.db.transport-service/payment-methods #{:debit-card :cash :credit-card}
    :ote.db.transport-service/accessibility-description
    [{:ote.db.transport-service/lang "FI", :ote.db.transport-service/text "Joissain autoissa voidaan kuljettaa pyörätuoli, varmista tilattaessa."}]
    :ote.db.transport-service/luggage-restrictions
    [{:ote.db.transport-service/lang "FI", :ote.db.transport-service/text "ei saa liikaa olla laukkuja"}]}})
