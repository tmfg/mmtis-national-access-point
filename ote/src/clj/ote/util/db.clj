(ns ote.util.db)

(defn PgArray->seqable [arr]
  (if arr
    (.getArray arr)
    []))

(defn PgArray->vec [arr]
  (if arr
    (vec (.getArray arr))
    []))

(defn str-vec->str [v]
  "Mainly convert used-packages vector of strings to list of integers"
  (let [v (if (string? v)
              (conj [] v)
              v)
        val (str "{"
                 (clojure.string/join "," v)
                 "}")]
    val))