(ns ote.views.transit-changes
  "Transit changes view. Shows when regular route based traffic schedules
  change with links to detailed view (transit visualization)"
  (:require [reagent.core :as r]
            [ote.app.controller.transit-changes :as tc]
            [ote.ui.common :as common]
            [ote.ui.table :as table]
            [clojure.string :as str]
            [ote.time :as time]
            [ote.localization :refer [tr]]))

(defn week-day-short [week-day]
  (tr [:enums :ote.db.transport-service/day :short
       (case week-day
         :monday :MON
         :tuesday :TUE
         :wednesday :WED
         :thursday :THU
         :friday :FRI
         :saturday :SAT
         :sunday :SUN)]))

(defn- change-description [{next-different-week :next-different-week}]
  (when next-different-week
    (let [{:keys [current-week current-year different-week different-year
                  current-week-traffic different-week-traffic]} next-different-week]
      (str "Viikko " different-year "/" different-week " liikenne eroaa nykyisestä ("
           current-year "/" current-week "): "
           (cond
             (and (not (empty? (:days-with-traffic current-week-traffic)))
                  (empty? (:days-with-traffic different-week-traffic)))
             "Mahdollinen liikennöinnin päättyminen"

             (and (empty? (:days-with-traffic current-week-traffic))
                  (not (empty? (:days-with-traffic different-week-traffic))))
             "Mahdollinen liikennöinnin alkaminen"

             :default
             (str "Eri liikennöinti päivinä "
                  (str/join ", "
                            (remove nil?
                                    (keep (fn [day]
                                            (when (not= ((:day->hash current-week-traffic) day)
                                                        ((:day->hash different-week-traffic) day))
                                              (week-day-short day)))
                                          time/week-days)))))))))

(defn transit-changes [e! {:keys [loading? changes] :as transit-changes}]
  [table/table {:no-rows-message (if loading?
                                   "Ladataan muutoksia, odota hetki..."
                                   "Ei löydettyjä muutoksia")
                :name->label str
                :on-select #(e! (tc/->ShowChangesForOperator (:transport-operator-id (first %))))}
   [{:name "Palveluntuottaja" :read :transport-operator-name :width "25%"}
    {:name "Muutospvm" :read (comp :change-date :next-different-week)
     :format time/format-date-opt :width "10%"}
    {:name "Muutos" :read change-description :width "65%"}]
   changes])
