(ns ote.util.tis-configs)

(defmulti base-task-names identity)

(defmethod base-task-names "gtfs" [_]
  {:validator "gtfs.canonical"
   :converter "gtfs2netex.fintraffic"})

(defmethod base-task-names "netex" [_]
  {:validator "netex.entur"})

(defmethod base-task-names "gbfs" [_]
  {:validator "gbfs.entur"})

(defmethod base-task-names :default [_]
  {})

(defn vaco-create-payload
  [format]
  (let [{:keys [validator converter]} (base-task-names format)]
    (merge
      {}  ; we want empty map instead of null by default
      (when validator {:validations [{:name   validator
                                      :config {}}]})
      (when converter {:conversions [{:name   converter
                                     :config {}}]}))))
