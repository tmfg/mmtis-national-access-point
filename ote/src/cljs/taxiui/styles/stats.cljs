(ns taxiui.styles.stats
  (:require [ote.theme.colors :as colors]
            [stylefy.core :as stylefy]
            [taxiui.theme :as theme]))

(def zebra-striping {::stylefy/mode {":nth-child(odd)" {:background-color colors/faint-gray}}})

(def table-row (merge zebra-striping
                      {:height "2.5em"}))

(def table-headers {:height "2.5em"})

(def table-header {:text-align "left"})