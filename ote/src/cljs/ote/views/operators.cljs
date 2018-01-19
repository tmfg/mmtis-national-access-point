(ns ote.views.operators
  "Transport operator listing view. Shows a filterable listing
  of all service providers."
  (:require [ote.app.controller.operators :as operators-controller]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form-fields :as form-fields]
            [ote.localization :refer [tr]]
            [clojure.string :as str]
            [ote.db.transport-operator :as t-operator]
            [ote.style.service-search :as style-service-search]
            [stylefy.core :as stylefy]
            [ote.views.service-search :as service-search]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :as common]))

(defn operators-list [e! operators]
  [:div
   (doall
    (for [{::t-operator/keys [id name business-id homepage email
                              phone gsm visiting-address service-count]} operators]
      ^{:key id}
      [ui/paper {:z-depth 1
                 :style style-service-search/result-card}
       [:div (stylefy/use-style style-service-search/result-header)
        [:div [:a {:href "#"
                   :on-click #(do
                                (.preventDefault %)
                                (e! :FIXME))}
               name]
         " "
         (tr [:operators :result-service-count] {:service-count service-count})]

        [service-search/data-items
         [ic/content-link {:style style-service-search/contact-icon}]
         (when homepage [common/linkify homepage homepage {:target "_blank"}])

         [ic/action-home {:style style-service-search/contact-icon}]
         (service-search/format-address visiting-address)

         [ic/communication-phone {:style style-service-search/contact-icon}]
         phone

         [ic/communication-phone {:style style-service-search/contact-icon}]
         (when (not= gsm phone) gsm) ; only show if different number than phone

         [ic/communication-email {:style style-service-search/contact-icon}]
         email]]]))])

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
        (tr [:operators (if (str/blank? (:filter operators))
                          :result-count-all
                          :result-count)]
            {:total-count (:total-count operators)})
        [operators-list e! (:results operators)]
        (when (> (:total-count operators) (count (:results operators)))
          (if (:loading? operators)
            [:div (tr [:operators :loading])]
            [common/scroll-sensor #(e! (operators-controller/->FetchMore))]))])]))
