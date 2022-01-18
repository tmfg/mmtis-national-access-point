(ns taxiui.styles.header
  (:require [ote.theme.colors :as colors]
            [taxiui.theme :as theme]))

(def header {:background-color colors/primary-background-color
             :color            colors/primary-text-color
             :display          "flex"
             :align-items      "center"
             :height           "2.8em"})

(def nap-logo (-> {:margin-left "0.8em"
                   :height      "1.6em"}
                  (theme/breather-padding)))