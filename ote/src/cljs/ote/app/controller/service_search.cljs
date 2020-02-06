(ns ote.app.controller.service-search
  "Service search controller"
  (:require [clojure.string :as str]
            [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.util.url :as url-util]
            [ote.util.fn :refer [flip]]
            [ote.app.routes :as routes]))


(defrecord UpdateSearchFilters [filters])
(defrecord SearchResponse [response params append?])
(defrecord InitServiceSearch [])
(defrecord FacetsResponse [facets])
(defrecord FetchMore [])

(defrecord FetchServiceGeoJSON [url])
(defrecord GeoJSONFetched [response])
(defrecord SetOperatorName [name])
(defrecord OperatorCompletionsResponse [completions name])
(defrecord AddOperator [business-id operator])
(defrecord RemoveOperatorById [id])

(defrecord SaveScrollPosition [])
(defrecord RestoreScrollPosition [])

(defrecord OperationAreaFilterChanged [input])
(defrecord PlacesSearchCompletionsResponse [completions])

(defmethod routes/on-navigate-event :service [{{:keys [transport-operator-id transport-service-id]} :params}]
  (->FetchServiceGeoJSON (str js/document.location.protocol "//" js/document.location.host
                              "/export/geojson/" transport-operator-id "/" transport-service-id)))

(defmethod routes/on-navigate-event :services [_]
  (->InitServiceSearch))

(defn- search-params [{operators :operators
                       oa ::t-service/operation-area
                       text :text-search
                       st ::t-service/sub-type
                       tt ::t-service/transport-type
                       dc ::t-service/data-content
                       limit :limit offset :offset
                       :as filters}]
  (merge
   (when-not (empty? oa)
     {:operation_area (str/join "," (map :value oa))})
   (when-not (empty? (get operators :chip-results))
     {:operators (str/join "," (map :business-id (get operators :chip-results)))})
   (when text
     {:text text})
   (when-not (empty? st)
     {:sub_types (str/join "," (map #(name %) st))})
   (when-not (empty? tt)
     {:transport_types (str/join "," (map #(name %) tt))})
   (when-not (empty? dc)
     {:data_content (str/join "," (map #(name %) dc))})
   (when (and limit offset)
     {:limit limit
      :offset offset})))

(defn- search
  ([app append?] (search app append? 500))
  ([{service-search :service-search :as app} append? timeout-ms]
   (let [params (search-params (:filters service-search))
         on-success (tuck/send-async! ->SearchResponse params append?)]
     ;; Clear old timeout, if any
     (when-let [search-timeout (:search-timeout service-search)]
       (.clearTimeout js/window search-timeout))

     (-> app
         (assoc-in [:service-search :params] params)
         (assoc-in [:service-search :search-timeout]
                   (.setTimeout js/window
                                #(comm/get! "service-search"
                                            {:params     params
                                             :on-success on-success})
                                timeout-ms))))))

(def page-size 25)


(defn add-operator-to-chip-list
  "Return app with a new operator added to chip list."
  ([app operator]
   (add-operator-to-chip-list app operator false))
  ([app operator clear?]
    (update-in app [:service-search :filters :operators :chip-results]
               #(conj (if clear? [] (or % []))
                      operator))))


(defn- operator-in-list?
  "Return nil if operator is not in given list"
  [operator list]
    (some #(= (:business-id operator) (:business-id %)) list))

(extend-protocol tuck/Event

  InitServiceSearch
  (process-event [_ app]
    (if (seq (get-in app [:service-search :results]))
      ;; We have already been in service search and coming back.
      ;; Don't fetch things again as we may be returning back from
      ;; a service page.
      app

      ;; Otherwise fetch facets and do an initial search
      (do
        (comm/get! "service-search/facets"
                   {:on-success (tuck/send-async! ->FacetsResponse)})
        ;; Immediately do a search without any parameters
        (search (update-in app [:service-search :filters] merge
                           {:limit page-size :offset 0})
                false 0))))

  FetchMore
  (process-event [_ app]
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
            :filters (merge (get-in app [:service-search :filters])
                            {::t-service/sub-type []})))

  UpdateSearchFilters
  (process-event [{filters :filters} app]
    (-> app
        (update-in [:service-search :filters] merge
                   (assoc filters
                          :limit page-size
                          :offset 0))
        (search false)))

  SearchResponse
  (process-event [{response :response params :params append? :append?} app]
    (if (not= params (get-in app [:service-search :params]))
      ;; If the response event has different search parameters than the
      ;; latest sent fetch, it is a stale response, do nothing
      app
      (let [{:keys [empty-filters? results total-service-count
                    total-company-count filter-service-count]} response]
        (update app :service-search
                #(assoc %
                        :results (if append?
                                   (into (get-in app [:service-search :results]) results)
                                   (vec results))
                        :empty-filters? empty-filters?
                        :total-service-count (or total-service-count (:total-service-count %))
                        :total-company-count (or total-company-count (:total-company-count %))
                        :fetching-more? false
                        :filter-service-count filter-service-count)))))

  FetchServiceGeoJSON
  (process-event [{:keys [url]} app]
    (comm/get! "viewer" {:params {:url url}
                         :on-success (tuck/send-async! ->GeoJSONFetched)
                         :response-format :json})
    (update app :service-search assoc
            :resource nil
            :geojson nil
            :loading-geojson? true))

  GeoJSONFetched
  (process-event [{response :response} app]
    (update app :service-search assoc
            :resource response
            :geojson (clj->js response)
            :loading-geojson? false))

  SetOperatorName
  (process-event [{name :name} app]
    (when (>= (count name) 2) ;; Search after two (2) chars is given
      (comm/get! (str "operator-completions/" (url-util/encode-url-component name))
                 {:on-success (tuck/send-async! ->OperatorCompletionsResponse name)}))
    (-> app
        (assoc-in [:service-search :filters :operators :name] name)
        (assoc-in [:service-search :filters :operators :results] [])))

  OperatorCompletionsResponse
  (process-event [result app]
    ;; Remove existing result from the list
    (let [clean-response (filter
                           #(not (operator-in-list? % (get-in app [:service-search :filters :operators :chip-results])))
                           (:completions result))]
      (assoc-in app [:service-search :filters :operators :results] clean-response)))

  AddOperator
  (process-event [{:keys [business-id operator]} app]
    (-> (if (some #(= business-id (:business-id (:operator %)))
                  (get-in app [:service-search :filters :operators :chip-results]))
          ;; This name has already been added, don't do it again
          app
          (add-operator-to-chip-list app {:business-id business-id
                                          :operator operator}))
        (assoc-in [:service-search :filters :operators :name] "")))

  RemoveOperatorById
  (process-event [{id :id} app]
    (update-in app [:service-search :filters :operators :chip-results]
               (fn [results]
                 (filterv
                   (fn [x] (not= id (get x :business-id)))
                   results))))

  SaveScrollPosition
  (process-event [_ app]
    (update app :service-search assoc :scroll-position js/window.scrollY))

  RestoreScrollPosition
  (process-event [_ app]
    (when-let [p (get-in app [:service-search :scroll-position])]
      (.scrollTo js/window 0 p))
    (update app :service-search dissoc :scroll-position))

  OperationAreaFilterChanged
  (process-event [{input :input} app]
    (when-not (str/blank? input) ;; only on filled input
      (comm/get! (str "place-completions/" input) {:on-success (tuck/send-async! ->PlacesSearchCompletionsResponse)}))
    app)

  PlacesSearchCompletionsResponse
  (process-event [{completions :completions} app]
    (let [available-query-terms (map :ote.db.places/namefin completions)]
      (assoc-in app [:service-search :operation-area-filter-completions] available-query-terms))))
