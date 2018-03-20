(ns ote.util.fn
  "Utilities for working with functions")

(defn flip [fun]
  (fn [a b]
    (fun b a)))
