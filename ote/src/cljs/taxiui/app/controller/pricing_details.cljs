(ns taxiui.app.controller.pricing-details
  (:require [taxiui.app.controller.loader :as loader]
            [taxiui.app.routes :as routes]
            [tuck.core :as tuck]
            [clojure.set :as set]
            [clojure.string :as str]
            [ote.communication :as comm]))

(defn- store-in
  "Small helper to ensure all values are updated within the same root"
  [app path value]
  (assoc-in app (concat [:taxi-ui :pricing-details] path) value))

(defn- clear
  [app path]
  (assoc-in app (concat [:taxi-ui :pricing-details] path) nil))

(tuck/define-event SearchResponse [results]
  {}
  (store-in app [:search :results] results))

(tuck/define-event Search [term]
  {}
  (when-not (str/blank? term) ;; only on filled input
    (comm/get! (str "place-completions/" term) {:on-success (tuck/send-async! ->SearchResponse)}))
                   app)

(tuck/define-event ClearSearch []
  {}
  (clear app [:search]))

(defn- store-prices
  [app prices]
  (reduce
    (fn [app [identifier price]]
      (store-in app [:price-information :prices identifier] price))
    app
    (select-keys prices [:start-price-daytime
                         :start-price-nighttime
                         :start-price-weekend
                         :price-per-minute
                         :price-per-kilometer
                         :accessibility-service-stairs
                         :accessibility-service-stretchers
                         :accessibility-service-fare])))

(defn- store-operating-areas
  [app operating-areas]
  (store-in app [:price-information :operating-areas] (vec operating-areas)))

(tuck/define-event LoadPriceInformationResponse [response]
  {}
  (tuck/fx
    (-> app
        (clear [:price-information])
        (store-prices (:prices response))
        (store-operating-areas (:operating-areas response)))
    (fn [e!]
      (e! (loader/->RemoveHit :loading-price-information)))))


(tuck/define-event LoadPriceInformationFailed [response]
  {}
  (tuck/fx
    app
    (fn [e!]
      (e! (loader/->RemoveHit :loading-price-information)))))

(tuck/define-event LoadPriceInformation []
  {}
  (let [{:keys [operator-id service-id]} (:params app)]
    (comm/post! "taxiui/price-info"
                {:operator-id operator-id
                 :service-id  service-id}
                {:on-success (tuck/send-async! ->LoadPriceInformationResponse)
                 :on-failure (tuck/send-async! ->LoadPriceInformationFailed)})
    (tuck/fx
      app
      (fn [e!]
        (e! (loader/->AddHit :loading-price-information))))))

(tuck/define-event UserSelectedResult [result]
  {}
  (store-in app [:search :selected] result))

(tuck/define-event AddOperatingArea [selected]
  {}
  (update-in app [:taxi-ui :pricing-details :price-information :operating-areas] conj selected))

(tuck/define-event RemoveOperatingArea [selected]
  {}
  (update-in
    app
    [:taxi-ui :pricing-details :price-information :operating-areas]
    (fn [areas removable]
      (letfn [(key-matches [k m1 m2]
                (and (some? (k m1))
                     (= (k m1)
                        (k m2))))]
        (filter
          (fn [stored]
            (not (or (key-matches :ote.db.transport-service/id stored removable)
                (key-matches :ote.db.places/id stored removable))))
          areas)))
      selected))

(tuck/define-event StorePrice [id value]
  {}
  (store-in app [:price-information :prices id] (-> (str value) (str/replace "," "."))))


(tuck/define-event SavePriceInformationResponse [response]
  {}
  (tuck/fx
    app
    (fn [e!]
      (e! (->LoadPriceInformation)))))

(tuck/define-event SavePriceInformationFailed [response]
  {}
  app)

(tuck/define-event SavePriceInformation [price-info]
  {}
  (let [{:keys [operator-id service-id]} (:params app)]
    (comm/post! (str "taxiui/price-info/" operator-id "/" service-id)
                price-info
                {:on-success (tuck/send-async! ->SavePriceInformationResponse)
                 :on-failure (tuck/send-async! ->SavePriceInformationFailed)})
    app))

(defmethod routes/on-navigate-event :taxi-ui/pricing-details [{params :params}]
  [(loader/->RemoveHit :page-loading)
   (->ClearSearch)
   (->LoadPriceInformation)])