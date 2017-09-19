(ns ote.kommunikaatio
  "Kommunikaatio palvelimen kanssa, wrapperit AJAX kutsuille sekä apurit poluille."
  (:require [ajax.core :refer [GET POST]]))

(defn get!
  "Tee GET pyyntö annettuun URL-osoitteeseen.
  URL parametrit voi antaa `:params` avaimella.
  Callbackit onnistuneen ja epäonnistuneen kutsun käsittelyyn annetaan `:on-success`
  ja `:on-failure` avaimilla."
  [url {:keys [params on-success on-failure response-format]}]
  (ajax/GET url {:params params
                 :handler on-success
                 :error-handler on-failure
                 :response-format (or response-format :transit)}))
