(ns ote.main
  "OTE app startup"
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [cljsjs.react-leaflet]
            [cljsjs.leaflet-draw]
            [cljsjs.nprogress]
            [tuck.core :as tuck]
            [ote.app.state :as state]
            [ote.views.main :as main]
            [taxiui.app.controller.front-page :as taxi-controller]
            [taxiui.views.main :as taxi-main]
            [ote.localization :as localization]
            [ote.app.routes :as ote-routes]
            [taxiui.app.routes :as taxiui-routes]
            [ote.app.controller.front-page :as fp-controller]
            [stylefy.core :as stylefy]
            [ote.communication :as comm]
            [goog.net.Cookies]
            [ote.app.controller.login :as login]
            [ote.transit :as transit]))

(defn language []
  (.get (goog.net.Cookies. js/document)
        "finap_lang" "fi"))

(defn init-app
  "Initializes a frontend application using provided functions and session data.

   - `navigator` is a Tuck event handler for handling navigation
   - `application` is the root view for that particular application
   - `router-fn` is a callback function for initializing the application's router
   - `root-element-id` is the id of the DOM element the application should be mounted to
   - `session-data` contains data relating to user's active session, if any"
  [navigator application router-fn root-element-id session-data]
  (if (nil? session-data)
    (swap! state/app login/unauthenticated)
    (swap! state/app login/update-transport-operator-data session-data))
  (stylefy/init)
  (router-fn navigator)
  (state/windowresize-handler nil) ;; Calculate window width
  (r/render-component [tuck/tuck state/app application]
                      (.getElementById js/document root-element-id)))

(defn- load-embedded-user-info []
  (let [elt (.getElementById js/document "ote-user-info")
        user-info (transit/transit->clj (.-innerText elt))]
    (.removeChild (.-parentNode elt) elt)
    user-info))

(defn ^:export main []
  (localization/load-embedded-translations!)
  (init-app fp-controller/->GoToUrl main/ote-application ote-routes/start! "oteapp" (load-embedded-user-info)))

(defn ^:export taxi-main []
  (localization/load-embedded-translations!)
  (init-app taxi-controller/->GoToUrl taxi-main/taxi-application taxiui-routes/start! "taxiapp" (load-embedded-user-info)))

(defn ^:export reload-hook []
  (r/force-update-all))
