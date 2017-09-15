(ns ote.ui.napit
  (:require [cljs-react-material-ui.reagent :as ui]))


(defn tallenna [opts teksti]
  [ui/raised-button
   (merge {:primary true} opts)
   teksti])
