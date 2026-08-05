(ns ote.gtfs.transform
  "Transform GTFS agency data"
  (:require [clojure.string :as str]
            [ote.db.transport-operator :as t-operator]
            [taoensso.timbre :as log]))

#?(:clj
   (defn ensure-has-protocol
     "Agency url must contain http or https protocol. If url does not contain protocol add https to it."
     [url transport-operator]
     (try
       (do
         (log/info "Checking if agency url contains a protocol ... " url)
         (clojure.java.io/as-url url))
       (catch Exception e
         (log/warn "Malformed url:" url e "transport-operator-id:" (::t-operator/id transport-operator))
         (str "https://" url))))
   :cljs
   (defn ensure-has-protocol
     "Agency url must contain http or https protocol. If url does not contain protocol add https to it."
     [url transport-operator]
     url))

(defn ^:private replace-newlines
  "Sanitize stringy value to not have newlines so that the generated CSV won't break"
  [s]
  (str/replace s #"\n|\r" ""))

(defn agency-txt [{::t-operator/keys [id name homepage phone email] :as transport-operator}]
  [{:gtfs/agency-id id
    :gtfs/agency-name (replace-newlines name)
    :gtfs/agency-url (ensure-has-protocol homepage transport-operator)
    :gtfs/agency-timezone "Europe/Helsinki"
    :gtfs/agency-lang "FI"
    :gtfs/agency-phone phone
    :gtfs/agency-email email}])
