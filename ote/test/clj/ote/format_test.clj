(ns ote.format-test
  (:require  [clojure.test :as t :refer [deftest is]]
             [ote.format :as sut]))

(deftest formatting-municipalities-with-postal-code
  (is (= "Takalo 89660" (sut/postal-code-at-end "89660 Takalo"))
      (= "Mänttälä-Vilppula 76606" (sut/postal-code-at-end "76606 Mänttälä-Vilppula"))))

(deftest formatting-municipalities-without-postal-code
  (is (= "Suomussalmi" (sut/postal-code-at-end "Suomussalmi")))
  (is (= "Keski-Suomi" (sut/postal-code-at-end "Keski-Suomi")))
  (is (= "Luoteinen Aasia" (sut/postal-code-at-end "Luoteinen Aasia"))))
