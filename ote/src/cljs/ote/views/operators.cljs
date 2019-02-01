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
            [ote.app.controller.front-page :as fp-controller]
            [ote.ui.common :as common-ui]
            [ote.util.text :as text]
            [ote.style.dialog :as style-dialog]))

(defn show-service-count-link [e! operator]
  (let [service-count (::t-operator/service-count operator)]
  [:span {:style {:font-weight "600" :font-size "14px"}}
   (if (zero? service-count)
     (tr [:operators :result-no-services])
     [:a
       {:style style-service-search/service-link
        :href "#"
        :on-click #(do (.preventDefault %)
                          (e! (operators-controller/->ShowOperatorServices operator)))}
      (tr [:operators (if (= 1 service-count)
                        :result-service-count-single
                        :result-service-count)] {:service-count service-count})])]))

(defn operator-row [label data]
  (let [data (if (= 0 (count data))
               "-"
               data)]
  [:div.col-xs-12.col-md-6 {:style {:padding-top "5px"}}
   [:div.col-xs-12.col-sm-6.col-md-5 {:style {:font-weight 600}} label]
   [:div.col-xs-12.col-sm-6.col-md-7 data]]))

(defn show-operator-data [e! operator]
  [:div
   [:div.row {:style {:padding-top "20px"}}
    (operator-row (tr [:field-labels ::t-operator/name]) (::t-operator/name operator))
    (operator-row (tr [:field-labels ::t-operator/business-id]) (::t-operator/business-id operator))
    (operator-row (tr [:field-labels ::t-operator/phone]) (::t-operator/phone operator))
    (operator-row (tr [:field-labels ::t-operator/gsm]) (::t-operator/gsm operator))
    (operator-row (tr [:field-labels ::t-operator/email]) (::t-operator/email operator))

    (operator-row (tr [:field-labels :ote.db.common/street]) (get-in operator [::t-operator/visiting-address :ote.db.common/street]))
    (operator-row (tr [:field-labels :ote.db.common/postal_code]) (get-in operator [::t-operator/visiting-address :ote.db.common/postal_code]))
    (operator-row (tr [:field-labels :ote.db.common/post_office]) (get-in operator [::t-operator/visiting-address :ote.db.common/post_office]))
    (operator-row (tr [:field-labels ::t-operator/homepage]) (::t-operator/homepage operator))]

   [:div.row {:style {:padding-top "50px"}}
    (when (< 0 (count (get-in operator [::t-operator/ckan-group ::t-operator/description])))
      [:p [:b (tr [:field-labels :ote.db.transport-operator/ckan-description])]])
    [:p
     (get-in operator [::t-operator/ckan-group ::t-operator/description])]]
   [:div {:style {:padding-top "10px"}}
    (show-service-count-link e! operator)]])


(defn operator-modal [e! operator]
  (when (:show-modal? operator)
    [ui/dialog
     {:open true
      :actionsContainerStyle style-dialog/dialog-action-container
      :modal false
      :auto-scroll-body-content true
      :title (::t-operator/name operator)
      :on-request-close #(e! (operators-controller/->CloseOperatorModal (::t-operator/id operator)))
      :actions [(r/as-element
                  [ui/flat-button
                   {:label (tr [:buttons :close])
                    :secondary true
                    :primary true
                    :on-click #(e! (operators-controller/->CloseOperatorModal (::t-operator/id operator)))}])]}

     (show-operator-data e! operator)]))


(defn operators-list [e! operators]
  [:div.row.operator-list
   (doall
     (for [{::t-operator/keys [id name business-id homepage email
                               phone gsm visiting-address service-count
                               ckan-group] :as operator} operators]
       ^{:key (str "operator-" id)}
       [:div {:class "col-md-6 operator" :style {:padding "10px 10px 0px 0px"}}
        [ui/paper {:z-depth 1
                   :style   {:min-height "155px"}}
         [:div.operator-header (stylefy/use-style style-service-search/operator-result-header)
          [:a
           {:href     "#"
            :on-click #(do (.preventDefault %)
                           (e! (operators-controller/->OpenOperatorModal id)))}
           [:span (stylefy/use-style style-service-search/operator-result-header-link) name]]]
         [:div (stylefy/use-style style-service-search/operator-description)
          [:div
           (if (< 120 (count (::t-operator/description ckan-group)))
             [:span (text/shorten-text-to 120 (::t-operator/description ckan-group))
              [:br]
              [:a.operator-link {:href     "#/operators"
                                 :on-click #(do (.preventDefault %)
                                                (e! (operators-controller/->OpenOperatorModal id)))}
               (tr [:operators :description-read-more])]]
             (::t-operator/description ckan-group))]
          (operator-modal e! operator)
          [:div {:style {:position "absolute" :bottom "5px"}}
           (show-service-count-link e! operator)]]]]))])

(defn operators [e! _]
  (e! (operators-controller/->Init))
  (fn [e! {operators :operators :as app}]
    [:div.operators
     [:h1 (tr [:operators :title])]
     [:div.row.form-field {:class "col-xs-12 col-md-6"}
      [form-fields/field {:type :string
                         :full-width? true
                         :label (tr [:operators :filter])
                         :update! #(e! (operators-controller/->UpdateOperatorFilter %))}
      (:filter operators)]]
      [:div.row.col-md-12
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
            [common/scroll-sensor #(e! (operators-controller/->FetchMore))]))])]]))
