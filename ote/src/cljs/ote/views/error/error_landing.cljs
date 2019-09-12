(ns ote.views.error.error-landing
  (:require [stylefy.core :as stylefy]
            [ote.style.buttons :as style-buttons]
            [ote.localization :refer [tr]]))

(defn error-landing-vw [{:keys [error-landing]}]
  [:div
   [:h2 (or (:title error-landing)
            (tr [:error-landing :resource-not-found]))]
   [:div (:desc error-landing)]
   [:br]
   [:div
    [:a (merge {:href (str "#/")
                :id "btn-go-to-frontpage"}
               (stylefy/use-style style-buttons/primary-button))
     (tr [:error-landing :go-to-frontpage])]]])
