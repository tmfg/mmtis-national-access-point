(ns ote.main
  "OTE-sovelluksen käynnistys"
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [tuck.core :as tuck]
            [ote.app.tila :as tila]
            [ote.views.main :as main]
            [ote.lokalisaatio :as lokalisaatio]))


(defn ^:export main []
  (lokalisaatio/lataa-kieli!
   :fi
   (fn [kieli _]
     (reset! lokalisaatio/valittu-kieli kieli)
     (r/render-component [tuck/tuck tila/app main/ote-sovellus]
                         (.getElementById js/document "oteapp")))))

(defn ^:export reload-hook []
  (r/force-update-all))
