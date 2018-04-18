(ns ote.email
  "Email sending utilities."
  (:require [postal.core :as postal]
            [ote.nap.cookie :as nap-cookie]
            [ote.nap.users :as nap-users]
            [taoensso.timbre :as log]
            [ote.localization :as localization]))


(defn send-email
  "Send a singular email using Postal."
  [& args]
  (apply postal/send-message args))