(ns ote.format
  "Yleisiä formatointiapureita"
  (:require #?(:clj [clj-time.format :as tf]
               :cljs [cljs-time.format :as tf])))

(def pvm-format (tf/formatter "dd.MM.yyyy"))

(defn pvm
  "Formatoi päivämäärän suomalaisessa muodossa pp.kk.vvvv"
  [date]
  (tf/unparse pvm-format date))
