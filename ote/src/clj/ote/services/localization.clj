(ns ote.services.localization
  "Services for fetching localization messages."
  (:require [com.stuartsierra.component :as component]
            [ote.komponentit.http :as http]
            [ote.localization :as localization]
            [compojure.core :refer [routes GET]]))

(defn- fetch-language [language-name]
  (http/transit-vastaus (localization/translations (keyword language-name))))

(defrecord Localization []
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc
     this ::stop
     (http/julkaise! http (routes
                           (GET "/language/:lang" [lang]
                                (fetch-language lang))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
