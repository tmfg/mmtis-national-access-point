(ns ote.integration.import.ytj-test
  (:require  [clojure.test :as t]
             [ote.integration.import.ytj :as ytj]
             [com.stuartsierra.component :as component]
             [ote.test :as ote-test :refer [fetch-id-for-username]]
             [cheshire.core :refer [encode]]
             [clj-http.client :as http-client]))

(t/use-fixtures :each
  (ote-test/system-fixture
   :operators (component/using (ytj/->YTJFetch {:enabled-features #{:open-ytj-integration}}) [:db :http])))

(t/deftest expired-removal
  (let [test-input {:foo [{:stuff 1 :endDate nil}
                          {:stuff 2 :endDate "2012-12-12"}
                          {:stuff 3 :endDate "2077-12-21"}]
                    :bar {:stuff 4 :endDate "12-12-12"}} ;; left alone because not in vector
        reference-output {:foo [{:stuff 1, :endDate nil}
                                {:stuff 3, :endDate "2077-12-21"}]
                          :bar {:stuff 4 :endDate "12-12-12"}}]
    (t/is (= reference-output (ytj/without-expired-items test-input)))))


;; remember to eval whole ns along with fixture
(t/deftest request-made
  (with-redefs [ytj/http-get (fn [url opts]
                               {:status 200
                                :headers {"Content-Type" "application/json"}
                                :body {:totalResults "1"
                                       :results [{:name "Acme"
                                                  :addresses {:endDate "2012-12-12"}
                                                  :auxiliaryNames [{:endDate "2012-12-12"}]}]}})]
    (let [result (ote-test/http-get (fetch-id-for-username (:db ote.test/*ote*) "normaluser")
                                    "fetch/ytj?company-id=123132-12")]
      (t/is (= "Acme" (-> result :transit :name)))
      (t/is (not-empty (-> result :transit :addresses)))
      (t/is (empty? (-> result :transit :auxiliaryNames))))))

