(ns ote.ui.service-calendar-timeline
  ""
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-time.core :as t]
            [cljs-time.format :as time-format]
            [ote.localization :as lang]
            [ote.db.transport-service :as t-service]
            [stylefy.core :as stylefy]
            [ote.time :as time]
            [cljs-react-material-ui.icons :as ic]
            [cljs.core.async :refer [<! put! chan timeout sliding-buffer]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]
            [goog.events.MouseWheelHandler.EventType :as MEventType]))

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

(defn- all-days [year]
  (apply concat
         (for [month (range 1 13)]
           (month-days year month))))

(defn- month-name [month]
  (let [lang (.get (goog.net.Cookies. js/document) "finap_lang" "fi")]
    (.toLocaleString (doto (js/Date.) (.setMonth (- month 1))) lang #js {:month "short"})))


(defn- handle-wheel [e]
  (.preventDefault e)

  (.-deltaY e))

(defn- handle-mouse-move [e]
  (.preventDefault e)
  (.-clientX e))

(defn handle-mouse-down [e]
  (let [target (.-target e)
        mouse-x (.-clientX e)
        client-rect (.getBoundingClientRect target)
        client-left (.-left client-rect)
        start-x (- mouse-x client-left)]
    start-x))

(defn- handle-mouse-up [e]
  (.preventDefault e)
  (.-clientX e))

(defn- events->chan [el event-type c]
  (events/listen el event-type #(put! c %)) c)

(defn scroll-chan [el]
  (let [c (chan 1 (map handle-wheel))]
    (events->chan (events/MouseWheelHandler. (or el js/window))
                  MEventType/MOUSEWHEEL c)
    c))

(defn mouse-down-chan [el]
  (let [c (chan (sliding-buffer 1) (map handle-mouse-down))]
    (events->chan (or el js/window)
                  EventType/MOUSEDOWN c)
    c))

(defn mouse-move-chan [el]
  (let [c (chan (sliding-buffer 1) (map handle-mouse-move))]
    (events->chan (or el js/window)
                  EventType/MOUSEMOVE c)
    c))

(defn mouse-up-chan [el]
  (let [c (chan 1 (map handle-mouse-up))]
    (events->chan (or el js/window)
                  EventType/MOUSEUP c)
    c))


;; Current zoom level
(def cur-zoom (r/atom 0))
;; Start position of the mouse (in viewport) when dragging
(def drag-start-offset (r/atom 0))
;; Drag offset of the svg view-box
(def view-offset (r/atom {:cur 0 :prev 0}))


(defn listen-scroll! [el]
  (let [chan (scroll-chan el)]
    (go-loop []
             (let [new-scroll (- (<! chan))
                   prev-zoom @cur-zoom
                   ;; Use bigger zoom increments on bigger zoom levels to achieve more linear zoom-in
                   zoom (+ prev-zoom (* new-scroll (+ 1 prev-zoom) 0.01))]
               (reset! cur-zoom (min (max zoom 0) 40))
               (recur)))))

(defn listen-mouse-move! [el]
  (let [mouse-down (mouse-down-chan el)
        mouse-move (mouse-move-chan el)
        mouse-up (mouse-up-chan js/document)]
    (go-loop []
             (loop []
               (alt! [mouse-down]
                     ([start-offset]
                       (reset! drag-start-offset start-offset))))
             (loop []
               (alt! [mouse-up]
                     (swap! view-offset assoc :prev (:cur @view-offset))

                     [mouse-move]
                     ([mouse-x]
                       (let [client-rect (.getBoundingClientRect el)
                             client-left (.-left client-rect)
                             x (- mouse-x client-left)
                             delta (- @drag-start-offset x)
                             prev-offset (:prev @view-offset)
                             zoom-factor (+ @cur-zoom 1)]
                         (swap! view-offset assoc :cur (+ prev-offset (/ delta zoom-factor)))
                         (recur)))))
             (recur))))




(defn svg-bars [items offset bar-width handle-val bar-style]
  [:g
   (doall
     (map-indexed
       (fn [i val]
         (let [w (- bar-width 4)
               x (- (* (+ w 4) i) offset)]
           ^{:key (str "svg-bar-" i)}
           [:svg {:style {:user-select "none"}
                  :width w :height "100%" :x x :y 0}
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

(defn month-bars [months offset bar-width]
  [svg-bars months offset bar-width #(month-name %)])

(defn week-bars [weeks offset bar-width]
  [svg-bars weeks offset bar-width (fn [val]
                                     (str "Vko " (t/week-number-of-year (first val))))
   (constantly "blue")])

(defn day-bars [days offset bar-width day-style]
  [svg-bars days offset bar-width #(str (t/day %) "." (t/month %)) day-style])

(defn timeline [weeks width height day-style]
  (let [cur-zoom @cur-zoom
        view-offset (:cur @view-offset)
        bar-offset (* cur-zoom (+ view-offset (/ width 2)))
        x-scale (max (+ 1 cur-zoom) 1)]
    [:svg {:xmlns "http://www.w3.org/2000/svg"
           :style {:width "100%" :height "100%"}
           :id "service-calendar-timeline"
           :view-box (str view-offset " 0 " width " " height)}
     [:g {:transform (str "translate(0," (- (/ height 2) 10) ")")}
      (cond
        (< x-scale 4)
        [month-bars (range 1 13) bar-offset (* x-scale
                                               ;; width / 12 months
                                               (/ width 12))]
        (< x-scale 12)
        [week-bars weeks bar-offset (* x-scale
                                       ;; Un-accurately, width / weeks
                                       (/ width 52))]
        (> x-scale 12)
        [day-bars (flatten weeks) bar-offset (* x-scale
                                                ;; Un-accurately, width / 365, not taking in
                                                ;; account any exceptions.
                                                (/ width 365))
         ;; Currently, we'll use background color only from the style to fill rects.
         (fn [day]
           (:background-color (day-style day false)))])]]))

(defn service-calendar-year [{:keys [selected-date? on-select on-hover
                                     day-style]} year]
  (let [dimensions (r/atom {:width nil :height nil})]
    (r/create-class
      {:component-did-mount (fn [this]
                              (let [node (r/dom-node this)
                                    wrapper-el (first (array-seq (.getElementsByClassName
                                                                   node "service-calendar-timeline-wrapper")))]
                                (swap! dimensions assoc
                                       :width (.-clientWidth wrapper-el)
                                       :height (.-clientHeight wrapper-el))
                                (listen-scroll! wrapper-el)
                                (listen-mouse-move! wrapper-el)))
       :reagent-render
       (fn []
         (let [day-style (or day-style (constantly nil))
               weeks (partition-by (complement #{::week-separator})
                                   (separate-weeks (all-days year)))
               weeks (filter #(not= ::week-separator (first %)) weeks)]
           [:div.service-calendar-year
            [:h3 year]
            [:div {:style {:display "flex" :align-items "center" :justify-content "center"}}
             [:div {:class "service-calendar-timeline-wrapper"
                    :style {:width "100%" :height "200px"
                            :user-select "none" :border "solid 1px black"}}
              (when (:width @dimensions)
                [timeline weeks (:width @dimensions) (:height @dimensions) day-style])]]]))})))
