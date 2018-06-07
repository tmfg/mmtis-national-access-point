(ns ote.integration.export.geojson
  "Integration service that serves GeoJSON documents for published
  transport services."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [GET]]
            [specql.core :as specql]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [jeesql.core :refer [defqueries]]
            [cheshire.core :as cheshire]
            [ote.db.modification :as modification]
            [clojure.set :as set]
            [ote.integration.export.transform :as transform]

            ;; Require time which extends PGInterval JSON generation
            [ote.time]))

(defqueries "ote/integration/export/geojson.sql")

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

(defn- json-response [data]
  {:status 200
   :headers {"Content-Type" "application/vnd.geo+json"}
   :body data})

(defn- feature-collection [geometry properties]
  {:type "FeatureCollection"
   :features [{:type "Feature"
               :properties properties
               :geometry geometry}]})

(def ^{:doc "Transport operator columns to set as properties in GeoJSON export"}
  transport-operator-properties-columns
  #{::t-operator/name ::t-operator/business-id
    ::t-operator/phone ::t-operator/homepage ::t-operator/email})

(def ^{:doc "Transport service columns to set as properties in GeoJSON export"}
  transport-service-properties-columns
  (set/difference (conj (specql/columns ::t-service/transport-service)
                        ;; Fetch linked external interfaces
                        [::t-service/external-interfaces (disj (specql/columns ::t-service/external-interface-description)
                                                               ::t-service/id
                                                               ::t-service/transport-service-id)])
                  modification/modification-field-keys
                  #{::t-service/notice-external-interfaces?
                    ::t-service/published?
                    ::t-service/company-csv-filename
                    ::t-service/company-source}))

(defn- link-to-companies-csv-url
  "Brokerage services could have lots of companies providing the service. With this function
  we remove company data from the geojson data set and replace it with link where companies data could be loaded."
  [service]
  (cond
    (not (empty? (::t-service/companies service)))
      (-> service
          (assoc :csv-url (str "/export-company-csv/" (::t-service/id service)))
          (dissoc ::t-service/companies))
    (not (nil? (::t-service/companies-csv-url service)))
      (assoc service :csv-url (::t-service/companies-csv-url service))
    :else service))

(defn- styled-operation-area [areas]
  {:type "GeometryCollection"
   :geometries (mapv
                (fn [{:keys [geojson primary?]}]
                  (assoc (cheshire/decode geojson keyword)
                         :style {:fill (if primary? "green" "orange")}))
                areas)})

(defn- export-geojson [db transport-operator-id transport-service-id]
  (let [areas (fetch-operation-area-for-service db {:transport-service-id transport-service-id})
        operator (first (specql/fetch db ::t-operator/transport-operator
                                      transport-operator-properties-columns
                                      {::t-operator/id transport-operator-id}))
        service (first (specql/fetch db ::t-service/transport-service
                                     transport-service-properties-columns
                                     {::t-service/transport-operator-id transport-operator-id
                                      ::t-service/id transport-service-id
                                      ::t-service/published? true}))
        service (link-to-companies-csv-url service)]

    (if (and (seq areas) operator service)
      (-> areas
          styled-operation-area
          (feature-collection (transform/transform-deep
                               {:transport-operator operator
                                :transport-service service}))
          (cheshire/encode {:key-fn name})
          json-response)
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
