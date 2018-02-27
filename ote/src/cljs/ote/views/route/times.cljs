(ns ote.views.route.times
  "Route wizard: route drive times table"
  (:require [ote.app.controller.route :as rc]
            [ote.time :as time]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]))

(defn route-times-header [stop-sequence]
  [:thead
   [:tr
    (doall
     (map-indexed
      (fn [i {:keys [port-id port-name]}]
        ^{:key port-id}
        [:th {:colSpan 2
              :style {:vertical-align "top"}}
         [:div {:style {:display "inline-block"
                        :width "160px"
                        :overflow-x "hidden"
                        :white-space "pre"}} port-name]
         [:div {:style {:display "inline-block"
                        :float "right"
                        :position "relative"
                        :left 14
                        :top -20}}
          (when (< i (dec (count stop-sequence)))
            [ic/navigation-chevron-right])]])
      stop-sequence))]
   [:tr
    (doall
     (for [{:keys [port-id port-name]} stop-sequence]
       (list
        ^{:key (str port-id "-arr")}
        [:th {:style {:font-size "80%" :font-variant "small-caps"}} "tulo"]
        ^{:key (str port-id "-dep")}
        [:th {:style {:font-size "80%" :font-variant "small-caps"}} "lähtö"])))]])

(defn stop-times-row
  "Render a single row of stop times."
  [e! stop-count i {stops :stops :as time}]
  ^{:key i}
  [:tr
   (map-indexed
    (fn [j {:keys [arrival-time departure-time] :as stop}]
      (let [update! #(e! (rc/->EditStopTime i j %))
            style {:style {:padding-left "5px"
                           :padding-right "5px"
                           :width "75px"
                           :background-color (if (even? j)
                                               "#f4f4f4"
                                               "#fafafa")}}]
        (list
         (if (zero? j)
           ^{:key (str j "-first")}
           [:td style " - "]
           ^{:key (str j "-arr")}
           [:td style [form-fields/field {:type :time
                                          :update! #(update! {:arrival-time %})}
                       arrival-time]])
         (if (= j (dec stop-count))
           ^{:key (str j "-last")}
           [:td style " - "]
           ^{:key (str j "-dep")}
           [:td style [form-fields/field {:type :time
                                          :update! #(update! {:departure-time %})}
                       departure-time]]))))
    stops)])

(defn times [e! _]
  (e! (rc/->InitRouteTimes))
  (fn [e! {route :route :as app}]
    (let [stop-sequence (:stop-sequence route)
          stop-count (count stop-sequence)
          times (:times route)]

      [:div.route-times
       [:table {:style {:text-align "center"}}
        [route-times-header stop-sequence]
        [:tbody
         (doall (map-indexed (partial stop-times-row e! stop-count) times))]]
       [:div
        "Uuden vuoron lähtöaika: "
        [form-fields/field {:type :time
                            :update! #(e! (rc/->NewStartTime %))} (:new-start-time route)]
        [ui/raised-button {:primary true
                           :disabled (not (:new-start-time route))
                           :on-click #(e! (rc/->AddRouteTime))}
         "Lisää vuoro"]]])))
