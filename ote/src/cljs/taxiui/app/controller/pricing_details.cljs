(ns taxiui.app.controller.pricing-details
  (:require [taxiui.app.routes :as routes]
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

(tuck/define-event LoadPriceInformationResponse [response]
  {}
  (js/console.log (str "LoadPriceInformationResponse Got response: " response))
                   ; [{:price 1, :timestamp #inst "2022-01-10T11:37:43.034-00:00", :identifier "start_price_daytime"} {:price 3, :timestamp #inst "2022-01-10T11:37:43.034-00:00", :identifier "start_price_nighttime"} {:price 2, :timestamp #inst "2022-01-10T11:37:43.034-00:00", :identifier "start_price_weekend"} {:price 5, :timestamp #inst "2022-01-10T11:37:43.034-00:00", :identifier "price_per_minute"} {:price 4, :timestamp #inst "2022-01-10T11:37:43.034-00:00", :identifier "price_per_kilometer"}]
  (reduce
    (fn [app {:keys [price identifier]}]
      (store-in app [:price-information :prices (-> (str/replace identifier "_" "-") keyword)] price))
    (clear app [:price-information])
    response))


(tuck/define-event LoadPriceInformationFailed [response]
  {}
  (js/console.log (str "LoadPriceInformationFailed to get response: " response))
  app)

(tuck/define-event LoadPriceInformation []
  {}
  (js/console.log (str "Loading price information..."))
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
  (store-in app [:price-information :prices id] value))


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
  [(->ClearSearch)
   (->LoadPriceInformation)])