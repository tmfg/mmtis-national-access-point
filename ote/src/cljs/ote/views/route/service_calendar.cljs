(ns ote.views.route.service-calendar
  (:require [ote.app.controller.route.route-wizard :as rw]
            [ote.time :as time]
            [ote.ui.service-calendar :as service-calendar]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]
            [ote.style.form :as style-form]
            [stylefy.core :as stylefy]
            [ote.ui.form-fields :as form-fields]))

(def rule-fields
  (delay (into [{:type :date-picker
                 :name ::transit/from-date
                 :label (tr [:route-wizard-page :route-calendar-from-date])
                 :width "29%"}
                {:type :date-picker
                 :name ::transit/to-date
                 :label (tr [:route-wizard-page :route-calendar-to-date])
                 :width "29%"}]
               (for [[name label] [[::transit/monday (tr [:enums :ote.db.transport-service/day :short :MON])]
                                   [::transit/tuesday (tr [:enums :ote.db.transport-service/day :short :TUE])]
                                   [::transit/wednesday (tr [:enums :ote.db.transport-service/day :short :WED])]
                                   [::transit/thursday (tr [:enums :ote.db.transport-service/day :short :THU])]
                                   [::transit/friday (tr [:enums :ote.db.transport-service/day :short :FRI])]
                                   [::transit/saturday (tr [:enums :ote.db.transport-service/day :short :SAT])]
                                   [::transit/sunday (tr [:enums :ote.db.transport-service/day :short :SUN])]]]
                 {:type :checkbox
                  :name name
                  :label label
                  :width "6%"}))))

(defn- rules-table [e! trip-idx calendar]
  [:div.row {:style {:padding "20px"}}
   [form-fields/field {:type :table
                       :update! #(e! (rw/->EditServiceCalendarRules {::transit/service-rules %} trip-idx))
                       :table-fields @rule-fields
                       :delete? true
                       :add-label (tr [:route-wizard-page :route-calendar-add-new-period])}
    (::transit/service-rules calendar)]])

(defn day-style-fn [calendar]
  (let [rule-dates (or (:rule-dates calendar) #{})
        removed-dates (or (::transit/service-removed-dates calendar) #{})
        added-dates (or (::transit/service-added-dates calendar) #{})]
    (fn [day selected?]
      (let [day (time/date-fields day)]
        (cond
          (removed-dates day)
          {:background-color "#c27272"
           :text-decoration "line-through"}

          (rule-dates day)
          {:background-color "lightblue"}

          (added-dates day)
          {:background-color "wheat"})))))

(defn service-calendar [e! {trip-idx :edit-service-calendar :as route}]
  (let [calendar (get-in route [::transit/service-calendars trip-idx])]
    [:div.route-service-calendar {:style {:padding-top "20px"}}
     [:div (stylefy/use-style style-form/form-card)
      [:div (stylefy/use-style style-form/form-card-label)
       "Ajopäiväkalenteri"]
      [ui/raised-button {:secondary true
                         :icon (ic/action-delete)
                         :style {:float "right" :margin-bottom "0.5em"}
                         :on-click #(e! (rw/->ClearServiceCalendar trip-idx))
                         :label (tr [:buttons :route-calendar-clear])}]

      [rules-table e! trip-idx calendar]

      [service-calendar/service-calendar
       {:selected-date? (constantly false)
        :on-select #(e! (rw/->ToggleDate % trip-idx))
        :day-style (day-style-fn calendar)}]]]))
