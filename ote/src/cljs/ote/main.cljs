(ns ote.main
  "OTE-sovelluksen käynnistys"
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [tuck.core :as tuck]
            [ote.app.tila :as tila]
            [ote.views.olennaiset-tiedot :as ot]))

(defn ote-sovellus
  "OTE-sovelluksen käyttöliittymän pääkomponentti"
  [e! app]
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme {:palette {:text-color (color :green600)}})}
   [:div.ote-sovellus
    [ui/app-bar {:title "OTE"}]
    [ui/paper
     ;"Ei täällä vielä mitään ole"
     [ot/olennaiset-tiedot e! (:muokattava-palvelu app)]]]])

(defn ^:export main []
  (r/render-component [tuck/tuck tila/app ote-sovellus]
                      (.getElementById js/document "oteapp")))

(defn ^:export reload-hook []
  (r/force-update-all))
