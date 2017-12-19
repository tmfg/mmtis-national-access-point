(ns ote.style.form
  "Form layout styles"
  (:require [stylefy.core :as stylefy]
            [garden.units :refer [pt px em]]
            [ote.style.base :as base]))

;; FIXME: use garden unit helpers (currently stylefy has a bug and they don't work)


(def form-field {:margin-right "10px"})

(def form-group-base {:margin-bottom "1em"})

(def form-group-column (merge form-group-base
                              (base/flex-container "column")))
(def form-group-row (merge form-group-base
                           (base/flex-container "row")
                           {:flex-wrap "wrap"}))

(def form-group-container {:padding-bottom "1em"})

(def form-info-text {:display "inline-block"
                     :position "relative"
                     :top "-0.5em"})

(def full-width {:width "100%"})

(def subtitle (merge full-width
                     {:margin "1em 0 0 0.5em"}))
(def subtitle-h {:margin "0"})

(def subheader {:color "#666"
                :margin-top "-10px"
                :padding-bottom "10px"})

(def border-color "#C4C4C4")
(def border-right {:border-right (str "solid 2px " border-color)
                   :box-sizing "border-box"
                   :padding-right "20px"})

(def help-icon-element {:padding "0px 0px 0px 10px"})
(def help-text-element {:padding "5px 5px 5px 10px"})

(def organization-padding {:padding-top "20px"})
(def table-header-style {:border-bottom "0px solid white" :color "black"})

(def padding-top {:padding-top "20px"})