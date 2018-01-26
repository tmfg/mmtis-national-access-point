(ns ote.app.controller.service-search
  "Service search controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [clojure.string :as str]))


(defrecord UpdateSearchFilters [filters])
(defrecord SearchResponse [response params append?])
(defrecord InitServiceSearch [])
(defrecord FacetsResponse [facets])
(defrecord FetchMore [])

(defrecord ShowServiceGeoJSON [url])
(defrecord CloseServiceGeoJSON [])
(defrecord GeoJSONFetched [response])
(defrecord SetOperatorName [name])
(defrecord OperatorCompletionsResponse [completions name])
(defrecord AddOperator [id])
(defrecord FetchOperatorResponse [response operator])

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
   (let [params (if-let [operator (get-in app [:params :operator])]
                  {:operators operator}
                  (search-params (:filters service-search)))
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


(defn- add-operator
  "Return app with a new operator added."
  [app operator]
  (update-in app [:operator-search :results]
             #(conj (or % [])
                    {:operator operator})))


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
    (.log js/console " Tätä kutsutaan kun data muuttuu? " (clj->js filters))
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
      (let [{:keys [empty-filters? results total-service-count filter-service-count]} response]
        (update app :service-search assoc
                :results (if append?
                           (into (get-in app [:service-search :results]) results)
                           (vec results))
                :empty-filters? empty-filters?
                :total-service-count total-service-count
                :fetching-more? false
                :filter-service-count filter-service-count))))

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
            :loading-geojson? false))

  SetOperatorName
  (process-event [{name :name} app]
    (.log js/console "Ssssssssssssssapp" (clj->js app))
    (let [app (assoc-in app [:operators :name] name)]
      (when (>= (count name) 2)
        (comm/get! (str "operator-completions/" name)
                   {:on-success (tuck/send-async! ->OperatorCompletionsResponse name)}))
      app))

  OperatorCompletionsResponse
  (process-event [result app]
    (.log js/console "OperatorCompletionsResponse " (pr-str result))
    (.log js/console "OperatorCompletionsResponse " (pr-str name))
    (.log js/console "OperatorCompletionsResponse app" (clj->js app))
    (assoc-in app [:operators :results] (:completions result))
    #_ (if-not (= name (get-in app [:service-search :operators :name]))
      ;; Received stale completions (name is not what was searched for), ignore
      app
      (assoc-in app [:service-search :operators :completions]
                (let [name-lower (str/lower-case name)]
                  (sort-by #(str/index-of (str/lower-case (::t-operator/name %))
                                          name-lower)
                           completions)))))

  AddOperator
  (process-event [{id :id} app]
    (.log js/console " AddOperator id " id)
    (if (some #(= id (::t-operator/id (:operator %)))
              (get-in app [:operator-search :results]))
      ;; This name has already been added, don't do it again
      app
      (if-let [operator (some #(when (= id (::t-operator/id %)) %)
                           (get-in app [:operator-search :completions]))]
        (do
          (comm/get! (str "transport-operator/" id)
                     {:on-success (tuck/send-async! ->FetchOperatorResponse operator)})
          (-> app
              (assoc-in [:operator-search :name] "")
              (assoc-in [:operator-search :completions] nil)))
        app)))

  FetchOperatorResponse
  (process-event [{:keys [operator]} app]
    (add-operator app operator))

  )
