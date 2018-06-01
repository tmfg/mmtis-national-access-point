(ns ote.services.robots
  "Serve robots.txt"
  (:require [ote.components.service :refer [define-service-component]]
            [compojure.core :refer [GET]]))

(defn robots-txt [allow-robots?]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "User-agent: *\n"
              (if allow-robots?
                "Disallow:"
                "Disallow: /"))})

(define-service-component RobotsTxt {:fields [allow-robots?]}
  ^:unauthenticated
  (GET "/robots.txt" []
       (robots-txt allow-robots?)))
