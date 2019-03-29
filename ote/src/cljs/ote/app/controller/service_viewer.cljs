(ns ote.app.controller.service-viewer
  (:require [clojure.string :as string]
            [clojure.walk :refer [postwalk]]
            [clojure.set :refer [difference]]
            [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.localization :as localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.util.url :as url-util]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.routes :as routes]
            [ote.app.controller.place-search :as place-search]))

(defn format-descriptions
  [data]
  (reduce
    (fn [new-collection item]
      (let [lang (keyword (string/lower-case (::t-service/lang item)))
            val (::t-service/text item)]
        (assoc new-collection lang val)))
    {}
    data))

(define-event ServiceSuccess [result]
              {}
              (let [areas (::t-service/operation-area result)
                    area-descriptions (map
                                        (fn [area]
                                          (::t-service/description area))
                                        areas)
                    areas (reduce
                            (fn [new-coll area]
                              (let [formatted-area {:name (::t-service/text (first (::t-service/description area)))
                                                    :id (::t-service/id area)
                                                    :lang (::t-service/lang (first (::t-service/description area)))}]

                                (if (::t-service/primary? area)
                                  (assoc new-coll :primary (conj (:primary new-coll) formatted-area))
                                  (assoc new-coll :secondary (conj (:secondary new-coll) formatted-area)))))
                            {}
                            areas)
                    accessibility-data (let [pt (::t-service/passenger-transportation result)
                                             guaranteed-vehicle-acc (set (::t-service/guaranteed-vehicle-accessibility pt))
                                             limited-vehicle-acc (set (::t-service/limited-vehicle-accessibility pt))
                                             guaranteed-info-acc (set (::t-service/guaranteed-info-service-accessibility pt))
                                             limited-info-acc (set (::t-service/limited-info-service-accessibility pt))
                                             guaranteed-transportable-aid (set (::t-service/guaranteed-transportable-aid pt))
                                             limited-transportable-aid (set (::t-service/limited-transportable-aid pt))
                                             guaraunteed-desc (::t-service/guaranteed-accessibility-description pt)
                                             limited-desc (::t-service/limited-accessibility-description pt)
                                             url (::t-service/accessibility-info-url pt)]
                                         {:accessibility-infos {::t-service/vehicle-accessibility {:guaranteed guaranteed-vehicle-acc
                                                                                                   :limited limited-vehicle-acc}
                                                                ::t-service/information-service-accessibility {:guaranteed guaranteed-info-acc
                                                                                                               :limited limited-info-acc}
                                                                ::t-service/transportable-aid {:guaranteed guaranteed-transportable-aid
                                                                                               :limited limited-transportable-aid}}
                                          :descriptions {:guaranteed guaraunteed-desc
                                                         :limited limited-desc}
                                          :url url})
                    pricing-data (let [pt (::t-service/passenger-transportation result)
                                       payment-method-desc (::t-service/payment-method-description pt)
                                       payment-methods (::t-service/payment-methods pt)
                                       price-classes (::t-service/price-classes pt)
                                       pricing (::t-service/pricing pt)]
                                   {:payment-method-description payment-method-desc
                                    :payment-methods payment-methods
                                    :price-classes price-classes
                                    :pricing pricing})
                    service-hours-data (let [pt (::t-service/passenger-transportation result)
                                             service-hours (::t-service/service-hours pt)
                                             service-hours-info (::t-service/service-hours-info pt)
                                             service-exceptions  (::t-service/service-exceptions pt)]
                                         {:service-hours service-hours
                                          :service-hours-info service-hours-info
                                          :exceptions service-exceptions})]
                (-> app
                    (assoc-in [:service-view :transport-service] result)
                    (assoc-in [:service-view :transport-service :accessibility] accessibility-data)
                    (assoc-in [:service-view :transport-service :pricing-info] pricing-data)
                    (assoc-in [:service-view :transport-service :service-hours-info] service-hours-data)
                    (assoc-in [:service-view :transport-service :areas] areas))))

(define-event OperatorSuccess [result]
              {}
              (assoc-in app [:service-view :transport-operator] result))

(define-event FetchServiceData [operator-id service-id]
              {}
              (do
                (comm/get! (str "transport-service/" (url-util/encode-url-component service-id))
                           {:on-success (tuck/send-async! ->ServiceSuccess)})
                (comm/get! (str "t-operator/" (url-util/encode-url-component operator-id))
                           {:on-success (tuck/send-async! ->OperatorSuccess)}))
              app)

(defmethod routes/on-navigate-event :service-view [{params :params}]
  (when params
    (->FetchServiceData (:transport-operator-id params) (:transport-service-id params))))
