(ns ote.services.index
  "Index page generation."
  (:require [ote.components.http :as http]
            [compojure.core :refer [routes GET]]
            [hiccup.core :refer [html]]
            [ote.localization :as localization :refer [tr]]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]))

(def supported-languages #{"fi" "sv" "en"})
(def default-language "fi")

(def stylesheets [{:href "css/bootstrap_style_grid.css"}
                  {:href "css/omat_tyylit.css"}
                  {:href "https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
                   :integrity "sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="}
                  {:href "https://cdnjs.cloudflare.com/ajax/libs/leaflet.draw/0.4.12/leaflet.draw.css"}])

(defn index-page [dev-mode?]
  [:html
   [:head
    [:title "FINAP"]
    (for [{:keys [href integrity]} stylesheets]
      [:link (merge {:rel "stylesheet"
                     :href href}
                    (when (str/starts-with? href "https://")
                      {:crossorigin ""})
                    (when integrity
                      {:integrity integrity}))])
    [:style {:id "_stylefy-constant-styles_"} ""]
    [:style {:id "_stylefy-styles_"}]]

   [:body {:onload "ote.main.main();"}
    [:div#oteapp]
    (when dev-mode?
      [:script {:src "js/out/goog/base.js" :type "text/javascript"}])
    [:script {:src "js/ote.js" :type "text/javascript"}]
    [:script {:type "text/javascript"} "goog.require('ote.main');"]]])

(defn index [dev-mode? accepted-languages]
  (let [lang (or (some supported-languages accepted-languages) default-language)]
    (localization/with-language lang
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (html (index-page dev-mode?))})))

(defrecord Index [dev-mode?]
  component/Lifecycle
  (start [{http :http :as this}]
    (assoc this ::stop
           (http/publish!
            http {:authenticated? false}
            (routes
             (GET "/" {lang :accept-language} (index dev-mode? lang))
             (GET "/index.html" {lang :accept-language} (index dev-mode? lang))))))
  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
