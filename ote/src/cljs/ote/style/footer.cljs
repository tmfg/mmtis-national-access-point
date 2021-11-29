(ns ote.style.footer
  (:require [ote.theme.colors :as colors]
            [ote.theme.screen-sizes :as screens]
            [stylefy.core :as stylefy]))

(def footer {:color            ote.theme.colors/primary-text-color
             :background-color ote.theme.colors/primary-background-color
             :padding-right    "1rem"
             :padding-left     "1rem"
             ::stylefy/media   {{:min-width (str screens/width-sm "px")} {:padding-left  "2rem"
                                                                          :padding-right "2rem"
                                                                          :display       "flex"
                                                                          :flex-wrap     "wrap"}}})

(def topbar {:height          "58px"
             :border-bottom   "1px rgba(255,255,255,0.25) solid"
             :margin-bottom   "1rem"
             :display         "flex"
             :justify-content "space-between"
             :align-items     "center"
             ::stylefy/media  {{:min-width (str screens/width-sm "px")} {:flex-basis "100%"}}})

(def link {:color           "inherit"
           :text-decoration "none"
           :font-size       "1rem"
           :line-height     "1.6"})

(def site-links {:margin-bottom  "1.5rem"
                 ::stylefy/media {{:min-width (str screens/width-sm "px")} {:margin-bottom         "3.5rem"
                                                                            :padding-bottom        "1rem"
                                                                            :display               "grid"
                                                                            :grid-template-columns "max-content min-content"
                                                                            :gap                   "0 3rem"}}})

(def site-link-entry {::stylefy/mode {":nth-child(4)" {:margin-bottom "1rem"}}})

(def fintraffic-links {:list-style-type "none"
                       ::stylefy/media  {{:min-width (str screens/width-sm "px")} {:display            "grid"
                                                                                   :grid-auto-flow     "column"
                                                                                   :grid-template-rows "repeat(4, max-content)"
                                                                                   :gap                "0 3rem"}}})

(def fintraffic-site-links-wrapper {:margin-bottom "1rem"
                                    :font-weight   "600"})

(def fintraffic-legal-links {:list-style "none"})

(def fintraffic-logo {:height "18px"
                      :width  "106px"})

(def some-link-wrapper {:display        "flex"
                        :padding-bottom "1rem"
                        :margin-left    "0"
                        ::stylefy/media {{:min-width (str screens/width-sm "px")} {:margin-left "3rem"}}})

(def some-link-icon {:color  colors/primary-text-color
                     :width  "24px"
                     :height "24px"})

(def some-link (merge link
                      {::stylefy/mode {":not(:first-child)" {:margin-left "1rem"}}}))