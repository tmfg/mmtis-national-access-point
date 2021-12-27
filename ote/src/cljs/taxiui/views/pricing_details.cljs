(ns taxiui.views.pricing-details
  (:require [stylefy.core :as stylefy]
            [taxiui.styles.pricing-details :as styles]
            [taxiui.views.components.formatters :as formatters]))

(defn- pricing-input
  [main-title subtitle]
  [:div (stylefy/use-style styles/pricing-input-container)
   [:h5 main-title]
   [:h5 subtitle]
   [:input (merge (stylefy/use-style styles/pricing-input-element)
                  {:type      "text"
                   :inputmode "decimal"
                   :on-focus  (fn [e] (set! (.. e -target -value)
                                            (or (.. e -target -dataset -rawvalue) "")))
                   :on-change (fn [e] (set! (.. e -target -dataset -rawvalue)
                                            (.. e -target -value)))
                   :on-blur   (fn [e] (set! (.. e -target -value)
                                            (formatters/currency (or (.. e -target -dataset -rawvalue) "0.0"))))})]])


(defn pricing-details
  [_ _]
  [:main
   [:h2 "Yrityksesi hintatiedot"]

   [:section (stylefy/use-style styles/pricing-inputs)
    [pricing-input "Aloitus" "(välille 06-18)"]
    [pricing-input "Aloitus" "(viikonloppu)"]
    [pricing-input "Aloitus" "(välille 18-24)"]
    [pricing-input "Matka" "(hinta per kilometri)"]
    [pricing-input "Matka" "(hinta per minuutti)"]]])