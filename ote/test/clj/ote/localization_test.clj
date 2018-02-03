(ns ote.localization-test
  (:require [clojure.test :as t :refer [deftest is]]
            [ote.localization :as localization]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(def languages ["fi" "sv" "en"])

(defn- deep-key-paths [prefix-path m]
  (reduce-kv (fn [key-paths key val]
               ;; if val is map, recurse
               (into key-paths
                     (if (map? val)
                       (deep-key-paths (conj prefix-path key) val)
                       [(conj prefix-path key)])))
             #{} m))

(defn- load-test-edn []
  (-> (slurp (str "test/resources/lang.edn"))
      read-string))


(deftest handle-unsupported-tr-operation
  (let [translations (load-test-edn)]
    (is (= (#'localization/message (get-in translations [:unsupported-op]) {})
           (str "{{unknown translation operation " :no-op "}}")))))

(deftest concatenate-tr-vec
  (let [translations (load-test-edn)]
    (is (= (#'localization/message (get-in translations [:vec]) {})
           "This is a vector"))))

(deftest handle-tr-plurals
  (let [translations (load-test-edn)]
    (is (= (#'localization/message (get-in translations [:plural]) {:count 0})
           "Got no results"))
    (is (= (#'localization/message (get-in translations [:plural]) {:count 1})
           "Got one result"))
    (is (= (#'localization/message (get-in translations [:plural]) {:count 2})
           "Got 2 results"))))

;; TODO: Needs cljs testing support for more thorough testing of this feature.
(deftest tr-markdown-cljs-only
  (let [translations (load-test-edn)]
    (is (thrown-with-msg?
          clojure.lang.ExceptionInfo #"Markdown formatted translations not supported."
          (#'localization/message (get-in translations [:markdown]) {})))))

(deftest all-languages-have-same-keys
  (let [langs (atom {})]
    (doseq [lang languages]
      (localization/load-language! lang (fn [_ translation]
                                          (swap! langs assoc lang (deep-key-paths [] translation)))))
    (let [langs @langs
          fi-key-paths (langs "fi")]
      (doseq [[lang lang-key-paths] langs]
        (doseq [key-path fi-key-paths]
          (is (lang-key-paths key-path)
              (str "Translation for " key-path " missing in language " lang)))
        (doseq [key-path lang-key-paths]
          (is (fi-key-paths key-path)
              (str "Extra key " key-path " in language " lang)))))))
