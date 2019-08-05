(ns ote.app.controller.common
  "Common controller functionality"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr]]))

(tuck/define-event ServerError [response]
  {}
  (if (= 403 (:status response))
    (assoc app :flash-message-error (tr [:common-texts :forbidden]))
    (assoc app :flash-message-error (tr [:common-texts :server-error]))))

(defn user-logged-in? [app]
  (not-empty (get-in app [:user])))
