(ns taxiui.views.components.devtools
  "Development mode tools and other niceties."
  (:require [reagent.core :as r]
            [datafrisk.core :as df]
            [clojure.string :as str]
            [ote.theme.colors :as colors]
            [stylefy.core :as stylefy]))

(defonce test-env? (let [host (.-host (.-location js/document))]
                     (or (str/includes? host "test")
                         (str/includes? host "localhost"))))

(defonce debug-visible? (r/atom (not= -1 (.indexOf js/document.location.host "localhost"))))

(defn debug-state [app]
  (when @debug-visible?
     [df/DataFriskShell app]))

(defn env-warning []
  (when test-env?
    [:div
     [:h5 (stylefy/use-style {:color      colors/basic-black
                              :background (str "repeating-linear-gradient(135deg,"
                                               colors/dark-yellow  ","
                                               colors/dark-yellow  " 20px,"
                                               colors/basic-yellow " 20px,"
                                               colors/basic-yellow " 40px)")
                              :text-align "center"})
      "Tämä on testipalvelu!"]]))