(ns ote.ui.nprogress
  "Wrapper for NProgress loading indicator"
  (:refer-clojure :exclude [inc set])
  (:require
    [cljsjs.nprogress]))

(defn configure []
  (.configure js/NProgress #js
      {:template (str "<div class=\"bar\" role=\"bar\">
                          <div class=\"peg\"></div>
                      </div>
                      <div class=\"spinner\" role=\"spinner\">
                        <span class=\"loading-msg\"></span>
                        <div class=\"spinner-icon\"></div>
                      </div>")}))
(defn start []
  (.start js/NProgress))

(defn done [& [force?]]
  (.done js/NProgress force?))

(defn inc [& [amount]]
  (.inc js/NProgress amount))

(defn set [& [amount]]
  (.set js/NProgress amount))

