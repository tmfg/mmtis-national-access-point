(ns ote.app.controller.flags
  (:require [tuck.core :as tuck]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [clojure.string :as str]
            [ote.communication :as comm]
            [clojure.string :as s]))

(def enabled-flags
  (delay (into #{}
               (map keyword)
               (str/split (.getAttribute js/document.body "features") #","))))


(defn enabled?
  "Check if given feature (f) is enabled in production"
  [f]
  (boolean (@enabled-flags f)))
