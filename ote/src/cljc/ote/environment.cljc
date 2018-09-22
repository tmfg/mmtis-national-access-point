(ns ote.environment
  "Access common environment info")

#?(:clj (def environment (atom {})))

#?(:clj (defn merge-environment! [environment-vars]
          (swap! environment merge environment-vars)))

(defn base-url []
  #?(:cljs (str js/document.location.origin "/")
     :clj (:base-url @environment)))
