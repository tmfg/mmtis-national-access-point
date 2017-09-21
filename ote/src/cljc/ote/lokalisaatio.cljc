(ns ote.lokalisaatio
  "Viestien lokalisaatio, sekä backend että frontend puolella.
  Rajapintana toimii funktio `tr`, joka ottaa viestin sekä parametrit.

  Frontend puolella on yleinen globaali kielitieto.
  Backend puolella kielen voi asettaa dynaamisella muuttujalla."
  (:require #?@(:cljs [[reagent.core :as r]
                       [ote.kommunikaatio :as k]]
                :clj [[clojure.java.io :as io]])
            [clojure.spec.alpha :as s]))

(defonce ladatut-kielet (atom {}))

(defn lataa-kieli!
  "Lataa annetun kielen käännöstiedoston, jos sitä ei ole vielä ladattu, ja lisää kielen
  tiedot `ladatut-kielet` atomiin.
  Kutsuu annettua callbackia, kun lataus on valmis."
  [kieli kun-ladattu]
  (if-let [kaannostiedot (get @ladatut-kielet kieli)]
    (kun-ladattu kieli kaannostiedot)
    #?(:clj (let [kaannostiedot (-> (str "public/kieli/" (name kieli) ".edn")
                                    io/resource slurp read-string)]
              (swap! ladatut-kielet assoc kieli kaannostiedot)
              (kun-ladattu kieli kaannostiedot))
       :cljs (k/get! (str "/kieli/" (name kieli))
                     {:on-success (fn [kaannostiedot]
                                    (swap! ladatut-kielet assoc kieli kaannostiedot)
                                    (kun-ladattu kieli kaannostiedot))}))))
(defn kaannostiedot
  "Lataa kielen (uudelleen) ja palauttaa kaikki sen käännöstiedot."
  [kieli]
  (swap! ladatut-kielet dissoc kieli)
  (lataa-kieli! kieli (constantly nil))
  (get @ladatut-kielet kieli))

#?(:cljs
   ;; Frontilla on atomi `valittu-kieli`
   (do
     ;; FIXME: tallennetaanko local-storageen käyttäjän kielivalinta?
     ;; vai jostain muualta? CKAN cookiesta?
     (defonce valittu-kieli (r/atom :fi))
     (defn aseta-kieli! [kieli]
       (lataa-kieli! kieli #(reset! valittu-kieli %1))))

   :clj
   ;; Backend puolella dynaaminen muuttuja `*kieli*`
   (do
     (def ^:dynamic *kieli* nil)
     (defn kielella
       "Aja funktio siten, että käännösten oletuskieli on annettu kieli."
       [kieli funktio & args]
       (lataa-kieli! kieli
                     (fn [kieli _]
                       (binding [*kieli* kieli]
                         (apply funktio args)))))))

(defn- viestin-osa [osa parametrit]
  (cond
    (keyword? osa)
    (viestin-osa (get parametrit osa) parametrit)

    ;; PENDING: tässä voitaisiin tehdä Date formatointi yms tyypin mukaista
    :default
    (str osa)))

(defn- viesti [viestin-maaritys parametrit]
  (reduce (fn [acc osa]
            (str acc (viestin-osa osa parametrit)))
          ""
          (if (string? viestin-maaritys)
            [viestin-maaritys]
            viestin-maaritys)))

(defn tr
  "Palauta käännös annetulle viestille.
  Optionaalinen `kieli` on avainsana, joka kertoo käytettävän kielen (esim `:fi`).
  Jos ei annettu, käytetään oletusta.

  `viestin-polku` on vektori avainsanoja käännöstiedostoon.

  Optionaaliset `parametrit` antavat arvot viestin korvattaville osille."
  ([viestin-polku]
   (tr #?(:clj *kieli* :cljs @valittu-kieli)
       viestin-polku {}))
  ([viestin-polku parametrit]
   (tr #?(:clj *kieli* :cljs @valittu-kieli) viestin-polku {}))
  ([kieli viestin-polku parametrit]
   (let [kieli (get @ladatut-kielet kieli)]
     (assert kieli (str "Kieltä " kieli " ei ole ladattu."))
     (viesti (get-in kieli viestin-polku) parametrit))))

(s/fdef tr-avain
        :args (s/cat :polku (s/coll-of keyword?))
        :ret fn?)

(defn tr-avain
  "Palauttaa funktion, joka kääntää tietyn keyword arvon annetun polun alla.
  Tämä on kätevä esim. keyword tyyppisen arvon formatteriksi."
  [polku]
  (fn [avain]
    (tr (conj polku avain))))
