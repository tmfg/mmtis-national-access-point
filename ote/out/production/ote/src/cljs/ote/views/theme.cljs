(ns ote.views.theme
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]))

(defn theme
  "Wrap the given content in OTE Material UI theme provider."
  [content]
  [ui/mui-theme-provider
   {:mui-theme
    (get-mui-theme
      {:palette   {;; primary nav color - Also Focus color in text fields
                   :primary1-color (color :blue700)

                   ;; Hint color in text fields
                   :disabledColor  (color :grey900)

                   ;; canvas color
                   ;;:canvas-color  (color :lightBlue50)

                   ;; Main text color
                   :text-color     (color :grey900)
                   }

       :button    {:labelColor "#fff"}
       ;; Change drop down list items selected color
       :menu-item {:selected-text-color (color :blue700)}})}
   content])