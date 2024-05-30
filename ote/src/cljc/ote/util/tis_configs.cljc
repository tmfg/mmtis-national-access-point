(ns ote.util.tis-configs)

(defmulti base-task-names identity)

(defmethod base-task-names "gtfs" [_]
  {:validator {:name "gtfs.canonical"}
   :converter {:name "gtfs2netex.fintraffic"
               :config {"codespace"     "FIN"
                        "maximumErrors" 1000}}})

(defmethod base-task-names "netex" [_]
  {:validator {:name "netex.entur"}})

(defmethod base-task-names "gbfs" [_]
  {:validator {:name "gbfs.entur"}})

(defmethod base-task-names :default [_]
  {})

(defn vaco-create-payload
  [format]
  (let [{:keys [validator converter]} (base-task-names format)]
    (merge
      {}  ; we want empty map instead of null by default
      (when validator {:validations [validator]})
      (when converter {:conversions [converter]}))))
