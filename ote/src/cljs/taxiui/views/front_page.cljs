(ns taxiui.views.front-page
  "Front page for Taxi UI"
  (:require [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [ote.theme.colors :as colors]
            [ote.ui.common :refer [linkify]]
            [ote.app.utils :refer [user-logged-in?]]
            [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]
            [taxiui.styles.front-page :as style-front-page]))

(let [host (.-host (.-location js/document))]
  (def test-env? (or (str/includes? host "test")
                     (str/includes? host "localhost"))))

(defn test-env-warning []
  [:div.test-env-warning
   {:style {:margin "0.2em"
            :border "4px dashed red"}}
   [:p {:style {:margin "10px 0px 0px 10px"
                :font-weight "bold"}}
    "TÄMÄ ON TESTIPALVELU!!"]
   [:p {:style {:margin "10px"}}
    "Julkinen NAP-palvelukatalogi löytyy osoitteesta: "
    [linkify "https://finap.fi/ote/#/services" "finap.fi"]]
   [:p {:style {:margin "10px"}}
    "Lisätietoa NAP-palvelukatalogin taustoista saat osoitteesta "
    [linkify (tr [:common-texts :footer-livi-url-link])
     (tr [:common-texts :footer-livi-url-link])]]])

(defn front-page
  "Front page info"
  [e! {user :user :as app}]
  [:div

   (when test-env?
     [test-env-warning])

   [:h1 "Hello!"]
   [:h2 "Hello!"]
   [:h3 "Hello!"]
   [:h4 "Hello!"]
   [:h5 "Hello!"]
   [:h6 "Hello!"]
   [:span "Span span span"]
   [:div "Paavo Pesusieni seikkailee"]

   [:p [:b "Liikenteenohjausyhtiö Fintraffic Oy"] " (vuosina 2018–2020 "[:i "Traffic Management Finland Oy"] ") on Suomen valtion kokonaan omistama erityistehtäväkonserni, joka toimii liikenne- ja viestintäministeriön omistajaohjauksessa."]
   [:p "Fintraffic tarjoaa ja kehittää liikenteenohjauksen ja -hallinnan palveluita kaikissa liikennemuodoissa sekä varmistaa liikenteen turvallisuuden ja sujuvuuden vastuullisesti kaikissa liikennemuodoissa. Palvelut tukevat kansalaisten liikkumista, elinkeinoelämän tarpeita ja kuljetuksia sekä turvallisuusviranomaisten toimintaa."]
   [:p "Fintraffic kerää, hallinnoi ja avaa tietoa luoden mahdollisuuksia markkinoille syntyvälle uudelle liiketoiminnalle. Yhtiö tarjoaa ja kehittää edistyksellisiä, uusia palveluita ja edesauttaa liikenteen ekosysteemien kasvua."]
   [:p "Konsernille annetulla erityistehtävällä turvataan yhteiskunnan, viranomaisten ja elinkeinoelämän tarvitsemat välttämättömät liikenteenohjauspalvelut. Lisäksi erityistehtävällä varmistetaan toimintavarmuus normaaliolojen häiriötilanteissa ja poikkeusoloissa."]
   [:p "Konsernissa työskentelee koko Suomessa yhteensä reilut 1000 henkilöä."]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-blue :padding "1em 1em 1em 1em"}) "testi"]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-green :padding "1em 1em 1em 1em"}) "testi"]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-basic-purple :padding "1em 1em 1em 1em"}) "testi"]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-dark-purple  :padding "1em 1em 1em 1em"}) "testi"]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-red :padding "1em 1em 1em 1em"}) "testi"]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-gray :padding "1em 1em 1em 1em"}) "testi"]
   [:p (stylefy/use-style {:color colors/primary-text-color :background-color colors/accessible-brown :padding "1em 1em 1em 1em"}) "testi"]
   ])
