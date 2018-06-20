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
            [ote.app.controller.flags :as flags]))


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

(defn header-links [app]
  (let [operators-list-link (if (flags/enabled? :ote-operators-list)
                              {:page  :operators
                               :label [:common-texts :navigation-organizations]}
                              {:page  :organizations
                               :label [:common-texts :navigation-organizations]
                               :url   "/organization"})]
    (filter some?
            [{:page  :front-page
              :label [:common-texts :navigation-front-page]}

             {:page  :services
              :label [:common-texts :navigation-dataset]}

             operators-list-link

             (when (logged-in? app)
               {:page  :own-services
                :label [:common-texts :navigation-own-service-list]})

             (when (:admin? (:user app))
               {:page :admin
                :label [:common-texts :navigation-admin]})])))

(defn user-menu [e! {:keys [name username transit-authority?]}]
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
                                   (e! (login/->Logout)))} ]

     [ui/menu-item {:primary-text (r/as-element [footer/language-selection e! style-base/language-selection-dropdown {:color "#fff"}])}]]))

(defn- top-nav-links [e! {current-page :page :as app} desktop? is-scrolled? pages]
  [:div.navbar (stylefy/use-style style-topnav/clear)
   [:ul (stylefy/use-style style-topnav/ul)
    (when (> (:width app) style-base/mobile-width-px)
      [:li
       [:a
        {:style (merge (if desktop?
                         style-topnav/desktop-link
                         style-topnav/link)
                       (if @is-scrolled?
                         {:padding-top "0px"}
                         {:padding-top "11px"}))
         :href "#"
         :on-click #(do
                      (.preventDefault %)
                      (e! (fp-controller/->ChangePage :front-page nil)))}
        [:img {:style (merge
                        style-topnav/logo
                        (when @is-scrolled?
                          style-topnav/logo-small))
               :src "img/icons/nap-logo.svg"}]]])

    (doall
      (for [{:keys [page label url]} (header-links app)]
        ^{:key page}
        [:li (if desktop? nil (stylefy/use-style style-topnav/mobile-li))
         [:a
          (merge (stylefy/use-style
                   (if (page-active? page current-page pages)
                     (if desktop? style-topnav/desktop-active style-topnav/active)
                     (if desktop? style-topnav/desktop-link style-topnav/link)))
                 {:href     "#"
                  :on-click #(do
                               (.preventDefault %)
                               (if url
                                 (e! (fp-controller/->GoToUrl url))
                                 (e! (fp-controller/->ChangePage page nil))))})
          (tr label)]]))
    [:div.user-menu {:class (is-user-menu-active app)
                     :style (merge
                              {:transition "padding-top 300ms ease"}
                              (when (> (:width app) style-base/mobile-width-px)
                                {:float "right" :padding-top "12px"})
                              (when @is-scrolled?
                                {:padding-top "0px"}))}
     [user-menu e! (:user app)]]

    (if (nil? (get-in app [:user :username]))
      [:ul (stylefy/use-style style-topnav/ul)
       [:li {:style {:float "right"}}
        [:a (merge (stylefy/use-style
                     (if desktop? style-topnav/desktop-link style-topnav/link))
                   {:href "#"
                    :on-click #(do
                                 (.preventDefault %)
                                 (e! (fp-controller/->ToggleRegistrationDialog)))})
         (tr [:common-texts :navigation-register])]]
       [:li
        (if (flags/enabled? :ote-login)
          [:a (merge (stylefy/use-style
                       (if desktop? style-topnav/desktop-link style-topnav/link))
                     {:style {:float "right"}}
                     {:on-click #(do
                                   (.preventDefault %)
                                   (e! (login/->ShowLoginDialog)))})
           (tr [:common-texts :navigation-login])]
          [linkify "/user/login" (tr [:common-texts :navigation-login])
           (merge (stylefy/use-style
                    (if desktop? style-topnav/desktop-link style-topnav/link))
                  {:style {:float "right"}})])]])

    [:li (if desktop? nil (stylefy/use-style style-topnav/mobile-li))
     [linkify "https://s3.eu-central-1.amazonaws.com/ote-assets/nap-ohje.pdf" (tr [:common-texts :user-menu-nap-help])
      (merge (stylefy/use-style
               (if desktop? style-topnav/desktop-link style-topnav/link))
             {:target "_blank"
              :style (when (> (:width app) style-base/mobile-width-px)
                       {:float "right"})})]]]])

(defn- mobile-top-nav-links [e! app is-scrolled? pages]
  [:div
   [:ul (stylefy/use-style style-topnav/ul)
    [:li (stylefy/use-style style-topnav/li)
     [:a (merge
           (stylefy/use-style style-topnav/link)
           {:href     "#"
            :on-click #(do
                         (.preventDefault %)
                         (e! (fp-controller/->GoToUrl "/")))})
      [:img {:src "img/icons/nap-logo.svg"}]]]
    [:li (stylefy/use-style style-topnav/right)
     [ui/icon-button {:on-click #(e! (fp-controller/->OpenHeader))
                      :style {:padding 8
                              :width 56
                              :height 56}
                      :icon-style {:height 40
                                   :width 40}}
      [ic/action-reorder {:style {:color "#fff"
                                  :width 40
                                  :height 40
                                  }}]]]]
   (when (get-in app [:ote-service-flags :header-open])
     (top-nav-links e! app false is-scrolled? pages))])


(defn- top-nav [e! app is-scrolled? desktop?
                pages]
  [:span
   [header-scroll-sensor is-scrolled? -250]
   [:div
    (stylefy/use-style (merge
                         (if desktop? style-topnav/topnav-desktop style-topnav/topnav)
                         (when (and desktop? @is-scrolled?)
                           {:height "56px" :line-height "56px"})))
    [:div.container
     (if desktop?
       (top-nav-links e! app true is-scrolled? pages)
       (mobile-top-nav-links e! app is-scrolled? pages))]]])
