(ns ote.app.controller.common
  "Common controller functionality"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr]]
            [ote.app.routes :refer [navigate!]]))

(defn error-landing [app txt]
  (navigate! :error-landing)
  (assoc-in app [:error-landing :desc] txt))

(defn- handle-error
  ([app response]
   (handle-error app response nil))
  ([app response args]
   (case (:status response)
     403 (assoc app :flash-message-error (tr [:common-texts :forbidden]))
     503 (error-landing app (or (:desc args)
                                (tr [:error-landing :txt-maintenance-break])))
     (assoc app :flash-message-error (tr [:common-texts :server-error])))))

(tuck/define-event ServerErrorDetails [response args]
                   {}
                   (handle-error app response args))

(tuck/define-event ServerError [response]
                   {}
                   (handle-error app response))
