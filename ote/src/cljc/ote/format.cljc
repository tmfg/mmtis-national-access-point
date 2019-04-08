(ns ote.format
  "Yleisiä formatointiapureita"
  (:require #?(:clj [clj-time.format :as tf]
               :cljs [cljs-time.format :as tf])))

(def pvm-format (tf/formatter "dd.MM.yyyy"))

(defn pvm
  "Formatoi päivämäärän suomalaisessa muodossa pp.kk.vvvv"
  [date]
  (tf/unparse pvm-format date))


(defn postal-code-at-end
  "If place is from postal code registry, it is shown as 89660 Takalo. These are swapped to Takalo 89660 etc.
  Places without postal codes are left as is"
  [place]
  (let [[_ postal-code _ place] (re-find #"(\d*)(\s*)(.+)" place)]
    (if (empty? postal-code)
      place
      (str place " " postal-code))))
