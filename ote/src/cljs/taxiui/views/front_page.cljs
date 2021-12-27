(ns taxiui.views.front-page
  "Front page for Taxi UI"
  (:require [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.common :refer [linkify]]
            [ote.app.utils :refer [user-logged-in?]]
            [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]
            [taxiui.views.components.formatters :as formatters]
            [taxiui.styles.front-page :as styles]
            [re-svg-icons.feather-icons :as feather-icons]))

(let [host (.-host (.-location js/document))]
  (def test-env? (or (str/includes? host "test")
                     (str/includes? host "localhost"))))

(defn info-panel
  [grid-area title icon label]
  [:div (merge (stylefy/use-style styles/info-panel)
               {:style {:grid-area grid-area}})
   [:h5 title]
   [icon] " " label])

(defn- pill
  [label]
  [:span (stylefy/use-style styles/area-pill) label])

(defn- panel-arrow
  []
  [feather-icons/chevron-right (merge (stylefy/use-style styles/details-arrow)
                                      {:stroke-width "0.5"
                                       :width        "50"
                                       :height       "100"
                                       :viewBox      "6 0 12 24"})])

(defn front-page
  "Front page info"
  [e! {user :user :as app}]
  [:main
   [:h2 "Omat palvelutiedot"]
   ; infobox
   [:section (stylefy/use-style styles/info-box)
    ; section title
    [:h4 (stylefy/use-style styles/info-section-title) "Palveluntuottajan tiedot"]
    [:div (stylefy/use-style styles/company-details)
     [:h5 {:style {:grid-area "title"}}
      [:span (stylefy/use-style styles/info-box-title) "Putkosen Kyyti Oy"] " 1234567-8"]
     [info-panel "personnel" "Henkilöstömäärä" feather-icons/users "1-4"]
     [info-panel "toimiala" "Toimialaluokitus" feather-icons/file "49320 Taksiliikenne"]
     [info-panel "liikevaihto" "Liikevaihtoluokka" feather-icons/file "0-0,2 milj. €"]
     [info-panel "yhtiomuoto" "Yhtiömuoto" feather-icons/tag "Osakeyhtiö"]
     [panel-arrow]]]

   [:section (stylefy/use-style styles/info-box)
    [:div (stylefy/use-style styles/pricing-details)
     [:div.logo {:style {:grid-area "logo"}}]
     [:h5 (stylefy/use-style styles/price-box-title)
      "Hintatiedot päivitetty 23.12.2021"]
     [:div (stylefy/use-style styles/example-price-title) "Esimerkkimatka (10 km + 15 min)"]
     [:div (stylefy/use-style styles/example-price)
      [:span (formatters/currency 36.90)]
      [:span (str (formatters/currency 1.5) "/km")]
      [:span (str (formatters/currency 1) "/min")]]
     [:div (stylefy/use-style styles/area-pills)
      [pill "hips"]
      [pill "hops"]
      [pill "kops"]]
     [panel-arrow]]]])
