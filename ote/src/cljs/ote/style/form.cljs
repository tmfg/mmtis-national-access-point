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
                           {:flex-wrap "wrap" :align-items "center"}))

(def form-group-container {:padding-bottom "1em" :width "100%"})

(def form-card {:background-color "#fff"
                :box-shadow "rgba(0, 0, 0, 0.12) 0px 1px 6px, rgba(0, 0, 0, 0.12) 0px 1px 4px"})

(def form-card-label {:padding "15px 15px"
                      :font-size "1.125em"
                      :font-weight "bold"
                      :color "#fff"
                      :background-color "#06c"})

(def form-card-body {:padding "15px 15px"
                     :font-size "1em"
                     :color "#444444"})

(def form-info-text {:display "inline-block"
                     :position "relative"
                     :top "-0.5em"})

(def full-width {:width "100%"})

(def half-width {:width "50%"})


(def subtitle (merge full-width
                     {:margin "1em 0 0 0.5em"}))
(def subtitle-h {:margin "0"})

(def subheader {:color "#666"
                :margin-top "-10px"})

(def border-color "#C4C4C4")
(def border-right {:border-right (str "solid 2px " border-color)
                   :box-sizing "border-box"
                   :padding-right "20px"})

(def help-icon-element {:padding "0px 0px 0px 10px"})
(def help-text-element {:padding "0" :line-height "21px"})

(def organization-padding {:padding-top "20px"})

(def padding-top {:padding-top "20px"})
