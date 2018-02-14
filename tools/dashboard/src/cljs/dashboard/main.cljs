(ns dashboard.main
  (:require [reagent.core :as r]
            [tuck.core :as tuck]
            [dashboard.view :refer [dashboard-view]]
            [dashboard.events :refer [->FetchDashboard]]))

(def app (r/atom {}))

(defn dashboard-main [e! app]
  (e! (->FetchDashboard))
  (fn [e! app]
    [dashboard-view e! app]))

(defn main []
  (r/render-component [tuck/tuck app dashboard-main]
                      (.getElementById js/document "dashboard-app")))

(defn reload-hook []
  (r/force-update-all))
