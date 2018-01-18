(ns ote.views.operators
  "Transport operator listing view. Shows a filterable listing
  of all service providers."
  (:require [ote.app.controller.operators :as operators-controller]
            [cljs-react-material-ui.reagent :as ui]
            [ote.ui.form-fields :as form-fields]
            [ote.localization :refer [tr]]
            [clojure.string :as str]
            [ote.db.transport-operator :as t-operator]))

(defn operators-list [e! operators]
  [:div
   (doall
    (for [{::t-operator/keys [id name business-id homepage email
                              phone gsm visiting-address service-count]} operators]
      ^{:key id}
      [ui/card
       [ui/card-header {:title name :subtitle homepage}]
       [ui/card-text
        [:div
         [:div service-count " palvelua"]
         (when email [:span "email: " email])
         (when phone [:span "phone: " phone])
         (when gsm [:span  "gsm: " gsm])
         (when visiting-address [:span  "visiting-address: " (str visiting-address)])]]]))])

(defn operators [e! _]
  (e! (operators-controller/->Init))
  (fn [e! {operators :operators :as app}]
    [:div.operators
     [form-fields/field {:type :string
                         :label "Hae nimen osalla XXX"
                         :update! #(e! (operators-controller/->UpdateOperatorFilter %))}
      (:filter operators)]

     (cond
       (:loading? operators)
       [:div "Haetaan palveluntuottajia... XXX"]

       (zero? (:total-count operators))
       [:div "Hakuehdoilla ei löytynyt operaattoreita XXX"]

       :default
       [:div
        (if (str/blank? (:filter operators))
          (str "Yhteensä " (:total-count operators) " palveluntuottajaa XXX.")
          (str "Hakuehdoilla löytyi " (:total-count operators) " palveluntuottajaa"))
        [operators-list e! (:results operators)]])]))
