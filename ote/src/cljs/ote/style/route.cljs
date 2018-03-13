(ns ote.style.route
  "Base styles for sea routes tool"
  (:require
    [stylefy.core :as stylefy :refer [use-style use-sub-style]]))

(def stepper {::stylefy/mode {:hover {:text-decoration "underline"}}})
