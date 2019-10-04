(ns ote.nap.cookie
  "OTE interoperability tools: cookie authentication.
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
  (byte-array (map (comp unchecked-byte #(Integer/parseInt %))
                   (str/split ip #"\."))))

(defn- ts-bytes [ts]
  (let [seconds (int (/ (.getTime ts) 1000))]
    (byte-array [(bit-shift-right (bit-and seconds 0xFF000000) 24)
                 (bit-shift-right (bit-and seconds 0xFF0000) 16)
                 (bit-shift-right (bit-and seconds 0xFF00) 8)
                 (bit-and seconds 0xFF)])))

(defn- calculate-digest
  [alg ip timestamp shared-secret user-id tokens user-data]
  (digest alg
          (digest alg
                  (ip-bytes ip)
                  (ts-bytes timestamp)
                  shared-secret
                  user-id
                  "\0"
                  tokens
                  "\0"
                  user-data)
          shared-secret))

(defn unparse
  "Write a cookie ticket. The reverse of parse."
  [ip shared-secret {:keys [digest-algorithm timestamp user-id user-data] :as cookie}]
  (str
   (calculate-digest digest-algorithm ip timestamp shared-secret user-id "" user-data)
   (format "%08x" (int (/ (.getTime timestamp) 1000)))
   user-id "!" user-data))

(defn verify-digest
  "Verify a parsed cookie digest based on the shared secret and IP address.
  Returns the cookie with a `:valid-digest?` key added"
  [shared-secret ip {cookie-digest :digest alg :digest-algorithm :as cookie}]
  (let [calculated-digest (calculate-digest alg ip (:timestamp cookie)
                                            shared-secret (:user-id cookie)
                                            (:tokens cookie) (:user-data cookie))]
    (assoc cookie
           :calculated-digest calculated-digest
           :valid-digest? (= calculated-digest cookie-digest))))

(defn verify-timestamp
  "Verify a parsed cookie timestamp and check that it isn't too old.
  Returns the cookie with a `:valid-timestamp?` key added."
  [max-age-in-seconds {ts :timestamp :as cookie}]
  (let [expires (Date. (+ (System/currentTimeMillis) (* 1000 max-age-in-seconds)))]
    (assoc cookie
           :valid-timestamp? (.before ts expires))))

(defn client-ip [{headers :headers}]
  (-> headers
      (get "x-forwarded-for")
      (str/split #",")
      last
      str/trim))

(defn- check-cookie [{:keys [digest-algorithm shared-secret max-age-in-seconds] :as options}
                     {cookies :cookies headers :headers :as req}]
    (let [auth-ticket (:value (get cookies "auth_tkt"))
          ip "0.0.0.0" ;; (client-ip req)  FIXME: ckan seems to always get 0.0.0.0 as IP
          cookie (and (not (str/blank? auth-ticket)) ip
                      (some->> auth-ticket
                               (parse (or digest-algorithm "MD5"))
                               (verify-digest shared-secret ip)
                               (verify-timestamp max-age-in-seconds)))]
      (and (:valid-digest? cookie)
           (:valid-timestamp? cookie)
           cookie)))

(defn wrap-check-cookie
  "Ring middleware to check auth_tkt cookie."
  [{:keys [allow-unauthenticated?] :as options} handler]
  (fn [{cookies :cookies headers :headers :as req}]
    (if-let [cookie (check-cookie options req)]
      (handler (assoc req :user-id (:user-id cookie)))

      ;; Ticket is invalid,
      (if allow-unauthenticated?
        ;; authentication is optional, pass request to handler without user info
        (handler req)

        ;; log this attempt and return error to client
        (do
          (log/warn "Access denied to " (:uri req) " with invalid cookie.")
          {:status 401
           :body "Invalid cookie"})))))
