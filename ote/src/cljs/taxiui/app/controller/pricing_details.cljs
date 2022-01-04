(ns taxiui.app.controller.pricing-details
  (:require [taxiui.app.routes :as routes]
            [tuck.core :as tuck]
            [clojure.set :as set]
            [clojure.string :as str]
            [ote.communication :as comm]))

(tuck/define-event SearchResponse [results]
  {}
  (->> results
       (map #(-> %
                 (set/rename-keys {:ote.db.places/id      :id
                                   :ote.db.places/namefin :label})
                 (select-keys [:id :label])))
       (assoc-in app [:taxi-ui :search :results])))

(tuck/define-event Search [term]
  {}
  (when-not (str/blank? term) ;; only on filled input
    (comm/get! (str "place-completions/" term) {:on-success (tuck/send-async! ->SearchResponse)}))
                   app)

(tuck/define-event ClearSearch []
  {}
  (assoc-in app [:taxi-ui :search] nil))

(tuck/define-event LoadPriceInformation []
  {}
                   (comm/post! "jokujoku/jotain/joo" (get-in app [:taxi-ui :price-information])
                               {:on-success (tuck/send-async! ->TransportOperatorDataResponse)
                                :on-failure (tuck/send-async! ->TransportOperatorDataFailed)})
  ; TODO: Right now this just resets the app state, should preload pricing info instead if any available
  (assoc-in app [:taxi-ui :price-information] nil))

(tuck/define-event UserSelectedResult [result]
  {}
  (assoc-in app [:taxi-ui :search :selected] result))

(tuck/define-event AddAreaOfOperation [selected]
  {}
  (update-in app [:taxi-ui :price-information :areas-of-operation] conj selected))

(tuck/define-event StorePrice [id value]
  {}
  (assoc-in app [:taxi-ui :price-information id] value))

(tuck/define-event SavePriceInformation [price-info]
  {}
  (js/console.log (str "Store this stuff: " price-info))
  app)

(defmethod routes/on-navigate-event :taxi-ui/pricing-details [{params :params}]
  [(->ClearSearch)
   (->LoadPriceInformation)])