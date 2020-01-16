(ns ote.app.localstorage)

(defn add-item!
  "Add given val to key in localstorage"
  [key val]
  (.setItem (.-localStorage js/window) key val))

(defn get-item
  "Return value of key from localstorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(defn remove-item!
  "Remove localStorage value for the given key"
  [key]
  (.removeItem (.-localStorage js/window) key))