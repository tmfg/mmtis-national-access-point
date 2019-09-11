(ns ote.app.controller.common
  "Common controller functionality"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr]]
            [ote.app.routes :refer [navigate!]]))

(defn error-landing [app txt]
  (navigate! :error-landing)
  (assoc-in app [:error-landing :desc] txt))

(tuck/define-event ServerError [response]
  {}
  (case (:status response)
    403 (assoc app :flash-message-error (tr [:common-texts :forbidden]))
    503 (error-landing app (tr [:error-landing :txt-maintenance-break]))
    :else (assoc app :flash-message-error (tr [:common-texts :server-error]))))

(defn user-logged-in? [app]
  (not-empty (get-in app [:user])))
