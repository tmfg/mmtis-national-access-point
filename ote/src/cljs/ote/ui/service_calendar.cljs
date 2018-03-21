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

(def base-day-style {:width 30
                     :height 30
                     :text-align "center"
                     :border "solid 1px black"
                     :cursor "pointer"
                     :user-select "none"})

(def no-day-style (merge base-day-style
                         {:font-size "75%"
                          :color "gray"
                          :background-color "lightGray"}))

(def selected-day-style (merge base-day-style
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
    1 (tr [:route-wizard-page :short-month-names :JAN])
    2 (tr [:route-wizard-page :short-month-names :FEB])
    3 (tr [:route-wizard-page :short-month-names :MAR])
    4 (tr [:route-wizard-page :short-month-names :APR])
    5 (tr [:route-wizard-page :short-month-names :MAY])
    6 (tr [:route-wizard-page :short-month-names :JUN])
    7 (tr [:route-wizard-page :short-month-names :JUL])
    8 (tr [:route-wizard-page :short-month-names :AUG])
    9 (tr [:route-wizard-page :short-month-names :SEP])
    10 (tr [:route-wizard-page :short-month-names :OCT])
    11 (tr [:route-wizard-page :short-month-names :NOV])
    12 (tr [:route-wizard-page :short-month-names :DEC])))

(defn- service-calendar-year [{:keys [selected-date? on-select
                                      day-style]} year]
  (let [day-style (or day-style (constantly nil))]
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
                 [:td.day
                  (let [selected? (selected-date? day)]
                    (merge
                     (stylefy/use-style
                      (merge (if selected?
                               selected-day-style
                               base-day-style)
                             (day-style day selected?)))
                     {:on-mouse-down #(do
                                        (.preventDefault %)
                                        (on-select day))
                      :on-mouse-over #(do
                                        (.preventDefault %)
                                        (when (pos? (.-buttons %))
                                          (on-select day)))}))
                  (t/day day)]))
             (separate-weeks (month-days year month))))

           ;; Fill days to fill out table
           (fill-days (t/plus (t/last-day-of-the-month year month) (t/days 1))
                      fill-days-after)]))]]]))

(defn service-calendar
  "Service calendar component."
  [{:keys [selected-date? on-select] :as options}]
  (let [current-year (t/year (t/now))
        next-year (inc current-year)]
    [:div.service-calendar
     [service-calendar-year options current-year]
     [service-calendar-year options next-year]]))
