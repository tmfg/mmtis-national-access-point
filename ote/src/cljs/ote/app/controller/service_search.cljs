(ns ote.app.controller.service-search
  "Service search controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [clojure.string :as str]))


(defrecord UpdateSearchFilters [filters])
(defrecord Search [])
(defrecord SearchResponse [results])
(defrecord InitServiceSearch [])
(defrecord FacetsResponse [facets])

(defn- search-params [{oa ::t-service/operation-area
                       text :text-search
                       st ::t-service/sub-type
                       :as filters}]
  (merge
   (when-not (empty? oa)
     {:operation_area (str/join "," (map :text oa))})
   (when text
     {:text text})
   (when-not (empty? st)
     {:sub_types (str/join "," (map (comp name :sub-type) st))})))

(defn- search [{service-search :service-search :as app}]
  (let [on-success (tuck/send-async! ->SearchResponse)]
    ;; Clear old timeout, if any
    (when-let [search-timeout (:search-timeout service-search)]
      (.clearTimeout js/window search-timeout))

    (assoc-in
     app
     [:service-search :search-timeout]
     (.setTimeout js/window
                  #(comm/get! "service-search"
                              {:params (search-params (:filters service-search))
                               :on-success on-success})
                  500))))

(extend-protocol tuck/Event

  InitServiceSearch
  (process-event [_ app]
    (comm/get! "service-search/facets"
               {:on-success (tuck/send-async! ->FacetsResponse)})
    app)

  FacetsResponse
  (process-event [{facets :facets} app]
    (update app :service-search assoc
            :facets facets
            :filters {::t-service/operation-area []
                      ::t-service/sub-type []}))

  UpdateSearchFilters
  (process-event [{filters :filters} app]
    (-> app
        (update-in [:service-search :filters] merge filters)
        search))

  SearchResponse
  (process-event [{results :results} app]
    (.log js/console "RESULTS: " (pr-str results))
    (assoc-in app [:service-search :results] results)))
