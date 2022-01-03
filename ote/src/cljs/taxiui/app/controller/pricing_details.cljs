(ns taxiui.app.controller.pricing-details
  (:require [taxiui.app.routes :as routes]
            [tuck.core :as tuck]
            [clojure.string :as str]))

(def ^:private test-data [{:id 1 :label "Eka tulos"}
                          {:id 2 :label "toka tulos"}
                          {:id 3 :label "kolmas tulos"}
                          {:id 4 :label "Todella pitkä ja tärkeä tulos, jota voisi verrata vaikka Ilkka Remeksen varhaistuotantoon sekä Yhdysvaltain Perustuslakiin, kaikkine lisäosineen ja muine jatkannaisineen"}])

(tuck/define-event Search [term]
  {}
  (assoc-in app [:taxi-ui :search :results] (->> test-data (filter #(str/includes? (:label %) term)) shuffle)))

(tuck/define-event ClearSearch []
  {}
  (assoc-in app [:taxi-ui :search] nil))

(tuck/define-event LoadPriceInformation []
  {}
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
  (js/console.log (str "Store this stuff: " price-info)
                  app))

(defmethod routes/on-navigate-event :taxi-ui/pricing-details [{params :params}]
  [(->ClearSearch)
   (->LoadPriceInformation)])