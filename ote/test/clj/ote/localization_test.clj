(ns ote.localization-test
  (:require  [clojure.test :as t :refer [deftest is]]
             [ote.localization :as localization]
             [clojure.set :as set]))

(def languages ["fi" "sv" "en"])

(defn- deep-key-paths [prefix-path m]
  (reduce-kv (fn [key-paths key val]
               ;; if val is map, recurse
               (into key-paths
                     (if (map? val)
                       (deep-key-paths (conj prefix-path key) val)
                       [(conj prefix-path key)])))
             #{} m))

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
