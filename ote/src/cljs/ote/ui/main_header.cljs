(ns ote.ui.main-header
  "Main header of the OTE app"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [cljs-react-material-ui.icons :as ic]
            [ote.ui.common :refer [linkify ckan-iframe-dialog]]
            [ote.views.transport-operator :as to]
            [ote.views.front-page :as fp]
            [ote.app.controller.front-page :as fp-controller]
            [ote.views.footer :as footer]
            [ote.localization :refer [tr tr-key]]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [ote.style.topnav :as style-topnav]
            [ote.views.theme :refer [theme]]
            [ote.app.controller.login :as login]
            [ote.app.controller.flags :as flags]
            [clojure.string :as str]
            [ote.localization :as localization]
            [ote.ui.common :as common-ui]))

(defn header-scroll-sensor [is-scrolled? trigger-offset]
  (let [sensor-node (atom nil)
        check-scroll (fn []
                       (let [element-y (.-top (.getBoundingClientRect @sensor-node))]
                         (reset! is-scrolled? (< element-y trigger-offset))))]

    (r/create-class
      {:component-did-mount
       (fn [this]
         (reset! sensor-node (aget this "refs" "sensor"))
         (check-scroll)
         (.addEventListener js/window "scroll" check-scroll))
       :component-will-unmount
       (fn [this]
         (.removeEventListener js/window "scroll" check-scroll))
       :reagent-render
       (fn [_]
         [:span {:ref "sensor"}])})))

(defn logged-in? [app]
  (not-empty (get-in app [:user :username])))

(defn- is-user-menu-active [app]
  (when (= true (get-in app [:ote-service-flags :user-menu-open]))
    "active"))

(defn page-active?
  "Return true if given current-page belongs to given page-group"
  [page-group current-page pages]
  (cond
    (= page-group :front-page current-page) true
    (page-group pages) ((page-group pages) current-page)
    :default false))

(defn- lang-menu [e! app]
  (let [header-open? (get-in app [:ote-service-flags :lang-menu-open])]
    [:div {:style (merge style-topnav/topnav-dropdown
                         (if header-open?
                           {:opacity    1
                            :visibility "visible"}
                           {:opacity    0
                            :visibility "hidden"
                            ;; Remove the element from normal document flow, by setting position absolute.
                            :position   "absolute"}))}
     [:div.container
      [:div.row
       [:div.col-sm-4.col-md-4]
       [:div.col-sm-4.col-md-4]
       [:div.col-sm-8.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         (doall
           (for [[lang flag] footer/selectable-languages]
             [:li
              [:a (merge
                    (stylefy/use-style style-topnav/topnav-dropdown-link)
                    {:key      lang
                     :href     "#"
                     :on-click #(do
                                  (.preventDefault %)
                                  (e! (fp-controller/->OpenLangMenu))
                                  (e! (fp-controller/->SetLanguage lang)))
                     })
               (str (str/upper-case lang) " - " flag)]]))]]]]]))

(defn- user-menu [e! app]
  (when (get-in app [:user :username])
    (let [header-open? (get-in app [:ote-service-flags :user-menu-open])]
      [:div {:style (merge style-topnav/topnav-dropdown
                           (if header-open?
                             {:opacity    1
                              :visibility "visible"}
                             {:opacity    0
                              :visibility "hidden"
                              ;; Remove the element from normal document flow, by setting position absolute.
                              :position   "absolute"}))}
       [:div.container
        [:div.row
         [:div.col-sm-2.col-md-4]
         [:div.col-sm-2.col-md-4]
         [:div.col-sm-8.col-md-4
          [:ul (stylefy/use-style style-topnav/ul)
           (when (and (flags/enabled? :pre-notice) (get-in app [:user :transit-authority?]))
             [:li
              [:a (merge (stylefy/use-style
                           style-topnav/topnav-dropdown-link)
                         {:href     "#/authority-pre-notices"
                          :on-click #(e! (fp-controller/->OpenUserMenu))})
               (tr [:common-texts :navigation-authority-pre-notices])]])
           (when (:admin? (:user app))
             [:li
              [:a (merge (stylefy/use-style
                           style-topnav/topnav-dropdown-link)
                         {:href "#/admin"
                          :on-click #(do
                                       (.preventDefault %)
                                       (e! (fp-controller/->OpenUserMenu)))})
               (tr [:document-title :admin])]])
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href     "#"
                        :on-click #(do (.preventDefault %)
                                       (e! (fp-controller/->OpenUserMenu))
                                       (e! (fp-controller/->ToggleUserEditDialog)))})
             (tr [:common-texts :user-menu-profile])]]
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href     "#"
                        :on-click #(do (.preventDefault %)
                                       (e! (fp-controller/->OpenUserMenu))
                                       (e! (login/->Logout)))})
             (tr [:common-texts :user-menu-log-out])]]]]]]])))

#_ (defn user-menu [e! {:keys [name username transit-authority?]}]
  (when username
    [ui/drop-down-menu
     {:menu-style {}
      :underline-style {}
      :label-style {:color "#FFFFFF" :font-weight 700}
      :list-style {:background-color "#323232"}
      :on-click #(e! (fp-controller/->OpenUserMenu))
      :anchor-origin {:horizontal "right" :vertical "bottom"}
      :target-origin {:horizontal "right" :vertical "top"}
      :selection-renderer (constantly name)}

     (when (flags/enabled? :sea-routes)
       [ui/menu-item {:style {:color "#FFFFFF"}
                      :primary-text (tr [:common-texts :navigation-route])
                      :on-click #(do (.preventDefault %)
                                     (e! (fp-controller/->ChangePage :routes nil)))}])
     (when (flags/enabled? :pre-notice)
       [ui/menu-item {:style {:color "#FFFFFF"}
                      :primary-text (tr [:common-texts :navigation-pre-notice])
                      :on-click #(do (.preventDefault %)
                                     (e! (fp-controller/->ChangePage :pre-notices nil)))}])

     (when (and (flags/enabled? :pre-notice) transit-authority?)
       [ui/menu-item {:style {:color "#FFFFFF"}
                      :primary-text (tr [:common-texts :navigation-authority-pre-notices])
                      :on-click #(do (.preventDefault %)
                                     (e! (fp-controller/->ChangePage :authority-pre-notices nil)))}])


     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :user-menu-profile])
                    :on-click #(do (.preventDefault %)
                                   (e! (fp-controller/->ToggleUserEditDialog)))}]
     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :navigation-give-feedback])
                    :on-click #(do (.preventDefault %)
                                   (e! (fp-controller/->OpenNewTab "http://bit.ly/nap-palaute")))}]
     [ui/menu-item {:style {:color "#FFFFFF"}
                    :primary-text (tr [:common-texts :user-menu-log-out])
                    :on-click #(do (.preventDefault %)
                                   (e! (login/->Logout)))}]

     [ui/menu-item {:primary-text (r/as-element [footer/language-selection e! style-base/language-selection-dropdown {:color "#fff"}])}]]))


(defn- top-nav-drop-down-menu [e! app is-scrolled? pages]
  (let [header-open? (get-in app [:ote-service-flags :header-open])]
    [:div {:style (merge style-topnav/topnav-dropdown
                         (if header-open?
                           {:opacity 1
                            :visibility "visible"}
                           {:opacity 0
                            :visibility "hidden"
                            ;; Remove the element from normal document flow, by setting position absolute.
                            :position "absolute"}))}
     [:div.container
      [:div.row
       [:div.col-sm-4.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         [:li
          [:a (merge (stylefy/use-style
                       style-topnav/topnav-dropdown-link)
                     {:href "#/"
                      :on-click #(e! (fp-controller/->OpenHeader))})
           (tr [:common-texts :navigation-front-page])]]
         [:li
          [:a (merge (stylefy/use-style
                       style-topnav/topnav-dropdown-link)
                     {:href "#/services"
                      :on-click #(e! (fp-controller/->OpenHeader))})
           (tr [:document-title :services])]]
         (when (get-in app [:user :username])
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/own-services"
                        :on-click #(e! (fp-controller/->OpenHeader))})
             (tr [:document-title :own-services])]])
         (when (get-in app [:user :username])
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/routes"
                        :on-click #(e! (fp-controller/->OpenHeader))})
             (tr [:common-texts :navigation-route])]])
         (when (get-in app [:user :username])
           [:li
            [:a (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link)
                       {:href "#/pre-notices"
                        :on-click #(e! (fp-controller/->OpenHeader))})
             (tr [:common-texts :navigation-pre-notice])]])]]
       [:div.col-sm-4.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         [:li
          [linkify "https://s3.eu-central-1.amazonaws.com/ote-assets/nap-ohje.pdf" (tr [:common-texts :user-menu-nap-help])
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]

         [:li
          [linkify "https://github.com/finnishtransportagency/mmtis-national-access-point/blob/master/docs/api/README.md"
           "Ohjelmointirajapinta kehittäjille"
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]]]
       [:div.col-sm-4.col-md-4
        [:ul (stylefy/use-style style-topnav/ul)
         (if (nil? (get-in app [:user :username]))
           [:ul (stylefy/use-style style-topnav/ul)
            [:li
             [:a (merge (stylefy/use-style
                          style-topnav/topnav-dropdown-link)
                        {:href "#"
                         :on-click #(do
                                      (.preventDefault %)
                                      (e! (fp-controller/->OpenHeader))
                                      (e! (fp-controller/->ToggleRegistrationDialog)))})
              (tr [:common-texts :navigation-register])]]
            [:li
             (if (flags/enabled? :ote-login)
               [:a (merge (stylefy/use-style
                            style-topnav/topnav-dropdown-link)
                          {:href "#"
                           :on-click #(do
                                        (.preventDefault %)
                                        (e! (fp-controller/->OpenHeader))
                                        (e! (login/->ShowLoginDialog)))})
                (tr [:common-texts :navigation-login])]
               [linkify "/user/login" (tr [:common-texts :navigation-login])
                (merge (stylefy/use-style
                         style-topnav/topnav-dropdown-link))])]])

         [:li
          [linkify "https://www.liikennevirasto.fi/yhteystiedot/tietosuoja" "Rekisteriseloste"
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]
         [:li
          [linkify "http://bit.ly/nap-palaute" "Palautelomake"
           (merge (stylefy/use-style
                    style-topnav/topnav-dropdown-link)
                  {:target "_blank"})]]]]]]]))

(defn- top-nav-links [e! {current-page :page :as app} is-scrolled? pages]
  (let [current-language @localization/selected-language]
    [:div.navbar (stylefy/use-style style-topnav/clear)
     [:ul (stylefy/use-style style-topnav/ul)
      [:li
       [:a
        {:style    (merge
                     style-topnav/desktop-link
                     (if @is-scrolled?
                       {:padding-top "0px"}
                       {:padding-top "11px"}))
         :href     "#"
         :on-click #(do
                      (.preventDefault %)
                      (e! (fp-controller/->CloseHeaderMenus))
                      (e! (fp-controller/->ChangePage :front-page nil)))}
        [:img {:style (merge
                        style-topnav/logo
                        (when @is-scrolled?
                          style-topnav/logo-small))
               :src   "img/icons/nap-logo.svg"}]]]

      (doall
        (for [{:keys [page label url]}
              (filter some? [{:page  :services
                              :label [:common-texts :navigation-dataset]}
                             (when (logged-in? app)
                               {:page  :own-services
                                :label [:common-texts :navigation-own-service-list]})])]
          ^{:key page}
          [:li
           [:a
            (merge
              (stylefy/use-style style-topnav/desktop-link)
              {:href     "#"
               :on-click #(do
                            (.preventDefault %)
                            (e! (fp-controller/->CloseHeaderMenus))
                            (if url
                              (e! (fp-controller/->GoToUrl url))
                              (e! (fp-controller/->ChangePage page nil))))})
            [:div
             (tr label)]]]))


      [:li (if (get-in app [:ote-service-flags :lang-menu-open])
             (stylefy/use-style style-topnav/li-right-blue)
             (stylefy/use-style style-topnav/li-right-white))
       [:div {:on-click #(e! (fp-controller/->OpenLangMenu))
              :style    {:cursor  "pointer"
                         :display "-webkit-box"}}
        [:div {:style (merge {:transition "margin-top 300ms ease"}
                             (if @is-scrolled?
                               {:margin-top "15px"}
                               {:margin-top "28px"}))}
         [ic/action-language {:style {:color "#fff" :height 24 :width 30 :top 5}}]]
        [:span.hidden-xs.hidden-sm {:style {:color "#fff"}}
         (str/upper-case (name current-language))]]]

      [:li (if (get-in app [:ote-service-flags :header-open])
             (stylefy/use-style style-topnav/li-right-blue)
             (stylefy/use-style style-topnav/li-right-white))
       [:div {:on-click #(e! (fp-controller/->OpenHeader))
              :style    {:cursor        "pointer"
                         :display       "-webkit-box"
                         :padding-right "20px"}}
        [:div {:style (merge {:transition "margin-top 300ms ease"}
                             (if @is-scrolled?
                               {:margin-top "15px"}
                               {:margin-top "28px"}))}
         [ic/action-reorder {:style {:color "#fff" :height 24 :width 30 :top 5}}]]
        [:span.hidden-xs.hidden-sm {:style {:color "#fff"}} " Valikko "]]]

      (when (get-in app [:user :username])
        [:li (if (get-in app [:ote-service-flags :user-menu-open])
               (stylefy/use-style style-topnav/li-right-blue)
               (stylefy/use-style style-topnav/li-right-white))
         [:div {:on-click #(e! (fp-controller/->OpenUserMenu))
                :style    {:cursor        "pointer"
                           :display       "-webkit-box"
                           :padding-right "20px"}}
          [:div {:style (merge {:transition "margin-top 300ms ease"}
                               (if @is-scrolled?
                                 {:margin-top "15px"}
                                 {:margin-top "28px"}))}
           [ic/social-person {:style {:color "#fff" :height 24 :width 30 :top 5}}]]
          [:span.hidden-xs.hidden-sm {:style {:color "#fff"}} (common-ui/maybe-shorten-text-to 25 (get-in app [:user :name]))]]])]]))


(defn- top-nav [e! app is-scrolled? pages]
  [:span
   [header-scroll-sensor is-scrolled? -250]
   [:div (stylefy/use-style style-topnav/topnav-wrapper)
    [:div
     (stylefy/use-style (merge
                          style-topnav/topnav-desktop
                          (when @is-scrolled?
                            {:height "56px" :line-height "56px"})))
     [:div.container
      [top-nav-links e! app is-scrolled? pages]]]
    [top-nav-drop-down-menu e! app is-scrolled?]
    [user-menu e! app]
    [lang-menu e! app]]])
