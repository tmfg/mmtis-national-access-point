(ns ote.app.controller.operators
  "Controller for operator listing view"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]))

(defrecord Init [])
(defrecord UpdateOperatorFilter [filter])
(defrecord SearchResponse [response append?])
(defrecord FetchMore [])

(def page-size 25)

(defn search [{operators :operators :as app} timeout-ms append?]
  (when-let [timeout (:timeout operators)]
    (.clearTimeout js/window timeout))
  (let [on-success (tuck/send-async! ->SearchResponse append?)]
    (assoc-in
     app [:operators :timeout]
     (.setTimeout js/window
                  #(comm/post! "operators/list"
                               {:filter (:filter operators)
                                :limit (:limit operators)
                                :offset (:offset operators)}
                               {:on-success on-success})
                  timeout-ms))))

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
      (.log js/console "got: " (pr-str results))
      (update app :operators assoc
              :results (if append?
                         (into (get-in app [:operators :results])
                               results)
                         (vec results))
              :total-count total-count))))
