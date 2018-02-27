(ns ote.ui.mui-chip-input
  (:require material-ui-chip-input
            [reagent.core :as r]))

(def chip-input (r/adapt-react-class (aget js/window "MaterialUIChipInput")))