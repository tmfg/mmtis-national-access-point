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
(def button-add-row {:padding-top "20px"})

;; Form elements
(def required-element { :color "#FF9800"})
(def error-element { :color "#B71C1C"
                    :font-weight "600"})

;; Front page

(def front-page-add-service {:border-right "1px solid grey"})
(def front-page-service-table {:a
                               {:text-decoration "none"
                                ::stylefy/mode {:visited {:text-decoration "none"}}}})
(def header-font {:font-size "18px"
                  :padding-top "20px"
                  :font-weight "600"})

(def success-flash-message-body {:background-color "rgba(128, 149, 50, 0.87)"})
(def error-flash-message-body {:background-color "rgba(190, 0, 0, 0.87)"})

(def title {:font-weight "bold"})


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
                  :margin "0.2em"
                  :align-items "center"
                  :white-space "pre-wrap"}))

(def language-selection
  {:border-top "solid 1px white"
   :margin-top "5px"
   :padding-top "5px"
   :text-align "center"})

(def language-flag
  {:padding-left "10px"
   :padding-right "10px"})

(def section-margin {:margin-top "1em"})
