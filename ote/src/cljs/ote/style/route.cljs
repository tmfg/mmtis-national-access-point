(ns ote.style.route
  "Base styles for sea routes tool"
  (:require
    [stylefy.core :as stylefy :refer [use-style use-sub-style]]))

(def stepper {::stylefy/mode {:hover {:text-decoration "underline"}}})
(def exception-icon-size {:width 24 :height 24})
(def exception-icon (merge {:color "#000"} exception-icon-size))
(def selected-exception-icon (merge {:color "#2D75B4"} exception-icon-size))
