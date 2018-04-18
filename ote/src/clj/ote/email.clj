(ns ote.email
  "Email sending utilities."
  (:require [postal.core :as postal]
            [ote.nap.cookie :as nap-cookie]
            [ote.nap.users :as nap-users]
            [taoensso.timbre :as log]
            [ote.localization :as localization]))

(defonce default-conn {:host "localhost"})


(defn send
  "Send a singular email"
  [email conn]
  (postal/send-message (merge default-conn conn) email))