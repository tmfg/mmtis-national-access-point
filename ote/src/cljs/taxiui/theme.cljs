(ns taxiui.theme
  "Shared styling and theming elements for entire Taxi UI"
  (:require [clojure.string :as str]))

(defn grid-template-areas
  "Small helper to produce properly quoted CSS grid template area string. Provide areas as 1D vector.
   - `areas` Grid of areas as vector"
  [styles areas]
  (assoc
    styles
    :grid-template-areas
    (->> areas
         (map
           (fn [row]
             (str "\"" row "\"")))
         (str/join "\n"))))

(defn breather-margin
  [styles]
  (assoc styles :margin "0.2rem 0rem 0.2rem 0rem"))

(defn breather-padding
  [styles]
  (assoc styles :padding "0.2em 0em 0.2em 0.2em"))

(def main-container
  "All pages should be wrapped by an element, ideally `[:main ...]` which uses this style."
  {:padding "0rem 0.8rem 0rem 0.8rem"})