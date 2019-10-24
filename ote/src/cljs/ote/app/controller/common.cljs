(ns ote.app.controller.common
  "Common controller functionality"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.localization :refer [tr tr-tree]]
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

(defn country-list [c-list]
  (let [select (first (sort-by first c-list))
        c-list (as-> (rest (sort-by first c-list)) c-list
                     (map first (sort-by second c-list))
                     (concat (conj [] (first select)) c-list))]
    c-list))