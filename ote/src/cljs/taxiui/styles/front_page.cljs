(ns taxiui.styles.front-page
  (:require [ote.theme.colors :as colors]
            [taxiui.theme :as theme :refer [breather-margin breather-padding grid-template-areas]]))

(def info-box {})

(def info-section-title (-> {:font-weight "600"}
                            breather-margin))

(def info-box-title (-> {:color       colors/accessible-blue
                         :font-weight "600"}
                        breather-margin
                        breather-padding))

(def company-details (-> {:display               "grid"
                          :grid-template-columns "0.8fr 1fr min-content"
                          :grid-template-rows    "auto auto auto"
                          :gap                   "0px 0px"
                          :border                (str "0.0625em solid " colors/light-gray)
                          :border-radius         "0.3em"}
                         (grid-template-areas ["title title arrow"
                                               "personnel toimiala arrow"
                                               "liikevaihto yhtiomuoto arrow"])))

(def pricing-details (-> {:display               "grid"
                          :grid-template-columns "min-content 1fr min-content"
                          :grid-template-rows    "auto auto auto auto"
                          :gap                   "0px 0px"
                          :border                (str "0.0625em solid " colors/light-gray)
                          :border-radius         "0.3em"}
                         (grid-template-areas ["logo price-information arrow"
                                               ". example-trip arrow"
                                               ". prices arrow"
                                               ". area-pills arrow"])))

(def details-arrow {:margin-left  "auto"
                    :height       "auto"
                    :grid-area    "arrow"
                    :justify-self "end"
                    :align-self   "center"})

(def info-panel (-> {}
                    breather-padding
                    breather-margin))

(def area-pills (-> {:grid-area "area-pills"
                     :display   "flex"
                     :gap       "1em"}
                    breather-padding
                    breather-margin))

(def example-price-title (-> {:grid-area "example-trip"}
                             breather-padding
                             breather-margin))

(def example-prices (-> {:grid-area "prices"
                         :display "flex"}
                        breather-padding
                        breather-margin))

(def flex-right-aligned {:margin-left "auto"})

(def price-box-title (-> {:grid-area "price-information"
                          :font-weight "600"}
                         breather-padding
                         breather-margin))

(def currency-breather {:margin-left "0.2em"})

(def info-box-link {:text-decoration "none"
                    :color           "inherit"})

(def panel-icon {:width "1em"})