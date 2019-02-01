;;(ns ote.services.operators-test
;;  (:require [ote.services.operators :as sut]
;;            [ote.test :refer [system-fixture http-get http-post]]
;;            [clojure.test :as t :refer [deftest is]]
;;            [com.stuartsierra.component :as component]
;;            [ote.db.transport-operator :as t-operator]))
;;
;;(t/use-fixtures :each
;;  (system-fixture
;;   :operators (component/using (sut/->Operators) [:db :http])))
;;
;;
;;(deftest operators-list
;;  (let [result (:transit (http-post "normaluser" "operators/list"
;;                                    {:filter ""
;;                                     :limit 10
;;                                     :offset 0}))]
;;    (is (pos? (:total-count result)))
;;    (is (some #(= (::t-operator/name %) "Ajopalvelu Testinen Oy") (:results result)))))
