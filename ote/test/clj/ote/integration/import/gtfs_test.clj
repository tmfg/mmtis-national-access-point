(ns ote.integration.import.gtfs_test
  (:require [ote.integration.import.gtfs :as gtfs]
            [clojure.test :as t :refer [use-fixtures deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [ote.test :refer [system-fixture *ote* http-post http-get sql-execute!]]
            [com.stuartsierra.component :as component]
            [ote.services.transport :as transport-service]
            [ote.db.service-generators :as service-generators]
            [clojure.test.check.generators :as gen]
            [ote.db.transport-service :as t-service]
            [ote.db.places :as places]
            [ote.db.generators :as generators]
            [ote.integration.export.transform :as transform]
            [ote.time :as time]
            [webjure.json-schema.validator.macro :refer [make-validator]]
            [cheshire.core :as cheshire]
            [taoensso.timbre :as log]
            [clojure.string :as str]))


