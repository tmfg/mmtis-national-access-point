(ns ote.ui.service-calendar
  "Component for selecting dates a service is run."
  (:require
    [cljs-time.core :as t]
    [ote.ui.service-calendar-compact :as compact-view]
    [ote.ui.service-calendar-weeks :as weeks-view]
    [ote.ui.service-calendar-timeline :as timeline-view]))


(defn service-calendar
  "Service calendar component."
  [{:keys [selected-date? on-select years view-mode] :as options}]
  (let [current-year (t/year (t/now))
        next-year (inc current-year)
        years (or years
                  [current-year next-year])]
    [:div.service-calendar {:style {:padding "20px"}}
     (doall
       (for [year years]
         (with-meta
           (case view-mode
             :weeks [weeks-view/service-calendar-year options year]
             :compact [compact-view/service-calendar-year options year]
             :timeline [timeline-view/service-calendar-year options year]
             [compact-view/service-calendar-year options year])
           {:key year})))]))
