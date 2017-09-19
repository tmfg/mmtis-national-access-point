(ns ote.domain.liikkumispalvelu
    "Liikkumispalvelun tietojen määritys")

(def palvelutyypin-nimi
  {:satama         "Satama, Asema, Terminaali"
   :kuljetus       "Henkilökuljetuspalvelu"
   :vuokraus       "Ajoneuvojen vuokrauspalvleut ja kaupalliset yhteisökäyttöpalvelut"
   :pysakointi     "Yhteiskäyttö-/Pysäköintipalvelu"
   :valityspalvelu "Välityspalvelu"})

;; Määrätään listan järjestys
(def palvelutyypit [:satama :kuljetus :vuokraus :pysakointi :valityspalvelu])