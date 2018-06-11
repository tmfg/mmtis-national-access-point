(ns ote.views.transit-changes
  "Transit changes view. Shows when regular route based traffic schedules
  change with links to detailed view (transit visualization)"
  (:require [reagent.core :as r]
            [ote.app.controller.transit-changes :as tc]
            [ote.ui.common :as common]
            [ote.ui.table :as table]
            [clojure.string :as str]
            [ote.time :as time]
            [cljs-time.core :as t]
            [ote.localization :refer [tr]]
            [cljs-react-material-ui.reagent :as ui]))

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
    (let [{:keys [current-week different-week current-week-traffic
                  different-week-traffic change-date]} next-different-week
          change-date-str (time/format-date-opt change-date)
          current-date-str (time/format-date-opt (t/minus (t/now) (t/days (dec (t/day-of-week (t/now))))))]
      [:div
       [:span (str change-date-str " alkava viikko (" different-week ") eroaa " current-date-str " alkaneesta viikosta ("
                   current-week "). \n")]
       [:span (str
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
                                               time/week-days))) ".")))]])))

(defn transit-changes [e! {:keys [loading? changes] :as transit-changes}]
  [:div.transit-changes
   [:h3 "Säännöllisen markkinaehtoisen reittiliikenteen tulevat muutokset"]
   [table/table {:no-rows-message (if loading?
                                    "Ladataan muutoksia, odota hetki..."
                                    "Ei löydettyjä muutoksia")
                 :name->label str
                 :on-select #(e! (tc/->ShowChangesForOperator (:transport-operator-id (first %))))}
    [{:name "Palveluntuottaja" :read :transport-operator-name :width "20%"}
     {:name "Palvelu" :read :transport-service-name :width "20%"}
     {:name "Aikaa muutokseen" :width "10%"
      :read (comp :change-date :next-different-week)
      :format #(str (t/in-days (t/interval (t/now) %)) " pv")}
     {:name "Muutos" :read change-description :width "50%"
      :col-style {:white-space "pre-wrap"}}]
    changes]])
