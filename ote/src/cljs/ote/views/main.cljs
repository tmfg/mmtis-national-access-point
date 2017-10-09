(ns ote.views.main
  "OTE-sovelluksen päänäkymä"
  (:require [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.views.transport-operator :as to]
            [ote.views.vuokraus :as vuokraus]
            [ote.views.alueet :as pysakointialueet]
            [ote.views.liikennevalineet :as liikennevalineet]
            [ote.views.valituspalvelut :as valityspalvelut]
            [ote.views.passenger_transportation :as pt]
            [ote.localization :as localization]
            [ote.views.kuljetus :as kuljetus]
            [ote.views.place-search :as place-search]
            [ote.ui.debug :as debug]))


(defn ote-application
  "OTE application main view"
  [e! app]

  [ui/mui-theme-provider
   {:mui-theme
    (get-mui-theme
     {:palette {;; primary nav color - Also Focus color in text fields
                :primary1-color (color :lightBlue300)

                ;; Hint color in text fields
                :disabledColor (color :grey900)

                ;; canvas color
                ;;:canvas-color  (color :lightBlue50)

                ;; Main text color
                :text-color (color :grey900)}

      :button {:labelColor "#fff"}

      })}
   [:div.ote-sovellus.container-fluid
    [ui/app-bar {:title "OTE"}]
    [:div.container-fluid
     (when (= :operator (:page app))
               [to/olennaiset-tiedot e! (:transport-operator app)])
     (when (= :passenger-transportation (:page app))
        [pt/passenger-transportation-info e! (:transport-service app)]
       )
     [debug/debug app]
     ]]])
