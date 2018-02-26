(ns ote.views.route.times
  "Route wizard: route drive times table"
  (:require [ote.app.controller.route :as rc]
            [ote.time :as time]
            [ote.ui.form-fields :as form-fields]
            [cljs-react-material-ui.reagent :as ui]))

(defn times [e! {route :route :as app}]
  (let [stop-sequence (:stop-sequence route)
        first-departure (:departure-time (first stop-sequence))
        first-arrival (:arrival-time (last stop-sequence))
        route-duration (time/minutes-elapsed
                        first-departure
                        first-arrival)
        stop-count (count stop-sequence)
        times (rc/route-times route)]

    [:div.route-times
     [:table {:style {:text-align "center"}}
      [:thead
       [:tr
        (doall
         (for [{:keys [port-id port-name]} stop-sequence]
           ^{:key port-id}
           [:th {:colSpan 2} port-name]))]
       [:tr
        (doall
         (for [{:keys [port-id port-name]} stop-sequence]
           (list
            ^{:key (str port-id "-arr")}
            [:th {:style {:font-size "80%" :font-variant "small-caps"}} "tulo"]
            ^{:key (str port-id "-dep")}
            [:th {:style {:font-size "80%" :font-variant "small-caps"}} "lähtö"])))]]
      [:tbody
       (doall
        (map-indexed
         (fn [i {stops :stops :as time}]
           ^{:key i}
           [:tr
            (map-indexed
             (fn [j {:keys [arrival-time departure-time] :as stop}]
               (let [style {:style {:padding-left "5px"
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
                    [:td style [form-fields/field {:type :time} arrival-time]])
                  (if (= j (dec stop-count))
                    ^{:key (str j "-last")}
                    [:td style " - "]
                    ^{:key (str j "-dep")}
                    [:td style [form-fields/field {:type :time} departure-time]])
                  )))
             stops)])
         times))]]
     [:div
      "Uuden vuoron lähtöaika: "
      [form-fields/field {:type :time
                          :update! #(e! (rc/->NewStartTime %))} (:new-start-time route)]
      [ui/raised-button {:primary true
                         :disabled (not (:new-start-time route))
                         :on-click #(e! (rc/->AddRouteTime))}
       "Lisää vuoro"]
      #_[:div "tässä vuorot, kestää " route-duration " minuuttia"]]]))
