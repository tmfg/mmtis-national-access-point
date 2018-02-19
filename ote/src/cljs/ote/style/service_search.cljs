(ns ote.style.service-search
  (:require [stylefy.core :as stylefy]
            [ote.style.base :as base]))

(def result-header {:width         "100%"
                    :display       "block"
                    :margin-bottom "0.5em"
                    ::stylefy/mode {:hover {:cursor          "pointer"
                                            :text-decoration "underline"}}})

(def result-border {:padding-bottom "10px"
                    :margin-bottom  "10px"
                    :border-bottom  "1px solid #d9d9d9"})

(def operator-result-header {:width            "100%"
                             :display          "block"
                             :padding          "10px"
                             :background-color "#0088ce"})

(def operator-result-header-link {:color       "white"
                                  :font-weight 600})

(def operator-description {:padding   "5px 10px 10px 10px"
                           :font-size "15px"
                           :color     "#666"})

(def service-card-description {:display       "inline-block"
                               :max-width     "100%"
                               :padding-right "40px"
                               :max-height    "21px"
                               :line-height   "21px"
                               :text-align    "justify"
                               :position      "relative"
                               :overflow      "hidden"})

(def result-card {:margin-top "20px"
                  :background-color "#fff"
                  :box-shadow       "rgba(0, 0, 0, 0.12) 0px 1px 6px, rgba(0, 0, 0, 0.12) 0px 1px 4px"})

(def result-card-label {:padding          "15px 15px"
                        :font-size        "1.125em"
                        :font-weight      "bold"
                        :color            "#fff"
                        :background-color "#00A9DF"})

(def result-card-small-label {:font-size "12px"
                              :font-weight "20"
                              :padding-left "20px"})

(def result-card-body {:padding   "15px 15px"
                       :font-size "1em"
                       :color     "#000"})

(def result-card-delete {:float "right"
                         :position "relative"
                         :top "-50px"})

(def delete-icon {:color         "rgba(255, 255, 255, 0,75)"
                  ::stylefy/mode {:hover {:color "rgba(255, 255, 255, 1) !important"}}})
(def partly-visible-delete-icon {:color "rgba(255, 255, 255, 0,75)"})

(def service-link {:color "#2D75B4" :text-decoration "none"})

(def data-items
  (merge base/item-list-container
         {:display   "inline-flex"
          :position  "relative"
          :font-size "13px"
          :color     "#999999"}))

(def external-interface-header
  {:font-size  "80%"
   :text-align "left"})

(def external-interface-body {:font-weight "normal"})

(def external-table-row {:height "20px"})

(def icon-div {:display  "inline-block"
               :position "relative"
               :top      "4px"})

(def contact-icon {:color  "#999999"
                   :height 16
                   :width  16})
