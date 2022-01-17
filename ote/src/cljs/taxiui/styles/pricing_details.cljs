(ns taxiui.styles.pricing-details
  (:require [ote.theme.colors :as colors]
            [stylefy.core :as stylefy]
            [taxiui.theme :as theme]))

(def flex-columns (-> {:display           "flex"
                         :flex-wrap       "wrap"
                         :justify-content "space-between"}
                        theme/breather-padding
                        theme/breather-margin))

(defn flex-column
  "How many relative spaces the column should take. This is effectively shorthand for `flex-grow` property"
  [width]
  {:flex (str width)})

(def spacer {:width "1em"})

(def area-pills {:display "flex"
                 :flex-wrap "wrap"})

(def autocomplete-result {:white-space   "nowrap"
                          :overflow      "hidden"
                          :text-overflow "ellipsis"
                          :font-size     "1.5em"
                          :padding       "0.2em 0 0.2em 0.2em"
                          :height        "1.5em"
                          :line-height   "1.5em"
                          ::stylefy/mode {:hover {:color            colors/primary-text-color
                                                  :background-color colors/primary-background-color}}})

(def primary-button {:text-transform "uppercase"
                     :color            colors/primary-text-color
                     :background-color colors/primary-background-color
                     ::stylefy/mode    {:hover {:color            colors/basic-black
                                                :background-color colors/basic-white}}})

(def secondary-button {:text-transform "uppercase"
                       :color            colors/basic-black
                       :background-color colors/basic-white
                       ::stylefy/mode    {:hover {:color            colors/primary-text-color
                                                  :background-color colors/accessible-gray}}})