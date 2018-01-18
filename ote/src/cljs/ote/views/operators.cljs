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
    (for [{::t-operator/keys [id name business-id homepage
                              email phone gsm visiting-address]} operators]
      ^{:key id}
      [ui/card
       [ui/card-header {:title name :subtitle homepage}]
       [ui/card-text
        [:div "email: " email
         "phone: " phone
         "gsm: " gsm
         "visiting-address: " (str visiting-address)]]]))])

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

       (empty? (:results operators))
       [:div "Hakuehdoilla ei löytynyt operaattoreita XXX"]

       :default
       [:div
        (if (str/blank? (:filter operators))
          (str "Yhteensä " (count (:results operators)) " palveluntuottajaa XXX.")
          (str "Hakuehdoilla löytyi " (count (:results operators)) " palveluntuottajaa"))
        [operators-list e! (:results operators)]])]))
