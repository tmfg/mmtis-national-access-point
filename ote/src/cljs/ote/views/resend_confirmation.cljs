(ns ote.views.resend-confirmation
  (:require [ote.app.utils :as utils]
            [ote.ui.form-fields :as form-fields]
            [ote.localization :as localization :refer [tr]]
            [ote.app.controller.resend-confirmation :as rc-controller]
            [ote.ui.buttons :as buttons]
            [ote.ui.notification :as notification]))

(defn email-confirmation-form
  [e! {:keys [send-confirmation] :as state}]
  (let [input-value (:confirmation-email send-confirmation)
        submit-success? (:success? send-confirmation)]
    [:div
     [:h1 (tr [:register :resend-confirmation])]
     (when submit-success?
       [notification/notification {:text (tr [:register :resend-success] {:email input-value})
                                   :type :success}])
     [:form {:on-submit (fn [e]
                          (.preventDefault e)
                          (when (and
                                  (some? input-value)
                                  (re-matches utils/email-regex input-value)
                                  (not submit-success?))
                            (e! (rc-controller/->SendConfirmation input-value @localization/selected-language))))}
      [form-fields/field {:element-id "user-email"
                          :type :string
                          :full-width? true
                          :disabled? submit-success?
                          :name :add-member
                          :update! #(e! (rc-controller/->EmailFieldOnChange %))
                          :label (tr [:register :email-to-be-confirmed])
                          :placeholder (tr [:transport-users-page :email-placeholder])}
       input-value]
      [:div {:style {:display "flex"
                     :align-items "center"}}
       [buttons/save
        {:disabled (not (and
                          (some? input-value)
                          (re-matches utils/email-regex input-value)
                          (not submit-success?)))}
        (tr [:common-texts :send-new-message])]]]]))
