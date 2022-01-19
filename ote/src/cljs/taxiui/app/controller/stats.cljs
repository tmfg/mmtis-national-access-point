(ns taxiui.app.controller.stats
  (:require [cljs-time.core :as time]
            [cljs-time.coerce :as tc]
            [clojure.set :as set]
            [taxiui.app.controller.loader :as loader]
            [taxiui.app.routes :as routes]
            [tuck.core :as tuck]
            [ote.communication :as comm]))

(defn- sanitize
  [statistics]
  (->> statistics
       (map
         #(-> % (set/rename-keys {:timestamp :updated})))
       (map #(update % :updated (fn [ts] (time/in-months (time/interval (time/minus (tc/from-date ts) (time/months (rand-int 24))) (time/now))))))))

(tuck/define-event LoadStatisticsResponse [response]
  {}
  (let [sanitized (sanitize response)]
    (tuck/fx
      (assoc-in app [:taxi-ui :stats :companies] sanitized)
      (fn [e!]
        (e! (loader/->RemoveHit :page-loading))))))

(tuck/define-event LoadStatisticsFailed [response]
  {}
  (js/console.log (str "LoadStatisticsFailed :: " response))
  app)

(tuck/define-event LoadStatistics [params]
  {}
  (comm/post! (str "taxiui/statistics")
              params
              {:on-success (tuck/send-async! ->LoadStatisticsResponse)
              :on-failure (tuck/send-async! ->LoadStatisticsFailed)})
  app)

(tuck/define-event SetFilter [id value]
  {}
  (assoc-in app [:taxi-ui :stats :filters id] value))

(defmethod routes/on-navigate-event :taxi-ui/stats [{params :params}]
  [(->LoadStatistics params)])
