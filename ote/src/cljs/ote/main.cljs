(ns ote.main
  "OTE-sovelluksen käynnistys"
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [tuck.core :as tuck]
            [ote.app.tila :as tila]
            [ote.views.olennaiset-tiedot :as ot]
            [ote.views.vuokraus :as vuokraus]
            [ote.views.alueet :as pysakointialueet]
            [ote.views.liikennevalineet :as liikennevalineet]
            [ote.views.valituspalvelut :as valityspalvelut]
            [ote.lokalisaatio :as lokalisaatio]))


(defn ote-sovellus
  "OTE-sovelluksen käyttöliittymän pääkomponentti"
  [e! app]
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme {:palette {:text-color (color :green600)}})}
   [:div.ote-sovellus.container
    [ui/app-bar {:title "OTE"}]
    [ui/tabs
     [ui/tab {:label "Olennaiset tiedot" :value "a"}
      [ui/paper {:class "paper-siirto"}
        [ot/olennaiset-tiedot e! (:muokattava-palvelu app)]]]
     [ui/tab {:label "Pysäköintialueet" :value "b"}
      [ui/paper {:class "paper-siirto"}
        [pysakointialueet/pysakointialueet e! (:muokattava-palvelu app)]]]
     [ui/tab {:label "Liikennevälineet" :value "c"}
      [ui/paper {:class "paper-siirto"}
       [liikennevalineet/liikennevalineet e! (:muokattava-palvelu app)]]]
     [ui/tab {:label "Välityspalvelut" :value "d"}
      [ui/paper {:class "paper-siirto"}
       [valityspalvelut/valityspalvelu e! (:muokattava-palvelu app)]]]
     ]
    ]])

(defn ^:export main []
  (lokalisaatio/lataa-kieli!
   :fi
   (fn [kieli _]
     (reset! lokalisaatio/valittu-kieli kieli)
     (r/render-component [tuck/tuck tila/app ote-sovellus]
                         (.getElementById js/document "oteapp")))))

(defn ^:export reload-hook []
  (r/force-update-all))
