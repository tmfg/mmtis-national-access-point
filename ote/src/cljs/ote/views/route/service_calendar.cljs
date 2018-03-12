(ns ote.views.route.service-calendar
  (:require [ote.app.controller.route :as rc]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.ui.service-calendar :as service-calendar]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.db.transit :as transit]))

(defn service-calendar [e! {route :route :as route}]
  (let [calendar (get-in route [::transit/service-calendars 0])]
    [:div.route-service-calendar
     [ui/raised-button {:secondary true
                        :icon (ic/action-delete)
                        :style {:float "right" :margin-bottom "0.5em"}
                        :on-click #(e! (rc/->ClearServiceCalendar))}
      "Tyhjennä kalenteri"]
     [form/form {:name "Reittikalenteri"
                 :update! #(e! (rc/->EditServiceCalendarRules %))}
      [(form/group
        {:label "Ajopäiväkalenteri" :columns 3}
        {:type :table
         :name ::transit/service-rules
         :table-fields (into [{:type :date-picker
                               :name ::transit/from-date
                               :label "Alkaen"
                               :width "29%"}
                              {:type :date-picker
                               :name ::transit/to-date
                               :label "Päättyen"
                               :width "29%"}]
                             (for [[name label] [[::transit/monday "Ma"]
                                                 [::transit/tuesday "Ti"]
                                                 [::transit/wednesday "Ke"]
                                                 [::transit/thursday "To"]
                                                 [::transit/friday "Pe"]
                                                 [::transit/saturday "La"]
                                                 [::transit/sunday "Su"]]]
                               {:type :checkbox
                                :name name
                                :label label
                                :width "6%"}))
         :delete? true
         :add-label "Lisää uusi ajojakso kalenteriin"})]
      calendar]

     (let [rule-dates (or (:rule-dates calendar) #{})
           removed-dates (or (::transit/service-removed-dates calendar) #{})
           added-dates (or (::transit/service-added-dates calendar) #{})]
       [service-calendar/service-calendar
        {:selected-date? (constantly false)
         :on-select #(e! (rc/->ToggleDate %))
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
