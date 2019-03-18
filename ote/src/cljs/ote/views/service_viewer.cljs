(ns ote.views.service-viewer
  (:require [ote.app.controller.service-viewer :as svc]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.ui.common :as common-ui]
            [ote.style.base :as style-base]
            [ote.localization :refer [tr tr-key]]
            [ote.ui.icons :as icons]
            [stylefy.core :as stylefy]
            [ote.theme.colors :as colors]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.front-page :as fp]
            [ote.ui.link-icon :refer [link-with-icon]]))

(def open-in-new-icon
  (ic/action-open-in-new {:style {:width 20
                                  :height 20
                                  :margin-right "0.5rem"
                                  :color colors/primary}}))

(defn- service-header
  [e! service-name o-id s-id]
  [:div
   [common-ui/linkify "/#/services" [:span [icons/arrow-back {:position "relative"
                                                              :top "6px"
                                                              :padding-right "5px"
                                                              :color style-base/link-color}]
                                     (tr [:service-search :back-link])]]
   [:h1 service-name]
   [link-with-icon open-in-new-icon (str "/export/geojson/" o-id "/" s-id) "Avaa tiedot GeoJSON-muodossa"]] ;TODO: create translation
  )

(defn- transport-operator-info
  [operator]
  )

(defn service-view
  [e! {{to :transport-operator ts :transport-service} :service-view}]
  [:div
   [service-header e! (::t-operator/name to) (::t-operator/id to) (::t-service/id ts)]
   [transport-operator-info to]])
