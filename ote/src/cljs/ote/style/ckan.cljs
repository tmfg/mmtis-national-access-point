(ns ote.style.ckan
  "Styles to use when embedding OTE viewer in CKAN page"
  (:require [stylefy.core :as stylefy]
            [garden.units :refer [pt px em]]))

(def info-block
  {:display "inline-block"
   :margin-right "0.75em"})

(def content-title {:padding-bottom "5px"
                    })