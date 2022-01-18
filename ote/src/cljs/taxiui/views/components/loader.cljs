(ns taxiui.views.components.loader
  "Dead simple full page spinner"
  (:require [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [ote.theme.colors :as colors]))

(def ^:private max-opacity "0.9")

(stylefy/keyframes "simple-animation"
                   [:from
                    {:transform "rotate(0)"}]
                   [:to
                    {:transform "rotate(360deg)"}])

(defn loader
  [_ _]
  (fn
    [app path]
    (let [show?  (-> (get-in app path) keys some?)]
      [:div (stylefy/use-style {:z-index          1985
                                :height           "100vh"
                                :width            "100vw"
                                :position         "absolute"
                                :background-color colors/basic-white
                                :display          (if show? "flex" "none")
                                :flex-direction   "column"
                                :opacity          max-opacity
                                :align-items      "center"
                                :justify-content  "center"})
       [feather-icons/loader {:style   {:animation "simple-animation 3s infinite linear"}
                              :width   "16em"
                              :height  "16em"}]
       (doall
         (for [label (-> (get-in app path) keys)]
           ^{:key (str "loading-" label)}
           [:h3 (tr [:taxi-ui :loader label])]))])))