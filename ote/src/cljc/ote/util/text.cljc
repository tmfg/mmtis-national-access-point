(ns ote.util.text
  (:require [clojure.string :as str]))

(defn shorten-text-to [max-length text]
  (str (subs text 0 max-length) "\u2026"))

(defn maybe-shorten-text-to [max-length text]
  (if (< max-length (count text))
    (shorten-text-to max-length text)
    text))

(defn rand-str
  "Input: Takes a length for resulting string,
  Output: returns a randomly generated ascii string or nil if arguent was invalid"
  [length]
  (when (pos? length)
    (let [ascii-codes (concat (range 48 58) (range 66 91) (range 97 123))]
      (clojure.string/join (repeatedly length #(char (rand-nth ascii-codes)))))))
