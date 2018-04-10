(ns ote.views.pre_notice.pre_notice
  "Pre notice main form"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.pre_notice :as notice]
            [ote.ui.buttons :as buttons]
            [ote.ui.form-fields :as form-fields]
    ;; db
            [ote.db.transport-operator :as t-operator]))



(defn new-pre-notice [e! app]
  (fn [e! app]
    (let [operator (:transport-operator app)
          operators (mapv :transport-operator (:transport-operators-with-services app))]
      [:span
       [:h2 (tr [:pre-notice-page :pre-notice-form-title])]
       ;; Select operator
       [:div.row
        [form-fields/field
         {:label       (tr [:field-labels :select-transport-operator])
          :name        :select-transport-operator
          :type        :selection
          :show-option ::t-operator/name
          :update!     #(e! (notice/->SelectOperatorForNotice %))
          :options     operators
          :auto-width? true}
         operator]
        ]
       [:div.row
        [:span (:ote.db.transport-operator/business-id operator)]
        ]

       (when (not (notice/valid-notice? (:route app)))
         [ui/card {:style {:margin "1em 0em 1em 0em"}}
          [ui/card-text {:style {:color "#be0000" :padding-bottom "0.6em"}} (tr [:pre-notice-page :publish-missing-required])]])
       [:div.col-xs-12.col-sm-6.col-md-6 {:style {:padding-top "20px"}}
        [buttons/save {:disabled (not (notice/valid-notice? (:pre-notice app)))
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (notice/->SaveToDb true)))}
         (tr [:buttons :save-and-send])]
        [buttons/save {:on-click #(do
                                    (.preventDefault %)
                                    (e! (notice/->SaveToDb false)))}
         (tr [:buttons :save-as-draft])]
        [buttons/cancel {:on-click #(do
                                      (.preventDefault %)
                                      (e! (notice/->CancelNotice)))}
         (tr [:buttons :cancel])]]])))
