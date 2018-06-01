(ns ote.app.controller.common
  "Common controller functionality"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr]]))

(tuck/define-event ServerError [response]
  {}
  (assoc app :flash-message-error (tr [:common-texts :server-error])))
