(ns ote.app.state
  "Contains the frontend application `app` database.
  Everything that is in the current state of the frontend is in the app atom."
  (:require [reagent.core :as r]))

(defonce app
  (r/atom {;; current page
           ;; see ote.app.routes
           :page :front-page
           :params nil ; parameters from url route
           :query nil ; query parameters from url (like "?foo=bar")

            :user {} ;; No user data by default
            :ote-service-flags {:user-menu-open true
                                :show-debug false ; By default debug section is not in use. You can set it visible from user menu
                                }

           ;; Currently selected / edited transport operator (company basic info)
           :transport-operator {}

           ;; Currently selected / edited transport service
           :transport-service
           #:ote.db.transport-service
           {:passenger-transportation
            {:ote.db.transport-service/real-time-information
             #:ote.db.transport-service {:url "www.example.com/url"}}}}))
