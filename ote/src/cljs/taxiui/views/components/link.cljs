(ns taxiui.views.components.link
  (:require [stylefy.core :as stylefy]
            [taxiui.app.routes :as routes]
            [taxiui.app.controller.front-page :as fp-controller]
            [taxiui.app.controller.loader :as loader]))

(defn link
  [e! page params styles children]
  [:a (merge (stylefy/use-style styles)
             {:href     (str "#" (routes/resolve page params))
              :on-click #(do
                           (.preventDefault %)
                           (e! (fp-controller/->ChangePage page params))
                           (e! (loader/->AddHit :page-loading {:type :info-progress})))})
   children])

