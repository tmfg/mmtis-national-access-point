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
            [ote.app.routes :as routes]
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
   - `root-element-id` is the id of the DOM element the application should be mounted to
   - `session-data` contains data relating to user's active session, if any"
  [navigator application root-element-id session-data]
  (if (nil? session-data)
    (swap! state/app login/unauthenticated)  ; TODO: this resets the session' user data, which is hyper annoying
    (swap! state/app login/update-transport-operator-data session-data))
  (stylefy/init)
  (routes/start! navigator)
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
  (init-app fp-controller/->GoToUrl main/ote-application "oteapp" (load-embedded-user-info)))

(defn ^:export taxi-main []
  (localization/load-embedded-translations!)
  (init-app taxi-controller/->GoToUrl taxi-main/taxi-application "taxiapp" (load-embedded-user-info)))

(defn ^:export reload-hook []
  (r/force-update-all))
