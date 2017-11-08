(ns ote.app.controller.service-search
  "Service search controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]))


(defrecord UpdateSearchFilters [filters])
(defrecord Search [])
(defrecord SearchResponse [results])
(defrecord InitServiceSearch [])
(defrecord FacetsResponse [facets])

(defn- search-params [filters]
  {:foo "fixme"})

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
                  100))))

(extend-protocol tuck/Event

  InitServiceSearch
  (process-event [_ app]
    (comm/get! "service-search/facets"
               {:on-success (tuck/send-async! ->FacetsResponse)})
    app
    #_(update app :service-search merge
            ;; FIXME: fetch from server
            {:facets
             {::t-service/operation-area
              [{:name "Oulu (5)" :value "Oulu"}
               {:name "Helsinki (12)" :value "Helsinki"}]
              ::t-service/type
              [{:name "Henkilöstön kuljetus (69)" :value :passenger-transportation}
               ]}
             }))

  FacetsResponse
  (process-event [{facets :facets} app]
    (assoc-in app [:service-search :facets] facets))

  UpdateSearchFilters
  (process-event [{filters :filters} app]
    (-> app
        (update-in [:service-search :filters] merge filters)
        search))

  SearchResponse
  (process-event [{results :results} app]
    (.log js/console "RESULTS: " (pr-str results))
    (assoc-in app [:service-search :results] results)))
