(ns ote.ui.validointi
  "OTE kenttien validointi"
  (:require [reagent.core :refer [atom] :as r]
            [clojure.string :as str]
            [cljs-time.core :as t]
            [ote.format :as fmt]))



;; Validointi
;; Rivin skeema voi määritellä validointisääntöjä.
;; validoi-saanto multimetodi toteuttaa tarkastuksen säännön keyword tyypin mukaan
;; nimi = Kentän nimi
;; data = Riville syötettävä data
;; rivi = Rivillä olevat tiedot
;; taulukko = Koko grid-taulukko
(defmulti validoi-saanto (fn [saanto nimi data rivi taulukko & optiot] saanto))

(defmethod validoi-saanto :vakiohuomautus [_ _ data _ _ & [viesti]]
  viesti)


(defmethod validoi-saanto :ei-tyhja [_ _ data _ _ & [viesti]]
  (when (str/blank? data)
    (or viesti "Anna arvo")))

(defmethod validoi-saanto :ei-negatiivinen-jos-avaimen-arvo [_ _ data rivi _ & [avain arvo viesti]]
  (when (and (= (avain rivi) arvo)
             (< data 0))
    (or viesti "Arvon pitää olla yli nolla")))

(defmethod validoi-saanto :ei-tyhja-jos-toinen-avain-nil
  [_ _ data rivi _ & [toinen-avain viesti]]
  (when (and (str/blank? data)
             (not (toinen-avain rivi)))
    (or viesti "Anna arvo")))

(defmethod validoi-saanto :ei-tulevaisuudessa [_ _ data _ _ & [viesti]]
  (when (and data (t/after? data (js/Date.)))
    (or viesti "Päivämäärä ei voi olla tulevaisuudessa")))

(defmethod validoi-saanto :ei-avoimia-korjaavia-toimenpiteitä [_ _ data rivi _ & [viesti]]
  (when (and (or (= data :suljettu) (= data :kasitelty))
             (not (every? #(= (:tila %) :toteutettu) (:korjaavattoimenpiteet rivi))))
    (or viesti "Avoimia korjaavia toimenpiteitä")))

(defmethod validoi-saanto :joku-naista [_ _ _ rivi _ & avaimet-ja-viesti]
  (let [avaimet (if (string? (last avaimet-ja-viesti)) (butlast avaimet-ja-viesti) avaimet-ja-viesti)
        viesti (if (string? (last avaimet-ja-viesti))
                 (last avaimet-ja-viesti)

                 (str "Anna joku näistä: "
                      (clojure.string/join ", "
                                           (map (comp clojure.string/capitalize name) avaimet))))]
    (when-not (some #(not (str/blank? (% rivi))) avaimet) viesti)))

(defmethod validoi-saanto :uniikki [_ nimi data _ taulukko & [viesti]]
  (let [rivit-arvoittain (group-by nimi (vals taulukko))]
    ;; Data on uniikkia jos sama arvo esiintyy taulukossa vain kerran
    (when (and (not (nil? data))
               (> (count (get rivit-arvoittain data)) 1))
      (or viesti "Arvon pitää olla uniikki"))))

(defmethod validoi-saanto :pvm-kentan-jalkeen [_ _ data rivi _ & [avain viesti]]
  (when (and
          (avain rivi)
          (t/before? data (avain rivi)))
    (or viesti (str "Päivämäärän pitää olla " (fmt/pvm (avain rivi)) " jälkeen"))))

(defmethod validoi-saanto :pvm-toisen-pvmn-jalkeen [_ _ data _ _ & [vertailtava-pvm viesti]]
  (when (and
          vertailtava-pvm
          (t/before? data vertailtava-pvm))
    (or viesti (str "Päivämäärän pitää olla " (fmt/pvm vertailtava-pvm) " jälkeen"))))

(defmethod validoi-saanto :pvm-ennen [_ _ data _ _ & [vertailtava-pvm viesti]]
  (when (and data vertailtava-pvm
             (not (t/before? data vertailtava-pvm)))
    (or viesti (str "Päivämäärän pitää olla " (fmt/pvm vertailtava-pvm) " ennen"))))

(def vuosi-kk-ja-paiva (juxt t/year t/month t/day))

(defmethod validoi-saanto :pvm-sama [_ _ data _ _ & [vertailtava-pvm viesti]]
  (when (and data vertailtava-pvm
             (not= (vuosi-kk-ja-paiva data)
                   (vuosi-kk-ja-paiva vertailtava-pvm)))
    (or viesti (str "Päivämäärän pitää olla sama kuin " (fmt/pvm vertailtava-pvm)))))

(defmethod validoi-saanto :toinen-arvo-annettu-ensin [_ _ data rivi _ & [avain viesti]]
  (when (and
          data
          (nil? (avain rivi)))
    (or viesti "Molempia arvoja ei voi syöttää")))

(defmethod validoi-saanto :ei-tyhja-jos-toinen-arvo-annettu [_ _ data rivi _ & [avain viesti]]
  (when (and
          (nil? data)
          (some? (avain rivi)))
    (or viesti "Syötä molemmat arvot")))

(defmethod validoi-saanto :ainakin-toinen-annettu [_ _ _ rivi _ & [[avain1 avain2] viesti]]
  (when-not (or (avain1 rivi)
                (avain2 rivi))
    (or viesti "Syötä ainakin toinen arvo")))

(defmethod validoi-saanto :rajattu-numero [_ _ data _ _ & [min-arvo max-arvo viesti]]
  (when-not (<= min-arvo data max-arvo)
    (or viesti (str "Anna arvo välillä " min-arvo " - " max-arvo ""))))

(defmethod validoi-saanto :rajattu-numero-tai-tyhja [_ _ data _ _ & [min-arvo max-arvo viesti]]
  (and
    data
    (when-not (<= min-arvo data max-arvo)
      (or viesti (str "Anna arvo välillä " min-arvo " - " max-arvo "")))))

(defmethod validoi-saanto :ytunnus [_ _ data _ _ & [viesti]]
  (and
    data
    (let [ ;; Halkaistaan tunnus välimerkin kohdalta
          [tunnus tarkastusmerkki :as halkaistu] (str/split data #"-")
          ;; Kun pudotetaan pois numerot, pitäisi tulos olla ["" "-" nil]
          [etuosa valimerkki loppuosa] (str/split data #"\d+")]
      (when-not (and (= 9 (count data))
                     (= 2 (count halkaistu))
                     (= 7 (count tunnus))
                     (= 1 (count tarkastusmerkki))
                     (empty? etuosa)
                     (= "-" valimerkki)
                     (nil? loppuosa))
       (or viesti "Y-tunnuksen pitää olla 7 numeroa, väliviiva, ja tarkastusnumero.")))))

(defn validoi-saannot
  "Palauttaa kaikki validointivirheet kentälle, jos tyhjä niin validointi meni läpi."
  [nimi data rivi taulukko saannot]
  (keep (fn [saanto]
          (if (fn? saanto)
            (saanto data rivi)
            (let [[saanto & optiot] saanto]
              (apply validoi-saanto saanto nimi data rivi taulukko optiot))))
        saannot))

(defn validoi-rivi
  "Tekee validoinnin yhden rivin / lomakkeen kaikille kentille. Palauttaa mäpin kentän nimi -> virheet vektori.
  Tyyppi on joko :validoi (default) tai :varoita"
  ([taulukko rivi skeema] (validoi-rivi taulukko rivi skeema :validoi))
  ([taulukko rivi skeema tyyppi]
   (loop [v {}
          [s & skeema] skeema]
     (if-not s
       v
       (let [{:keys [nimi hae]} s
             validoi (tyyppi s)]
         (if (empty? validoi)
           (recur v skeema)
           (let [virheet (validoi-saannot nimi (if hae
                                                 (hae rivi)
                                                 (get rivi nimi))
                                          rivi taulukko
                                          validoi)]
             (recur (if (empty? virheet) v (assoc v nimi virheet))
                    skeema))))))))

(defn tyhja-arvo? [arvo]
  (or (nil? arvo)
      (str/blank? arvo)))

(defn puuttuvat-pakolliset-kentat
  "Palauttaa pakolliset kenttäskeemat, joiden arvo puuttuu"
  [rivi skeema]
  (keep (fn [{:keys [pakollinen? hae nimi tyyppi] :as s}]
          (when (and pakollinen?
                     (tyhja-arvo? (if hae
                                    (hae rivi)
                                    (get rivi nimi))))
            s))
        skeema))
