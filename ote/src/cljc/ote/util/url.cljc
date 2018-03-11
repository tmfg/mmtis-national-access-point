(ns ote.util.url
  "URL related utilities for clj/cljs."
  #?(:clj
     (:import (java.net URLEncoder URLDecoder))))

(defn encode-url [url]
  #?(:clj
     (URLEncoder/encode (str url) "UTF-8")

     :cljs
     (js/encodeURI url)))


(defn decode-url [url]
  #?(:clj
     (URLDecoder/decode (str url) "UTF-8")

     :cljs
     (js/decodeURI url)))

(defn encode-url-component [component]
  #?(:clj
     (encode-url component)

     :cljs
     (js/encodeURIComponent component)))

(defn decode-url-component [component]
  #?(:clj
     (decode-url component)

     :cljs
     (js/decodeURIComponent component)))