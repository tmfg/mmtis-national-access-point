(ns ote.main
  "OTE app startup"
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [cljsjs.react-leaflet]
            [cljsjs.leaflet-draw]
            [tuck.core :as tuck]
            [ote.app.state :as state]
            [ote.views.main :as main]
            [ote.views.ckan-service-viewer :as ckan-service-viewer]
            [ote.views.ckan-org-viewer :as ckan-org-viewer]
            [ote.views.ckan-org-editor :as ckan-org-edit]
            [ote.localization :as localization]
            [ote.app.routes :as routes]
            [stylefy.core :as stylefy]
            [ote.communication :as comm]))

(defn ^:export main []
  (localization/load-language!
   :fi
   (fn [lang _]
     (reset! localization/selected-language lang)
     (stylefy/init)
     (routes/start!)
     (state/windowresize-handler nil) ;; Calculate window width
     (r/render-component [tuck/tuck state/app main/ote-application]
                         (.getElementById js/document "oteapp")))))

(defn ^:export reload-hook []
  (r/force-update-all))

(defn ^:export geojson_view []
  (comm/set-base-url! "/ote/")
  (localization/load-language!
   :fi
   (fn [lang _]
     (reset! localization/selected-language lang)
     (stylefy/init)
     (r/render-component [tuck/tuck state/viewer ckan-service-viewer/viewer]
                         (.getElementById js/document "nap_viewer")))))

(defn ^:export ckan_org_view []
  (comm/set-base-url! "/ote/")
  (localization/load-language!
    :fi
    (fn [lang _]
      (reset! localization/selected-language lang)
      (stylefy/init)
      (r/render-component [tuck/tuck state/app ckan-org-viewer/viewer]
                          (.getElementById js/document "nap_viewer")))))


(defn ^:export ckan_org_edit [ckan-organization-id]
  (comm/set-base-url! "/ote/")
  (localization/load-language!
    :fi
    (fn [lang _]
      (reset! localization/selected-language lang)
      (stylefy/init)
      (state/set-init-state! {:ckan-organization-id ckan-organization-id})
      (r/render-component [tuck/tuck state/app ckan-org-edit/editor]
                          (.getElementById js/document "nap_viewer")))))