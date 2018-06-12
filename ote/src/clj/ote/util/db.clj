(ns ote.util.db)

(defn PgArray->seqable [arr]
  (if arr
    (.getArray arr)
    []))

(defn PgArray->vec [arr]
  (if arr
    (vec (.getArray arr))
    []))