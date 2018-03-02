(ns ote.views.route.service-calendar
  (:require [ote.app.controller.route :as rc]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.ui.service-calendar :as service-calendar]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]))

(defn service-calendar [e! {route :route :as route}]
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
       :add-label "Lisää uusi ajojakso kalenteriin"})]
    (get-in route [:service-calendar :rules])]

   (let [rule-dates (or (get-in route [:service-calendar :rule-dates]) #{})
         removed-dates (or (get-in route [:service-calendar :removed-dates]) #{})
         added-dates (or (get-in route [:service-calendar :added-dates]) #{})]
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
                        {:background-color "wheat"})))}])])
