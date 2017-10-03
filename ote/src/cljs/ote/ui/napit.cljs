(ns ote.ui.napit
  (:require [cljs-react-material-ui.reagent :as ui]))


(defn tallenna [opts teksti]
  [ui/raised-button ;{:style {:labelColor "#fff" :margin "12"} :primary true}
   (merge {:primary true} opts)
   teksti])
