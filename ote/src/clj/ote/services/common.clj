(ns ote.services.common
  "Services for getting common data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [specql.op :as op]
            [ote.db.common :as common]
            [ote.db.user :as user]
            [ote.util.email-template :as email-template]
            [ote.config.email-config :as email-config]
            [compojure.core :refer [routes GET POST DELETE]]
            [taoensso.timbre :as log]
            [ote.services.operators :as operators]
            [ote.authorization :as authorization]
            [jeesql.core :refer [defqueries]]
            [ote.db.tx :as tx]
            [ote.email :as email]
            [hiccup.core :refer [html]]
            [ote.time :as time]
            [clj-time.core :as t]
            [clojure.set :refer [rename-keys]]
            [clj-time.coerce :as tc]
            [clojure.string :as str]
            [ote.services.transport :as transport-service])
  (:import (java.util UUID)))

(defn country-list [db lang]
  (specql/fetch db ::common/country-list
                #{::common/country_code ::common/value}
                {::common/locale_code lang}))

(defn- common-routes
  "Unauthenticated routes"
  [db]
  (routes

    (GET "/country-list"
         {cookies :cookies :as req}
      (let [lang (str/upper-case (get-in cookies ["finap_lang" :value]))]
        (http/transit-response (country-list db lang))))))

(defrecord Common [config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http (common-routes db))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
