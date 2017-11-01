(ns ote.views.ckan-org-viewer
  "OTE organization data viewerfor CKAN organization info page. (CKAN embedded view)
  Note that this view uses CKAN css classes, mainly bootstrap 2.x."
  (:require [ote.app.controller.ckan-org-viewer :as org-viewer]
            [clojure.string :as str]
            [ote.app.controller.transport-operator :as to]
            [ote.db.transport-operator :as to-definitions]
            [ote.db.common :as common]
            [stylefy.core :as stylefy]
            [ote.style.ckan :as style-ckan]
            [ote.localization :refer [tr tr-or]]))

(defn transform-val
  "Translate and transform value."
  [key value]
  (tr-or (tr [key value]) (str value)))

(defn basic-info [operator]
  [:div.row
   [:div.span12 [:h2 (transform-val :common-texts :title-operator-basic-details)]]

   [:div.span2 [:b (transform-val :field-labels ::to-definitions/name)]]
   [:div.span10 (operator ::to-definitions/name "N/A")]

   [:div.span2 [:b (transform-val :field-labels ::to-definitions/business-id)]]
   [:div.span10 (operator ::to-definitions/business-id "N/A")]

   [:div.span2 [:b (transform-val :field-labels ::to-definitions/visiting-address)]]
   (let [address (operator ::to-definitions/visiting-address)]
     [:div.span10
      (if address
        (interpose ", " (vals (select-keys address [::common/street, ::common/postal_code, ::common/post_office])))
        "N/A")])

   [:div.span2 [:b (transform-val :field-labels ::to-definitions/homepage)]]
   [:div.span10 (operator ::to-definitions/homepage "N/A")]])


(def contact-methods [::to-definitions/phone
                      ::to-definitions/gsm
                      ::to-definitions/email
                      ::to-definitions/facebook
                      ::to-definitions/twitter
                      ::to-definitions/instant-message])

(defn contact-info [operator]
  [:div.row
   [:div.span12 [:h2 "Yhteystavat"]]                        ;FIXME: Translate

   (doall
     (map (fn [key]
            ^{:key key} [:row
                         [:div.span2 [:b (transform-val :field-labels key)]]
                         [:div.span10 (operator key "N/A")]]) contact-methods))])

(defn viewer [e! status]
  ;; init
  (e! (org-viewer/->StartViewer))
  (fn [e! {:keys [transport-operator] :as app}]
    (when transport-operator
      [:div.container
       [basic-info transport-operator]
       [contact-info transport-operator]])))
