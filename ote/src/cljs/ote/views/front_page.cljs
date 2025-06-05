(ns ote.views.front-page
  "Front page for OTE service - Select service type and other basic functionalities"
  (:require [clojure.string :as s]
            [reagent.core :as r]
            [re-svg-icons.feather-icons :as feather-icons]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [ote.style.front-page :as style-front-page]
            [ote.ui.icons :as icons]
            [ote.ui.common :refer [linkify extended-help-link] :as uicommon]
            [ote.app.utils :refer [user-logged-in?]]
            [ote.app.controller.flags :as flags]
            [ote.app.controller.front-page :as fp]))


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

(defn downtime-apology-message
  []
  [:div
   {:style {:margin "0.2em"
            :border "4px solid #213BF7"}}
   [:p {:style {:margin "10px 0px 0px 10px"
                :font-weight "bold"}}
    (tr [:downtime-apology :title])]
   [:p {:style {:margin "10px"}}
    (tr [:downtime-apology :content])]])

(defn merirae-discontinued-message
  []
  [:div
   {:style {:margin "0.2em" :border "4px solid #213BF7"}}
   [:p {:style {:margin "10px 0px 0px 10px"}}
    (tr [:front-page :rae-discontinued-info1])
    (linkify (tr [:front-page :rae-discontinued-info-link]) (tr [:front-page :rae-discontinued-info-desc]) {:target "_blank"})
    (tr [:front-page :rae-discontinued-info2])]])

(defn front-page
  "Front page info"
  [e! {user :user :as app}]
  [:div
   [:div.hero (stylefy/use-style style-front-page/hero-img)
    [:div {:style {:padding-top "20px"}}
     [:h1 (stylefy/use-style style-front-page/front-page-h1) "NAP"]
     [:div (stylefy/use-style style-front-page/front-page-hero-text) (tr [:front-page :hero-title])]
     [:div.row (stylefy/use-style style-front-page/hero-btn-container)
      [:div {:style {:width "330px" :margin-left "auto" :margin-right "auto"}}
       [:a {:style {:text-decoration "none"}
            :href "/#/services"
            :on-click #(do
                         (.preventDefault %)
                         (e! (fp/->ChangePage :services nil)))}
        [:button (stylefy/use-style style-front-page/hero-btn)
         [:span [feather-icons/monitor {:style {:height "23px" :width "40px" :padding-top "0px" :color "#fff"}}]]
         (tr [:buttons :transport-service-catalog])]]]
      (when (flags/enabled? :other-catalogs)
        [:div {:style {:width "330px" :margin-left "auto" :margin-right "auto" :margin-top "1.5rem"}}
         [:a {:style {:text-decoration "none"}
              :href (tr [:buttons :other-access-points-url])
              :target "_blank"}
          [:button (stylefy/use-style style-front-page/hero-btn)
           [:span [feather-icons/external-link {:style {:height "23px" :width "40px" :padding-top "0px" :color "#fff"}}]]
           (tr [:buttons :other-access-points])]]])]]]

   (when test-env?
     [test-env-warning])

   ; NOTE: commented out just in case we want to find this easier the next time we want to show an alert on the front page
   ; [downtime-apology-message]

   [merirae-discontinued-message]

   [:div.container
    [:div.row (stylefy/use-style style-front-page/row-media)
     [:div.col-xs-12.col-sm-3.col-md-3 (stylefy/use-style style-front-page/large-icon-container)
      [icons/all-out style-front-page/large-font-icon]]
     [:div.col-xs-12.col-sm-9.col-md-9 (stylefy/use-style style-front-page/large-text-container)
      [:h2
       (stylefy/use-style style-front-page/h2)
       (tr [:front-page :title-NAP])]
      [:p {:style {:font-size "1em" :font-weight 400 :text-align "left" :line-height "1.5rem"}}
       (tr [:front-page :column-NAP])]]]

    [:div.row (stylefy/use-style style-front-page/row-media)
     [:div.col-xs-12.col-sm-9.col-md-9 (stylefy/use-style style-front-page/large-text-container)
      [:h2
       (stylefy/use-style style-front-page/h2)
       (tr [:front-page :title-transport-services])]
      [:p {:style {:font-size "1em" :font-weight 400 :text-align "left" :line-height "1.5rem"}}
       (tr [:front-page :column-transport-services])]]
     [:div.col-xs-12.col-sm-3.col-md-3 (stylefy/use-style style-front-page/large-icon-container)
      [icons/airport-shuttle style-front-page/large-font-icon]]]

    [:div.row (stylefy/use-style style-front-page/row-media)
     [:div.col-xs-12.col-sm-3.col-md-3 (stylefy/use-style style-front-page/large-icon-container)
      [icons/flag style-front-page/large-font-icon]]
     [:div.col-xs-12.col-sm-9.col-md-9 (stylefy/use-style style-front-page/large-text-container)
      [:h2
       (stylefy/use-style style-front-page/h2)
       (tr [:front-page :title-essential-info])]
      [:p {:style {:font-size "1em" :font-weight 400 :text-align "left" :line-height "1.5rem"}}
       (tr [:front-page :column-essential-info])]]]]

   [:div (stylefy/use-style style-front-page/lower-section)
    [:div.container {:style {:display "flex"
                             :flex-wrap "wrap"
                             :justify-content "center"}}
     [:div.col-md-6 (stylefy/use-style style-front-page/media-transport-service)
      [:div (stylefy/use-style style-front-page/lower-section-data-container)
       [icons/train style-front-page/lower-section-font-icon]
       [:h3 (stylefy/use-style style-front-page/lower-section-title) (tr [:front-page :title-transport-operator])]
       [:p (stylefy/use-style style-front-page/lower-section-text)
        (tr [:front-page :column-transport-operator])]

       [:div {:style {:padding-top "20px"}}
        (if (not (user-logged-in? app))
          [:a {:style {:text-decoration "none"}
               :on-click #(do
                            (.preventDefault %)
                            (e! (fp/->ToggleRegistrationDialog)))}
           [:button (stylefy/use-style style-front-page/front-page-button)
            [:span [feather-icons/user-plus {:style {:height 23 :width 40 :padding-top 0 :color "#fff"}}]]
            (tr [:buttons :register-to-service])]]
          [:div (stylefy/use-style style-front-page/front-page-button-disabled)
           [:span [feather-icons/user-plus {:style {:height 23 :width 40 :padding-top 0 :color "#fff"}}]]
           (tr [:buttons :register-to-service])])]]]

     [:div.col-md-6 (stylefy/use-style style-front-page/media-transport-service)
      [:div (stylefy/use-style style-front-page/lower-section-data-container)
       [icons/developer-mode style-front-page/lower-section-font-icon]
       [:h3 (stylefy/use-style style-front-page/lower-section-title) (tr [:front-page :title-developer])]
       [:p (stylefy/use-style style-front-page/lower-section-text)
        (tr [:front-page :column-developer])]
       [:div {:style {:padding-top "20px"}}
        [:a {:on-click #(do
                          (.preventDefault %)
                          (e! (fp/->ChangePage :services nil)))}
         [:button (stylefy/use-style style-front-page/front-page-button)
          [:span [feather-icons/monitor {:style {:height 23 :width 40 :padding-top 0 :color "#fff"}}]]
          (tr [:buttons :check-out-the-service])]]]]]

     [:div.col-md-6 (stylefy/use-style style-front-page/media-transport-service)
      [:div (stylefy/use-style style-front-page/lower-section-data-container)
       [:img (merge (stylefy/use-style style-front-page/lower-section-font-icon)
                    {:src "/img/icons/Traficom.svg"
                     :alt (tr [:common-texts :footer-livi-logo])}) ]
       [:p (stylefy/use-style style-front-page/lower-section-text)
        (tr [:front-page :column-service-operator-info])]
       [:div {:style {:padding-top "20px"}}
        [linkify
         "https://www.traficom.fi/fi/liikenne-ja-viestintavirasto-traficom"
         [:button (stylefy/use-style style-front-page/front-page-button)
          [:span [feather-icons/compass {:style {:height 23 :width 40 :padding-top 0 :color "#fff"}}]]
          (tr [:buttons :visit-service-operator])]
         {:hide-external-icon? true}]]]]]]])
