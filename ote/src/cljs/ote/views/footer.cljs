(ns ote.views.footer
  "NAP - footer"
  (:require [ote.localization :refer [tr tr-key] :as localization]
            [ote.style.footer :as footer-styles]
            [ote.ui.common :as common :refer [linkify]]
            [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]))

(def selectable-languages [["fi" "Suomeksi"]
                           ["sv" "På Svenska"]
                           ["en" "In English"]])

(def ^:private legal-links
  {:fi [{:href "https://www.fintraffic.fi/fi/fintraffic/tietosuoja" :label "Tietosuoja"}
        {:href "https://www.fintraffic.fi/fi/palaute" :label "Palaute"}
        {:href "https://www.fintraffic.fi/fi/yhteystiedot" :label "Yhteystiedot"}
        {:href "https://www.fintraffic.fi/fi/fintraffic/saavutettavuusseloste" :label "Saavutettavuus"}]
   :sv [{:href "https://www.fintraffic.fi/sv/fintraffic/kontaktuppgifter" :label "Kontaktinformation"}
        {:href "https://www.fintraffic.fi/sv/fintraffic/dataskydd" :label "Dataskydd"}]
   :en [{:href "https://www.fintraffic.fi/en/fintraffic/contact-information-and-invoicing-instructions" :label "Contact information"}
        {:href "https://www.fintraffic.fi/en/fintraffic/privacy-policy" :label "Privacy policy"}]})

(defn footer []
  [:footer (stylefy/use-style footer-styles/footer)
   ; topbar
   [:div (stylefy/use-style footer-styles/topbar)
    [:img (merge (stylefy/use-style footer-styles/fintraffic-logo)
                 {:src "img/icons/Fintraffic_vaakalogo_valkoinen.svg"})]
    [:a (merge (stylefy/use-style footer-styles/link) {:href "https://fintraffic.fi"}) "fintraffic.fi"]]

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
           [:a (stylefy/use-style footer-styles/link {:href href}) (tr [:quicklink-header service])]]))]]

    [:ul (stylefy/use-style footer-styles/fintraffic-legal-links)
     (let [language (or (keyword @localization/selected-language) :fi)]
       (doall
         (for [{:keys [href label]} (get legal-links language)]
           ^{:key (str "legallink_" (clojure.string/lower-case (name label)))}
           [:li [:a (merge (stylefy/use-style footer-styles/link) {:href href})
                 label]])))
     ]

    [:ul (stylefy/use-style footer-styles/fintraffic-support-link)
     [:li
      [:a (merge (stylefy/use-style footer-styles/link)
                 {:href (tr [:common-texts :navigation-feedback-link])})
       [:span {:style {:font-weight 600}} (tr [:common-texts :navigation-give-feedback])][:br]
       (tr [:common-texts :navigation-feedback-email])]]]]

   ; social media links
   [:div (stylefy/use-style footer-styles/some-link-wrapper)
    [:a  (merge (stylefy/use-style footer-styles/some-link)
                {:href "https://www.facebook.com/FintrafficFI"})
     [feather-icons/facebook (stylefy/use-style footer-styles/some-link-icon)]]
    [:a (merge (stylefy/use-style footer-styles/some-link)
               {:href "https://twitter.com/Fintraffic_fi"})
     [feather-icons/twitter (stylefy/use-style footer-styles/some-link-icon)]]
    [:a (merge (stylefy/use-style footer-styles/some-link)
               {:href "https://www.instagram.com/fintraffic_stories_fi"})
     [feather-icons/instagram (stylefy/use-style footer-styles/some-link-icon)]]
    [:a (merge (stylefy/use-style footer-styles/some-link)
               {:href "https://www.youtube.com/channel/UCpnhwBRjt58yUu_Oky7vyxQ"})
     [feather-icons/youtube (stylefy/use-style footer-styles/some-link-icon)]]
    [:a (merge (stylefy/use-style footer-styles/some-link)
               {:href "https://www.linkedin.com/company/fintraffic"})
     [feather-icons/linkedin (stylefy/use-style footer-styles/some-link-icon)]]]])