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


(stylefy/keyframes "fade-in"
                   [:from
                    {:opacity 0}]
                   [:to
                    {:opacity 1}])


(stylefy/keyframes "fade-out"
                   [:from
                    {:opacity 1}]
                   [:to
                    {:opacity 0}])

(defn- visible
  [speed]
  {:opacity 1
   :animation (str "fade-in " (/ speed 1000) "s ease-in")})

(defn- hidden
  [speed]
  {:opacity 0
   :animation (str "fade-out " (/ speed 1000) "s ease-out")})

(def ^:private info-progress {:color            colors/primary-text-color
                              :background-color colors/primary-background-color
                              :border-radius    "0.3em"
                              :padding          "0 0.3em 0 0.3em"
                              :margin-top       "0.3em"
                              :text-align       "center"})

(def ^:private info-successful {:color            colors/primary-text-color
                                :background-color colors/accessible-green
                                :border-radius    "0.3em"
                                :padding          "0 0.3em 0 0.3em"
                                :margin-top       "0.3em"
                                :text-align       "center"})

(defn loader
  [_ _]
  (fn
    [app path]
    (let [show?  (-> (get-in app path) keys some?)]
      [:div (stylefy/use-style {:z-index          1985
                                :height           "100vh"
                                :width            "100vw"
                                :position         "fixed"
                                :background-color colors/basic-white
                                :display          (if show? "flex" "none")
                                :visibility       (if show? "visible" "hidden")
                                :flex-direction   "column"
                                :opacity          max-opacity
                                :align-items      "center"
                                :justify-content  "center"
                                :transition       "opacity 5s ease-in-out"})
       [feather-icons/loader {:style   {:animation "simple-animation 3s infinite linear"}
                              :width   "16em"
                              :height  "16em"}]
       (doall
         (for [k (-> (get-in app path) keys)]
           (let [{:keys [hits data]} (get-in app (conj path k))]
             ^{:key (str "loading-" k)}
             [:h3
              (stylefy/use-style (merge (if (= :fade-in (:phase data))
                                          (visible (:speed data))
                                          (hidden (:speed data)))
                                        (case (:type data)
                                          :info-progress   info-progress
                                          :info-successful info-successful
                                          nil)))
              (tr [:taxi-ui :loader k])])))])))