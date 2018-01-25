(ns ote.app.controller.flags
  (:require [tuck.core :as tuck]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [clojure.string :as str]
            [ote.communication :as comm]
            [clojure.string :as s]))

(defn true-string? [s] (= s "true"))

(defn is-in-production? []
  (let [page-url (-> (.-location js/window))]
    (if (or (s/includes? (str page-url) "testi") (s/includes? (str page-url) "localhost"))
      false
      true)))

(defn use-in-production?
  "Check if given feature (f) is enabled in production"
  [f]
  (let [features  (into #{}  (str/split (.getAttribute js/document.body "features") #","))]
    ;; Check environment - if in production use config.edn value, if not, return true
    (if (is-in-production?)
      (contains? features f)
      true)))