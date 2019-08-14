(ns ote.style.admin
  "Admin panel styles"
  (:require [stylefy.core :as stylefy]
            [garden.units :refer [pt px em]]))

(def modal-data-label {:font-size     "1.1em"
                       :color         "#323232"
                       :font-weight   "400"
                       :text-align    "right"
                       :padding-right "20px"})

(def detection-button-container {:display "flex"
                                 :flex-wrap "wrap"
                                 :justify-content "flex-end"})

(def detection-info-text {:margin "0 0 0.5em 0"})

(def detection-button-with-input {:margin "0 0 0 2em"})


