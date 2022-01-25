(ns taxiui.views.front-page
  "Front page for Taxi UI"
  (:require [clojure.string :as str]
            [re-svg-icons.feather-icons :as feather-icons]
            [stylefy.core :as stylefy]
            [taxiui.app.routes :as routes]
            [taxiui.app.controller.front-page :as fp-controller]
            [taxiui.app.controller.loader :as loader]
            [taxiui.styles.front-page :as styles]
            [taxiui.views.components.formatters :as formatters]
            [taxiui.views.components.pill :refer [pill]]
            [taxiui.views.components.link :refer [link]]
            [taxiui.app.routes :as routes]
            [taxiui.theme :as theme]
            [ote.localization :refer [tr]]
            [ote.theme.colors :as colors]))

(let [host (.-host (.-location js/document))]
  (def test-env? (or (str/includes? host "test")
                     (str/includes? host "localhost"))))

(defn info-panel
  [grid-area title icon label]
  [:div (merge (stylefy/use-style styles/info-panel)
               {:style {:grid-area grid-area}})
   [:h5 title]
   [icon (stylefy/use-style styles/panel-icon)] " " label])

(defn- panel-arrow
  []
  [feather-icons/chevron-right (merge (stylefy/use-style styles/details-arrow)
                                      {:stroke-width "0.5"
                                 :width        "50"
                                 :height       "100"
                                 :viewBox      "6 0 12 24"})])

(defn front-page
  [_ _]
  (fn [e! app]
   [:main (stylefy/use-style theme/main-container)
    [:h2 "Omat palvelutiedot"]
    [:h3 [:span (get-in app [:transport-operator :ote.db.transport-operator/name])] (str " " (get-in app [:transport-operator :ote.db.transport-operator/business-id]))]
    ; company information infobox, use this when YTJ integration gets implemented
    #_[link e! "#" :taxi-ui/front-page
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
       [panel-arrow]]]]

    (doall
      (for [service (get-in app [:taxi-ui :front-page :services])]
        ^{:key (str "price_infobox_" (:service-id service))}
        [link
         e!
         :taxi-ui/pricing-details
         {:operator-id (:operator-id service)
          :service-id  (:service-id service)}
         styles/info-box-link

         [:section (stylefy/use-style styles/info-box)
          [:h4 (stylefy/use-style styles/info-section-title) (str (:service-name service) (tr [:taxi-ui :front-page :sections :service-summary :price-information-header]))]
          [:div (stylefy/use-style styles/pricing-details)
           [:div.logo {:style {:grid-area "logo"}}]
           (if (:updated service)
             [:h5 (stylefy/use-style styles/price-box-title)
              (tr [:taxi-ui :front-page :sections :service-summary :information-last-updated]) (:updated service)]
             [:h5 (stylefy/use-style styles/price-box-title)
              (tr [:taxi-ui :front-page :sections :service-summary :information-never-updated])])
           [:div (stylefy/use-style styles/example-price-title) (tr [:taxi-ui :front-page :sections :service-summary :example-trip])]

           (if (:example-trip service)
             [:div (stylefy/use-style styles/example-prices)
              [:span (formatters/currency (:example-trip service))]
              [:span (stylefy/use-style styles/flex-right-aligned)
               [:span (str (formatters/currency (:price-per-kilometer service)) (tr [:taxi-ui :front-page :sections :service-summary :per-kilometer]))]
               [:span (stylefy/use-style styles/currency-breather) (str (formatters/currency (:price-per-minute service)) (tr [:taxi-ui :front-page :sections :service-summary :per-minute]))]]]

             [:div (stylefy/use-style styles/example-prices)
              [:span (tr [:taxi-ui :front-page :sections :service-summary :prices-not-added-yet])]])

           [:div (stylefy/use-style styles/area-pills)
            (doall
              (for [area (:operating-areas service)]
                ^{:key (str "service_" (:service-id service) "_area_" area)}
                [pill area]))]
           [panel-arrow]]]]))]))
