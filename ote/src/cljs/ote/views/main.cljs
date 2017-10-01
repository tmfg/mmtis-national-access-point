(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.views.olennaiset-tiedot :as ot]
            [ote.views.vuokraus :as vuokraus]
            [ote.views.alueet :as pysakointialueet]
            [ote.views.liikennevalineet :as liikennevalineet]
            [ote.views.valituspalvelut :as valityspalvelut]
            [ote.views.passenger_transportation :as pt]
            [ote.localization :as localization]
            [ote.views.kuljetus :as kuljetus]
            [ote.views.place-search :as place-search]
            ))


(defn ote-application
  "OTE application main view"
  [e! app]

  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme {:palette {:text-color (color :green600)}})}
   [:div.ote-sovellus.container
    [ui/app-bar {:title "OTE"}]

    [ui/tabs {:value (:page app)}
     [ui/tab {:label "1. Tuottajan tiedot" :value :operator}

      [ui/paper {:class "paper-siirto"}
        [ot/olennaiset-tiedot e! (:transport-operator app)]]]

     [ui/tab {:label "2. Kuljetus" :value :passenger-transportation}
      [ui/paper {:class "paper-siirto"}
       [pt/passenger-transportation-info e! (:transport-service app)]]]

     [ui/tab {:label "Pysäköintialueet" :value :parking}
      [ui/paper {:class "paper-siirto"}
        [pysakointialueet/pysakointialueet e! (:data app)]]]
     [ui/tab {:label "Liikennevälineet" :value "c"}
      [ui/paper {:class "paper-siirto"}
       [liikennevalineet/liikennevalineet e! (:data app)]]]
     [ui/tab {:label "Välityspalvelut" :value "d"}
      [ui/paper {:class "paper-siirto"}
       [valityspalvelut/valityspalvelu e! (:data app)]]]
     [ui/tab {:label "Reitit" :value "d"}
      [ui/paper {:class "paper-siirto"}
       [kuljetus/reitti e! (:data app)]]]]]])
