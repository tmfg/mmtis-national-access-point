(ns ote.services.taxiui-service
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [jeesql.core :refer [defqueries]]
            [ote.components.service :refer [define-service-component]]
            [ote.components.http :as http]
            [ote.authorization :as authorization]
            [taoensso.timbre :as log]
            [ote.db.tx :as tx]
            [clojure.string :as str]))

(defqueries "ote/services/taxiui_service.sql")

(defn fetch-priceinfo-for-service
  [db user {service-id  :service-id
            operator-id :operator-id :as form-data}]
  (log/info (str "fetch-priceinfo-for-service " form-data))
  (authorization/with-transport-operator-check
    db user operator-id
    (fn []
      (vec (select-price-information db {:service-id (Integer/parseInt service-id)})))))

(defn update-priceinfo-for-service
  [db user operator-id service-id price-info]
  (log/info (str "update-priceinfo-for-service " operator-id " / " service-id " / " price-info))
  (authorization/with-transport-operator-check
    db user operator-id
    (fn []
      (tx/with-transaction
        db
        (let [{:keys [prices areas-of-operation]} price-info]
          (log/info "got prices " prices)
          (doseq [[id price] prices]
            (log/info "Store price " id " / " price)
            (insert-price-information! db {:service-id (Integer/parseInt service-id)
                                           :identifier (str/replace (name id) "-" "_")
                                           :price      (new BigDecimal price)}))
          (when areas-of-operation
            ; TODO
            (log/info "Update areas with " areas-of-operation))


                           )))))

(defrecord TaxiUIService []
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
                (http/publish!
                  http
                  (routes
                    (POST "/taxiui/price-info" {user      :user
                                               form-data :body}
                      (log/info "form data " form-data)
                      (http/transit-response
                        (fetch-priceinfo-for-service db user (http/transit-request form-data))))

                    (POST "/taxiui/price-info/:operator-id/:service-id" {{:keys [operator-id service-id]} :params
                                                                         user                             :user
                                                                         form-data                        :body}
                         (http/transit-response
                           (update-priceinfo-for-service db user operator-id service-id (http/transit-request form-data))))))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))