(ns ote.style.form
  "Form layout styles"
  (:require [stylefy.core :as stylefy]
            [garden.units :refer [pt px em]]))

;; FIXME: use garden unit helpers (currently stylefy has a bug and they don't work)

(defn- flex-container [dir]
  {:display "flex" :flex-direction dir})

(def form-group-base {:margin-bottom "1em" ;(em 1)
                      ::stylefy/sub-styles {:form-field {:margin "1em" #_(em 1)}}})

(def form-group-column (merge form-group-base
                              (flex-container "column")))
(def form-group-row (merge form-group-base
                           (flex-container "row")
                           {:flex-wrap "wrap"}))

(def form-group-container {:padding-bottom "0.33em"})

(def form-info-text {:display "inline-block"
                     :position "relative"
                     :top "-0.5em"})
