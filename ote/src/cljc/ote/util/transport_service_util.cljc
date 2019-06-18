(ns ote.util.transport-service-util
  "Util functions for transport service")

(def week-day-order {:MON 1, :TUE 2, :WED 3, :THU 4, :FRI 5, :SAT 6, :SUN 7})

;; Reorder week days mon 1, tue 2, wed 3, thu 4, fri 5, sat 6, sun 7
(defn reorder-week-days
  "Receives set of week days. Returns list of week days to make sure that ordering remains correct."
  [week-day-set]
  (let [ordered-days (sort (mapv #(% week-day-order) week-day-set))
        ordered-days->list (map
                             (fn [od]
                               (first
                                 (first
                                   ;; Return map which value equals ordered week day number
                                   (filter
                                     #(when (= od (second %)) %)
                                     week-day-order))))
                             ordered-days)]                  ;; MAP through ordered service-week
    ordered-days->list))