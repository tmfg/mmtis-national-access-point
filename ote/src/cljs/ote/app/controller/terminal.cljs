(ns ote.app.controller.terminal
  "Port, Station and Terminal service controls "
  (:require [tuck.core :as t]
            [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.app.controller.place-search :as place-search]
            [ote.app.controller.transport-service :as transport-service]
            [ote.app.routes :as routes]))

(defrecord EditTerminalState [data])

(extend-protocol t/Event

  EditTerminalState
  (process-event [{data :data} app]
    (update-in app [:transport-service ::t-service/terminal] merge data)))
