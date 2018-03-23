(ns ote.views.route.service-calendar
  (:require [ote.app.controller.route.route-wizard :as rw]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.ui.service-calendar :as service-calendar]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]))

(defn service-calendar [e! {{trip-idx :edit-service-calendar :as route} :route :as app}]
  (let [calendar (get-in route [::transit/service-calendars trip-idx])]
    [:div.route-service-calendar
     [ui/raised-button {:primary true
                        :icon (ic/navigation-arrow-back)
                        :style {:float "left" :margin-bottom "0.5em"}
                        :on-click #(e! (rw/->CloseServiceCalendar))
                        :label (tr [:buttons :route-calendar-back-to-trips])}]
     [ui/raised-button {:secondary true
                        :icon (ic/action-delete)
                        :style {:float "right" :margin-bottom "0.5em"}
                        :on-click #(e! (rw/->ClearServiceCalendar trip-idx))
                        :label (tr [:buttons :route-calendar-clear])}]
     [form/form {:name (tr [:route-wizard-page :route-calendar-name])
                 :update! #(e! (rw/->EditServiceCalendarRules % trip-idx))}
      [(form/group
        {:label   (tr [:route-wizard-page :route-calendar-group-name])
         :columns 3}
        {:type :table
         :name ::transit/service-rules
         :table-fields (into [{:type :date-picker
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
                                :width "6%"}))
         :delete? true
         :add-label (tr [:route-wizard-page :route-calendar-add-new-period])})]
      calendar]

     (let [rule-dates (or (:rule-dates calendar) #{})
           removed-dates (or (::transit/service-removed-dates calendar) #{})
           added-dates (or (::transit/service-added-dates calendar) #{})]
       [service-calendar/service-calendar
        {:selected-date? (constantly false)
         :on-select #(e! (rw/->ToggleDate % trip-idx))
         :day-style (fn [day selected?]
                      (let [day (time/date-fields day)]
                        (cond
                          (removed-dates day)
                          {:background-color "#c27272"
                           :text-decoration "line-through"}

                          (rule-dates day)
                          {:background-color "lightblue"}

                          (added-dates day)
                          {:background-color "wheat"})))}])]))
