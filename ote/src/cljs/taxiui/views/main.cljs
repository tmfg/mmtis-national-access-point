(ns taxiui.views.main
  "OTE application view composer"
  (:require [reagent.core :as r]
            [cljs-react-material-ui.core :refer [color]]
            [stylefy.core :as stylefy]
            [ote.localization :refer [tr tr-key]]
            [ote.style.base :as style-base]
            [ote.ui.circular_progress :as spinner]
            [ote.ui.main-header :refer [header]]
            [ote.ui.common :refer [linkify]]
            [ote.app.controller.front-page :as fp-controller]
            [taxiui.views.front-page :as fp]  ;; TODO copy
            [ote.views.footer :as footer]
            [ote.views.theme :refer [theme]]))

(defn document-title [page]
  (set! (.-title js/document)
        (case page
          :services (tr [:document-title :services])
          :own-services (tr [:document-title :own-services])
          :admin (tr [:document-title :admin])

          ;; default title for other pages
          (tr [:document-title :default])))

  ;; Render as an empty span
  [:span])


(defn- scroll-to-page
  "Invisible component that scrolls to the top of the window whenever it is mounted."
  [page]
  (r/create-class
   {:component-did-mount #(.scrollTo js/window 0 0)
    :reagent-render
    (fn [page]
      [:span])}))

(def wide-pages #{:transit-visualization :transit-changes :authority-pre-notices
                  :own-services :admin :services :edit-route :new-route :routes
                  :new-notice :edit-pre-notice :edit-service :new-service
                  :view-gtfs})

(defn taxi-application
  "Taxi UI application main view"
  [e! app]
  (fn [e! {loaded? :transport-operator-data-loaded? :as app}]
    (let [desktop? (> (:width app) style-base/mobile-width-px)
          wide? (boolean (wide-pages (:page app)))]
      [:div {:style (stylefy/use-style style-base/body)}
       [theme e! app
        (if (nil? app)
          [spinner/circular-progress]
          [:div.ote-sovellus {:style {:display "flex"
                                      :flex-direction "column"
                                      :height "100vh"}}
           [header e! app desktop?]
           [:div (merge (stylefy/use-style style-base/sticky-footer)
                        {:on-click #(e! (fp-controller/->CloseHeaderMenus))})
            (if (not loaded?)
              [spinner/circular-progress]
              [(if wide? :div :div.wrapper)
               (if wide?
                 {}
                 (stylefy/use-style {:transition "margin-top 300ms ease"}))
               [:div (cond
                       (= :front-page (:page app))
                       {:class "container-fluid"}

                       wide?
                       {}

                       :else
                       {:style {:padding-bottom "20px"}
                        :class "container"})
                [document-title (:page app)]

                ;; Ensure that a new scroll-to-page component is created when page changes
                ^{:key (name (:page app))}
                [scroll-to-page (:page app)]

                (case (:page app)
                  :front-page [fp/front-page e! app]

                  [:div (tr [:common-texts :no-such-page]) (pr-str (:page app))])]])
            [footer/footer]]])]])))
