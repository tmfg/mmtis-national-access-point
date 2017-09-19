(ns ote.palvelut.lokalisaatio
  "Palvelu kielitiedostojen lataamiseksi"
  (:require [com.stuartsierra.component :as component]
            [ote.komponentit.http :as http]
            [ote.lokalisaatio :as lokalisaatio]
            [compojure.core :refer [routes GET]]))

(defn- hae-kieli [kielen-nimi]
  (http/transit-vastaus (lokalisaatio/kaannostiedot (keyword kielen-nimi))))

(defrecord Lokalisaatio []
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc
     this ::lopeta
     (http/julkaise! http (routes
                           (GET "/kieli/:kieli" [kieli]
                                (hae-kieli kieli))))))
  (stop [{lopeta ::lopeta :as this}]
    (lopeta)
    (dissoc this ::lopeta)))
