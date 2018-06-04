(ns ote.style.service-search
  (:require [stylefy.core :as stylefy]
            [ote.style.base :as base]))

(def result-header {:width         "100%"
                    :display       "block"
                    :margin-bottom "0.5em"
                    ::stylefy/mode {:hover {:cursor          "pointer"
                                            :text-decoration "underline"}}})

(def operator-result-header {:width            "100%"
                             :display          "block"
                             :padding          "10px"
                             :background-color "#06c"})

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

(def result-card-title {:padding-top          "20px"
                        :padding-right          "0px"
                        :padding-bottom          "20px"
                        :font-size        "1.125em"
                        :font-weight      "700"
                        :color            "#fff"
                        :background-color "#06c"})

(def result-card-header {:font-size "1em"
                         :color "#323232"
                         :font-weight "700"})

(def result-card-body {:padding-top "20px"
                       :padding-bottom "20px"
                       :font-size "1em"
                       :color     "#444444"})

(def simple-result-card-row {:padding-top "3px"})

(def link-result-card-row {:padding-bottom "15px"
                           :font-weight 400
                           :color "#06c"})

(def result-card-delete {:float "right"
                         :position "relative"
                         :top "-15px"
                         :color "#fff"})

(def result-card-chevron {:float "right"
                          :position "relative"
                          :top "-41px"
                          :color "#fff"})

(def result-card-show-data {:float "right"
                            :position "relative"
                            :top "-37px"
                            :color "#fff"})

(def result-card-header-link {:color "#fff"
                              ::stylefy/mode {:hover {:cursor          "pointer"
                                                :text-decoration "underline"}}})

(def delete-icon {:color         "rgba(0, 0, 0, 0,75)"
                  ::stylefy/mode {:hover {:color "rgba(0, 0, 0, 1) !important"}}})
(def partly-visible-delete-icon {:color "rgba(0, 0, 0, 0,75)"})

(def service-link {:color "#06c" :text-decoration "none"})

(def data-items
  (merge base/item-list-container
         {:display   "inline-flex"
          :position  "relative"
          :font-size "13px"
          :color     "#999999"}))

(def external-table-row {:height "20px"})

(def icon-div {:display  "inline-block"
               :position "relative"
               :top      "4px"})

(def contact-icon {:color  "#999999"
                   :height 16
                   :width  16})
