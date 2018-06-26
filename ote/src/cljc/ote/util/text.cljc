(ns ote.util.text
  (:require [clojure.string :as str]))

(defn shorten-text-to [max-length text]
  (str (subs text 0 max-length) "\u2026"))

(defn maybe-shorten-text-to [max-length text]
  (if (< max-length (count text))
    (shorten-text-to max-length text)
    text))
