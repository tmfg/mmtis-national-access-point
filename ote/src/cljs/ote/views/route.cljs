(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route :as rc]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.service-calendar :as service-calendar]
            [ote.time :as time]
            [ote.ui.form :as form]

            ;; Subviews for wizard
            [ote.views.route.basic-info :as route-basic-info]
            [ote.views.route.stop-sequence :as route-stop-sequence]
            [ote.views.route.times :as route-times]))



(defn route-service-calendar [e! {route :route :as route}]
  [service-calendar/service-calendar
   {:selected-date? (fn [d]
                      (let [selected (or (:dates route) #{})
                            df (time/date-fields d)]
                        (selected df)))
    :on-select #(e! (rc/->ToggleDate %))}])

(defn route-save [e! {route :route :as app}]
  [:div "tässä tallennellaan"])



(def wizard-steps
  [{:name :basic-info
    :label "Reitin nimi"
    :component route-basic-info/basic-info
    :validate  rc/valid-basic-info?}
   {:name :stop-sequence
    :label "Reittipysäkit"
    :component route-stop-sequence/stop-sequence
    :validate rc/valid-stop-sequence?}
   {:name :times
    :label "Vuorot"
    :component route-times/times
    :validate rc/valid-stop-times?}
   {:name :calendar
    :label "Kalenteri"
    :component route-service-calendar}
   {:name :save
    :label "Reitin tallennus"
    :component route-save}])

(defn- route-wizard [e! wizard-steps {route :route :as app}]
  (let [step (or (:step route) (:name (first wizard-steps)))

        [i {:keys [component validate] :as current-step}]
        (first (keep-indexed
                (fn [i s]
                  (when (= step (:name s))
                    [i s]))
                wizard-steps))
        validate (or validate (constantly true))

        previous-step (some (fn [[prev current]]
                              (when (= step (:name current))
                                (:name prev)))
                            (partition-all 2 1 wizard-steps))
        next-step (some (fn [[current next]]
                          (when (= step (:name current))
                            (:name next)))
                        (partition-all 2 1 wizard-steps))]
    [:div.route
     [ui/stepper {:active-step i}
      (doall
       (for [{label :label} wizard-steps]
         ^{:key label}
         [ui/step
          [ui/step-label label]]))]

     [component e! app]

     [:div.route-wizard-navigation
      {:style {:display "flex"
               :justify-content "space-between"}}
      (if (= step (:name (first wizard-steps)))
        [:span]
        [ui/flat-button {:primary true
                         :on-click #(e! (rc/->GoToStep previous-step))
                         :icon (ic/navigation-arrow-back)
                         :label-position "before"}
         "Edellinen"])
      (when (not= step (:name (last wizard-steps)))
        [ui/flat-button {:primary true
                         :disabled (not (validate route))
                         :on-click #(e! (rc/->GoToStep next-step))
                         :icon (ic/navigation-arrow-forward)}
         "Seuraava"])]]))

(defn new-route [e! _]
  (e! (rc/->LoadStops))
  (fn [e! {route :route :as app}]
    (let [page (or (:page route) 0)]
      [route-wizard
       e! wizard-steps
       app])))
