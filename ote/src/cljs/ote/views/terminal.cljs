(ns ote.views.terminal
  "Required datas for port, station and terminal service"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.buttons :as buttons]
            [ote.app.controller.terminal :as terminal]
            [ote.db.transport-service :as t-service]
            [ote.db.common :as common]
            [ote.localization :refer [tr tr-key]]
            [ote.views.place-search :as place-search]
            [tuck.core :as tuck]
            [ote.style.base :as style-base]))

(defn terminal-form-options [e!]
  {:name->label (tr-key [:field-labels :terminal] [:field-labels :transport-service-common])
   :update!     #(e! (terminal/->EditTerminalState %))
   :name        #(tr [:olennaiset-tiedot :otsikot %])
   :footer-fn   (fn [data]
                  [buttons/save {:on-click #(e! (terminal/->SaveTerminalToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn name-and-type-group [e!]
  (form/group
    {:label (tr [:terminal-page :header-service-info])
     :columns 3
     :layout :row}

    {:name ::t-service/name
     :type :string
     :required? true}))

(defn place-marker-group [e!]
  (place-search/place-marker-form-group
    (tuck/wrap-path e! :transport-service ::t-service/terminal ::t-service/operation-area)
    (tr [:field-labels :transport-service-common ::t-service/location])
    ::t-service/operation-area))

(defn- indoor-map-group []
  (form-groups/service-url
   (tr [:field-labels :terminal ::t-service/indoor-map])
   ::t-service/indoor-map))

(defn- accessibility-and-other-services-group []
  (form/group
   {:label (tr [:terminal-page :header-other-services-and-accessibility])
    :columns 3
    :layout :row}

   {:style       style-base/long-drowpdown ;; Pass only style from stylefy base
    :name        ::t-service/information-service-accessibility
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/information-service-accessibility])
    :options     t-service/information-service-accessibility}

   {:style style-base/long-drowpdown ;; Pass only style from stylefy base
    :name        ::t-service/accessibility-tool
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/accessibility-tool])
    :options     t-service/accessibility-tool}

   {:style style-base/long-drowpdown ;; Pass only style from stylefy base
    :name        ::t-service/accessibility
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/accessibility])
    :options     t-service/accessibility}

   {:style style-base/long-drowpdown ;; Pass only style from stylefy base
    :name        ::t-service/mobility
    :type        :multiselect-selection
    :show-option (tr-key [:enums ::t-service/mobility])
    :options     t-service/mobility}))

(defn- accessibility-description-group []
  (form/group
   {:label (tr [:terminal-page :header-accessibility-description])
    :columns 3
    :layout :row}

   {:name ::t-service/accessibility-description
    :type :localized-text
    :rows 1 :max-rows 5}))

(defn terminal [e! {form-data ::t-service/terminal}]
  (r/with-let [options (terminal-form-options e!)
               groups [(name-and-type-group e!)
                       (form-groups/contact-info-group)
                       (place-marker-group e!)
                       (indoor-map-group)
                       (accessibility-and-other-services-group)
                       (accessibility-description-group)]]
    [:div.row
     [:div {:class "col-lg-12"}
      [:div
       [:h3 (tr [:terminal-page :header-add-new-terminal])]]
      [form/form options groups form-data]]]))
