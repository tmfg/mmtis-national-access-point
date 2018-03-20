(ns ote.util.fn
  "Utilities for working with functions")

(defn flip
  "Flip argument order of given 2 argument function."
  [fun]
  (fn [a b]
    (fun b a)))
