(ns taxiui.views.pricing-details
  (:require [stylefy.core :as stylefy]
            [taxiui.styles.pricing-details :as styles]
            [taxiui.views.components.formatters :as formatters]
            [taxiui.views.components.forms :as forms]
            [taxiui.views.components.pill :refer [pill]]
            [taxiui.app.controller.front-page :as fp-controller]
            [re-svg-icons.feather-icons :as feather-icons]
            [taxiui.app.routes :as routes]
            [taxiui.theme :as theme]))

(defn- pricing-input
  [main-title subtitle]
  [:div (stylefy/use-style styles/pricing-input-container)
   [:h5 main-title]
   [:h5 subtitle]
   [forms/input {:type      "text"
                 :input-mode "decimal"
                 :on-focus  (fn [e] (set! (.. e -target -value)
                                          (or (.. e -target -dataset -rawvalue) "")))
                 :on-change (fn [e] (set! (.. e -target -dataset -rawvalue)
                                          (.. e -target -value)))
                 :on-blur   (fn [e] (set! (.. e -target -value)
                                          (formatters/currency (or (.. e -target -dataset -rawvalue) "0.0"))))}]])


(defn pricing-details
  [_ _]
  (fn [_ _]
    [:main (stylefy/use-style theme/main-container)
     [:a {#_#_:style (stylefy/use-style styles/link)
          :href     "#/"
          :on-click #(do
                       (.preventDefault %)
                       (routes/navigate! :taxi-ui/front-page nil))}
      [feather-icons/arrow-left] " Palaa omiin palvelutietoihin"]
     [:h2 "Yrityksesi hintatiedot"]

     [:section (stylefy/use-style styles/flex-columns)
      [:div (stylefy/use-style styles/flex-column)
       [pricing-input "Aloitus" "(välille 06-18)"]
       [pricing-input "Aloitus" "(välille 18-24)"]
       [pricing-input "Matka" "(hinta per minuutti)"]]
      [:div (stylefy/use-style styles/spacer)]
      [:div (stylefy/use-style styles/flex-column)
       [pricing-input "Aloitus" "(viikonloppu)"]
       [pricing-input "Matka" "(hinta per kilometri)"]]
      ]

     [:h3 "Toiminta-alue"]
     ; pills here
     [:section
      [:div (stylefy/use-style styles/area-pills)
       [pill "001" {:clickable #(.preventDefault %)}]
       [pill "002" {:clickable #(.preventDefault %)}]
       [pill "003" {:filled? true :clickable #(.preventDefault %)}]]

     [:div (stylefy/use-style styles/flex-columns)
      [:div (stylefy/use-style styles/flex-column)
       [forms/input]]
      [:div (stylefy/use-style styles/spacer)]
      [:div (stylefy/use-style styles/flex-column)
       [forms/button "Lisää"]]]]]))