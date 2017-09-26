(ns ote.main
  "OTE app startup"
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [tuck.core :as tuck]
            [ote.app.state :as state]
            [ote.views.main :as main]
            [ote.localization :as localization]))


(defn ^:export main []
  (localization/load-language!
   :fi
   (fn [lang _]
     (reset! localization/selected-language lang)
     (r/render-component [tuck/tuck state/app main/ote-application]
                         (.getElementById js/document "oteapp")))))

(defn ^:export reload-hook []
  (r/force-update-all))
