(ns ote.domain.liikkumispalvelu
  "Liikkumispalvelun tietojen määritys"
  (:require [clojure.spec.alpha :as s]
            #?(:clj [ote.tietokanta.specql-db :refer [define-tables]])
            [specql.rel :as rel]
            [specql.transform :as xf])
  #?(:cljs
     (:require-macros [ote.tietokanta.specql-db :refer [define-tables]])))

(define-tables
  ;; Määritellään enumit
  ["viikonpaiva" ::viikonpaiva (specql.transform/transform (specql.transform/to-keyword))]
  ["maksutapa" ::maksutapa (specql.transform/transform (specql.transform/to-keyword))]
  ["liikkumispalvelutyyppi" ::liikkumispalvelutyyppi (specql.transform/transform (specql.transform/to-keyword))]
  ["esteettomyystuki" ::esteettomyystuki (specql.transform/transform (specql.transform/to-keyword))]
  ["erityispalvelu" ::erityispalvelu]
  ["liikennevalinetyyppi" ::liikennevalinetyyppi (specql.transform/transform (specql.transform/to-keyword))]
  ["vuokrauksenlisapalvelu" ::vuokraus-lisapalvelu]
  ["noutopaikantyyppi" ::noutopaikantyyppi]
  ["valityspalvelutyyppi" ::valityspalvelutyyppi]

  ;; UDT tyypit
  ["osoite" ::osoite]
  ["palvelutietolinkki" ::linkki
   {"osoite" ::url}]
  ["aukioloaika" ::aukioloaika]
  ["terminaalitiedot" ::terminaalitiedot]
  ["toimintaalue" ::toiminta-alue]
  ["henkilokuljetustiedot" ::henkilokuljetustiedot]
  ["noutopaikka" ::noutopaikka]
  ["vuokraustiedot" ::vuokraustiedot]
  ["pysakointialue" ::pysakointialue]
  ["pysakointitiedot" ::pysakointitiedot]
  ["valitettavapalvelu" ::valitettava-palvelu]
  ["valitystiedot" ::valitystiedot]

  ;; Taulut
  ["palveluntuottaja" ::palveluntuottaja]
  ["liikkumispalvelu" ::liikkumispalvelu
   {::tuottaja (specql.rel/has-one ::palveluntuottaja-id ::palveluntuottaja ::id)}])

;; Määrätään listan järjestys
(def palvelutyypit [:satama :kuljetus :vuokraus :pysakointi :valityspalvelu])
