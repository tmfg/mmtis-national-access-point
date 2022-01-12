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
  (->> results
       (map #(-> %
                 (set/rename-keys {:ote.db.places/id      :id
                                   :ote.db.places/namefin :label})
                 (select-keys [:id :label])))
       (store-in app [:search :results])))

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
    (select-keys prices [:start-price-daytime :start-price-nighttime :start-price-weekend :price-per-minute :price-per-kilometer])))

(tuck/define-event LoadPriceInformationResponse [response]
  {}
  (-> app
      (clear [:price-information])
      (store-prices (:prices response))))


(tuck/define-event LoadPriceInformationFailed [response]
  {}
  ;TODO: something?
  app)

(tuck/define-event LoadPriceInformation []
  {}
  (let [{:keys [operator-id service-id]} (:params app)]
    (comm/post! "taxiui/price-info"
                {:operator-id operator-id
                 :service-id  service-id}
                {:on-success (tuck/send-async! ->LoadPriceInformationResponse)
                 :on-failure (tuck/send-async! ->LoadPriceInformationFailed)})
    ; TODO: could add loading indicator flash thingy here, now just returns app to keep things working
    app))

(tuck/define-event UserSelectedResult [result]
  {}
  (store-in app [:search :selected] result))

(tuck/define-event AddAreaOfOperation [selected]
  {}
  (update-in app [:taxi-ui :pricing-details :price-information :areas-of-operation] conj selected))

(tuck/define-event StorePrice [id value]
  {}
  (store-in app [:price-information :prices id] (-> (str value) (str/replace "," "."))))


(tuck/define-event SavePriceInformationResponse [response]
  {}
  (js/console.log (str "SavePriceInformationResponse Got response: " response))
  app)

(tuck/define-event SavePriceInformationFailed [response]
  {}
  (js/console.log (str "SavePriceInformationFailed to get response: " response))
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