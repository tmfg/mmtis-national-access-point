(ns ote.nap.cookie
  "OTE CKAN interoperability tools: cookie authentication.
  Read and validate cookies in mod_auth_tkt format."
  (:import (java.security MessageDigest)
           (java.util Date))
  (:require [clojure.string :as str]
            [ring.middleware.cookies :as cookies]
            [taoensso.timbre :as log]))


(defn- bytes->hex-string [bytes]
  (reduce str (map #(format "%02x" (bit-and 0xFF %)) bytes)))

(defn- digest [algorithm & items]
  (let [md (MessageDigest/getInstance algorithm)]
    (doseq [item items]
      (.update md (if (bytes? item)
                    item
                    (.getBytes (str item) "UTF-8"))))
    (bytes->hex-string (.digest md))))

(def ^{:const true
       :doc "Length of a digest when printed in hex characters."}
  digest-length
  {"MD5" 32
   "SHA-1" 40
   "SHA-256" 64})

(defn parse
  "Parse a cookie ticket and return a map of the data."
  [digest-algorithm cookie]
  (assert (digest-length digest-algorithm)
          (str "Invalid digest algorithm: " digest-algorithm))
  (let [len (digest-length digest-algorithm)
        cookie-digest (subs cookie 0 len)
        cookie-timestamp (-> cookie
                             (subs len (+ 8 len))
                             (Integer/parseInt 16)
                             (* 1000)
                             Date.)
        [user-id user-data] (-> cookie
                                (subs (+ 8 len))
                                (str/split #"!"))]

    {:digest-algorithm digest-algorithm
     :digest cookie-digest
     :timestamp cookie-timestamp
     :user-id user-id
     :user-data user-data

     ;; PENDING: CKAN doesn't seem to use tokens
     :tokens ""}))

(defn- ip-bytes [ip]
  (byte-array (map (comp byte #(Integer/parseInt %))
                   (str/split ip #"\."))))

(defn- ts-bytes [ts]
  (let [seconds (/ (.getTime ts) 1000)]
    (byte-array [(bit-shift-right (bit-and seconds 0xFF000000) 24)
                 (bit-shift-right (bit-and seconds 0xFF0000) 16)
                 (bit-shift-right (bit-and seconds 0xFF00) 8)
                 (bit-and seconds 0xFF)])))

(defn verify-digest
  "Verify a parsed cookie digest based on the shared secret and IP address.
  Returns the cookie with a `:valid-digest?` key added"
  [shared-secret ip {cookie-digest :digest alg :digest-algorithm :as cookie}]
  (let [calculated-digest
        (digest alg
                (digest alg
                        (ip-bytes ip)
                        (ts-bytes (:timestamp cookie))
                        shared-secret
                        (:user-id cookie)
                        "\0"
                        (:tokens cookie)
                        "\0"
                        (:user-data cookie))
                shared-secret)]
    (assoc cookie
           :d calculated-digest
           :valid-digest? (= calculated-digest cookie-digest))))

(defn verify-timestamp
  "Verify a parsed cookie timestamp and check that it isn't too old.
  Returns the cookie with a `:valid-timestamp?` key added."
  [max-age-in-seconds {ts :timestamp :as cookie}]
  (let [expires (Date. (+ (System/currentTimeMillis) (* 1000 max-age-in-seconds)))]
    (assoc cookie
           :valid-timestamp? (.before ts expires))))

(defn wrap-check-cookie
  "Ring middleware to check auth_tkt cookie."
  [{:keys [digest-algorithm shared-secret max-age-in-seconds] :as options} handler]
  (cookies/wrap-cookies
   (fn [{cookies :cookies headers :headers :as req}]
     (println "HEADERS: " (pr-str headers))
     (let [auth-ticket (:value (get cookies "auth_tkt"))
           ip (get headers "x-forwarded-for")
           cookie (and auth-ticket ip
                       (some->> auth-ticket
                                (parse (or digest-algorithm "MD5"))
                                (verify-digest shared-secret ip)
                                (verify-timestamp max-age-in-seconds)))]
       (if (and (:valid-digest? cookie)
                (:valid-timestamp? cookie))
         ;; Ticket is valid, pass it to the handler
         (handler (assoc req :user-id (:user-id cookie)))

         ;; Ticket is invalid, log this attempt and return
         (do
           (log/warn "Access denied to " (:uri req) " with invalid cookie:" auth-ticket ", ip:" ip)
           {:status 401
            :body "Invalid cookie"}))))))
