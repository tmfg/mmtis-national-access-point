(ns taxiui.styles.front-page
  (:require [ote.theme.colors :as colors]))

(defn- grid-template-areas
  "Small helper to produce properly quoted CSS grid template area string. Provide areas as 1D vector.
   - `areas` Grid of areas as vector"
  [styles areas]
  (assoc
    styles
    :grid-template-areas
    (->> areas
         (map
           (fn [row]
           (str "\"" row "\"")))
         (clojure.string/join "\n"))))

(defn- breather-margin
  [styles]
  (assoc styles :margin "0.2rem 0.2rem 0.2rem 0.2rem"))

(defn- breather-padding
  [styles]
  (assoc styles :padding "0.2em 0.2em 0.2em 0.2em"))

(def info-box {:margin "1em 1em 1em 1em"})

(def info-section-title (-> {:font-weight "600"}
                            breather-margin))

(def info-box-title (-> {:color       colors/accessible-blue
                         :font-weight "600"}
                        breather-margin
                        breather-padding))

(def company-details (-> {:display               "grid"
                          :grid-template-columns "1fr 1fr min-content"
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

(def area-pill {:border        (str "0.0625em solid " colors/basic-black)
                :border-radius "1em"
                :padding       "0 0.8em 0 0.8em"
                :margin        "0.8em"})

(def example-price-title (-> {:grid-area "example-trip"}
                             breather-padding
                             breather-margin))

(def example-price (-> {:grid-area "prices"}
                       breather-padding
                       breather-margin))

(def area-pills (-> {:grid-area "area-pills"}
                    breather-padding
                    breather-margin))

(def price-box-title (-> {:grid-area "price-information"
                          :font-weight "600"}
                         breather-padding
                         breather-margin))