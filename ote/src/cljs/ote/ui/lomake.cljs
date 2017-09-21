(ns ote.ui.lomake
  "Yleinen lomakekomponentti"
  (:require [ote.ui.validointi :as validointi]
            [ote.ui.kentat :as kentat]
            [cljs-time.core :as t]
            [clojure.string :as str]))

(defrecord Ryhma [otsikko optiot skeemat])

(defn ryhma [otsikko-tai-optiot & skeemat]
  (if-let [optiot (and (map? otsikko-tai-optiot)
                       otsikko-tai-optiot)]
    (->Ryhma (:otsikko optiot)
             (merge {:ulkoasu :oletus}
                    optiot)
             skeemat)
    (->Ryhma otsikko-tai-optiot
             {:ulkoasu :oletus} skeemat)))

(defn rivi
  "Asettaa annetut skeemat vierekkäin samalle riville"
  [& skeemat]
  (->Ryhma nil {:rivi? true} skeemat))

(defn ryhma? [x]
  (instance? Ryhma x))

(defn muokattu?
  "Tarkista onko mitään lomakkeen kenttää muokattu"
  [data]
  (not (empty? (::muokatut data))))

(defn puuttuvat-pakolliset-kentat
  "Palauttaa setin pakollisia kenttiä, jotka puuttuvat"
  [data]
  (::puuttuvat-pakolliset-kentat data))

(defn pakollisia-kenttia-puuttuu? [data]
  (not (empty? (puuttuvat-pakolliset-kentat data))))

(defn virheita?
  "Tarkistaa onko lomakkeella validointivirheitä"
  [data]
  (not (empty? (::virheet data))))

(defn validi?
  "Tarkista onko lomake validi, palauttaa true jos lomakkeella ei ole validointivirheitä
ja kaikki pakolliset kentät on täytetty"
  [data]
  (and (not (virheita? data))
       (not (pakollisia-kenttia-puuttuu? data))))

(defn voi-tallentaa-ja-muokattu?
  "Tarkista voiko lomakkeen tallentaa ja onko sitä muokattu"
  [data]
  (and (muokattu? data)
       (validi? data)))

(defn voi-tallentaa?
  "Tarkista onko lomakkeen tallennus sallittu"
  [data]
  (validi? data))

(defn ilman-lomaketietoja
  "Palauttaa lomakkeen datan ilman lomakkeen ohjaustietoja"
  [data]
  (dissoc data
          ::muokatut
          ::virheet
          ::varoitukset
          ::huomautukset
          ::puuttuvat-pakolliset-kentat
          ::ensimmainen-muokkaus
          ::viimeisin-muokkaus
          ::skeema))

(defrecord ^:private Otsikko [otsikko])
(defn- otsikko? [x]
  (instance? Otsikko x))

(defn- pura-ryhmat
  "Purkaa skeemat ryhmistä yhdeksi flat listaksi, jossa ei ole nil arvoja.
Ryhmien otsikot lisätään väliin Otsikko record tyyppinä."
  [skeemat]
  (loop [acc []
         [s & skeemat] (remove nil? skeemat)]
    (if-not s
      acc
      (cond
        (otsikko? s)
        (recur acc skeemat)

        (ryhma? s)
        (recur acc
               (concat (remove nil? (:skeemat s)) skeemat))

        :default
        (recur (conj acc s)
               skeemat)))))

(defn- rivita
  "Rivittää kentät siten, että kaikki palstat tulee täyteen.
  Uusi rivi alkaa kun palstat ovat täynnä, :uusi-rivi? true on annettu tai tulee uusi ryhmän otsikko."
  [skeemat]
  (loop [rivit []
         rivi []
         palstoja 0
         [s & skeemat] (remove nil? skeemat)]
    (if-not s
      (if-not (empty? rivi)
        (conj rivit rivi)
        rivit)
      (let [kentan-palstat (or (:palstoja s) 1)]
        (cond
          (and (ryhma? s) (:rivi? (:optiot s)))
          ;; Jos kyseessä on ryhmä, joka haluataan samalle riville, lisätään
          ;; ryhmän skeemat suoraan omana rivinään riveihin
          (recur (vec (concat (if (empty? rivi)
                                rivit
                                (conj rivit rivi))
                              [[(->Otsikko (:otsikko s))]
                               (with-meta
                                 (remove nil? (:skeemat s))
                                 {:rivi? true})]))
                 []
                 0
                 skeemat)

          (ryhma? s)
          ;; Muuten lisätään ryhmän otsikko ja jatketaan rivitystä normaalisti
          (recur rivit rivi palstoja
                 (concat [(->Otsikko (:otsikko s))] (remove nil? (:skeemat s)) skeemat))

          :default
          ;; Rivitä skeema
          (if (or (otsikko? s)
                  (:uusi-rivi? s)
                  (> (+ palstoja kentan-palstat) 2))
            (recur (if (empty? rivi)
                     rivit
                     (conj rivit rivi))
                   [s]
                   (if (otsikko? s) 0 kentan-palstat)
                   skeemat)
            ;; Mahtuu tälle riville, lisätään nykyiseen riviin
            (recur rivit
                   (conj rivi s)
                   (+ palstoja kentan-palstat)
                   skeemat)))))))


(defn validoi [tiedot skeema]
  (let [kaikki-skeemat (pura-ryhmat skeema)
        kaikki-virheet (validointi/validoi-rivi nil tiedot kaikki-skeemat :validoi)
        kaikki-varoitukset (validointi/validoi-rivi nil tiedot kaikki-skeemat :varoita)
        kaikki-huomautukset (validointi/validoi-rivi nil tiedot kaikki-skeemat :huomauta)
        puuttuvat-pakolliset-kentat (into #{}
                                          (map :nimi)
                                          (validointi/puuttuvat-pakolliset-kentat tiedot
                                                                                  kaikki-skeemat))]
    (assoc tiedot
      ::virheet kaikki-virheet
      ::varoitukset kaikki-varoitukset
      ::huomautukset kaikki-huomautukset
      ::puuttuvat-pakolliset-kentat puuttuvat-pakolliset-kentat)))

(defn- muokkausaika [{ensimmainen ::ensimmainen-muokkaus
                      viimeisin ::viimeisin-muokkaus :as tiedot}]
  (assoc tiedot
    ::ensimmainen-muokkaus (or ensimmainen (t/now))
    ::viimeisin-muokkaus (t/now)))


(defn kentta
  "UI yhdelle kentälle, renderöi otsikon ja kentän"
  [{:keys [palstoja nimi otsikko tyyppi hae fmt col-luokka yksikko pakollinen?
           komponentti] :as s}
   data muokkaa-fn muokattava? muokkaa
   muokattu? virheet varoitukset huomautukset]
  ;;[:pre (pr-str s) " => " (pr-str data)]
  [:div.form-group {:class (str (or
                                 ;; salli skeeman ylikirjoittaa ns-avaimella
                                 (::col-luokka s)
                                 col-luokka
                                 (case (or palstoja 1)
                                   1 "col-xs-12 col-sm-6 col-md-5 col-lg-4"
                                   2 "col-xs-12 col-sm-12 col-md-10 col-lg-8"
                                   3 "col-xs-12 col-sm-12 col-md-12 col-lg-12"))
                                (when pakollinen?
                                  " required")
                                (when-not (empty? virheet)
                                  " sisaltaa-virheen")
                                (when-not (empty? varoitukset)
                                  " sisaltaa-varoituksen")
                                (when-not (empty? huomautukset)
                                  " sisaltaa-huomautuksen"))}
   (if (= tyyppi :komponentti)
     [:div.komponentti (komponentti {:muokkaa-lomaketta (muokkaa s)
                                     :data data})]
     (if muokattava?
       [kentat/kentta (assoc s
                             :lomake? true
                             :muokkaa! muokkaa-fn
                             :virhe (when (not (empty? virheet))
                                      (str/join " " virheet))) data]
       [:div.form-control-static
        (if fmt
          (fmt ((or hae #(get % nimi)) data))
          (nayta-arvo s arvo))]))

   #_(when (and muokattu?
              (not (empty? virheet)))
     [virheen-ohje virheet :virhe])
   (when (and muokattu?
              (not (empty? varoitukset)))
     [virheen-ohje varoitukset :varoitus])
   (when (and muokattu?
              (not (empty? huomautukset)))
     [virheen-ohje huomautukset :huomautus])

   #_[kentan-vihje s]])

(defn nayta-rivi
  "UI yhdelle riville"
  [skeemat data muokkaa-fn voi-muokata? nykyinen-fokus aseta-fokus!
   muokatut virheet varoitukset huomautukset muokkaa]
  (let [rivi? (-> skeemat meta :rivi?)
        col-luokka (when rivi?
                     (col-luokat (count skeemat)))]
    [:div.row.lomakerivi
     (doall
       (for [{:keys [nimi muokattava? hae] :as s} skeemat
             :let [muokattava? (and voi-muokata?
                                    (or (nil? muokattava?)
                                        (muokattava? data)))]]
         ^{:key nimi}
         [kentta (assoc s
                   :col-luokka col-luokka
                   :focus (= nimi nykyinen-fokus)
                   :on-focus #(aseta-fokus! nimi))
          ((or hae nimi) data)
          #(muokkaa-fn nimi %)
           muokattava? muokkaa
          (get muokatut nimi)
          (get virheet nimi)
          (get varoitukset nimi)
          (get huomautukset nimi)]))]))

(defn- kentan-otsikoilla
  "Lisää kentän otsikon nimi->otsikko funktiolla.
  Jos skeemalla on annettu otsikko, sitä ei ylikirjoiteta."
  [nimi->otsikko skeemat]
  (.log js/console "OTSIKOI: " nimi->otsikko)
  (if-not nimi->otsikko
    skeemat
    (mapv (fn [{:keys [nimi otsikko] :as s}]
            (assoc s :otsikko (or otsikko
                                  (nimi->otsikko nimi))))
          skeemat)))

(defn lomake
  "Yleiskäyttöinen lomakekomponentti, joka ottaa asetukset, lomakkeen kenttien
  määritykset sekä lomakkeen tämänhetkisen tilan.

  Mahdolliset asetukset:

  :muokkaa!   Callback, jota kutsutaan kun johonkin lomakkeen kenttään on tehty muutos
  :footer-fn  Funktio, jota kutsutaan muodostamaan lomakkeen alle tuleva footer komponentti.
              Parametrina annetaan nykyinen lomakedata validointitietojen kanssa.
  :luokka     Ylimääräinen CSS-luokka lomakkeelle
  :nimi->otsikko  Funktio, joka muuntaa kentän `:nimi` arvon sen otsikoksi.
                  Tämä on hyödyllinen, jos ei haluta antaa otsikoita lomake kentissä
                  vaan muodostaa ne automaattisesti käännösfunktiolla nimen perusteella.
  "
  [_ _ _]
  (let [fokus (atom nil)]
    ;; FIXME: tee material-ui v1 gridillä
    (fn [{:keys [muokkaa! luokka footer-fn virheet varoitukset huomautukset voi-muokata? ] :as opts}
         skeema
         {muokatut ::muokatut
          :as data}]
      (let [{virheet ::virheet
             varoitukset ::varoitukset
             huomautukset ::huomautukset :as validoitu-data} (validoi data skeema)
            voi-muokata? (if (some? voi-muokata?)
                           voi-muokata?
                           true)
            muokkaa-kenttaa-fn (fn [nimi arvo]
                                 (let [uudet-tiedot (assoc data nimi arvo)]
                                   (assert muokkaa! (str ":muokkaa! puuttuu, opts:" (pr-str opts)))
                                   (-> uudet-tiedot
                                       muokkausaika
                                       (validoi skeema)
                                       (assoc ::muokatut (conj (or (::muokatut uudet-tiedot)
                                                                   #{}) nimi))
                                       muokkaa!)))]
        [:div
         {:class (str "lomake " (when ei-borderia? "lomake-ilman-borderia")
                      luokka)}
         (when otsikko
           [:h3.lomake-otsikko otsikko])
         (doall
          (map-indexed
           (fn [i skeemat]
             (let [otsikko (when (otsikko? (first skeemat))
                             (first skeemat))
                   skeemat (kentan-otsikoilla (:nimi->otsikko opts)
                                              (if otsikko
                                                (rest skeemat)
                                                skeemat))
                   rivi-ui [nayta-rivi skeemat
                            validoitu-data
                            muokkaa-kenttaa-fn
                            voi-muokata?
                            @fokus
                            #(reset! fokus %)
                            muokatut
                            virheet
                            varoitukset
                            huomautukset
                            #(muokkaa-kenttaa-fn (:nimi %))]]
               (if otsikko
                 ^{:key i}
                 [:span
                  [:h3.lomake-ryhman-otsikko (:otsikko otsikko)]
                  rivi-ui]
                 (with-meta rivi-ui {:key i}))))
           (rivita skeema)))

         (when-let [footer (if footer-fn
                             (footer-fn (assoc validoitu-data
                                               ::skeema skeema))
                             footer)]
           [:div.lomake-footer.row
            [:div.col-md-12 footer]])]))))
