(ns ote.views.route
  "Main views for route based traffic views"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.leaflet :as leaflet]
            [ote.app.controller.route.route-wizard :as rw]
            [cljs-react-material-ui.icons :as ic]
            [ote.time :as time]
            [ote.ui.form :as form]
            [ote.style.route :as style-route]
            [stylefy.core :as stylefy]
            [ote.ui.buttons :as buttons]
            [ote.localization :refer [tr tr-key]]

            ;; Subviews for wizard
            [ote.views.route.basic-info :as route-basic-info]
            [ote.views.route.stop-sequence :as route-stop-sequence]
            [ote.views.route.trips :as route-trips]
            [ote.views.route.service-calendar :as route-service-calendar]))

(defn route-save [e! {route :route :as app}]
  [ui/raised-button {:primary true
                     :on-click #(e! (rw/->SaveAsGTFS))}
   (tr [:buttons :save-as-gtfs])])

(def wizard-steps
  [{:name :basic-info
    :label :wizard-step-basic-info
    :component route-basic-info/basic-info
    :validate  rw/valid-basic-info?}
   {:name :stop-sequence
    :label :wizard-step-stop-sequence
    :component route-stop-sequence/stop-sequence
    :validate rw/valid-stop-sequence?}
   {:name :times
    :label :wizard-step-times
    :component route-trips/trips
    :validate rw/valid-trips?}
   {:name :save
    :label :wizard-step-save
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
     [ui/stepper {:active-step i
                  :connector (r/as-element [ic/navigation-arrow-forward])}
      (doall
        (for [{label :label current-step :name} wizard-steps
              :let [prev-valid? (rw/validate-previous-steps route current-step wizard-steps)]]
          ^{:key label}
          [ui/step (when prev-valid?
                     (stylefy/use-style style-route/stepper))
           [ui/step-label {:on-click (when prev-valid?
                                       #(e! (rw/->GoToStep current-step)))}
            [:span (tr [:route-wizard-page label])]]]))]

     [component e! app]

     [:div.route-wizard-navigation
      {:style {:display "flex"
               :justify-content "space-between"}}
      (if (= step (:name (first wizard-steps)))
        [:span]
        [ui/flat-button {:primary true
                         :on-click #(e! (rw/->GoToStep previous-step))
                         :icon (ic/navigation-arrow-back)
                         :label-position "before"}
         (tr [:buttons :previous-step])])
      (when (not= step (:name (last wizard-steps)))
        [ui/flat-button {:primary true
                         :disabled (not (validate route))
                         :on-click #(e! (rw/->GoToStep next-step))
                         :icon (ic/navigation-arrow-forward)}
         (tr [:buttons :next-step])])]]))

(defn new-route [e! app]
  (when (not (:route app))
    (e! (rw/->InitRoute)))
  (fn [e! {route :route :as app}]
    (let [page (or (:page route) 0)]
      [:span
       [route-wizard
        e! wizard-steps
        app]
       [:div.col-xs-12.col-sm-6.col-md-6
        [buttons/save {:disabled false
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->SaveToDb)))}
         (tr [:buttons :save])]
        [buttons/cancel {:on-click #(do
                                      (.preventDefault %)
                                      (e! (rw/->CancelRoute)))}
         (tr [:buttons :cancel])]]])))

(defn edit-route-by-id [e! app]
  (e! (rw/->LoadRoute (get-in app [:params :id])))
  (fn [e! {route :route :as app}]
    (let [page (or (:page route) 0)]
      [:span
       [route-wizard
        e! wizard-steps
        app]
       [:div.col-xs-12.col-sm-6.col-md-6
        [buttons/save {:disabled false
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (rw/->SaveToDb)))}
         (tr [:buttons :save])]
        [buttons/cancel {:on-click #(do
                                      (.preventDefault %)
                                      (e! (rw/->CancelRoute)))}
         (tr [:buttons :cancel])]]])))