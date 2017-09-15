(ns ote.ui.kentat
  "Erilaisten kenttätyyppien komponentit"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]))


(defn vain-luku-atomina [arvo]
  (r/wrap arvo
          #(assert false (str "Ei voi kirjoittaa vain luku atomia arvolle: " (pr-str arvo)))))

(defmulti kentta
  "Tekee muokattavan kentän komponentin tyypin perusteella.
  Kentällä on aina oltava :muokkaa! optio, jolla muutokset välitetään takaisin."
  (fn [t _] (:tyyppi t)))

(defmulti nayta-arvo
  "Tekee vain-luku näyttömuodon kentän arvosta tyypin perusteella.
   Tämän tarkoituksena ei ole tuottaa 'disabled' tai 'read-only' elementtejä
   vaan tekstimuotoinen kuvaus arvosta. Oletustoteutus muuntaa datan vain merkkijonoksi."
  (fn [t _] (:tyyppi t)))

(defmethod nayta-arvo :default [_ data]
  [:span (str data)])

(defmethod nayta-arvo :komponentti [skeema data]
  (let [komponentti (:komponentti skeema)]
    [komponentti data]))


(defn placeholder [{:keys [placeholder placeholder-fn rivi] :as kentta} data]
  (or placeholder
      (and placeholder-fn (placeholder-fn rivi))))

(defmethod kentta :string [{:keys [muokkaa! otsikko nimi pituus-max pituus-min regex
                                   focus on-focus lomake?
                                   virhe]
                                :as kentta} data]
  [ui/text-field
   {:floatingLabelText otsikko
    :hintText (placeholder kentta data)
    :on-change #(muokkaa! %2)
    :value data
    :error-text virhe}])



(defmethod kentta :puhelin [{:keys [on-focus pituus lomake? placeholder] :as kentta} data]
  [:input {:class (when lomake? "form-control")
           :type "tel"
           :value @data
           :max-length pituus
           :on-focus on-focus
           :placeholder placeholder
           :on-change #(let [uusi (-> % .-target .-value)]
                         (when (re-matches #"\+?(\s|\d)*" uusi)
                           (reset! data uusi)))}])

(defmethod kentta :default [opts data]
  [:div.error "Ei kenttätyyppiä: " (:tyyppi opts)])
