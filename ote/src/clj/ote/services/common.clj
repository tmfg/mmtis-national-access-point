(ns ote.services.common
  "Services for getting common data from database"
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [specql.core :refer [fetch update! insert! upsert! delete!] :as specql]
            [compojure.core :refer [routes GET POST DELETE]]
            [jeesql.core :refer [defqueries]]
            [hiccup.core :refer [html]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]
            [ote.db.common :as common]))

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
