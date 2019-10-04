(ns ote.app.controller.common
  "Common controller functionality"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr]]
            [ote.communication :as comm]
            [ote.app.routes :refer [navigate!]]))

(defn error-landing [app landing]
  (navigate! :error-landing)
  (assoc app :error-landing landing))

(defn- handle-error
  ([app response]
   (handle-error app response {}))
  ([app response landing]
   (case (:status response)
     403 (assoc app :flash-message-error (tr [:common-texts :forbidden]))
     503 (error-landing app (update landing
                                    :desc
                                    #(or %
                                         (tr [:error-landing :txt-maintenance-break]))))
     (assoc app :flash-message-error (tr [:common-texts :server-error])))))

(tuck/define-event ServerErrorDetails [response landing]
                   {}
                   (handle-error app response landing))

(tuck/define-event ServerError [response]
                   {}
                   (handle-error app response))

(tuck/define-event CountryListResponse [response]
 {}
 (assoc app :country-list response
            :country-list-loaded? true))

(defn get-country-list [app]
  (comm/get! "country-list"
             {:on-success (tuck/send-async! ->CountryListResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  (assoc app :country-list-loaded? false))