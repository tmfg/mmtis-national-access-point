(ns ote.domain.liikkumispalvelu-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as s]
            [ote.domain.liikkumispalvelu :as lp]
            [ote.domain.yleiset :as yleiset])
  (:import (java.time LocalTime)))

(def kallioparkki
  {::lp/tyyppi :pysakointi
   ::lp/palveluntuottaja-id 1
   ::lp/pysakointi {::lp/pysakointitiedot
                    {::lp/pysakointialueet
                     [{::lp/alue {::lp/sijainti "FIXME: kallioparkin polygon"
                                  ::lp/alueen-kuvaus "Oulun keskustan alla"}
                       ::lp/aukioloajat [{::lp/viikonpaivat [:MA :TI :KE :TO :PE]
                                          ::lp/avaamisaika (LocalTime/of 7 0)
                                          ::lp/sulkemisaika (LocalTime/of 23 59)}]
                       ::lp/maksutavat [:kateinen :mobiilimaksu]
                       ::lp/esteettomyys [:inva-wc :induktiosilmukka]
                       ::lp/esteettomyyskuvaus "Löytyy inva wc ja induktiosilmukka kuulolaitteille"
                       ::lp/latauspisteet "onhan noita pari normaalia töpseliä"
                       ::lp/varauspalvelu {::lp/url "http://example.com/varaa-pysakointi"}
                       ::lp/liikennevalineet [:henkiloauto]}]}}})

(deftest pysakointidata-validi
  (is (= nil
         (s/explain-data ::lp/liikkumispalvelu kallioparkki))))
