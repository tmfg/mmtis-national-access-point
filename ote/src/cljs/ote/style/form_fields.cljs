(ns ote.style.form-fields
  (:require [stylefy.core :as stylefy]))

(def localized-text-language
  {:text-decoration "none"
   :margin-left "0.5em"
   ;:margin-right "1em"
   :cursor "pointer"})

(def localized-text-language-selected
  (merge localized-text-language
          {:font-weight "bold"}))

(def checkbox-group-label {:margin-bottom "4px"})
