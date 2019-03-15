(ns ote.style.service-search
  (:require [stylefy.core :as stylefy]
            [ote.style.base :as base]
            [ote.theme.screen-sizes :refer [width-xxs width-xs width-sm width-md width-l width-xl]]
            [ote.theme.colors :as colors]))

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

(def result-card {:box-shadow "1px 1px 10px 0 hsla(0, 0%, 86%, .5)"
                  :background-color "#fff"
                  :margin-top "1.5rem"
                  :border (str "1px solid " colors/gray550)
                  :border-top (str "4px solid " colors/gray750)
                  ::stylefy/sub-styles {:header {:align-items "center"
                                                 :background-color "#fff"
                                                 :border-bottom (str "1px solid " colors/gray550)
                                                 :display "flex"
                                                 :justify-content "space-between"
                                                 :padding "1rem"
                                                 ::stylefy/media {{:max-width (str width-md "px")}
                                                                  {:padding "0.7rem"}
                                                                  {:max-width (str width-xxs "px")}
                                                                  {:flex-direction "column"
                                                                   :align-items "flex-start"}}}
                                        :sub-header {:margin 0
                                                     ::stylefy/media {{:max-width (str width-md "px")}
                                                                      {:margin "0 0 0.5rem 0"}}}
                                        :body {:padding "1rem"
                                               :display "flex"
                                               ::stylefy/media {{:max-width (str width-md "px")}
                                                                {:padding "1rem 0.7rem 0 0.7rem"
                                                                 :flex-direction "column"}}}
                                        :body-left {:flex 1
                                                    ::stylefy/media {{:max-width (str width-md "px")}
                                                                     {:padding-bottom "1rem"}}}
                                        :body-right {:flex 1
                                                     :padding-left "1.5rem"
                                                     :padding-bottom "1rem"
                                                     ::stylefy/media {{:max-width (str width-md "px")}
                                                                      {:padding-left 0
                                                                       :padding-bottom "0.7rem"}}}
                                        :info-row {:border-bottom (str "1px solid " colors/gray350)
                                                   :display "flex"
                                                   :margin-bottom "0.5rem"
                                                   :font-size "0.875rem"}
                                        :info-title {:flex 1
                                                     :color colors/gray800
                                                     :word-break "break-word"}
                                        :info-content {:flex 2}
                                        :foot {:padding "0 1rem 1rem 1rem"
                                               ::stylefy/media {{:max-width (str width-md "px")}
                                                                {:padding "0 0.7rem 0.7rem 0.7rem"}}}}})

(def simple-result-card-row {:padding-top "3px"})

(def link-result-card-row {:padding-bottom "15px"
                           :font-weight 400
                           :color "#323232"})

(def result-card-header-link {:color "#fff"
                              ::stylefy/mode {:hover {:cursor          "pointer"
                                                      :text-decoration "underline"}}})

(def delete-button {:padding "0"
                    :margin-top "5px"
                    :height "24px"
                    :width "24px"})

(def delete-icon {:color colors/gray900
                  ::stylefy/mode {:hover {:color (str colors/primary " !important")}}})

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
