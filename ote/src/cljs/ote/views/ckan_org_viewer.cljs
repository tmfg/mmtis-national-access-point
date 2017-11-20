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
            [ote.views.theme :refer [theme]]
            [ote.localization :refer [tr tr-or]]))

(defn transform-val
  "Translate and transform value."
  [key value]
  (tr-or (tr [key value]) (str value)))

(defn basic-info [operator]


  [:div.row.organization-info ;; HOX: I could not make stylefy work via class adding to ckan side.
   [:div.span12 (stylefy/use-style style-ckan/content-title) [:h2 (transform-val :common-texts :title-operator-basic-details)]]

   [:div.span3 [:b (transform-val :field-labels ::to-definitions/name)]]
   [:div.span9 (operator ::to-definitions/name " - ")]

   [:div.span3 [:b (transform-val :field-labels ::to-definitions/business-id)]]
   [:div.span9 (operator ::to-definitions/business-id " - ")]

   [:div.span3 [:b (transform-val :field-labels ::to-definitions/visiting-address)]]
   (let [address (operator ::to-definitions/visiting-address)]
     [:div.span9
      (if address
        (interpose ", " (vals (select-keys address [::common/street, ::common/postal_code, ::common/post_office])))
        "-")])

   [:div.span3 [:b (transform-val :field-labels ::to-definitions/homepage)]]
   [:div.span9 (operator ::to-definitions/homepage " - ")]])


(def contact-methods [::to-definitions/phone
                      ::to-definitions/gsm
                      ::to-definitions/email
                     ; ::to-definitions/facebook
                     ; ::to-definitions/twitter
                     ; ::to-definitions/instant-message
                     ])

(defn contact-info [operator]
  [:div.row
   [:div.span12 (stylefy/use-style style-ckan/content-title)  [:h2 (tr [:organization-page :contact-types])]]

   (doall
     (map (fn [key]
            ^{:key key} [:row.organization-info ;; HOX: I could not make stylefy work via class adding to ckan side.
                         [:div.span3 [:b (transform-val :field-labels key)]]
                         [:div.span9 (operator key " - ")]]) contact-methods))])

(defn viewer [e! status]
  ;; init
  (e! (org-viewer/->StartViewer))
  (fn [e! {:keys [transport-operator] :as app}]
    (if (not-empty transport-operator)
      [theme
       [:div.container
        [basic-info transport-operator]
        [contact-info transport-operator]]]
      [:div.container
       [:div (str (transform-val :common-texts :data-not-found) ".")]])))
