(ns ote.style.tabs
  "Styles for own tabs component"
  (:require [stylefy.core :as stylefy]))


(def tab {:font-weight "bold"
         :font-size "1em"
         :display "inline"
         :padding "0px 10px 10px 10px"
         ::stylefy/mode {:hover {:border-bottom "2px solid blue"}}})
(def tab-selected (merge tab {:border-bottom "2px solid blue" :color "blue"}))