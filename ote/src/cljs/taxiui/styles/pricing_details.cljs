(ns taxiui.styles.pricing-details
  (:require [ote.theme.colors :as colors]
            [taxiui.theme :as theme]))

(def pricing-inputs (-> {:display         "flex"
                         :flex-wrap       "wrap"
                         :justify-content "space-between"}
                        theme/breather-padding
                        theme/breather-margin))

(def pricing-input-container {})

(def left-column {:flex "1"})
(def spacer {:width "2em"})
(def right-column {:flex "1"})