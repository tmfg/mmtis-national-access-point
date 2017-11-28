(ns ote.style.base
  "Base styles for OTE application. Everything that affects the overall look and feel of the app."
  (:require
  [stylefy.core :as stylefy :refer [use-style use-sub-style]]))

(def body {:margin 0
           :padding 0})

(def mobile-width-px 950)

(def wrapper {:padding-top "20px"})

(def font {:font-family "'Roboto', sans-serif"})

(def inline-block {:display "inline-block"})

(def action-button-container (merge inline-block
                                    {:margin-right "1em"}))

(def base-button {:padding-left "1.1em"
                  :padding-right "1.1em"
                  :text-transform "uppercase"
                  :color "#FFFFFF"
                  :background-color "#1565C0"
                  :font-size "12px"
                  :font-weight "bold"
                  })

(def disabled-button (merge base-button {:background-color "#CCCCCC"}))

(def button-label-style {:font-size "12px"
                         :font-weight "bold"
                         :text-transform "uppercase"
                         :color "#FFFFFF"})

;; Form elements
(def required-element { :color "#FF9800"})
(def error-element { :color "#B71C1C"
                    :font-weight "600"})
(def long-drowpdown {:width "350px" })
(def extra-long-drowpdown {:width "425px" })

;; Front page

(def front-page-add-service {:border-right "1px solid grey"})
(def front-page-service-table {:a
                               {:text-decoration "none"
                                ::stylefy/mode {:visited {:text-decoration "none"}}}})
(def header-font {:font-size "18px"
                  :padding-top "20px"
                  :font-weight "600"})

(def flash-message {:top "100px"})

(def title {:font-weight "bold"})

(def large-title (merge title {:font-size "150%"}))

(defn flex-container [dir]
  {:display "flex" :flex-direction dir})

(def item-list-container
  (merge (flex-container "row")
         {:flex-wrap "wrap"}))

(def item-list-row-margin
  {:margin-right "1em"})

(def item-list-item
  (merge inline-block
         {:position "relative"
          :margin-left "0.2em"}))

(def help (merge (flex-container "row")
                 {:border-radius "0.5em"
                  :border "solid 1px #c4c4c4"
                  :color "#434343"
                  :background-color "#DAEDF7"
                  :padding "0.2em"
                  :margin "0.2em"}))
