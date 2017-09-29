(ns ote.domain.liikkumispalvelu-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as s]
            [ote.db.transport-operator :as to-definitions])
  (:import (java.time LocalTime)))

(def kallioparkki
  {::to-definitions/tyyppi :pysakointi
   ::to-definitions/palveluntuottaja-id 1
   ::to-definitions/pysakointi {::to-definitions/pysakointialueet
                    [{::to-definitions/alue {;;::to-definitions/sijainti "FIXME: kallioparkin polygon"
                                 ::to-definitions/alueen-kuvaus "Oulun keskustan alla"}
                      ::to-definitions/aukioloajat [{::to-definitions/viikonpaivat [:MA :TI :KE :TO :PE]
                                         ::to-definitions/avaamisaika (LocalTime/of 7 0)
                                         ::to-definitions/sulkemisaika (LocalTime/of 23 59)}]
                      ::to-definitions/maksutavat [:kateinen :mobiilimaksu]
                      ::to-definitions/esteettomyys [:inva-wc :induktiosilmukka]
                      ::to-definitions/esteettomyyskuvaus "Löytyy inva wc ja induktiosilmukka kuulolaitteille"
                      ::to-definitions/latauspisteet "onhan noita pari normaalia töpseliä"
                      ::to-definitions/varauspalvelu {::to-definitions/url "http://example.com/varaa-pysakointi"}
                      ::to-definitions/liikennevalineet [:henkiloauto]}]}})

(deftest pysakointidata-validi
  (is (= nil
         (s/explain-data ::to-definitions/liikkumispalvelu kallioparkki))))
