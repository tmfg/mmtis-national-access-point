(ns ote.style.base
  "Base styles for OTE application. Everything that affects the overall look and feel of the app."
  (:require
  [stylefy.core :as stylefy :refer [use-style use-sub-style]]))

(def body {:margin 0
           :padding 0})

(def font {:font-family "'Roboto', sans-serif"})

(def inline-block {:display "inline-block"})

(def action-button-container (merge inline-block
                                    {:margin-right "1em"}))

(def action-button {:padding-left "0.3em" :padding-right "0.3em"})

;; Front page

(def front-page-add-service {:border-right "1px solid grey"})
(def front-page-service-table {:a
                               {:text-decoration "none"
                                ::stylefy/mode {:visited {:text-decoration "none"}}}})