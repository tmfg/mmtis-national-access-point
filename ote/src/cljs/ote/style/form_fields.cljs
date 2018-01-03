(ns ote.style.form-fields
  (:require [stylefy.core :as stylefy]))

(def localized-text-language
  {:text-decoration "none"
   :margin-left "0.5em"
   :cursor "pointer"})

(def localized-text-language-container
  {:font-size "12px"
   :float "right"
   :padding-right "2em"
   :margin-top "-10px"})

(def localized-text-language-selected
  (merge localized-text-language
          {:font-weight "bold"}))

(def checkbox-group-label {:margin-bottom "4px"})
