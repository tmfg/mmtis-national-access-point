(ns ote.app.controller.service-search
  "Service search controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [clojure.string :as str]))


(defrecord UpdateSearchFilters [filters])
(defrecord SearchResponse [response append?])
(defrecord InitServiceSearch [])
(defrecord FacetsResponse [facets])
(defrecord FetchMore [])

(defrecord ShowServiceGeoJSON [url])
(defrecord CloseServiceGeoJSON [])
(defrecord GeoJSONFetched [response])

(defn- search-params [{oa ::t-service/operation-area
                       text :text-search
                       st ::t-service/sub-type
                       limit :limit offset :offset
                       :as filters}]
  (merge
   (when-not (empty? oa)
     {:operation_area (str/join "," (map :text oa))})
   (when text
     {:text text})
   (when-not (empty? st)
     {:sub_types (str/join "," (map (comp name :sub-type) st))})
   (when (and limit offset)
     {:limit limit
      :offset offset})))

(defn- search
  ([app append?] (search app append? 500))
  ([{service-search :service-search :as app} append? timeout-ms]
   (let [on-success (tuck/send-async! ->SearchResponse append?)]
     ;; Clear old timeout, if any
     (when-let [search-timeout (:search-timeout service-search)]
       (.clearTimeout js/window search-timeout))

     (assoc-in
      app
      [:service-search :search-timeout]
      (.setTimeout js/window
                   #(comm/get! "service-search"
                               {:params     (search-params (:filters service-search))
                                :on-success on-success})
                   timeout-ms)))))

(def page-size 25)

(extend-protocol tuck/Event

  InitServiceSearch
  (process-event [_ app]
    (comm/get! "service-search/facets"
               {:on-success (tuck/send-async! ->FacetsResponse)})
    ;; Immediately do a search without any parameters
    (search (update-in app [:service-search :filters] merge
                       {:limit page-size :offset 0})
            false 0))

  FetchMore
  (process-event [_ app]
    (.log js/console "HAETAAN LISÄÄ")
    (search
     (-> app
         (update :service-search assoc :fetching-more? true)
         (update-in [:service-search :filters]
                    (fn [{:keys [limit offset] :as filters}]
                      (assoc filters
                             :limit page-size
                             :offset (count (get-in app [:service-search :results]))))))
     true 0))

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
        (search false)))

  SearchResponse
  (process-event [{response :response append? :append?} app]
    (let [{:keys [empty-filters? results total-service-count]} response]
      (update app :service-search assoc
              :results (if append?
                         (into (get-in app [:service-search :results]) results)
                         (vec results))
              :empty-filters? empty-filters?
              :total-service-count total-service-count
              :fetching-more? false)))

  ShowServiceGeoJSON
  (process-event [{:keys [url]} app]
    (comm/get! "viewer" {:params {:url url}
                         :on-success (tuck/send-async! ->GeoJSONFetched)
                         :response-format :json})
    (update app :service-search assoc
            :resource nil
            :geojson nil
            :loading-geojson? true))

  CloseServiceGeoJSON
  (process-event [_ app]
    (update app :service-search dissoc :resource :geojson :loading-geojson?))

  GeoJSONFetched
  (process-event [{response :response} app]
    (update app :service-search assoc
            :resource response
            :geojson (clj->js response)
            :loading-geojson? false)))
