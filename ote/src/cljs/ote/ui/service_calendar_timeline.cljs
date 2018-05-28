(ns ote.ui.service-calendar-timeline
  ""
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-time.core :as t]
            [cljs-time.format :as time-format]
            [ote.localization :as lang]
            [ote.db.transport-service :as t-service]
            [stylefy.core :as stylefy]
            [ote.time :as time]
            [cljs-react-material-ui.icons :as ic]
            [cljs.core.async :refer [<! put! chan timeout]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]
            [goog.events.MouseWheelHandler.EventType :as MEventType]))

(def base-day-style {:width 96
                     :height 34
                     :line-height "34px"
                     :text-align "center"
                     :vertical-align "middle"
                     :border "solid 1px black"
                     :cursor "pointer"
                     :user-select "none"})

(def no-day-style (merge base-day-style
                         {:font-size "75%"
                          :color "gray"
                          :background-color "lightGray"}))

(def week-header-item-style {:width 96
                             :height 34
                             :text-align "center"})
(def week-count-style {:width 60
                       :height 34
                       :text-align "center"})

(def selected-day-style (merge base-day-style
                               {:background-color "wheat"
                                :font-weight "bold"}))

(def static-holidays
  [[1 1] [6 1] [1 5] [6 12] [25 12] [26 12]])

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
        ^{:key i}
        [:td (stylefy/use-style no-day-style)
         (t/day day)])
      (separate-weeks (map #(t/plus start-date (t/days %))
                           (range n))))))

(defn- week-days-header []
  ;; week days
  [:tr
   [:td (stylefy/use-style week-count-style) "Vko"]
   (doall
     (map-indexed
       (fn [i week-day]
         (when-not (= week-day ::week-separator)
           ;; Normal week day
           ^{:key i}
           [:td (stylefy/use-style week-header-item-style)
            (lang/tr [:enums ::t-service/day :short week-day])]))

       ;; Add week separators to repeating list of week days
       (apply concat
              (interleave (partition-all 7 week-days)
                          (repeat '(::week-separator))))))])

(defn- all-days [year]
  (apply concat
         (for [month (range 1 13)]
           (month-days year month))))

(defn- month-name [month]
  (let [lang (.get (goog.net.Cookies. js/document) "finap_lang" "fi")]
    (.toLocaleString (doto (js/Date.) (.setMonth (- month 1))) lang #js {:month "short"})))


(def cur-zoom (r/atom 0))

(defn- handle-wheel [e]
  (.preventDefault e)

  (-> e (.-deltaY)))

(defn- events->chan [el event-type c]
  (events/listen el event-type #(put! c %)) c)

(defn scroll-chan [el]
  (events->chan (events/MouseWheelHandler. (or el js/window))
                MEventType/MOUSEWHEEL (chan 1 (map handle-wheel))))

(defn listen-scroll! [el]
  (let [chan (scroll-chan el)]
    (go-loop []
             (let [new-scroll (* -1 (<! chan))
                   zoom (max (+ @cur-zoom (* new-scroll 0.05)) 0)]
               (reset! cur-zoom zoom))
             (recur))))

(defn handle-mouse-down [e]
  (listen-scroll! (.-target e)))

(defn svg-bars [items bar-width x-scale handle-val bar-style]
  [:g
   (doall
     (map-indexed
       (fn [i val]
         (let [w (* x-scale bar-width)
               x (* (+ w 5) i)]
           ^{:key (str "svg-bar-" i)}
           [:svg {:width w :height "100%" :x x :y 0}
            ^{:key (str "svg-bar-rect" i)}
            [:rect {:x 0 :y 0 :width w :height 20 :fill (if bar-style
                                                          (bar-style val)
                                                          "green")}]
            ^{:key (str "svg-bar-line" i)}
            [:line {:x1 "50%" :y1 "40%" :x2 "50%" :y2 "20"
                    :stroke "black" :strokeWidth "1"}]
            ^{:key (str "svg-bar-label-box" i)}
            [:rect {:x 0 :y "40%" :width 50 :height 20 :fill "#fff"}]
            ^{:key (str "svg-bar-label" i)}
            [:text {:x "50%" :y "50%" :dy "0" :text-anchor "middle"} (handle-val val)]]))
       items))])

(defn month-bars [months bar-width bar-scale]
  [svg-bars months bar-width bar-scale #(identity %)])

(defn week-bars [weeks bar-width bar-scale]
  [svg-bars weeks bar-width bar-scale #(t/week-number-of-year (first %)) (constantly "blue")])

(defn day-bars [days bar-width bar-scale day-style]
  [svg-bars days bar-width bar-scale #(str (t/day %) "." (t/month %)) day-style])

(defn service-calendar-year [{:keys [selected-date? on-select on-hover
                                     day-style]} year]
  (let [day-style (or day-style (constantly nil))
        weeks (partition-by (complement #{::week-separator})
                            (separate-weeks (all-days year)))
        weeks (filter #(not= ::week-separator (first %)) weeks)
        holidays (map #(t/date-time year (second %) (first %)) static-holidays)
        cur-zoom @cur-zoom
        chart-height 200
        x-scale (max (+ 1 cur-zoom) 1)]
    [:div.service-calendar-year
     [:h3 year]
     [:div {:style {:display "flex" :align-items "center" :justify-content "center"}}
      [:div {:style {:width "100%" :border "solid 1px black"}
             :on-mouse-down handle-mouse-down}
       [:svg {:id "service-calendar-timeline" :width "100%" :height chart-height
              :style {:transition "1s all"}}
        [:g {:transform (str "translate(0," (- (/ chart-height 2) 10) ")")}
         (cond
           (< x-scale 2)
           [month-bars (range 1 13) 100 x-scale]
           (< x-scale 4)
           [week-bars weeks 50 x-scale]
           (< x-scale 6)
           [day-bars (flatten weeks) 25 x-scale
            ;; Currently, we'll use background color only from the style to fill rects.
            (fn [day]
              (:background-color (day-style day false)))])]]]]]))
