(ns ote.views.service-viewer
  (:require [ote.app.controller.service-view :as svc]))

(defn service-view
  [e! app]
  [:h1 (:service-id app) " - " (:operator-id app)])
