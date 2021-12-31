(ns taxiui.styles.pricing-details
  (:require [ote.theme.colors :as colors]
            [taxiui.theme :as theme]))

(def flex-columns (-> {:display         "flex"
                         :flex-wrap       "wrap"
                         :justify-content "space-between"}
                        theme/breather-padding
                        theme/breather-margin))

(def pricing-input-container {})

(def flex-column {:flex "1"})
(def spacer {:width "2em"})

(def area-pills {:display "flex"
                 :gap     "1em"})