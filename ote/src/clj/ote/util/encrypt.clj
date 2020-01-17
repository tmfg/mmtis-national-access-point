(ns ote.util.encrypt
  (:require [buddy.hashers :as hashers]
            [clojure.string :as str])
  (:import (java.util UUID Base64 Base64$Decoder)))

(defn base64->hex
  "Convert base64 encoded string to hex"
  [base64]
  (let [bytes (.decode (Base64/getDecoder)
                ;; Passlib uses unconventional \. character instead of \+ in base64
                (str/replace base64 #"\." "+"))]
    (str/join (map #(format "%02x" %) bytes))))

(defn hex->base64
  "Convert hex string to base64 encoded"
  [hex]
  (str/replace
    (->> hex
      (partition 2)
      (map #(Integer/parseInt (str/join %) 16))
      byte-array
      (.encode (Base64/getEncoder))
      (String.))
    #"\+" "."))

(defn passlib->buddy
  "Convert encrypted password from Python passlib format to buddy format."
  [hash]
  ;; Passlib format:
  ;; $pbkdf2-sha512$iterations$salt$hash
  ;;
  ;; buddy format:
  ;; pbkdf2+sha512$salt$iterations$hash
  ;;
  ;; passlib uses base64 encoding and buddy uses hex strings
  (let [[_ alg iterations salt password] (str/split hash #"\$")]
    (assert (= alg "pbkdf2-sha512"))
    (format "pbkdf2+sha512$%s$%s$%s"
      (base64->hex salt)
      iterations
      (base64->hex password))))

(defn buddy->passlib
  "Reverse of passlib->buddy."
  [hash]
  (let [[alg salt iterations password] (str/split hash #"\$")]
    (assert (= alg "pbkdf2+sha512"))
    (format "$pbkdf2-sha512$%s$%s$%s"
      iterations
      (hex->base64 salt)
      (hex->base64 password))))

(defn encrypt
  "Encrypt raw password. Returns buddy formatted password hash."
  [password]
  (hashers/derive password {:alg :pbkdf2+sha512}))

(defn random-string
  "Do not use for long things. Password and similar will work ok."
  [n]
  (let [chars (map char (range 33 127))
        password (take n (repeatedly #(rand-nth chars)))]
    (reduce str password)))