(ns taxiui.views.components.pill
  "Pill is a visual pill-like element which can either display a value or have additional controls for removing the
  pill and by proxy its contents."
  (:require [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]
            [re-svg-icons.feather-icons :as feather-icons]))

(def ^:private area-pill {:border        (str "0.0625em solid " colors/primary-background-color)
                          :border-radius "1.6em"
                          :padding       "0.3em 1em 0.3em 1em"})

(def ^:private area-pill-filled (merge area-pill
                                       {:background-color colors/primary-background-color
                                        :color            colors/primary-text-color}))

(def ^:private pill-link {:text-decoration "none"})

(def ^:private pill-button {:margin-left  "0.25em"
                            :margin-right "0.25em"})

(defn pill
  ([label] (pill label nil))
  ([label {:keys [filled? clickable]
           :or   {filled? false clickable nil}}]

   (let [root-styles  {:display       "flex"
                       :align-items   "center"
                       :margin-top    "0.25em"
                       :margin-bottom "0.25em"}
         pill         [:span (stylefy/use-style (if filled? area-pill-filled area-pill)) label]
         button       [feather-icons/x-circle {:stroke colors/accessible-red
                                               :style  pill-button}]]

     (if clickable
       [:a (stylefy/use-style (merge root-styles pill-link)
                              {:href     "#"
                               :on-click clickable})
         pill
         button]
       [:span (stylefy/use-style root-styles) pill]))))
