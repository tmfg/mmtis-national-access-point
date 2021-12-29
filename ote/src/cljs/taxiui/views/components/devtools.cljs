(ns taxiui.views.components.devtools
  "Development mode tools and other niceties."
  (:require [reagent.core :as r]
            [datafrisk.core :as df]))

(defonce debug-visible? (r/atom (not= -1 (.indexOf js/document.location.host "localhost"))))

(defn debug-state [app]
  (when @debug-visible?
     [df/DataFriskShell app]))
