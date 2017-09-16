(ns ote.komponentit.http
  "HTTP-kit palvelin"
  (:require [org.httpkit.server :as server]
            [com.stuartsierra.component :as component]
            [compojure.route :as route]
            [cognitect.transit :as transit]))

(defn- palvele-pyynto [kasittelijat req]
  ((apply some-fn kasittelijat) req))

(defrecord HttpPalvelin [http-kit-asetukset kasittelijat]
  component/Lifecycle
  (start [this]
    (let [resurssit (route/resources "/")]
      (assoc this ::stop
             (server/run-server
              (fn [req]
                (palvele-pyynto (conj @kasittelijat resurssit) req))
              http-kit-asetukset))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn http-palvelin
  "Luo HTTP-palvelinkomponentin annetuilla asetuksilla"
  [asetukset]
  (->HttpPalvelin asetukset (atom [])))

(defn julkaise!
  "Julkaisee HTTP-palvelinkomponenttiin uuden käsittelijän.
  Pyyntöä käsiteltäessä kutsutaan käsittelijöitä julkaisujärjestyksessä,
  kunnes joku niistä palauttaa truthy arvon.

  Palauttaa 0 arity funktion, jolla julkaistun käsittelijän voi poistaa."
  [{kasittelijat :kasittelijat} kasittelija]
  (swap! kasittelijat conj kasittelija)
  #(swap! kasittelijat
          (fn [kasittelijat]
            (filterv (partial not= kasittelija) kasittelijat))))
