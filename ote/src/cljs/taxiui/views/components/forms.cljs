(ns taxiui.views.components.forms
  (:require [taxiui.theme :as theme]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr]]
            [ote.theme.colors :as colors]
            [clojure.string :as str]
            [tuck.core :as tuck]
            [ote.communication :as comm]))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(def ^:private scaling-and-borders {:border-width  "0.0625em"
                                    :border-style  "solid"
                                    :border-color  colors/basic-gray
                                    :border-radius "0.3em"
                                    :height        "3rem"
                                    :font-size     "1.5em"
                                    :width         "100%"
                                    :box-sizing    "border-box"
                                    ::stylefy/mode {:focus {:outline-width "0"}}})

(def ^:private input-element (-> (deep-merge scaling-and-borders
                                             {::stylefy/mode {"::placeholder" {:color   colors/accessible-black
                                                                               :opacity 1}}})
                                 (theme/breather-padding)))

(def ^:private button-element (-> (merge scaling-and-borders
                                         {:border-color colors/basic-black
                                          :background-color colors/basic-white})
                                  (theme/breather-padding)))

(defn- form-element
  "Creates an accessible form element container with fancy label and optional content."
  [el id label styles props inner-content post-content]
  [:div
   [:h5
    (cond
      (nil? label) [:br]  ; this simulates empty header line, which aligns form elements when placed together
      (vector? label) [:label {:for id} (first label) [:br] (second label)]
      (string? label) [:label {:for id} label])]
   (let [extra-styles (:styles props)
         all-styles   (deep-merge styles extra-styles)
         props        (merge (stylefy/use-style all-styles) (dissoc props :styles) {:id id})]
     (if (some? inner-content)
       [el props inner-content]
       [el props]))
   post-content])

(defn input
  ([id label] (input id label nil nil))
  ([id label props post-content]
   (form-element :input id label input-element props nil post-content)))

(defn button
  ([id label] (button id label nil))
  ([id label props]
   (form-element :button id nil button-element props label nil)))

(defn simple-input
  "Simple input-box only input, defaults to text, is styled similarly to all other inputs on the site."
  [id props]
  (let [extra-styles (:styles props)
        all-styles   (deep-merge input-element extra-styles)
        props        (merge (stylefy/use-style all-styles) (dissoc props :styles) {:id id})]
    [:input props]))


(def autocomplete-result {:white-space   "nowrap"
                          :overflow      "hidden"
                          :text-overflow "ellipsis"
                          :font-size     "1.5em"
                          :padding       "0.2em 0 0.2em 0.2em"
                          :height        "1.5em"
                          :line-height   "1.5em"
                          ::stylefy/mode {:hover {:color            colors/primary-text-color
                                                  :background-color colors/primary-background-color}}})

(tuck/define-event UserSelectedResult [selected-fn path result]
  {}
  (tuck/fx
    (assoc-in app path result)
    #(selected-fn result)))

(tuck/define-event SearchResponse [results result-fn path]
  {}
  (assoc-in app (conj path :results) (result-fn results)))

(tuck/define-event Search [search-fn result-fn path term]
  {}
  (let [{:keys [method url params]} (search-fn term)]
    (case method
      :get  (comm/get! url {:on-success (tuck/send-async! ->SearchResponse result-fn path)})
      :post (comm/post! url params {:on-success (tuck/send-async! ->SearchResponse result-fn path)})))
  app)

(defn- autocomplete-results
  [e! app selected-fn autocomplete-input-id result-container-id results-id storage-path]
  [:div (stylefy/use-style {:position "relative"
                            :z-index  1000
                            :height   "0px"
                            ::stylefy/mode {:hover {:cursor "pointer"}}}
                           {:id       result-container-id
                            :tabIndex "-1"})
   [:ul {:style {:position         "absolute"
                 :list-style       "none"
                 :margin           "0"
                 :top              "-.5em"
                 :background-color "white"
                 :border-style     "none solid solid"
                 :border-width     "1px"
                 :border-radius    "0 0 .5em .5em"
                 :border-color     colors/basic-gray
                 :display          "none"}
         :id results-id}
    (let [results (get-in app (conj storage-path :results))]
      (js/console.log (str "search results >> " results))
      (doall
        (for [[index result] (map-indexed vector results)
              :when (some? results)]
          (let [{label :label} result]  ; TODO: elsewhere this is :ote.db.places/namefin
            ^{:key (str "result_" index)}
            [:li (stylefy/use-style autocomplete-result
                                    {:on-click (fn [e]
                                                 (.preventDefault e)
                                                 (e! (->UserSelectedResult selected-fn (conj storage-path :selected) result))
                                                 (set! (.. (.getElementById js/document autocomplete-input-id) -value) label)
                                                 (set! (.. (.getElementById js/document results-id) -style -display) "none"))})
             (str label)]))))]])

(def search (goog.functions.debounce (fn [e! search-fn result-fn path term]
                                           (e! (->Search search-fn result-fn path term))) 500))

(defn autocomplete-input
  [e! app id storage-path localization-path search-fn result-fn selected-fn]
  (let [autocomplete-input-id (str id "-autocomplete")
        result-container-id   (str id "-result-container")
        results-id            (str id "-results")]
    [input autocomplete-input-id (tr (conj localization-path :add-operating-area))
     {:on-click (fn [e]
                  (.preventDefault e)
                  (letfn [(pixels [v] (js/parseFloat (subs v 0 (- (count v) 2))))]
                    (let [styles (js/getComputedStyle (.. e -target))
                          results (.getElementById js/document results-id)
                          border-widths (+ (pixels (. styles -borderLeftWidth))
                                           (pixels (. styles -borderRightWidth)))
                          parent-width (- (pixels (. styles -width))
                                          border-widths)]
                      (set! (.. results -style -width) (str parent-width "px"))
                      (set! (.. results -style -display) "block")
                      false)))
      :on-blur  (fn [e]
                  (when-not (.contains (.. e -currentTarget -parentElement) (.. e -relatedTarget))
                    (set! (.. (.getElementById js/document results-id) -style -display) "none")))
      :on-input (fn [e]
                  (search e! search-fn result-fn storage-path (.. e -target -value)))}
     [autocomplete-results e! app selected-fn autocomplete-input-id result-container-id results-id storage-path]]))