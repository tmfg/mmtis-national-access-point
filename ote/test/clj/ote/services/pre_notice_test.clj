(ns ote.services.pre-notice-test
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [com.stuartsierra.component :as component]
            [ote.db.transport-service :as transit]
            [ote.db.service-generators :as s-generators]
            [ote.services.pre-notices :as pre-notices]))

(t/use-fixtures :each
                (system-fixture
                  :pre-notices (component/using (pre-notices/->PreNotices
                                                  (:pre-notices (read-string (slurp "config.edn")))) [:http :db])))

(deftest save-pre-notice
  (let [generated-notice (gen/generate s-generators/gen-pre-notice)
        response (http-post "admin" "pre-notice" generated-notice)
        notice (:transit response)
        id (get notice :ote.db.transit/id)]
    ;; Saved ok
    (is (pos? id))))