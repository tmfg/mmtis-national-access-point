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

(def result-card-new {:box-shadow "1px 1px 10px 0 hsla(0, 0%, 86%, .5)"
                      :background-color "#fff"
                      :margin-top "1.5rem"
                      :border (str "1px solid " colors/gray400)
                      ::stylefy/sub-styles {:header {:align-items "center"
                                                     :background-color "#fff"
                                                     :border-bottom (str "1px solid " colors/gray400)
                                                     :display "flex"
                                                     :justify-content "space-between"
                                                     :padding "1.5rem"
                                                     ::stylefy/media {{:max-width (str width-xxs "px")}
                                                                      {:flex-direction "column"
                                                                       :align-items "flex-start"}}}
                                            :body {:padding "1.5rem"}
                                            :foot {:display "flex"
                                                   :justify-content "space-between"
                                                   :padding "0 1.5rem 1.5rem 1.5rem"
                                                   ::stylefy/media {{:max-width (str width-sm "px")}
                                                                    {:flex-direction "column"
                                                                     :align-items "flex-start"}}}}})

(def result-card {:margin-top "20px"
                  :background-color "#fff"
                  :box-shadow "rgba(0, 0, 0, 0.12) 0px 1px 6px, rgba(0, 0, 0, 0.12) 0px 1px 4px"})

(def result-card-title {:display "flex"
                        :flex-flow "row nowrap"
                        :justify-content "space-between"
                        :padding-bottom "15px" ;; Only bottom is required, other "air" comes from the margins in the children
                        :color "#fff"
                        :background-color "#06c"
                        ::stylefy/media {{:max-width (str width-xxs "px")} {:flex-flow "row wrap"}}
                        ::stylefy/sub-styles {:title {:font-size "1.125em"
                                                      :font-weight "700"
                                                      :max-width "65%"
                                                      :white-space "pre-line"
                                                      :word-break "break-all"
                                                      :hyphens "auto"
                                                      :margin "20px 20px 0 0" ;; We define proper margins here in case of row wrapping
                                                      ::stylefy/vendors ["webkit" "moz" "ms"]
                                                      ::stylefy/auto-prefix #{:hyphens}
                                                      ::stylefy/media {{:max-width (str width-xxs "px")}
                                                                       {:max-width "100%"}}}
                                              :actions {:display "flex"
                                                        :position "relative"
                                                        :top "-10px"
                                                        :flex-flow "row nowrap"
                                                        :margin-left "20px"
                                                        :min-width "200px"
                                                        :margin "20px 20px 0 20px" ;; We define proper margins here in case of row wrapping
                                                        ::stylefy/media {{:max-width (str width-xxs "px")}
                                                                         {:margin-left "auto"}}}}})

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
                           :color "#323232"})

(def result-card-header-link {:color "#fff"
                              ::stylefy/mode {:hover {:cursor          "pointer"
                                                      :text-decoration "underline"}}})

(def delete-button {:padding "0"
                    :margin-top "5px"
                    :height "24px"
                    :width "24px"})

(def delete-icon {:color "rgba(0, 0, 0, 0,75)"
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
