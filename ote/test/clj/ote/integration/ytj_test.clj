(ns ote.integration.ytj-test
  (:require  [clojure.test :as t]
             [ote.integration.import.ytj :as sut]))

(t/deftest expired-removal
  (let [test-input {:foo [{:stuff 1 :endDate nil}
                     {:stuff 2 :endDate "2012-12-12"}
                          {:stuff 3 :endDate "2077-12-21"}]}
        reference-output {:foo [{:stuff 1, :endDate nil} {:stuff 3, :endDate "2077-12-21"}]}])
  (t/is (= reference-output (sut/without-expired-items test-input))))
