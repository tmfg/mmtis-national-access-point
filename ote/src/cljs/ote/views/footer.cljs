(ns ote.views.footer
  "NAP - footer"
  (:require [ote.localization :refer [tr tr-key] :as localization]
            [ote.style.footer :as footer-styles]
            [ote.ui.common :as common]
            [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]))

(def selectable-languages [["fi" "Suomeksi"]
                           ["sv" "På Svenska"]
                           ["en" "In English"]])

(def ^:private legal-links
  {:fi [{:href "https://www.fintraffic.fi/fi/ekosysteemi-tietosuoja" :label "Tietosuoja"}
        {:href "https://www.fintraffic.fi/fi/palaute" :label "Palaute"}
        {:href "https://www.fintraffic.fi/fi/yhteystiedot" :label "Yhteystiedot"}
        {:href "https://www.fintraffic.fi/fi/fintraffic/saavutettavuusseloste" :label "Saavutettavuus"}]
   :sv [{:href "https://www.fintraffic.fi/sv/fintraffic/kontaktuppgifter" :label "Kontaktinformation"}
        {:href "https://www.fintraffic.fi/fi/ekosysteemi-tietosuoja" :label "Dataskydd"}]
   :en [{:href "https://www.fintraffic.fi/en/fintraffic/contact-information-and-invoicing-instructions" :label "Contact information"}
        {:href "https://www.fintraffic.fi/fi/ekosysteemi-tietosuoja" :label "Privacy policy"}]})

(defn footer []
  [:footer (stylefy/use-style footer-styles/footer)
   ; topbar
   [:div (stylefy/use-style footer-styles/topbar)
    [:img (merge (stylefy/use-style footer-styles/fintraffic-logo)
                 {:src "img/icons/Fintraffic_vaakalogo_valkoinen.svg"})]
    [common/linkify "https://www.fintraffic.fi" "fintraffic.fi" {:style footer-styles/link :hide-external-icon? true}]]

   ; Fintraffic links
   [:div (stylefy/use-style footer-styles/site-links)
    [:div (stylefy/use-style footer-styles/fintraffic-site-links-wrapper)
     [:ul (stylefy/use-style footer-styles/fintraffic-links)
      (doall
        (for [[href service] (map (juxt common/localized-quicklink-uri identity)
                                [:traffic-situation
                                 :feedback-channel
                                 :train-departures
                                 :skynavx
                                 :digitraffic
                                 :digitransit
                                 :finap])]
          ^{:key (str "quicklink_" (name service))}
          [:li (stylefy/use-style footer-styles/site-link-entry)
           [common/linkify href (tr [:quicklink-header service]) {:style footer-styles/link :hide-external-icon? true}]]))]]

    [:ul (stylefy/use-style footer-styles/fintraffic-legal-links)
     (let [language (or (keyword @localization/selected-language) :fi)]
       (doall
         (for [{:keys [href label]} (get legal-links language)]
           ^{:key (str "legallink_" (clojure.string/lower-case (name label)))}
           [:li
            [common/linkify
             href
             label
             {:style               footer-styles/link
              :hide-external-icon? true}]])))
     ]

    [:ul (stylefy/use-style footer-styles/fintraffic-support-link)
     [:li
      ; linkify not used here as this is a mailto: link with very specific custom styling
      [:a (merge (stylefy/use-style footer-styles/link)
                 {:href (tr [:common-texts :navigation-feedback-link])})
       [:span {:style {:font-weight 600}} (tr [:common-texts :navigation-give-feedback])][:br]
       (tr [:common-texts :navigation-feedback-email])]]]]

   ; social media links
   [:div (stylefy/use-style footer-styles/some-link-wrapper)
    (doall
      (for [[link icon tag] [["https://www.facebook.com/FintrafficFI" feather-icons/facebook :facebook]
                             ["https://twitter.com/Fintraffic_fi" feather-icons/twitter :twitter]
                             ["https://www.instagram.com/fintraffic_stories_fi" feather-icons/instagram :instagram]
                             ["https://www.youtube.com/channel/UCpnhwBRjt58yUu_Oky7vyxQ" feather-icons/youtube :youtube]
                             ["https://www.linkedin.com/company/fintraffic" feather-icons/linkedin :linkedin]]]
        ^{:key (str "some_" tag)}
        [common/linkify
         link
         [icon (stylefy/use-style footer-styles/some-link-icon)]
         {:style               footer-styles/some-link
          :hide-external-icon? true}]))]

   [:div#footer-fundedby {:style {:margin-left "auto"
                                  :display "flex"}}
    [:img {:style {:width "80px" :height "52px" :margin-right "20px"} :src "/img/EU-logo.svg"}]
    (tr [:common-texts :footer-funded])]])