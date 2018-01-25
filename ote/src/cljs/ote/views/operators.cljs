(ns ote.views.operators
  "Transport operator listing view. Shows a filterable listing
  of all service providers."
  (:require [ote.app.controller.operators :as operators-controller]
            [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form-fields :as form-fields]
            [ote.localization :refer [tr]]
            [clojure.string :as str]
            [ote.db.transport-operator :as t-operator]
            [ote.style.service-search :as style-service-search]
            [stylefy.core :as stylefy]
            [ote.views.service-search :as service-search]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :as common]
            [ote.app.controller.front-page :as fp-controller]))

(defn show-service-count-link [e! service-count operator-id]
  [:p {:style {:font-weight "600" :font-size "14px"}}
   (cond
     (= 0 service-count)
     (tr [:operators :result-no-services])
     (= 1 service-count)
      [:a
       {:style style-service-search/service-link
        :href "#"
        :on-click #(do (.preventDefault %)
                          (e! (fp-controller/->ChangePage :services {:operator operator-id})))}
      (tr [:operators :result-service-count-single])]
     :else
     [:a
      {:style style-service-search/service-link
       :href "#"
       :on-click #(do (.preventDefault %)
                         (e! (fp-controller/->ChangePage :services {:operator operator-id})))}
     (tr [:operators :result-service-count] {:service-count service-count})])
   ]
  )

(defn operator-row [label data]
  (let [data (if (= 0 (count data))
               "-"
               data)]
  [:div.col-xs-12.col-md-6
   [:div.col-xs-12.col-sm-6.col-md-4 {:style {:font-weight 600}} label]
   [:div.col-xs-12.col-sm-6.col-md-8 data]
   ]
  ))

(defn show-operator-data [e! operator]
  [:div
   [:div.row
    (operator-row (tr [:field-labels ::t-operator/name]) (get operator ::t-operator/name))
    (operator-row (tr [:field-labels ::t-operator/business-id]) (get operator ::t-operator/business-id))
    (operator-row (tr [:field-labels ::t-operator/phone]) (get operator ::t-operator/phone))
    (operator-row (tr [:field-labels ::t-operator/gsm]) (get operator ::t-operator/gsm))
    (operator-row (tr [:field-labels ::t-operator/email]) (get operator ::t-operator/email))
    (operator-row (tr [:field-labels ::t-operator/homepage]) (get operator ::t-operator/homepage))
    (operator-row (tr [:field-labels :ote.db.common/street]) (get-in operator [::t-operator/visiting-address :t-operator/street]))
    (operator-row (tr [:field-labels :ote.db.common/post_code]) (get-in operator [::t-operator/visiting-address :t-operator/postal_code]))
    (operator-row (tr [:field-labels :ote.db.common/post_office]) (get-in operator [::t-operator/visiting-address :t-operator/post_office]))
    ]

   [:div.row {:style {:padding-top "10px"}}
    [:p [:b (tr [:field-labels :ote.db.transport-operator/ckan-description])]]
    [:p
     (get-in operator [::t-operator/ckan-group ::t-operator/description])]]
   [:div {:style {:padding-top "10px"}}
    (show-service-count-link  e! (get operator ::t-operator/service-count) (get operator ::t-operator/id))
    ]



   ]
  )


(defn operator-modal [e! show-modal? operator]
  (when show-modal?
    [ui/dialog
     {:open true
      :modal false
      :auto-scroll-body-content true
      :title   (get operator ::t-operator/name)
      :actions [(r/as-element
                  [ui/flat-button
                   {:label     (tr [:buttons :close])
                    :secondary true
                    :primary   true
                    :on-click  #(e! (operators-controller/->CloseModal (get operator ::t-operator/id)))}])]}


     (show-operator-data e! operator)])

  )


(defn operators-list [e! operators]
  [:div
   (doall
    (for [{::t-operator/keys [id name business-id homepage email
                              phone gsm visiting-address service-count
                              ckan-group] :as operator} operators]
      ^{:key (str "operator-" id)}
      [:div {:class "col-md-6" :style {:padding "1em 1em 0 0"}}
      [ui/paper {:z-depth 1
                 :style {:min-height "180px"}}
       [:div (stylefy/use-style style-service-search/operator-result-header)
         [:a
          {:href "#"
           :on-click #(do (.preventDefault %)
                          (e! (operators-controller/->OpenModal id)))}
               [:p (stylefy/use-style  style-service-search/operator-result-header-link) name]]
         ]
        [:div (stylefy/use-style style-service-search/operator-description)
         [:div
          (if (< 80 (count (::t-operator/description ckan-group)))
            [:p (str (subs (::t-operator/description ckan-group) 0 120) "...")
              [:br]
              [:a {:href "#/operators" :on-click #(e! (operators-controller/->OpenModal id))}
               (tr [:operators :description-read-more])]
             (operator-modal e! (get operator :show-modal?) operator)
             ]
            (::t-operator/description ckan-group))
          ]
         [:div {:style {:position "absolute" :bottom "1em" }}
          (show-service-count-link e! service-count id)
         ;; Hidden for now - we need to figure out how to make this work with different screen widths
          #_ [:div.hidden
          [service-search/data-items
           [ic/content-link {:style style-service-search/contact-icon}]
           (when homepage [common/linkify homepage homepage {:target "_blank"}])

           [ic/action-home {:style style-service-search/contact-icon}]
           (when-not (every? str/blank? (vals visiting-address))
             (service-search/format-address visiting-address))

           [ic/communication-phone {:style style-service-search/contact-icon}]
           phone

           [ic/communication-phone {:style style-service-search/contact-icon}]
           (when (not= gsm phone) gsm) ; only show if different number than phone

           [ic/communication-email {:style style-service-search/contact-icon}]
           email]

          ]]
         ]
        ]]))])

(defn operators [e! _]
  (e! (operators-controller/->Init))
  (fn [e! {operators :operators :as app}]
    [:div.operators
     [:h3 (tr [:operators :title])]
     [form-fields/field {:type :string
                         :label (tr [:operators :filter])
                         :update! #(e! (operators-controller/->UpdateOperatorFilter %))}
      (:filter operators)]

     (if (zero? (:total-count operators))
       [:div (tr [:operators :no-results])]

       [:div
        (when-not (:loading? operators)
          (tr [:operators (if (str/blank? (:filter operators))
                            :result-count-all
                            :result-count)]
              {:total-count (:total-count operators)}))
        [operators-list e! (:results operators)]
        (when (> (:total-count operators) (count (:results operators)))
          (if (:loading? operators)
            [:div (tr [:operators :loading])]
            [common/scroll-sensor #(e! (operators-controller/->FetchMore))]))])]))
