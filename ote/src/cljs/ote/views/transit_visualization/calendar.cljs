(ns ote.views.transit-visualization.calendar
  "Calendar section in transit visualization page."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.time :as time]
            [cljs-time.core :as t]
            [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr]]
            [ote.ui.icon_labeled :as icon-l]
            [ote.ui.service-calendar :as service-calendar]
            [ote.ui.table :as table]
            [ote.theme.colors :as colors]
            [ote.style.transit-changes :as style]
            [ote.style.base :as style-base]
            [ote.views.transit-visualization.change-utilities :as tv-section]
            [ote.views.transit-visualization.change-icons :as tv-change-icons]
            [ote.app.controller.transit-visualization :as tv]
            [ote.ui.circular_progress :as prog]
            [ote.app.routes :as routes]))

;; Utility methods
(defn day-of-week-number->text [dof]
  (tr [:common-texts (keyword (str "day-of-week-" dof "-short"))]))

(defn select-day [e! day loading?]
  (when-not loading?
    (e! (tv/->SelectDatesForComparison day))))

(defn day-style [hash->color date->hash date1 date2 day]
  ;; `when` check because date->hash is nil until RouteCalendarDatesResponse completes.
  ;; That happens a bit late if route is already selected by url element on first navigation to view.
  (when date->hash
    (let [d (time/format-date-iso-8601 day)
          hash (date->hash d)
          hash-color (hash->color hash)]
      (merge
        {:font-size "0.75rem"
         :background-color hash-color
         :color colors/calendar-day-font
         :transition "box-shadow 0.25s"
         :box-shadow "inset 0 0 0 2px transparent, inset 0 0 0 3px transparent, inset 0 0 0 100px transparent"}
        (cond (and (= (time/format-date-iso-8601 date1) d) (some? date2))
              (style/date1-highlight-style hash-color)

              (= (and (some? date2) (time/format-date-iso-8601 date2)) d)
              (style/date2-highlight-style hash-color)

              (and (= (time/format-date-iso-8601 date1) d) (nil? date2))
              (style/date-highlight-style hash-color colors/gray800))))))

(defn comparison-dates [{:keys [date1 date2]}]
  [:div (stylefy/use-style (merge (style-base/flex-container "row")
                                  {:justify-content "space-between"
                                   :width "20%"}))
   [:div
    [:div {:style (merge {:display "inline-block"
                               :position "relative"
                               :top "5px"
                               :margin-right "0.5em"
                               :width "20px"
                               :height "20px"}
                              (style/date1-highlight-style))}]
    (when date1
      (time/format-date date1))]
   [:div
    [:div {:style (merge {:display "inline-block"
                               :position "relative"
                               :top "5px"
                               :margin-right "0.5em"
                               :width "20px"
                               :height "20px"}
                              (style/date2-highlight-style))}]
    (when date2
      (time/format-date date2))]])

(defn comparison-date-changes [{diff :differences :as compare}]
  [:div
   [comparison-dates compare]

   (when (seq diff)
     [tv-change-icons/change-icons-for-calendar diff true])])

;; Ui
(defn route-calendar [e! url-router-params
                      {:keys [date->hash hash->color show-previous-year? show-next-year? compare open-sections route-dates-selected-from-calendar?]
                       :as transit-visualization}
                      routes selected-route]
  (let [current-year (time/year (time/now))
        changes (filter
                  (fn [x]
                    (= (:route-hash-id x)
                       (:route-hash-id selected-route)))
                  routes)]
    [:div.route-service-calendar
     [tv-section/section {:toggle! #(e! (tv/->ToggleSection :route-service-calendar))
               :open? (get open-sections :route-service-calendar true)}
      "Kalenteri"
      [:div
       [:p (tr [:transit-visualization-page :calendar-description-1])]
       [:p (tr [:transit-visualization-page :calendar-description-2])]
       [:div (stylefy/use-style (merge (style-base/flex-container "row")
                                       {:margin-top "1rem"
                                        :margin-bottom "1rem"}))
        [:div {:style {:flex 1}}
         [ui/checkbox {:label "Näytä myös edellinen vuosi"
                       :checked show-previous-year?
                       :on-check #(e! (tv/->ToggleShowPreviousYear))}]]
        [:div {:style {:flex 1}}
         [ui/checkbox {:style {:flex 1}
                       :label "Näytä myös seuraava vuosi"
                       :checked show-next-year?
                       :on-check #(e! (tv/->ToggleShowNextYear))}]]]]
      [:div
      [:div.change-list
       [tv-section/route-changes-legend]
       [table/table {:table-name "tbl-route-calendar"
                     :name->label str
                     :label-style style-base/table-col-style-wrap
                     :show-row-hover? true
                     :on-select #(when-let [{:keys [route-hash-id different-week-date]} (first %)]
                                   (routes/navigate! :transit-visualization
                                                     (assoc url-router-params
                                                       :route-hash-id route-hash-id
                                                       :change-id different-week-date)))
                     :row-selected? #(and
                                       (= (:different-week-date %) (:different-week-date selected-route))
                                       (not route-dates-selected-from-calendar?))}
        [{:name ""
          :read identity
          :format (fn [{:keys [recent-change? change-detected]}]
                    (when recent-change?
                      [:div (merge (stylefy/use-style style/new-change-container)
                                   {:title (str "Muutos tunnistettu: " (time/format-timestamp->date-for-ui change-detected)) })
                       [:div (stylefy/use-style style/new-change-indicator)]]))
          :col-style style-base/table-col-style-wrap
          :width "2%"}
         {:name "Aikaa muutokseen"
          :read :different-week-date
          :col-style style-base/table-col-style-wrap
          :format (fn [different-week-date]
                    (let [ddate (or different-week-date (time/now))]
                      [:div
                       [:span (stylefy/use-style {;; nowrap for the "3 pv" part to prevent breaking "pv" alone to new row.
                                                  :white-space "nowrap"})
                        (str (time/days-until different-week-date) " " (tr [:common-texts :time-days-abbr]) " ")]
                       [:span (stylefy/use-style {:color "gray"
                                                  :overflow-wrap "break-word"})
                        (str "("
                             (day-of-week-number->text (t/day-of-week (time/js-date->goog-date ddate)))
                             " "
                             (time/format-timestamp->date-for-ui ddate) ")")]]))}
         {:name "Muutos tunnistettu"
          :read :change-detected
          :col-style style-base/table-col-style-wrap
          :format (fn [change-detected]
                    (time/format-timestamp->date-for-ui change-detected))}
         {:name "Vertailupäivien väliset muutokset" :width "35%"
          :read identity
          :col-style style-base/table-col-style-wrap
          :format (fn [{change-type :change-type :as route}]
                    (case change-type
                      :no-traffic
                      [icon-l/icon-labeled style/transit-changes-icon
                       [ic/av-not-interested]
                       "Tauko liikennöinnissä"]

                      :added
                      [icon-l/icon-labeled style/transit-changes-icon
                       [ic/content-add-circle-outline {:color colors/add-color}]
                       "Uusi reitti"]

                      :removed
                      [icon-l/icon-labeled
                       [ic/content-remove-circle-outline {:color colors/remove-color}]
                       "Mahdollisesti päättyvä reitti"]

                      :no-change
                      [icon-l/icon-labeled style/transit-changes-icon
                       [ic/navigation-check]
                       "Ei muutoksia"]

                      :changed
                      [tv-change-icons/change-icons-for-calendar route]))}]
        changes]]

      [:div.route-service-calendar-content
       [service-calendar/service-calendar {:selected-date? (constantly false)
                                           :on-select #(select-day e! % (:route-differences-loading? transit-visualization))
                                           :day-style (r/partial day-style hash->color date->hash
                                                                 (:date1 compare) (:date2 compare))
                                           :years (vec (concat (when show-previous-year?
                                                                 [(dec current-year)])
                                                               [current-year]
                                                               (when show-next-year?
                                                                 [(inc current-year)])))
                                           :hover-style #(let [d (time/format-date-iso-8601 %)
                                                               hash (date->hash d)
                                                               hash-color (hash->color hash)]
                                                           (style/date-highlight-style hash-color
                                                                                       style/date-highlight-color-hover))}]

       [:h3 "Valittujen päivämäärien väliset muutokset"]
       ;; :differences used to detect when new route selection or new calendar day selection is being loaded
       (if (empty? (:differences compare))
         [prog/circular-progress (tr [:common-texts :loading])]
         ;; :date2 used to hide difference statistics but not the title, when user selects new diff dates from calendar
         (when (some? (:date2 compare))
           [:div
            [comparison-date-changes compare]]))]]]]))
