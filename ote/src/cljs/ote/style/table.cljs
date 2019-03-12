(ns ote.style.table
  "Table styles."
  (:require
    [stylefy.core :as stylefy :refer [use-style use-sub-style]]
    [ote.theme.colors :as colors]))


(def no-rows-message {:line-height "2rem"
                      :padding-left "1rem"})