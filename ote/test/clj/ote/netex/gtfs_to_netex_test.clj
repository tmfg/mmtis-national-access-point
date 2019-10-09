(ns ote.netex.gtfs-to-netex-test
  (:require [ote.netex.netex :as sut]
            [clojure.test :as t :refer [deftest is]]
            [ote.test :refer [system-fixture]]
            [ote.util.zip :as zip]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [ote.util.file :as file]))

(def chouette-path "/opt/chouette/")                        ; Folder where chouette script is
(def conversion-work-path-test "/tmp/ote/netex-conversion-test/") ; Temp folder to be created and removed for conversion
(def test-input-gtfs-filepath "test/resources/netex/test_data_gtfs.zip") ; Conversion input gtfs zip
(def test-reference-netex-filepath "test/resources/netex/test_data_netex.zip") ; Conversion reference result zip

(t/use-fixtures :each
                (system-fixture))

;; File names contained by result archive, which shall have full comparison in test, instead of simple comparison
;; Update: all files include publication date so no full comparison to any, because don't want to parse every element
;; in order to handle changing date
(def full-compare-netex-names #{})

(defn test-are-zips-same
  "Takes `reference-filepath` and `result-filepath` and compares if those match using clojure.test/is macros.
  Only names of files in zips are compared because result timestamp elements change on each test run.
  Result zip name matching to a name in `full-compare-netex-names` will have full comparison instead of simple comparsion."
  [reference-filepath result-filepath full-compare-netex-names]
  (let [extra-result-elements (loop [ref-coll (zip/read-zip (io/input-stream reference-filepath))
                                     result-coll (zip/read-zip (io/input-stream result-filepath))]
                                (if (seq ref-coll)
                                  (let [ref (first ref-coll)
                                        rst (some #(when (= (:name ref) (:name %))
                                                     %)
                                                  result-coll)]
                                    (is rst (str "Reference element should be found from result collection, ref element name="
                                                 (:name ref)))

                                    (when (some? (full-compare-netex-names (:name ref)))
                                      (is (= ref rst)
                                          "This is a reference element pre-defined for full comparison and should match exactly to result element"))
                                    (recur (rest ref-coll)
                                           (if rst
                                             (remove #(= rst %)
                                                     result-coll)
                                             result-coll)))
                                  (map :name result-coll)))]
    (is (empty? extra-result-elements)
        "Result collection should not contain more elements than reference result collection")))

(deftest gtfs->netex-test!
  []
  (let [filename (.getName (io/file test-input-gtfs-filepath))
        config-netex {:chouette-path chouette-path
                      :conversion-work-path conversion-work-path-test
                      :bucket nil}
        _ (sut/cleanup-dir-recursive! (:conversion-work-path config-netex)) ; Cleanup if previous run aborted without cleanup
        conversion-meta {:gtfs-file (file/slurp-bytes test-input-gtfs-filepath)
                         :gtfs-filename filename
                         :gtfs-basename (org.apache.commons.io.FilenameUtils/getBaseName filename)
                         :operator-name "gtfs-to-netex-conversion-test-operatorname-1"
                         :service-id 2
                         :external-interface-description-id 1}
        res (sut/gtfs->netex! conversion-meta
                              config-netex)]
    (is (not (str/blank? res))
        "Conversion function should return a path to created netex archive")

    (test-are-zips-same res
                        test-reference-netex-filepath
                        full-compare-netex-names)

    (sut/cleanup-dir-recursive! (:conversion-work-path config-netex))))
