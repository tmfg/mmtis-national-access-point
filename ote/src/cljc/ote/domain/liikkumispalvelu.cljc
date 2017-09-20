(ns ote.domain.liikkumispalvelu
  "Liikkumispalvelun tietojen määritys"
  (:require #?(:clj [ote.tietokanta.specql-db :refer [define-tables]]))
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ["osoite" ::osoite]
  ["palveluntuottaja" ::palveluntuottaja]
  ["liikkumispalvelutyyppi" ::liikkumispalvelutyyppi]
  ["liikkumispalvelu" ::liikkumispalvelu])

;; Määrätään listan järjestys
(def palvelutyypit [:satama :kuljetus :vuokraus :pysakointi :valityspalvelu])
