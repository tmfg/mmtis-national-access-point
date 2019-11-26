(ns ote.views.transport-service
  "Transport service related functionality"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.db.transport-service :as t-service]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [ote.style.form :as style-form]
            [ote.ui.circular_progress :as circular-progress]
            [ote.ui.info :as info]
            [ote.ui.form-fields :as form-fields]
            [ote.ui.common :as ui-common]
            [ote.app.controller.transport-service :as ts]
            [ote.app.controller.transport-operator :as to]
            [ote.views.passenger-transportation :as pt]
            [ote.views.terminal :as terminal]
            [ote.views.parking :as parking]
            [ote.views.rental :as rental]))

(def modified-transport-service-types
  ;; Create order for service type selection dropdown
  [:taxi
   :request
   :schedule
   :terminal
   :rentals
   :parking])

(defn select-service-type [e! {:keys [transport-operator transport-service] :as state}]
  (let [disabled? (or (nil? (::t-service/sub-type transport-service))
                      (nil? (::t-operator/id transport-operator)))]
  [:div.row
   [:div {:class "col-sx-12 col-md-12"}
    [:div
     [:h1 (tr [:select-service-type-page :title-required-data])]]
    [:div.row
     [info/info-toggle
      (tr [:common-texts :instructions])
      [:span
       [:p (tr [:select-service-type-page :transport-service-type-selection-help-text])]
       [:p (tr [:select-service-type-page :transport-service-type-brokerage-help-text])]
       [:p {:style {:font-style "italic"}}
        (tr [:select-service-type-page :transport-service-type-selection-help-example])]]
      {:default-open? false}]]

    [:div.row
        [:div
          [:div {:class "col-sx-12 col-sm-4 col-md-4"}
          [form-fields/field

           {:label (tr [:field-labels :transport-service-type-subtype])
             :name        ::t-service/sub-type
             :type        :selection
             :update!      #(e! (ts/->SelectServiceType %))
             :show-option (tr-key [:enums ::t-service/sub-type])
             :options     modified-transport-service-types
             :auto-width? true}

           (::t-service/sub-type transport-service)]]

           [:div {:class "col-sx-12 col-sm-4 col-md-4"}
             [form-fields/field
              {:label (tr [:field-labels :select-transport-operator])
               :name        :select-transport-operator
               :type        :selection
               :update!     #(e! (to/->SelectOperator %))
               :show-option ::t-operator/name
               :options     (mapv :transport-operator (:transport-operators-with-services state))
               :auto-width? true}

              transport-operator]]]]
    [:div.row
     [:div {:class "col-sx-12 col-sm-4 col-md-4"}
      [ui/raised-button {:id "own-service-next-btn"
                         :style {:margin-top "20px"}
                         :label    (tr [:buttons :next])
                         :on-click #(e! (ts/->NavigateToNewService))
                         :primary  true
                         :disabled disabled?}]]]]]))

(defn edit-service-header-text [service-type]
  (case service-type
    :passenger-transportation (tr [:passenger-transportation-page :header-edit-passenger-transportation])
    :terminal (tr [:terminal-page :header-edit-terminal])
    :rentals (tr [:rentals-page :header-edit-rentals])
    :parking (tr [:parking-page :header-edit-parking])))

(defn new-service-header-text [service-type]
  (case service-type
    :passenger-transportation (tr [:passenger-transportation-page :header-new-passenger-transportation])
    :terminal (tr [:terminal-page :header-new-terminal])
    :rentals (tr [:rentals-page :header-new-rentals])
    :parking (tr [:parking-page :header-new-parking])))

(defn- license-info []
  [:p {:style {:padding-top "20px"
               :padding-bottom "20px"}}
   (tr [:common-texts :nap-data-license])
   [ui-common/linkify (tr [:common-texts :nap-data-license-url]) (tr [:common-texts :nap-data-license-url-label]) {:target "_blank"}]
   "."])

(defn edit-service [e! type {service :transport-service :as app}]
  [:span
   [license-info]
   (case type
     :passenger-transportation [pt/passenger-transportation-info e! (:transport-service app) app]
     :terminal [terminal/terminal e! (:transport-service app) app]
     :rentals [rental/rental e! (:transport-service app) app]
     :parking [parking/parking e! (:transport-service app) app])])

(defn edit-service-by-id [e! {loaded? :transport-service-loaded? service :transport-service :as app}]
  (if (or (nil? service) (not loaded?))
    [circular-progress/circular-progress]
    ;; Render transport service page only if given id matches with the loaded id
    ;; This will prevent page render with "wrong" or "empty" transport-service data
    (when (= (get-in app [:params :id]) (str (get-in app [:transport-service ::t-service/id])))
      [:div
       [ui-common/rotate-device-notice]

       [:h1 (edit-service-header-text (keyword (::t-service/type service)))]
       ;; Passenger transport service has sub type, and here it is shown to users
       (when (= :passenger-transportation (keyword (::t-service/type service)))
         [:p (stylefy/use-style style-form/subheader)
          (tr [:enums :ote.db.transport-service/sub-type
               (get-in app [:transport-service ::t-service/sub-type])])])
       ;; Show service owner name only for service owners
       (when (ts/is-service-owner? app)
         [:h2 (get-in app [:transport-operator ::t-operator/name])])
       ;; Render the form
       [edit-service e! (::t-service/type service) app]])))


(defn create-new-service
  "Render container and headers for empty service form"
  [e! app]
  (when (get-in app [:transport-service ::t-service/type])
    (let [service-sub-type (get-in app [:transport-service ::t-service/sub-type])
          service-type (get-in app [:transport-service ::t-service/type])
          new-header-text (new-service-header-text service-type)]

      [:div
       [ui-common/rotate-device-notice]

       [:h1 new-header-text]
       ;; Passenger transport service has sub type, and here it is shown to users
       (when (= :passenger-transportation service-type)
         [:p (stylefy/use-style style-form/subheader)
          (tr [:enums :ote.db.transport-service/sub-type
               (get-in app [:transport-service ::t-service/sub-type])])])

       [:div.row
        [:h2 (get-in app [:transport-operator ::t-operator/name])]]
       [edit-service e! service-type  app]])))
