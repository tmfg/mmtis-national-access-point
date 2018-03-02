(ns ote.services.routes
  "Routes api."
  (:require [com.stuartsierra.component :as component]
            [ote.components.http :as http]
            [compojure.core :refer [routes GET POST DELETE]]))

(defn get-user-routes [db groups user]
  (println " ****************** -> tööttäspä osotteeseen")
  (let [response
        [{:operator "Ajopalvelu testinen"
          :routes   [{:id   1
                      :name "Oulu - Hailuoto"}
                     {:id   2
                      :name "Oulu - Liminka"}]}
         {:operator "Joku toinen firma"
          :routes   [{:id   3
                      :name "Liminka - Kokkola"}
                     {:id   4
                      :name "Haukipudas - Vantaa"}]}]]
        (println " my response " response)
        response
    ))

(defn- routes-auth
  "Routes that require authentication"
  [db nap-config]
  (routes
    (POST "/routes/routes" {user :user}
      (http/transit-response
        (get-user-routes db (:groups user) (:user user))))))

(defrecord Routes [nap-config]
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc
      this ::stop
           [(http/publish! http (routes-auth db nap-config))]))
  (stop [{stop ::stop :as this}]
    (doseq [s stop]
      (s))
    (dissoc this ::stop)))
