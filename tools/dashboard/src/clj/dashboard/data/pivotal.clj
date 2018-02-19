(ns dashboard.data.pivotal
  (:require [org.httpkit.client :as http]
            [cheshire.core :as cheshire])
  (:import (org.commonmark.parser Parser)
           (org.commonmark.renderer.html HtmlRenderer)))

(def config (delay (read-string (slurp "pivotal.edn"))))

(def pivotal-api-url "https://www.pivotaltracker.com/services/v5/")

(defn- pivotal-url [& parts]
  (apply str pivotal-api-url parts))

(defn project-id [] (:project-id @config))

(defn- pivotal-get [& parts]
  (let [{:keys [project-id token]} @config]
    (-> (apply pivotal-url parts)
        (http/get {:as :text :headers {"X-TrackerToken" token}})
        deref :body (cheshire/decode keyword))))

(defn- search [query]
  (-> (pivotal-get "projects/" (project-id) "/search?query="
                   (java.net.URLEncoder/encode query))
      :stories :stories))

(defn to-html [markdown-string]
  (let [p (.build (Parser/builder))
        document (.parse p markdown-string)
        renderer (.build (HtmlRenderer/builder))]
    (.render renderer document)))

(defn fetch-sprint-themes []
  (-> (search  "label:sprintin-tavoitteet")
      first :description to-html))

(defn fetch-story-stats []
  (let [stories (search "state:started OR state:finished OR (state:delivered AND label:test)")
        by-state (group-by :current_state stories)]
    {:issues-in-implementation (count (by-state "started"))
     :issues-waiting-review (count (by-state "finished"))
     :issues-waiting-test (count (by-state "delivered"))}))
