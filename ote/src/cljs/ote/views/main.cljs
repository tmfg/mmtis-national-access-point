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
            [ote.localization :as localization]
            [ote.views.kuljetus :as kuljetus]

            [reagent.core :as r]
            [ote.app.place-search :as ps]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.napit :as napit]
            [ote.ui.debug :as debug]
            [ote.ui.leaflet :as leaflet]))


(defn ote-application
  "OTE application main view"
  [e! app]

  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme {:palette {:text-color (color :green600)}})}
   [:div.ote-sovellus.container
    [ui/app-bar {:title "OTE"}]


    [leaflet/Map {:center #js [65 25]
                  :zoom 10}
     [leaflet/TileLayer {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
                         :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}]

     (for [{location :location
            tags :tags} (get-in app [:place-search :results])]
       ^{:key (:name tags)}
       [leaflet/Marker {:position (clj->js location)}
        [leaflet/Popup [:div (pr-str tags)]]])]

    [form-fields/field {:type :string :label "Paikan nimi"
                        :update! #(e! (ps/->SetPlaceName %))}
     (get-in app [:place-search :name])]

    [napit/tallenna {:on-click #(e! (ps/->SearchPlaces))} "Hae paikkoja"]

    [debug/debug (:place-search app)]


    #_[ui/tabs
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
     [ui/tab {:label "Reitit" :value "d"}
      [ui/paper {:class "paper-siirto"}
       [kuljetus/reitti e! (:muokattava-palvelu app)]]]]]])
