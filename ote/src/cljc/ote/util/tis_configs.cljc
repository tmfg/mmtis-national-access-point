(ns ote.util.tis-configs)

;; IMPLEMENTATION NOTE! Both :name and :config keys are mandatory!
(defmulti base-task-names identity)

(defmethod base-task-names "gtfs" [_]
  {:validator {:name   "gtfs.canonical"
               :config {}}
   :converter {:name   "gtfs2netex.fintraffic"
               :config {"codespace"     "FSR"
                        "maximumErrors" 1000}}})

(defmethod base-task-names "netex" [_]
  {:validator {:name   "netex.entur"
               :config {}}
   :converter {:name   "netex2gtfs.entur"
               :config {"codespace" "FSR"}}})

(defmethod base-task-names "gbfs" [_]
  {:validator {:name   "gbfs.entur"
               :config {}}})

(defmethod base-task-names :default [_]
  {})

(defn vaco-create-payload
  [format]
  (let [{:keys [validator converter]} (base-task-names format)]
    (merge
      {}  ; we want empty map instead of null by default
      (when validator {:validations [validator]})
      (when converter {:conversions [converter]}))))
