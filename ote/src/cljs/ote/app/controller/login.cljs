(ns ote.app.controller.login
  (:require [tuck.core :as tuck]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :refer [tr]]))

(defrecord ShowLoginDialog [])
(defrecord UpdateLoginCredentials [credentials])
(defrecord Login [])
(defrecord LoginResponse [response])
(defrecord LoginFailed [response])
(defrecord LoginCancel [])

(defn update-transport-operator-data
  [{:keys [page ckan-organization-id transport-operator] :as app}
   response]

  (let [app (assoc app
                   :transport-operator-data-loaded? true
                   :user (:user (first response)))]
    (if (and (nil? (:transport-operator (first response)))
             (not= :services page))
      ;; If page is :transport-operator and user has no operators, start creating a new one
      (do
        (if (= (:page app) :transport-operator)
          (assoc app
                 :transport-operator {:new? true}
                 :services-changed? true)
          app))

        ;; Get services from response.
        ;; Use selected operator if possible, if not, use the first one from the response.
        ;; Selected can either be previously selected or ckan-organization-id (CKAN edit view)
        (let [selected-operator (or
                                 (some #(when (or (= (::t-operator/id transport-operator)
                                                     (get-in % [:transport-operator ::t-operator/id]))
                                                  (= ckan-organization-id
                                                     (get-in % [:transport-operator ::t-operator/ckan-group-id])))
                                          %)
                                       response)
                                 (first response))]

          (assoc app
                 :transport-operators-with-services response
                 :transport-operator (:transport-operator selected-operator)
                 :transport-service-vector (:transport-service-vector selected-operator))))))

(extend-protocol tuck/Event

  ShowLoginDialog
  (process-event [_ app]
    (assoc app :login {:show? true}))

  UpdateLoginCredentials
  (process-event [{credentials :credentials} app]
    (update-in app [:login :credentials] merge credentials))

  Login
  (process-event [_ app]
    (comm/post! "login"
                (select-keys (get-in app [:login :credentials]) #{:email :password})
                {:on-success (tuck/send-async! ->LoginResponse)
                 :on-failure (tuck/send-async! ->LoginFailed)})
    (update app :login
            #(-> %
                 (dissoc :failed? :error)
                 (assoc :in-progress? true))))

  LoginResponse
  (process-event [{response :response} app]
    (if (:success? response)
      (-> app
          (dissoc :login)
          (update-transport-operator-data (:session-data response))
          (assoc :flash-message (tr [:common-texts :logged-in])))
      (update app :login assoc
              :failed? true
              :in-progress? false
              :error (:error response))))

  LoginFailed
  (process-event [{response :response} app]
    ;; The login request itself failed
    (assoc-in app [:login :error] response))

  LoginCancel
  (process-event [_ app]
    (dissoc app :login)))
