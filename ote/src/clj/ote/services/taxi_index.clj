(ns ote.services.taxi-index
  "Index page generation for Taxi UI. Based loosely on [[ote.services.index]]"
  (:require [ote.components.http :as http]
    [compojure.core :refer [routes GET]]
    [hiccup.core :refer [html]]
    [hiccup.element :refer [javascript-tag]]
    [ote.localization :as localization :refer [tr]]
    [clojure.string :as str]
    [com.stuartsierra.component :as component]
    [ote.tools.git :refer [current-revision-sha]]
    [ring.middleware.anti-forgery :as anti-forgery]
    [ote.transit :as transit]
    [ote.services.transport-operator :as transport-operator]))

(def stylesheets [; Normalize CSS across browsers to be visually similar, works as base for all other styles
                  {:href "css/taxiui/normalize.css"}
                  ; Taxi UI global base definitions
                  {:href "css/taxiui/styles.css"}
                  ; Google fonts preconnects, improves performance
                  {:rel "preconnect" :href "https://fonts.googleapis.com"}
                  {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin "true"}
                  ; Fintraffic primary font
                  {:href "https://fonts.googleapis.com/css?family=Public+Sans:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900" :rel "stylesheet"}
                  ; Fintraffic secondary font
                  {:href "https://fonts.googleapis.com/css2?family=Piazzolla:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap" :rel "stylesheet"}])

(defn ote-js-location [dev-mode?]
  (str "js/ote"
       (when-not dev-mode?
         (str "-" (:current-revision-sha (current-revision-sha))))
       ".js"))

(def favicons
  [{:rel "apple-touch-icon" :sizes "180x180" :href "/favicon/apple-touch-icon.png?v=E6jNQXq6yK"}
   {:rel "icon" :type "image/png" :sizes "32x32" :href "/favicon/favicon-32x32.png?v=E6jNQXq6yK"}
   {:rel "icon" :type "image/png" :sizes "16x16" :href "/favicon/favicon-16x16.png?v=E6jNQXq6yK"}
   {:rel "manifest" :href "/favicon/manifest.json?v=E6jNQXq6yK"}
   {:rel "mask-icon" :href "/favicon/safari-pinned-tab.svg?v=E6jNQXq6yK" :color "#5bbad5"}
   {:rel "shortcut icon" :href "/favicon/favicon.ico?v=E6jNQXq6yK"}])

(def dev-favicons
  [{:rel "icon" :type "image/png" :sizes "32x32" :href "/favicon/favicon-dev-32x32.png"}
   {:rel "icon" :type "image/png" :sizes "16x16" :href "/favicon/favicon-dev-16x16.png"}])

(defn translations [language]
  [:script#ote-translations {:type "x-ote-translations"}
   (transit/clj->transit
     {:language language
      :translations (localization/translations language)})])

(defn user-info [db user]
  [:script#ote-user-info {:type "x-ote-user-info"}
   (transit/clj->transit
     (when user
       (transport-operator/get-user-transport-operators-with-services db (:groups user) (:user user))))])

(defn index-page [db user config]
  (let [dev-mode? (:dev-mode? config)
        testing-env? (:testing-env? config)
        flags (str/join "," (map name (:enabled-features config)))
        matomo-config (:matomo config)]
    [:html
     [:head
      (if testing-env?
        (for [f dev-favicons]
          [:link f])
        (for [f favicons]
          [:link f]))
      [:meta {:name "theme-color" :content "#ffffff"}]
      [:meta {:name    "viewport"
              :content "width=device-width, initial-scale=1.0"}]
      [:title "FINAP"]
      (for [{:keys [href integrity]} stylesheets]
        [:link (merge {:rel  "stylesheet"
                       :href href}
                      (when (str/starts-with? href "https://")
                        {:crossorigin ""})
                      (when integrity
                        {:integrity integrity}))])
      [:style {:id "_stylefy-constant-styles_"} ""]
      [:style {:id "_stylefy-styles_"}]
      (translations localization/*language*)
      (user-info db user)]

     [:body (merge
              {:id "main-body"
               :onload "ote.main.taxi_main();"
               :features flags
               :data-language localization/*language*}
              (when dev-mode?
                {:data-dev-mode? true})
              (when (bound? #'anti-forgery/*anti-forgery-token*)
                {:data-anti-csrf-token anti-forgery/*anti-forgery-token*}))
      [:div#taxiapp]
      (when (not dev-mode?)
        [:script#CookieConsent {:src "https://policy.app.cookieinformation.com/uc.js" :data-culture "FI" :type "text/javascript"}])
      (when dev-mode?
        [:script {:src "js/out/goog/base.js" :type "text/javascript"}])
      [:script {:src (ote-js-location dev-mode?) :type "text/javascript"}]
      (when dev-mode?
        [:script {:type "text/javascript"} "goog.require('ote.main');"])]]))

(defn index [db req dev-mode?]
  (http/with-no-cache-headers
    {:status 200
     :headers {"Content-Type" "text/html; charset=UTF-8"}
     ;; hiccup can't add all html elements so we need to add them by hand
     :body (str "<!DOCTYPE html>"
                (html (index-page db (:user req) dev-mode?)))}))

(defrecord Index [dev-mode?]
  component/Lifecycle
  (start [{http :http
           db :db
           :as this}]
    (assoc this ::stop
                (http/publish!
                  http {:authenticated? false}
                  (routes
                    (GET "/taxiui" req (index db req dev-mode?))
                    (GET "/taxiui/index.html" req (index db req dev-mode?))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
