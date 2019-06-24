(ns ote.util.fn
  "Utilities for working with functions"
  (:require
    [clojure.spec.alpha :as spec]))

(defn flip
  "Flip argument order of given 2 argument function."
  [fun]
  (fn [a b]
    (fun b a)))

;; Output: nil if form `f` validates against `s`, spec explain error string if not.
(defn form-validation-error-str [s f]
  (when-not (spec/valid? s f)
    (spec/explain-str s f)))
