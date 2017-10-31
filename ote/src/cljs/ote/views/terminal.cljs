(ns ote.views.terminal
  "Required datas for port, station and terminal service"
  (:require [reagent.core :as reagent]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.form :as form]
            [ote.ui.form-groups :as form-groups]
            [ote.ui.napit :as napit]
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
                  [napit/tallenna {:on-click #(e! (terminal/->SaveTerminalToDb))
                                   :disabled (form/disable-save? data)}
                   (tr [:buttons :save])])})

(defn name-and-type-group [e!]
  (form/group
    {:label "Palvelun perustiedot"
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

(defn contact-info-group []
  (form/group
    {:label   "Palvelun yhteystiedot"
     :columns 3
     :layout :row}


    {:name        ::common/street
     :type        :string
     :read (comp ::common/street ::t-service/contact-address)
     :write (fn [data street]
              (assoc-in data [::t-service/contact-address ::common/street] street))
     :label (tr [:field-labels ::common/street])
     :required? true}

    {:name        ::common/postal_code
     :type        :string
     :read (comp ::common/postal_code ::t-service/contact-address)
     :write (fn [data postal-code]
              (assoc-in data [::t-service/contact-address ::common/postal_code] postal-code))
     :label (tr [:field-labels ::common/postal_code])
     :required? true
     :validate [[:postal-code]]}

    {:name        ::common/post_office
     :type        :string
     :read (comp ::common/post_office ::t-service/contact-address)
     :write (fn [data post-office]
              (assoc-in data [::t-service/contact-address ::common/post_office] post-office))
     :label (tr [:field-labels ::common/post_office])
     :required? true}

    {:name        ::t-service/contact-email
     :type        :string}

    {:name        ::t-service/contact-phone
     :type        :string}

    {:name        ::t-service/homepage
     :type        :string}
    ))


(defn terminal [e! status]
  [:div.row
   [:div {:class "col-lg-12"}
    [:div
     [:h3 "Lisää Satamaan, Asemaan tai Terminaaliin liittyvän palvelun tiedot."]]
    [form/form (terminal-form-options e!)

     [
      (name-and-type-group e!)
      (place-marker-group e!)
      (form-groups/service-url
        (tr [:field-labels :terminal ::t-service/indoor-map])
        ::t-service/indoor-map)

      (form/group
        {:label   "Muut palvelut ja esteettömyys"
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
         :options     t-service/mobility}
        )

      (form/group
        {:label   "Kuvaus esteettömyyspalveluista"
         :columns 3
         :layout  :row}

        {:name ::t-service/accessibility-description
         :type :localized-text
         :rows 1 :max-rows 5}
        )

      (contact-info-group)
     ]

     (get status ::t-service/terminal)]

    ]])
