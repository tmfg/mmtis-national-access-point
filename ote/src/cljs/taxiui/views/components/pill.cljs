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
(defn pill
  ([label] (pill label nil))
  ([label {:keys [filled? clickable]
           :or   {filled? false clickable nil}}]

   (let [root    [:span {:style {:display "flex"
                                 :align-items "center"}}]
         link    [:a {:href "#"
                      :on-click clickable}]
         content [[:span (stylefy/use-style (if filled? area-pill-filled area-pill)) label]
                  (when clickable [feather-icons/x-circle {:stroke colors/accessible-red}])]]
     (if clickable
       (conj root (into link content))
       (into root content)))))
