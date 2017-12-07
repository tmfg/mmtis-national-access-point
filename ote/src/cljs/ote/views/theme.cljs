(ns ote.views.theme
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [ote.ui.debug :as debug]
            [ote.style.base :as style-base]
            [reagent.core :as r]))

(defn- flash-message [msg]
  [ui/snackbar {:open (boolean msg)
                :message (or msg "")
                :style style-base/flash-message
                :auto-hide-duration 5000}])

(defonce debug-state-toggle-listener (atom false))

(defn- debug-state [_]
  (let [visible? (r/atom false)]
    (when-not @debug-state-toggle-listener
      (reset! debug-state-toggle-listener true)
      (.addEventListener
       js/window "keypress"
       (fn [e]
         (when (or (and (.-ctrlKey e) (= "d" (.-key e)))
                   (and (.-ctrlKey e) (= "b" (.-key e))))
           (swap! visible? not)))))
    (fn [app]
      [:span
       (when @visible?
         [:div.row
          [debug/debug app]])])))

(defn theme
  "App container that sets the theme and common elements like flash message."
  [{msg :flash-message :as app} content]
  [ui/mui-theme-provider
   {:mui-theme
    (get-mui-theme
      {:palette   {;; primary nav color - Also Focus color in text fields
                   :primary1-color (color :blue700)

                   ;; Hint color in text fields
                   :disabledColor  (color :grey900)

                   :shadowColor (color :grey900)

                   ;; canvas color
                   ;;:canvas-color  (color :lightBlue50)

                   ;; Main text color
                   :text-color     (color :grey900)
                   }

       :button    {:labelColor "#fff"}
       ;; Change drop down list items selected color
       :menu-item {:selected-text-color (color :blue700)}})}
   [:span
    (when msg
      [flash-message msg])
    content
    [debug-state app]]])
