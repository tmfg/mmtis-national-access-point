(ns ote.app.controller.login
  "Login, register and user edit controller"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.app.controller.user-edit :as user-edit]
            [ote.db.transport-operator :as t-operator]
            [ote.localization :as localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]
            [clojure.string :as str]))

(defrecord ShowLoginPage [])
(defrecord UpdateLoginCredentials [credentials])
(defrecord Login [])
(defrecord LoginResponse [response])
(defrecord LoginFailed [response])
(defrecord LoginCancel [])
(defrecord Logout [])
(defrecord LogoutResponse [response])
(defrecord LogoutFailed [response])

(defn unauthenticated
  "Init session without user."
  [app]
  (assoc app
         :transport-operator-data-loaded? true
         :user nil))

(defn update-transport-operator-data
  [{:keys [page ckan-organization-id transport-operator] :as app}
   {:keys [user transport-operators] :as response}]

  (let [app (assoc app
                   :transport-operator-data-loaded? true
                   :user user)]
    (if (and (empty? transport-operators)
             (not= :services page))
      ;; If page is :transport-operator and user has no operators, start creating a new one
      (if (= (:page app) :transport-operator)
        (assoc app
          :transport-operator {:new? true}
          :services-changed? true)
        app)

        ;; Get services from response.
        ;; Use selected operator if possible, if not, use the first one from the response.
        ;; Selected can either be previously selected or ckan-organization-id (CKAN edit view)
        (let [selected-operator (or
                                 (some #(when (or (= (::t-operator/id transport-operator)
                                                     (get-in % [:transport-operator ::t-operator/id]))
                                                  (= ckan-organization-id
                                                     (get-in % [:transport-operator ::t-operator/ckan-group-id])))
                                          %)
                                       transport-operators)
                                 (first transport-operators))]

          (assoc app
                 :transport-operators-with-services transport-operators
                 :transport-operator (:transport-operator selected-operator)
                 :transport-service-vector (:transport-service-vector selected-operator))))))

(defn- login-navigate->page
  ([app response]
   (login-navigate->page app response (tr [:common-texts :logged-in])))
  ([app response flash-message]
   (let [authority? (get-in response [:session-data :user :transit-authority?])
         operators-count (count (get-in response [:session-data :transport-operators]))
         navigate-to (get-in app [:login :navigate-to])
         new-page (cond
                    (not (empty? navigate-to)) (:page navigate-to)
                    (and authority? (= 0 operators-count)) :authority-pre-notices
                    :else :own-services)]
     (routes/navigate! new-page (:params navigate-to))
     (-> app
       (dissoc :login)
       (update-transport-operator-data (:session-data response))
       (assoc :flash-message flash-message)))))

(extend-protocol tuck/Event

  ShowLoginPage
  (process-event [_ app]
    (routes/navigate! :login)
    app)

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
        (login-navigate->page app response)
        (update app :login assoc
                :failed? true
                :in-progress? false
                :error (:error response))))

  LoginFailed
  (process-event [{response :response} app]
    ;; The login request itself failed
    (update app :login assoc
      :failed? true
      :in-progress? false
      :error (:error (:response response))))

  LoginCancel
  (process-event [_ app]
    (dissoc app :login))

  Logout
  (process-event [_ app]
    (comm/post! "logout" nil
                {:on-success (tuck/send-async! ->LogoutResponse)
                 :on-failure (tuck/send-async! ->LogoutFailed) })
    app)

  LogoutResponse
  (process-event [_ app]
    (routes/navigate! :front-page)
    (-> app
        (dissoc :user
                :transport-operator
                :transport-operators-with-services
                :transport-service-vector
                :routes)
        (assoc :flash-message (tr [:login :logged-out]))))

  LogoutFailed
  (process-event [_ app]
    (assoc app :flash-message (tr [:common-texts :server-error]))))

(define-event UpdateRegistrationForm [form-data]
  {:path [:register :form-data]}
  (-> app
      (merge form-data)
      (update :name (fnil str/triml ""))
      (update :email (fnil str/trim ""))
      (update :username (fnil str/trim ""))))

(define-event RegisterSuccess [response]
  {}
  (-> app
    (assoc-in [:register :success?] true)))

(define-event SaveUserSuccess [response]
  {}
  ;; If email is changed we can't login the user until the new email address is validated
  (if (:email-changed? response)
    (-> app
      (assoc-in [:user :edit-response] response)
      (assoc-in [:user :email] (:new-email response))
      (assoc-in [:user :form-data :current-password] ""))
    (login-navigate->page app response (tr [:common-texts :save-user-success]))))

(define-event RegisterError [response]
  {}
  (user-edit/handle-user-save-error app :register (:response response)))

(define-event Register [form-data]
  {}
  (let [token (get-in app [:params :token])
        form-data (cond-> form-data
                    true (assoc :language @localization/selected-language)
                    (some? token) (assoc :token token))]
    (comm/post! "register" form-data
                {:on-success (tuck/send-async! ->RegisterSuccess)
                 :on-failure (tuck/send-async! ->RegisterError)}))
  app)

(define-event UpdateUser [user]
  {:path [:user]}
  (-> app
      (assoc :form-data user)
      (dissoc :password-incorrect?)))

(define-event SaveUserError [response]
  {}
  (user-edit/handle-user-save-error app :user (:response response)))

(define-event SaveUser [form-data]
  {}
  (comm/post! "save-user" (assoc form-data :language @localization/selected-language)
              {:on-success (tuck/send-async! ->SaveUserSuccess)
               :on-failure (tuck/send-async! ->SaveUserError)})
  app)

(define-event UserSettingsInit [navigate-back?]
  {:path [:user]}
  (when navigate-back?
    (.back js/history))
  (dissoc app
          :form-data
          :username-taken
          :email-taken
          :password-incorrect?
          :edit-response))

(define-event UpdateResetPasswordForm [form-data]
  {:path [:reset-password]}
  form-data)

(define-event ResetPasswordResponse [response]
  {}
  (if (:success? response)
    (do (routes/navigate! :login)
        (-> app
            (dissoc :reset-password)
            (assoc :flash-message (tr [:reset-password :password-changed]))))
    (assoc app :flash-message-error (tr [:login :check-email-for-link]))))

(define-event ResetPassword []
  {}
  (let [payload {:key (get-in app [:query :key])
                 :id (get-in app [:query :id])
                 :new-password (get-in app [:reset-password :new-password])}]
    (comm/post! "reset-password" payload
                {:on-success (tuck/send-async! ->ResetPasswordResponse)
                 :on-failure (tuck/send-async! ->ServerError)})
    app))

(define-event GoToResetPassword []
  {}
  (routes/navigate! :reset-password {})
  app)

(define-event RequestPasswordResetResponse [response email]
  {:path [:reset-password]}
  (assoc app :code-sent-for-email email))

(define-event RequestPasswordReset []
  {:path [:reset-password]}
  (comm/post! "request-password-reset" {:email (:email app)
                                        :language @localization/selected-language}
              {:on-success (tuck/send-async! ->RequestPasswordResetResponse (:email app))
               :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event ValiditySuccess [response]
  {:path [:register]}
  (-> app
    (assoc-in [:token-info] response)))

(define-event ValidityFailure [response]
  {:path [:register]}
  (-> app
    (assoc-in [:token-info] :token-invalid)))

(define-event CheckTokenValidity []
  {}
  (let [token (get-in app [:params :token])]
    (if (some? token)
      (do
        (comm/post! "token/validate" {:token (get-in app [:params :token])}
          {:on-success (tuck/send-async! ->ValiditySuccess)
           :on-failure (tuck/send-async! ->ValidityFailure)})
        (assoc-in app [:register :token-info :loading?] true))
      app)))

(defmethod routes/on-navigate-event :register [_]
  (->CheckTokenValidity))

(define-event LeaveUserRegister []
  {}
  (dissoc app :register))

(defmethod routes/on-leave-event :register [_]
  (->LeaveUserRegister))
