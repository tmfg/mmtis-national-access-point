(ns ote.app.controller.operators
  "Controller for operator listing view"
  (:require [tuck.core :as tuck]
            [ote.db.transport-operator :as t-operator]
            [ote.communication :as comm]
            [ote.app.controller.service-search :as ss]
            [ote.app.routes :as routes]))

(defrecord Init [])
(defrecord UpdateOperatorFilter [filter])
(defrecord SearchResponse [response append?])
(defrecord FetchMore [])
(defrecord OpenOperatorModal [id])
(defrecord CloseOperatorModal [id])
(defrecord ShowOperatorServices [operator])

(def page-size 32)

(defn search [{operators :operators :as app} timeout-ms append?]
  (when-let [timeout (:timeout operators)]
    (.clearTimeout js/window timeout))
  (let [on-success (tuck/send-async! ->SearchResponse append?)]
    (update
     app :operators assoc
     :loading? true
     :timeout (.setTimeout js/window
                           #(comm/post! "operators/list"
                                        {:filter (:filter operators)
                                         :limit (:limit operators)
                                         :offset (:offset operators)}
                                        {:on-success on-success})
                           timeout-ms))))

(defn- update-operator-by-id [app id update-fn & args]
  (update-in app [:operators :results]
             (fn [results]
               (map #(if (= (::t-operator/id %) id)
                       (apply update-fn % args)
                       %)
                    results))))

(extend-protocol tuck/Event
  Init
  (process-event [_ app]
    (search (update app :operators assoc
                    :filter ""
                    :limit page-size
                    :offset 0)
            0 false))

  UpdateOperatorFilter
  (process-event [{filter :filter} app]
    (search (update app :operators assoc
                    :filter filter
                    :limit page-size
                    :offset 0)
            500 false))

  FetchMore
  (process-event [_ app]
    (search (update app :operators assoc
                    :limit page-size
                    :offset (count (get-in app [:operators :results])))
            0 true))

  SearchResponse
  (process-event [{response :response append? :append?} app]
    (let [results (:results response)
          total-count (:total-count response)]
      (update app :operators assoc
              :loading? false
              :results (if append?
                         (into (get-in app [:operators :results])
                               results)
                         (vec results))
              :total-count total-count)))
  OpenOperatorModal
  (process-event [{id :id} app]
    (update-operator-by-id
      app id
      assoc :show-modal? true))

  CloseOperatorModal
  (process-event [{id :id} app]
    (update-operator-by-id
      app id
      dissoc :show-modal?))

  ShowOperatorServices
  (process-event [{operator :operator} app]
    (let [filtered-operator {:operator (::t-operator/name operator)
                             :business-id (::t-operator/business-id operator)}
          app (assoc-in app [:service-search :filters :text-search] "")]
      (routes/navigate! :services)
    (ss/add-operator-to-chip-list app filtered-operator true))))
