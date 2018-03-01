(ns ote.views.route.service-calendar
  (:require [ote.app.controller.route :as rc]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.ui.service-calendar :as service-calendar]))

(defn service-calendar [e! {route :route :as route}]
  [:div.route-service-calendar
   [form/form {:name "Reittikalenteri"
               :update! #(e! (rc/->EditServiceCalendarRules %))}
    [(form/group
      {:label "Säännöllinen aikataulu" :columns 3}
      {:type :table
       :name :rules
       :table-fields (into [{:type :date-picker
                             :name :from
                             :label "Alkaen"
                             :width "29%"}
                            {:type :date-picker
                             :name :to
                             :label "Päättyen"
                             :width "29%"}]
                           (for [[name label] [[:monday "Ma"]
                                               [:tuesday "Ti"]
                                               [:wednesday "Ke"]
                                               [:thursday "To"]
                                               [:friday "Pe"]
                                               [:saturday "La"]
                                               [:sunday "Su"]]]
                             {:type :checkbox
                              :name name
                              :label label
                              :width "6%"}))
       :delete? true
       :add-label "Lisää säännöllinen aikataulu"})]
    (:calendar-rules route)]

   (let [rule-dates (or (:rule-dates route) #{})]
     [service-calendar/service-calendar
      {:selected-date? (fn [d]
                         (let [selected (or (:dates route) #{})
                               df (time/date-fields d)]
                           (selected df)))
       :on-select #(e! (rc/->ToggleDate %))
       :day-style (fn [day selected?]
                    (when (and (rule-dates (time/date-fields day))
                               (not selected?))
                      {:background-color "lightblue"}))}])])
