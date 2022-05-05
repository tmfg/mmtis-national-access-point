(ns taxiui.views.pricing-details
  (:require [clojure.string :as str]
            [stylefy.core :as stylefy]
            [taxiui.app.controller.pricing-details :as controller]
            [taxiui.styles.pricing-details :as styles]
            [taxiui.views.components.formatters :as formatters]
            [taxiui.views.components.forms :as forms]
            [taxiui.views.components.pill :refer [pill]]
            [taxiui.views.components.link :refer [link]]
            [taxiui.app.controller.front-page :as fp-controller]
            [re-svg-icons.feather-icons :as feather-icons]
            [taxiui.app.routes :as routes]
            [taxiui.theme :as theme]
            [ote.localization :refer [tr]]
            [ote.theme.colors :as colors]
            [reagent.core :as r]))

(defn- input-spacer
  "Purpose of this element is to push the inputs apart a bit to make the UI lighter."
  []
  [:div {:style {:padding-bottom "1.5em"}} " "])

(defn- pricing-input
  [e! app tab-index id]
  [forms/input
   id
   [(tr [:taxi-ui :pricing-details :inputs id :main-title]) (tr [:taxi-ui :pricing-details :inputs id :subtitle])]
   (merge {:type       "text"
           :input-mode "decimal"
           :tabIndex   tab-index
           :on-focus   (fn [e] (set! (.. e -target -value)
                                     (or (. (.. e -target -dataset) -rawvalue) "")))
           :on-change  (fn [e]
                         (let [value (str (.. e -target -value))]
                           (set! (. (.. e -target -dataset) -rawvalue) value)
                           (e! (controller/->StorePrice id value))))
           :on-blur    (fn [e] (set! (.. e -target -value)
                                     (formatters/currency (or (. (.. e -target -dataset) -rawvalue) "0.0"))))}
          (when-let [existing-price (get-in app [:taxi-ui :pricing-details :price-information :prices id])]
            {:data-rawvalue existing-price
             :placeholder   (formatters/currency existing-price)}))
   (input-spacer)])

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
        (let [{label :ote.db.places/namefin} result]
          ^{:key (str "result_" index)}
          [:li (stylefy/use-style styles/autocomplete-result
                                  {:on-click (fn [e]
                                               (.preventDefault e)
                                               (e! (controller/->UserSelectedResult result))
                                               (set! (.. (.getElementById js/document "operating-areas") -value) label)
                                               (set! (.. (.getElementById js/document "results") -style -display) "none"))})
           (str label)])))]])

(defn- autocomplete-input
  [e! search-results]
  [forms/input :operating-areas (tr [:taxi-ui :pricing-details :add-operating-area])
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
     [link e! :taxi-ui/front-page nil {}
      [:span (stylefy/use-style styles/back-link-wrapper)
       [feather-icons/arrow-left] (tr [:taxi-ui :pricing-details :return-to-front-page])]]

     [:h2 (tr [:taxi-ui :pricing-details :page-main-title])]

     [:h3 (tr [:taxi-ui :pricing-details :sections :prices :title])]

     [:section (stylefy/use-style styles/flex-columns)
      [:div (stylefy/use-style (styles/flex-column 1))
       [pricing-input e! app 1 :start-price-daytime]
       [pricing-input e! app 3 :start-price-nighttime]
       [pricing-input e! app 5 :price-per-minute]]
      [:div (stylefy/use-style styles/spacer)]
      [:div (stylefy/use-style (styles/flex-column 1))
       [pricing-input e! app 2 :start-price-weekend]
       [pricing-input e! app 4 :price-per-kilometer]]]

     [:h3 (tr [:taxi-ui :pricing-details :sections :tools-and-services :title])]

     [:section (stylefy/use-style styles/flex-columns)
      [:div (stylefy/use-style (styles/flex-column 1))
       [pricing-input e! app 5 :accessibility-service-stairs]
       [pricing-input e! app 7 :accessibility-service-fare]]
      [:div (stylefy/use-style styles/spacer)]
      [:div (stylefy/use-style (styles/flex-column 1))
       [pricing-input e! app 6 :accessibility-service-stretchers]]]

     [:h3 (tr [:taxi-ui :pricing-details :sections :operating-areas :title])]
     ; pills here
     [:section
      [:div (stylefy/use-style styles/area-pills)
       (doall
         (for [operating-area (get-in app [:taxi-ui :pricing-details :price-information :operating-areas])]
           (let [saved? (some? (:ote.db.transport-service/id operating-area))
                 label (if saved?
                         (-> operating-area :ote.db.transport-service/description first :ote.db.transport-service/text)
                         (:ote.db.places/namefin operating-area))
                 opts  {:clickable (fn [e] (.preventDefault e)
                                     (e! (controller/->RemoveOperatingArea operating-area)))
                        :filled?   (:ote.db.transport-service/primary? operating-area)}]
             ^{:key (str "pill_" label)}
             [pill label opts])))]

      [:div (stylefy/use-style styles/flex-columns)
       [:div (stylefy/use-style (styles/flex-column 3))
        [autocomplete-input e! (get-in app [:taxi-ui :pricing-details :search :results])]]

       [:div (stylefy/use-style styles/spacer)]
       [:div (stylefy/use-style (styles/flex-column 1))
        ; TODO: the actions here shouldn't be directly hardcoded to specific path in app
        [forms/button
         :add-button
         (tr [:taxi-ui :pricing-details :sections :operating-areas :add-button])
         {:styles styles/secondary-button
          :type "button"
          :on-click (fn [e]
                      (e! (controller/->AddOperatingArea (get-in app [:taxi-ui :pricing-details :search :selected]))))}]]]
      [forms/button
       :save-button
       (tr [:taxi-ui :pricing-details :sections :operating-areas :save-button])
       {:styles styles/primary-button
        :type "button"
        :on-click (fn [e]
                    (e! (controller/->SavePriceInformation (get-in app [:taxi-ui :pricing-details :price-information]))))}]]]))