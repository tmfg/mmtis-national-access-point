(ns ote.services.index
  "Index page generation."
  (:require [ote.components.http :as http]
            [compojure.core :refer [routes GET]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [javascript-tag]]
            [ote.localization :as localization :refer [tr]]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [ote.tools.git :refer [current-revision-sha]]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ote.transit :as transit]))

(def stylesheets [{:href "css/normalize.css"}               ;; Normalize css on wide range of browsers
                  {:href "css/bootstrap_style_grid.css"}
                  {:href "css/nprogress.css"}
                  {:href "css/balloon.css"}
                  {:href "css/styles.css"}
                  {:href "https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
                   :integrity "sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="}
                  {:href "https://cdnjs.cloudflare.com/ajax/libs/leaflet.draw/0.4.12/leaflet.draw.css"}])

(defn ote-js-location [dev-mode?]
  (str "js/ote"
       (when-not dev-mode?
         (str "-" (:current-revision-sha (current-revision-sha))))
       ".js"))

(defn google-analytics-scripts [ga-conf]
  (list
   [:script {:async nil :src (str "https://www.googletagmanager.com/gtag/js?id=" (:tracking-code ga-conf))}]
   (javascript-tag
    (str "var host = window.location.hostname; "
         "window.dataLayer = window.dataLayer || []; "
         "function gtag(){dataLayer.push(arguments);} "
         "gtag('js', new Date()); "
         "gtag('config','" (:tracking-code ga-conf) "'); "
         "if (host === 'localhost' || host.indexOf('testi') !== -1) { "
         "  window['ga-disable-" (:tracking-code ga-conf) "'] = true; "
         "}"))))

(def favicons
  [{:rel "apple-touch-icon" :sizes "180x180" :href "/ote/favicon/apple-touch-icon.png?v=E6jNQXq6yK"}
   {:rel "icon" :type "image/png" :sizes "32x32" :href "/ote/favicon/favicon-32x32.png?v=E6jNQXq6yK"}
   {:rel "icon" :type "image/png" :sizes "16x16" :href "/ote/favicon/favicon-16x16.png?v=E6jNQXq6yK"}
   {:rel "manifest" :href "/ote/favicon/manifest.json?v=E6jNQXq6yK"}
   {:rel "mask-icon" :href "/ote/favicon/safari-pinned-tab.svg?v=E6jNQXq6yK" :color "#5bbad5"}
   {:rel "shortcut icon" :href "/ote/favicon/favicon.ico?v=E6jNQXq6yK"}])

(defn translations [language]
  [:script#ote-translations {:type "x-ote-translations"}
   (transit/clj->transit
    {:language language
     :translations (localization/translations language)})])

(defn index-page [config]
  (let [dev-mode? (:dev-mode? config)
        ga-conf (:ga config)
        flags (str/join "," (map name (:enabled-features config)))]
    [:html
     [:head

      (for [f favicons]
        [:link f])
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

      (when (not dev-mode?)
        (google-analytics-scripts ga-conf))]

     [:body (merge
             {:id "main-body"
              :onload "ote.main.main();"
              :features flags
              :data-language localization/*language*}
             (when (bound? #'anti-forgery/*anti-forgery-token*)
               {:data-anti-csrf-token anti-forgery/*anti-forgery-token*}))
      [:div#oteapp]
      (when dev-mode?
        [:script {:src "js/out/goog/base.js" :type "text/javascript"}])
      [:script {:src (ote-js-location dev-mode?) :type "text/javascript"}]
      (when dev-mode?
        [:script {:type "text/javascript"} "goog.require('ote.main');"])]]))

(defn index [dev-mode?]
  {:status 200
   :headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (html (index-page dev-mode?))})

(defrecord Index [dev-mode?]
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
           (http/publish!
            http {:authenticated? false}
            (routes
             (GET "/" req (index dev-mode?))
             (GET "/index.html" req (index dev-mode?))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
