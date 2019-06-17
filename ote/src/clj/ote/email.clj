(ns ote.email
  "Email sending utilities."
  (:require [postal.core :as postal]
            [ote.nap.cookie :as nap-cookie]
            [ote.nap.users :as nap-users]
            [taoensso.timbre :as log]
            [ote.localization :as localization]
            [com.stuartsierra.component :as component]))

(defprotocol Send
  (send! [this message]
    "Send a single email.
  Message can have the following keys.

  Sender and recipient:
  :from, :to, :cc, :bcc

  Message subject and content:
  :subject, :body"))

(defn- send-email
  "Send a singular email using Postal."
  [server msg]
  (if (-> server :host some?)
    (postal/send-message server msg)
    (log/warn "not sending email because configured smtp host is empty")))

(defrecord Email [email-opts]
  component/Lifecycle
  (start [this] this)
  (stop [this] this)

  Send
  (send! [{{:keys [server msg]} :email-opts} message]
    (send-email server
                (merge msg message))))
