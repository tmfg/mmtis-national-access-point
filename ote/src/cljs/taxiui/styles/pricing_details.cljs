(ns taxiui.styles.pricing-details
  (:require [ote.theme.colors :as colors]
            [taxiui.theme :as theme]))

(def pricing-inputs (-> {:display         "flex"
                         :flex-wrap       "wrap"
                         :justify-content "space-between"}
                        theme/breather-padding
                        theme/breather-margin))

(def pricing-input-container {:width "47.5%"})

(def pricing-input-element (-> {:border        (str "0.0625em solid " colors/light-gray)
                                :border-radius "0.3em"
                                :height        "3rem"
                                :font-size     "2em"
                                :width         "100%"
                                :box-sizing    "border-box"}
                               (theme/breather-padding)))
