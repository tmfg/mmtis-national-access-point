(ns ote.views.route.trips
  "Route wizard: route trips table"
  (:require
    [ote.app.controller.route.route-wizard :as rw]
    [ote.time :as time]
    [ote.ui.form-fields :as form-fields]
    [cljs-react-material-ui.reagent :as ui]
    [cljs-react-material-ui.icons :as ic]
    [ote.db.transit :as transit]
    [stylefy.core :as stylefy]
    [ote.style.route :as style-route]
    [reagent.core :as r]
    [ote.ui.common :as common]
    [ote.localization :refer [tr tr-key]]

    ;; Calendar subview
    [ote.views.route.service-calendar :as route-service-calendar]
    [ote.style.form :as style-form]))

(defn exception-icon [e! stop-type drop-off-type pickup-type stop-idx trip-idx]
  [:div
   [ui/icon-menu
    {:icon-button-element (r/as-element
                            [ui/icon-button
                             {:style      {:padding 4
                                           :width   24
                                           :height  24}
                              :icon-style {:height 24
                                           :width  24}}
                             (cond
                               (and (nil? drop-off-type) (nil? pickup-type))
                               [ic/communication-call {:style style-route/exception-icon}]
                               (and (= "arrival" stop-type) (= drop-off-type :regular)) [ic/maps-pin-drop {:style style-route/selected-exception-icon}]
                               (and (= "departure" stop-type) (= pickup-type :regular)) [ic/maps-pin-drop {:style style-route/selected-exception-icon}]
                               (and (= "arrival" stop-type) (= drop-off-type :not-available)) [ic/notification-do-not-disturb {:style style-route/selected-exception-icon}]
                               (and (= "departure" stop-type) (= pickup-type :not-available)) [ic/notification-do-not-disturb {:style style-route/selected-exception-icon}]
                               (and (= "arrival" stop-type) (= drop-off-type :phone-agency)) [ic/communication-call {:style style-route/selected-exception-icon}]
                               (and (= "departure" stop-type) (= pickup-type :phone-agency)) [ic/communication-call {:style style-route/selected-exception-icon}]
                               (and (= "arrival" stop-type) (= drop-off-type :coordinate-with-driver)) [ic/social-people {:style style-route/selected-exception-icon}]
                               (and (= "departure" stop-type) (= pickup-type :coordinate-with-driver)) [ic/social-people {:style style-route/selected-exception-icon}]
                               :else [ic/communication-call {:style style-route/exception-icon}])
                             ])}
    [ui/menu-item {:primary-text (if (= "arrival" stop-type)
                                   (tr [:route-wizard-page :trip-stop-arrival-exception-default])
                                   (tr [:route-wizard-page :trip-stop-departure-exception-default]))
                   :left-icon    (ic/maps-pin-drop)
                   :on-click     #(do
                                    (.preventDefault %)
                                    (e! (rw/->ShowStopException stop-type stop-idx :regular trip-idx)))}]
    [ui/menu-item {:primary-text (if (= "arrival" stop-type)
                                   (tr [:route-wizard-page :trip-stop-arrival-exception-no])
                                   (tr [:route-wizard-page :trip-stop-departure-exception-no]))
                   :left-icon    (ic/notification-do-not-disturb)
                   :on-click     #(do
                                    (.preventDefault %)
                                    (e! (rw/->ShowStopException stop-type stop-idx :not-available trip-idx)))}]
    [ui/menu-item {:primary-text (if (= "arrival" stop-type)
                                   (tr [:route-wizard-page :trip-stop-arrival-exception-agency])
                                   (tr [:route-wizard-page :trip-stop-departure-exception-agency]))
                   :left-icon    (ic/communication-call)
                   :on-click     #(do
                                    (.preventDefault %)
                                    (e! (rw/->ShowStopException stop-type stop-idx :phone-agency trip-idx)))}]
    [ui/menu-item {:primary-text (if (= "arrival" stop-type)
                                   (tr [:route-wizard-page :trip-stop-arrival-exception-driver])
                                   (tr [:route-wizard-page :trip-stop-departure-exception-driver]))
                   :left-icon    (ic/social-people)
                   :on-click     #(do
                                    (.preventDefault %)
                                    (e! (rw/->ShowStopException stop-type stop-idx :coordinate-with-driver trip-idx)))}]]])

(defn route-times-header [stop-sequence]
  [:thead
   [:tr
    [:th ""]
    (doall
     (map-indexed
      (fn [i {::transit/keys [code name]}]
        ^{:key (str code "_" i)}
        [:th {:colSpan 2
              :style {:vertical-align "top"}}
         [:div {:style {:display "inline-block"
                        :width "180px"
                        :overflow-x "hidden"
                        :white-space "pre"
                        :text-overflow "ellipsis"}} name]
         [:div {:style {:display "inline-block"
                        :float "right"
                        :position "relative"
                        :left "16px"
                        :top -20
                        :padding-right "5px"
                        :padding-left "5px"}}
          (when (< i (dec (count stop-sequence)))
            [ic/navigation-chevron-right])]])
      stop-sequence))]
   [:tr
    [:th ""]
    (doall
     (for [{::transit/keys [code name]} stop-sequence]
       (list
        ^{:key (str code "-arr")}
        [:th {:style {:font-size "80%" :font-variant "small-caps"}}
         (tr [:route-wizard-page :trip-stop-arrival-header])]
        ^{:key (str code "-dep")}
        [:th {:style {:font-size "80%" :font-variant "small-caps"}}
         (tr [:route-wizard-page :trip-stop-departure-header])])))]])

(defn trip-row
  "Render a single row of stop times."
  [e! stop-count edit-service-calendar row-idx {stops ::transit/stop-times :as trip}]
  ^{:key row-idx}
  [:tr
   [:td [:div
         [:span {:data-balloon        (tr [:route-wizard-page :trip-stop-calendar])
                 :data-balloon-pos    "right"
                 :data-balloon-length "medium"
                 :style {:overflow "visible"}}
          [ui/icon-button {
                           :style (if (= edit-service-calendar row-idx)
                                    {:border-radius "25px" :background-color "#b3b3b3"}
                                    {})
                           :href     "#"
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (rw/->EditServiceCalendar row-idx)))}
           (if (= edit-service-calendar row-idx)
             [ic/action-today]
             [ic/action-today])]]]]
   (map-indexed
     (fn [stop-idx {::transit/keys [arrival-time departure-time pickup-type drop-off-type] :as stop}]
       (let [update! #(e! (rw/->EditStopTime row-idx stop-idx %))
             style {:style {:padding-left     "5px"
                            :padding-right    "5px"
                            :width            "125px"
                            :background-color (if (even? stop-idx)
                                                "#f4f4f4"
                                                "#fafafa")}}]
         (list
           (if (zero? stop-idx)
             ^{:key (str stop-idx "-first")}
             [:td style " - "]
             ^{:key (str stop-idx "-arr")}
             [:td style
              [:div.col-md-11
                [form-fields/field {:type    :time
                                    :update! #(update! {::transit/arrival-time %})}
               arrival-time]]
              [:div.col-md-1 {:style {:margin-left "-10px"}}
                [exception-icon e! "arrival" pickup-type drop-off-type stop-idx row-idx]]])
           (if (= stop-idx (dec stop-count))
             ^{:key (str stop-idx "-last")}
             [:td style " - "]
             ^{:key (str stop-idx "-dep")}
             [:td style
              [:div.col-md-11
                [form-fields/field {:type    :time
                                    :update! #(update! {::transit/departure-time %})}
                         departure-time]]
              [:div.col-md-1 {:style {:margin-left "-10px"}}
                [exception-icon e! "departure" pickup-type drop-off-type stop-idx row-idx]]]))))
     stops)])

(defn trips-list [e! route app]
  (let [stop-sequence (::transit/stops route)
        stop-count (count stop-sequence)
        trips (::transit/trips route)
        empty-calendar? (empty? (first (::transit/service-calendars route)))]
    [:div.route-times
     [:div {:style {:overflow "auto"}}
     [:table {:style {:text-align "center"}}
      [route-times-header stop-sequence]
      [:tbody
       (doall (map-indexed (partial trip-row e! stop-count (get-in app [:route :edit-service-calendar])) trips))]]]

     (when (:edit-service-calendar route)
       [route-service-calendar/service-calendar e! app])

     (when empty-calendar?
       [:div {:style {:margin-top "10px"}}
        [common/help (tr [:form-help :trip-editor-no-calendar])]])

     [:div
      (tr [:route-wizard-page :trip-schedule-new-trip])
      [form-fields/field {:type :time
                          :update! #(e! (rw/->NewStartTime %))} (:new-start-time route)]
      [ui/raised-button {:style {:margin-left "5px"}
                         :primary true
                         :disabled (or (time/empty-time? (:new-start-time route)) empty-calendar?)
                         :on-click #(e! (rw/->AddTrip))
                         :label (tr [:route-wizard-page :trip-add-new-trip])}]]]))

(defn trips [e! {route :route :as app}]
  (if (empty? (::transit/trips route))
    (e! (rw/->InitRouteTimes))
    (e! (rw/->CalculateRouteTimes)))
  (fn [e! {route :route :as app}]
    [:div {:style {:padding-top "20px"}}
     [:div (stylefy/use-style style-form/form-card)
      [:div (stylefy/use-style style-form/form-card-label) "Vuorot"]
      [:div (merge (stylefy/use-style style-form/form-card-body))
       (if (seq (get-in route [::transit/trips 0 ::transit/stop-times]))
         [trips-list e! route app]
         [common/help (tr [:form-help :trip-editor-no-stop-sequence])])]]]))
