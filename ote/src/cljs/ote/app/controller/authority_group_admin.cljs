(ns ote.app.controller.authority-group-admin
  (:require [ote.app.controller.common :refer [->ServerError]]
            [tuck.core :as tuck :refer-macros [define-event]]
            [tuck.effect :as tuck-effect]
            [ote.communication :as comm]
            [ote.util.url :as url-util]
            [ote.app.controller.operator-users :as op-users]
            [ote.app.controller.front-page :as fp]))

(defrecord LoadAuthorityGroupDetails [])
(defrecord LoadAuthorityGroupDetailsResponse [])

(extend-protocol tuck/Event

  LoadAuthorityGroupDetailsResponse
  (process-event [{response :response} app]
    (assoc-in app [:admin :authority-group] response))

  LoadAuthorityGroupDetails
  (process-event [_ app]
    #_(comm/get! "/admin/authority-group"
               {:on-success (tuck/send-async! ->LoadAuthorityGroupDetailsResponse)
                :on-failure (tuck/send-async! ->ServerError)})
    (tuck/fx
      ; this allows us to piggyback the authority group id and completely reuse the normal operator members editing view
      (assoc-in app [:params :ckan-group-id] (get-in app [:authority-group-id]))
      (fn [e!]
        (e! (op-users/->InitTransportUserView))))))