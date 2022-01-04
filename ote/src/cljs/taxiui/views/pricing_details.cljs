(ns taxiui.views.pricing-details
  (:require [clojure.string :as str]
            [stylefy.core :as stylefy]
            [taxiui.app.controller.pricing-details :as controller]
            [taxiui.styles.pricing-details :as styles]
            [taxiui.views.components.formatters :as formatters]
            [taxiui.views.components.forms :as forms]
            [taxiui.views.components.pill :refer [pill]]
            [taxiui.app.controller.front-page :as fp-controller]
            [re-svg-icons.feather-icons :as feather-icons]
            [taxiui.app.routes :as routes]
            [taxiui.theme :as theme]
            [ote.theme.colors :as colors]
            [reagent.core :as r]))

(defn- pricing-input
  [e! tab-index id main-title subtitle]
  [forms/input
   id
   [main-title subtitle]
   {:type       "text"
    :input-mode "decimal"
    :tabIndex   tab-index
    :on-focus   (fn [e] (set! (.. e -target -value)
                              (or (.. e -target -dataset -rawvalue) "")))
    :on-change  (fn [e]
                  (let [value (.. e -target -value)]
                    (set! (.. e -target -dataset -rawvalue) value)
                    (e! (controller/->StorePrice id value))))
    :on-blur    (fn [e] (set! (.. e -target -value)
                              (formatters/currency (or (.. e -target -dataset -rawvalue) "0.0"))))}
   nil])

(defn- autocomplete-results
  [e! results]
  [:div#autocomplete (stylefy/use-style {:position "relative"
                              :z-index  1000
                              :height   "0px"
                                         ::stylefy/mode {:hover {:cursor "pointer"}}}
                                        {:tabIndex "-1"})
   [:ul#results {:style {:position         "absolute"
                         :list-style       "none"
                         :margin           "0"
                         :top              "-.5em"
                         :background-color "white"
                         :border-style     "none solid solid"
                         :border-width     "1px"
                         :border-radius    "0 0 .5em .5em"
                         :border-color     colors/basic-gray
                         :display          "none"}}
    (doall
      (for [[index result] (map-indexed vector results)
            :when (some? results)]
        (let [{:keys [id label]} result]
          ^{:key (str "result_" index)}
          [:li (stylefy/use-style styles/autocomplete-result
                                  {:on-click (fn [e]
                                               (.preventDefault e)
                                               (e! (controller/->UserSelectedResult result))
                                               (set! (.. (.getElementById js/document "area-of-operation") -value) label)
                                               (set! (.. (.getElementById js/document "results") -style -display) "none"))})
           (str label)])))]])

(defn- autocomplete-input
  [e! search-results]
    [forms/input :area-of-operation "Lisää toiminta-alue"
     ; TODO: Extract this function + parameterize to work based on given elements, not inlined
     {:on-click (fn [e]
                  (.preventDefault e)
                  (letfn [(pixels [v] (js/parseFloat (subs v 0 (- (count v) 2))))]
                    (let [styles (js/getComputedStyle (.. e -target))
                          results (.getElementById js/document "results")
                          border-widths (+ (pixels (. styles -borderLeftWidth))
                                           (pixels (. styles -borderRightWidth)))
                          parent-width (- (pixels (. styles -width))
                                          border-widths)]
                      (set! (.. results -style -width) (str parent-width "px"))
                      (set! (.. results -style -display) "block")
                      false)))
      :on-blur  (fn [e]
                  (when-not (.contains (.. e -currentTarget -parentElement) (.. e -relatedTarget))
                    (set! (.. (.getElementById js/document "results") -style -display) "none")))
      :on-input (fn [e]
                  (e! (controller/->Search (.. e -target -value))))}
     [autocomplete-results e! search-results]])

(defn pricing-details
  [_ _]
  (fn [e! app]
    [:main (stylefy/use-style theme/main-container)
     [:a {#_#_:style (stylefy/use-style styles/link)
          :href     "#/"
          :on-click #(do
                       (.preventDefault %)
                       (routes/navigate! :taxi-ui/front-page nil))}
      [feather-icons/arrow-left] " Palaa omiin palvelutietoihin"]
     [:h2 "Yrityksesi hintatiedot"]

     [:section (stylefy/use-style styles/flex-columns)
      [:div (stylefy/use-style (styles/flex-column 1))
       [pricing-input e! 1 :start-price-daytime "Aloitus" "(arkipäivisin)"]
       [pricing-input e! 3 :start-price-nighttime "Aloitus" "(öisin)"]
       [pricing-input e! 5 :travel-cost-per-minute "Matka" "(hinta per minuutti)"]]
      [:div (stylefy/use-style styles/spacer)]
      [:div (stylefy/use-style (styles/flex-column 1))
       [pricing-input e! 2 :start-time-weekend "Aloitus" "(viikonloppuna)"]
       [pricing-input e! 4 :travel-cost-per-km "Matka" "(hinta per kilometri)"]]]

     [:h3 "Toiminta-alueet"]  ; TODO: design uses singular form, but logically this should be plural
     ; pills here
     [:section
      [:div (stylefy/use-style styles/area-pills)
       [pill "001" {:clickable #(.preventDefault %)}]
       [pill "002" {:clickable #(.preventDefault %)}]
       [pill "003" {:filled? true :clickable #(.preventDefault %)}]]

     [:div (stylefy/use-style styles/flex-columns)
      [:div (stylefy/use-style (styles/flex-column 3))
       [autocomplete-input e! (get-in app [:taxi-ui :search :results])]]

      [:div (stylefy/use-style styles/spacer)]
      [:div (stylefy/use-style (styles/flex-column 1))
       [forms/button :add-button "Lisää" {:styles styles/secondary-button
                                          :type "button"
                                          :on-click (fn [e]
                                                      (e! (controller/->AddAreaOfOperation (get-in app [:taxi-ui :search :selected]))))}]]]
      [forms/button :save-button "Tallenna" {:styles styles/primary-button
                                             :type "button"
                                             :on-click (fn [e]
                                                         (e! (controller/->SavePriceInformation (get-in app [:taxi-ui :price-information]))))}]]]))