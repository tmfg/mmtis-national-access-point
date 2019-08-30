(ns ote.app.controller.route.route-list
  "Route lsit controller"
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]
            [ote.app.routes :as routes]
            [ote.app.controller.route.route-wizard :as route-wizard]
            [ote.db.transport-operator :as t-operator]))

;; Load users own routes
(defrecord LoadRoutes [])
(defrecord LoadRoutesResponse [response])

;; Delete
(defrecord OpenDeleteRouteModal [id])
(defrecord CancelDeleteRoute [id])
(defrecord ConfirmDeleteRoute [id])
(defrecord DeleteRouteResponse [response])
(defrecord DeleteRouteResponseFailed [response])

;; Create new route
(defrecord CreateNewRoute [])

(defn- update-route-by-id [app id update-fn & args]
  (update app :routes-vector
          (fn [services]
            (map #(if (= (::transit/id %) id)
                    (apply update-fn % args)
                    %)
                 services))))

(defn- get-routes-for-operator
  [app response]
  (:routes (some #(when (= (get-in app [:transport-operator ::t-operator/id])
                           (get-in % [:transport-operator ::t-operator/id])) %)
                 response)))

(extend-protocol tuck/Event
  LoadRoutes
  (process-event [_ app]
    (let [on-success (tuck/send-async! ->LoadRoutesResponse)]
      (comm/post! "routes/routes" {}
                  {:on-success on-success})
      app))

  LoadRoutesResponse
  (process-event [{response :response} app]
    (-> app
        (handle-routes-response response)))

  LinkInterfaceResponse
  (process-event [{is-linked? :is-linked?
                   response :response} app]
    (let [current-operator (get app :transport-operator)
          services (:services response)
          routes (:routes response)]
      (if services
        (-> app
            (show-success-flash-message is-linked?)
            (handle-routes-response routes)
            ; Replace operators services with services from backend
            (update :transport-operators-with-services
                    (fn [operator-list]
                      (map (fn [operator-with-services]
                             (if (=
                                   (::t-operator/id current-operator)
                                   (get-in operator-with-services [:transport-operator ::t-operator/id]))
                               (assoc operator-with-services :transport-service-vector services)
                               operator-with-services))
                           operator-list)))
            ; Replace transport-service-vector with services from backend
            (assoc :transport-service-vector services))
        app)))

  LinkInterfaceFailedResponse
  (process-event [{is-linked? :is-linked?
                   response :response} app]
    (show-error-flash-message app is-linked?))

  ToggleLinkInterfaceToService
  (process-event [{service-id :service-id is-linked? :is-linked?} app]
    (let [on-success (tuck/send-async! ->LinkInterfaceResponse is-linked?)
          on-failure (tuck/send-async! ->LinkInterfaceFailedResponse is-linked?)]
      (comm/post! "routes/link-interface" {:is-linked? is-linked?
                                           :service-id service-id
                                           :operator-id (get-in app [:transport-operator ::t-operator/id])} ; Currently selected operator
                  {:on-success on-success
                   :on-failure on-failure})
      app))

  CreateNewRoute
  (process-event [_ app]
    (routes/navigate! :new-route)
    app)

  OpenDeleteRouteModal
  (process-event [{id :id} app]
    (update-route-by-id
      app id
      assoc :show-delete-modal? true))

  CancelDeleteRoute
  (process-event [{id :id} app]
    (update-route-by-id
      app id
      dissoc :show-delete-modal?))

  ConfirmDeleteRoute
  (process-event [{id :id} app]
    (comm/post! "routes/delete" {:id id}
                {:on-success (tuck/send-async! ->DeleteRouteResponse)
                 :on-failure (tuck/send-async! ->DeleteRouteResponseFailed)})
    app)

  DeleteRouteResponse
  (process-event [{response :response} app]
    (let [filtered-map (filter #(not= (::transit/id %) (int response)) (get app :routes-vector))]
      (assoc app :routes-vector filtered-map
                 :flash-message (tr [:common-texts :delete-route-success])
                 :routes-changed? true)))

  DeleteRouteResponseFailed
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :delete-route-error]))))
