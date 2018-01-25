(ns ote.app.controller.flags
  (:require [tuck.core :as tuck]
            [ote.db.transport-service :as t-service]
            [ote.localization :refer [tr tr-key]]
            [clojure.string :as str]
            [ote.communication :as comm]
            [clojure.string :as s]))

(defn true-string? [s] (= s "true"))

(defn is-in-production []
  (let [page-url (-> (.-location js/window))]
    (if (or (s/includes? (str page-url) "testi") (s/includes? (str page-url) "localhost"))
      false
      true)))

(defn should-use-new-login []
  (let [new-login  (-> js/document
                       (.getElementById "main-body")
                       (.getAttribute "new-login"))]
    ;; Check environment - if in production use config.edn value, if not, return true
    (if (is-in-production)
      (true-string? new-login)
      true)))