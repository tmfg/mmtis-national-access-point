(ns ote.ui.service-calendar
  "Component for selecting dates a service is run."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-time.core :as t]
            [cljs-time.format :as time-format]
            [ote.localization :refer [tr]]
            [ote.db.transport-service :as t-service]
            [stylefy.core :as stylefy]
            [ote.time :as time]))

(def day-style {:width 30
                :height 30
                :text-align "center"
                :border "solid 1px black"
                :cursor "pointer"})

(def no-day-style (merge day-style
                         {:background-color "lightGray"}))

(def selected-day-style (merge day-style
                               {:background-color "wheat"
                                :font-weight "bold"}))

(def week-separator-style
  {:background "repeating-linear-gradient(45deg, transparent, transparent 3px, #ccc 3px, #ccc 6px)"
   :width "6px"})

(defn month-days [year month]
  (let [first-date (t/first-day-of-the-month year month)
        days (t/number-of-days-in-the-month year month)]

    (map #(t/plus first-date (t/days %))
         (range days))))

(def week-days [:MON :TUE :WED :THU :FRI :SAT :SUN])

(defn- separate-weeks [[d & dates]]
  (when d
    (let [sunday? (= 7 (t/day-of-week d))]
      (cons d
            (if sunday?
              (cons ::week-separator
                    (lazy-seq (separate-weeks dates)))
              (lazy-seq (separate-weeks dates)))))))

(defn- fill-days [start-date n]
  (doall
   (map-indexed
    (fn [i day]
      (if (= ::week-separator day)
        ^{:key i}
        [:td.week-separator (stylefy/use-style week-separator-style)]
        ^{:key i}
        [:td (stylefy/use-style no-day-style)
         (t/day day)]))
     (separate-weeks (map #(t/plus start-date (t/days %))
                          (range n))))))

(defn- week-days-header []
  [:thead
   ;; week days
   [:tr
    [:th " "]
    (doall
     (map-indexed
      (fn [i week-day]
        (if (= week-day ::week-separator)
          ;; Separator for weeks
          ^{:key i}
          [:th " "]

          ;; Normal week day
          ^{:key i}
          [:th (tr [:enums ::t-service/day :short week-day])]))

      ;; Add week separators to repeating list of week days
      (apply concat
             (interleave (partition-all 7 (take 37 (cycle week-days)))
                         (repeat '(::week-separator))))))]])

(defn- month-name [month]
  ;; FIXME: use translation
  (case month
    1 "Tammi"
    2 "Helmi"
    3 "Maalis"
    4 "Huhti"
    5 "Touko"
    6 "Kesä"
    7 "Heinä"
    8 "Elo"
    9 "Syys"
    10 "Loka"
    11 "Marras"
    12 "Joulu"))

(defn- service-calendar-year [{:keys [selected-date? on-select]} year]
  [:div.service-calendar-year
   [:h3 year]
   [:table

    [week-days-header]

    [:tbody
     (doall
      (for [month (range 1 13)
            :let [start-date (t/first-day-of-the-month year month)
                  fill-days-before (dec (t/day-of-week start-date))
                  fill-days-after (- 37 (t/number-of-days-in-the-month year month)
                                     (dec (t/day-of-week start-date))) ]]
        ^{:key month}
        [:tr
         [:td (month-name (t/month start-date))]

         ;; Fill days, so that first week days align
         (fill-days (t/minus start-date (t/days fill-days-before))
                    fill-days-before)

         ;; Cell for each day in the month
         (doall
          (map-indexed
           (fn [i day]
             (if (= ::week-separator day)
               ^{:key i}
               [:td.week-separator (stylefy/use-style week-separator-style)]
               ^{:key i}
               [:td.day (merge
                         (stylefy/use-style (if (selected-date? day)
                                              selected-day-style
                                              day-style))
                         {:on-mouse-down #(do
                                            (.preventDefault %)
                                            (on-select day))
                          :on-mouse-over #(do
                                            (.preventDefault %)
                                            (when (pos? (.-buttons %))
                                              (on-select day)))})
                (t/day day)]))
           (separate-weeks (month-days year month))))

         ;; Fill days to fill out table
         (fill-days (t/plus (t/last-day-of-the-month year month) (t/days 1))
                    fill-days-after)]))]]])

(defn service-calendar
  "Service calendar component."
  [{:keys [selected-date? on-select] :as options}]
  (let [current-year (t/year (t/now))
        next-year (inc current-year)]
    [:div.service-calendar
     [service-calendar-year options current-year]
     [service-calendar-year options next-year]]))
