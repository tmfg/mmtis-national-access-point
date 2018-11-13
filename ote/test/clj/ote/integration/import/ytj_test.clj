(ns ote.integration.import.ytj-test
  (:require  [clojure.test :as t]
             [ote.integration.import.ytj :as sut]
             [com.stuartsierra.component :as component]
             [ote.test :as ote-test]
             [cheshire.core :refer [encode]]
             [clj-http.client :as http-client]))

(t/use-fixtures :each
  (ote-test/system-fixture
   :operators (component/using (sut/->YTJFetch) [:db :http])))

(t/deftest expired-removal
  (let [test-input {:foo [{:stuff 1 :endDate nil}
                     {:stuff 2 :endDate "2012-12-12"}
                          {:stuff 3 :endDate "2077-12-21"}]}
        reference-output {:foo [{:stuff 1, :endDate nil} {:stuff 3, :endDate "2077-12-21"}]}]
    (t/is (= reference-output (sut/without-expired-items test-input)))))


;; remember to eval whole ns along with fixture
(t/deftest request-made
  (with-redefs [sut/http-get (fn [url opts]
                               {:status 200
                                :headers {"Content-Type" "application/json"}
                                :body (encode {:totalResults "1"
                                               :results [{:name "Acme"}]})})]
    (let [result (ote-test/http-get "normaluser" "fetch/ytj?company-id=123132-12")]
      ;;(t/is (=  {:name "Acme"}))
      (println "result was" (pr-str result))
      (t/is (= (:name result) "Acme"))
      )
    )
  )

