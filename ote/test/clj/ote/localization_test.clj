(ns ote.localization-test
  (:require  [clojure.test :as t :refer [deftest is]]
             [ote.localization :as localization]
             [clojure.set :as set]))

(def languages ["fi" "se"])

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
          all-key-paths (reduce set/union (vals langs))]
      (doseq [[lang lang-key-paths] langs]
        (doseq [key-path all-key-paths]
          (is (lang-key-paths key-path)
              (str "Translation for " key-path " missing in language " lang)))))))
