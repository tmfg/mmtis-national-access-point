(ns ote.app.controller.pre-notices
  "Controller for 60 day pre notices"
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [ote.communication :as comm]
            [ote.app.routes :as routes]
            [ote.db.transit :as transit]
            [ote.db.transport-operator :as t-operator]
            [ote.ui.form :as form]
            [ote.localization :refer [tr]]
            [ote.app.controller.common :refer [->ServerError]]
            [clojure.string :as str]))

(declare ->LoadPreNoticesResponse ->LoadPreNoticeResponse
          ->RegionsResponse effective-date-description->set
         ->LoadRegions load-regions-from-server)




(defn load-organization-pre-notices! []
  (comm/get! "pre-notices/list"
             {:on-success (tuck/send-async! ->LoadPreNoticesResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

;; Load the pre-notices that are available
(tuck/define-event LoadOrganizationPreNotices []
  {:path [:pre-notices]}
  (load-organization-pre-notices!)
  :loading)

(declare ->ShowPreNotice)

(tuck/define-event LoadAuthorityPreNotices []
  {:path [:pre-notices]}
  (comm/get! "pre-notices/authority-list"
             {:on-success (tuck/send-async! ->LoadPreNoticesResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  :loading)


(defmethod routes/on-navigate-event :pre-notices [_]
  (->LoadOrganizationPreNotices))

(defmethod routes/on-navigate-event :authority-pre-notices [{params :params}]
  [(when-let [id (:id params)]
     (->ShowPreNotice id))
   (->LoadAuthorityPreNotices)])

(declare load-region-geojson)

(tuck/define-event LoadPreNoticeResponse [response]
  {:path [:pre-notice]
   :app pre-notice}
  (load-region-geojson pre-notice (::transit/regions response))
  (merge
   (select-keys pre-notice #{:regions})
   (effective-date-description->set response)))

(tuck/define-event LoadPreNoticesResponse [response]
  {:path [:pre-notices]}
  response)

(tuck/define-event LoadRegions []
  {}
  (load-regions-from-server)
  app)

(defmethod routes/on-navigate-event :new-notice [_]
  (->LoadRegions))

(tuck/define-event RegionsResponse [response]
  {:path [:pre-notice :regions]}
  (into {}
        (map (juxt :id identity))
        response))

(defn effective-dates-desc->text
  "Before save convert effective date descriptions from set to text."
  [pre-notice]
  (update pre-notice ::transit/effective-dates
          (fn [dates]
            (mapv
              (fn [date]
                {::transit/effective-date             (::transit/effective-date date)
                 ::transit/effective-date-description (first (::transit/effective-date-description date))})
              dates))))

(defn effective-date-description->set [pre-notice]
  "After load convert effective date descriptions to set."
  (update pre-notice ::transit/effective-dates
          (fn [dates]
            (mapv
              (fn [date]
                {::transit/effective-date             (::transit/effective-date date)
                 ::transit/effective-date-description (conj #{} (::transit/effective-date-description date))})
              dates))))


;; Create new route
(defrecord CreateNewPreNotice [])
(defrecord SelectOperatorForNotice [data])
(defrecord EditForm [form-data])
(defrecord EditSingleFormElement [element data])
(defrecord OpenSendModal [])
(defrecord CloseSendModal [])
(defrecord SaveToDb [published?])
(defrecord SaveNoticeResponse [response])
(defrecord SaveNoticeFailure [response published?])
(defrecord CancelNotice [])
(defrecord DeleteEffectiveDate [index])
(defrecord LoadPreNotice [id])

(defmethod routes/on-navigate-event :edit-pre-notice [{params :params}]
  (->LoadPreNotice (:id params)))

(extend-protocol tuck/Event

  LoadPreNotice
  (process-event [{id :id} app]
    (do
      (load-regions-from-server)
      (comm/get! (str "pre-notices/" id)
                 {:on-success (tuck/send-async! ->LoadPreNoticeResponse)
                  :on-failure (tuck/send-async! ->ServerError)}))
    (assoc-in app [:pre-notice :loading] true))


  CreateNewPreNotice
  (process-event [_ app]
    (routes/navigate! :new-notice)
    (-> app
        (dissoc :pre-notice)
        (assoc-in [:pre-notice ::t-operator/id] (get-in app [:transport-operator ::t-operator/id]))))

  SelectOperatorForNotice
  (process-event [{data :data} app]
    (let [id (get data ::t-operator/id)
          selected-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                     %)
                                  (:transport-operators-with-services app))]
      (-> app
          (assoc-in [:pre-notice ::t-operator/id] id)
          (assoc
            :transport-operator (:transport-operator selected-operator)
            :transport-service-vector (:transport-service-vector selected-operator)
            :before-unload-message [:dialog :navigation-prompt :unsaved-data]))))

  EditSingleFormElement
  (process-event [{element :element data :data} app]
    (-> app
        (assoc-in [:pre-notice element] data)
        (assoc :before-unload-message [:dialog :navigation-prompt :unsaved-data])))

  EditForm
  (process-event [{form-data :form-data} app]
    (-> app
        (update :pre-notice merge form-data)
        (assoc :before-unload-message [:dialog :navigation-prompt :unsaved-data])))

  OpenSendModal
  (process-event [_ app]
    (assoc app :show-pre-notice-send-modal? true))

  CloseSendModal
  (process-event [_ app]
    (dissoc app :show-pre-notice-send-modal?))

  SaveToDb
  (process-event [{published? :published?} app]
    (let [notice (as-> (:pre-notice app) n
                       (form/without-form-metadata n)
                       (effective-dates-desc->text n)
                       (dissoc n :regions)
                       (if published?
                         (assoc n ::transit/pre-notice-state :sent)
                         n))]
      (comm/post! "pre-notice" notice
                  {:on-success (tuck/send-async! ->SaveNoticeResponse)
                   :on-failure (tuck/send-async! ->SaveNoticeFailure published?)})
      app))

  SaveNoticeResponse
  (process-event [{response :response} app]
    (routes/navigate! :pre-notices)
    (-> app
        (dissoc :pre-notice
                :before-unload-message)
        (assoc :flash-message
               (if (::transit/sent response)
                 (tr [:pre-notice-page :save-success-send])
                 (tr [:pre-notice-page :save-success])))))

  SaveNoticeFailure
  (process-event [{response :response
                   published? :published?} app]
    (.error js/console "Save notice failed:" (pr-str response))
    ;; TODO: when published? true, use save-failure-send
    (assoc app
      :flash-message-error
      (if published?
        (tr [:pre-notice-page :save-failure-send])
        (tr [:pre-notice-page :save-failure]))))

  CancelNotice
  (process-event [_ app]
    (routes/navigate! :pre-notices)
    app)

  DeleteEffectiveDate
  (process-event [{id :id} app]
    (.log js/console " DeleteEffectiveDate id " id)
    app))

(define-event RegionLocationResponse [response id]
  {:path [:pre-notice :regions]}
  (update app id assoc :geojson response))

(defn- load-region-geojson [pre-notice region-ids]
  (doseq [region-id region-ids
          :when (not (get-in pre-notice [:regions region-id :geojson]))]
    (comm/get! (str "pre-notices/region/" region-id)
               {:on-success (tuck/send-async! ->RegionLocationResponse region-id)
                :on-failure (tuck/send-async! ->ServerError)})))

(define-event SelectedRegions [regions]
  {}
  (let [region-ids (mapv :id regions)]
    ;; Get locations for all regions
    (load-region-geojson (:pre-notice app) region-ids)
    (-> app
        (assoc-in [:pre-notice ::transit/regions] region-ids)
        (assoc :before-unload-message [:dialog :navigation-prompt :unsaved-data]))))

(define-event ShowPreNoticeResponse [response]
  {:path [:pre-notice-dialog]}
  response)

(define-event ClosePreNotice []
  {}
  (dissoc app :pre-notice-dialog))

(define-event ShowPreNotice [id]
  {:path [:pre-notice-dialog]}
  (comm/get! (str "pre-notices/show/" id)
             {:on-success (tuck/send-async! ->ShowPreNoticeResponse)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event UpdateNewCommentText [text]
  {:path [:pre-notice-dialog :new-comment]}
  text)

(define-event AddCommentResponse [new-comment]
  {:path [:pre-notice-dialog ::transit/comments]}
  (conj (or app []) new-comment))

(define-event AddComment []
  {}
  (comm/post! "pre-notices/comment"
              {:id (get-in app [:pre-notice-dialog ::transit/id])
               :comment (get-in app [:pre-notice-dialog :new-comment])}
              {:on-success (tuck/send-async! ->AddCommentResponse)
               :on-failure (tuck/send-async! ->ServerError)})
  (update app :pre-notice-dialog dissoc :new-comment))

(define-event UploadResponse [response]
  {}
  (-> app
      (update-in [:pre-notice :attachments]
                 #(conj (or (vec (butlast %)) []) response))
      (assoc :before-unload-message [:dialog :navigation-prompt :unsaved-data])))

(define-event UploadAttachment [input]
  {}
  (let [filename (str/lower-case (.-name (first (array-seq (.-files input)))))]
    (if (re-matches #".*\.(png|jpg|jpeg|pdf)" filename)
      (do
        (comm/upload! "pre-notice/upload" input {:on-success (tuck/send-async! ->UploadResponse)
                                                 :on-failure (tuck/send-async! ->ServerError)})
        app)
      (->
        app
        (update-in [:pre-notice :attachments]
                   #(conj (or (vec (butlast %)) [])
                          {:error (str (tr [:common-texts :invalid-file-type]) ": " filename)}))
        (assoc :flash-message-error (str (tr [:common-texts :invalid-file-type]) ": " filename))))))

(define-event DeleteAttachment [row-index]
  {}
  (-> app
      (update-in [:pre-notice :attachments]
                 #(vec (concat (take row-index %)
                               (drop (inc row-index) %))))
      (assoc :before-unload-message [:dialog :navigation-prompt :unsaved-data])))

;; Download attachment file
(define-event DownloadResponse [content filename]
  {}
  (let [blob (new js/Blob
                  (clj->js [content])
                  #_ (clj->js {:type "text/csv"}
                           ))]
    (js/saveAs blob filename)
    app))

(define-event DownloadAttachment [id filename]
  {}
  (.log js/console "DownloadAttachment " (pr-str id) (pr-str filename))
  (comm/get! (str "pre-notice/attachment/" id)
             {:type :blob
              :content-type "text/csv"
              :response-format :text
              :on-success (tuck/send-async! ->DownloadResponse filename)
              :on-failure (tuck/send-async! ->ServerError)})
  app)

(defn load-regions-from-server []
  (comm/get! "pre-notices/regions"
             {:on-success (tuck/send-async! ->RegionsResponse)
              :on-failure (tuck/send-async! ->ServerError)}))

(define-event DeletePreNotice [pre-notice]
  {:path [:delete-pre-notice-dialog]}
  pre-notice)

(define-event DeletePreNoticeResponse [response]
  {}
  (load-organization-pre-notices!)
  (-> app
      (dissoc :delete-pre-notice-dialog)
      (assoc :pre-notices :loading
             :flash-message (tr [:pre-notice-list-page :delete-pre-notice-dialog :success-message]))))

(define-event DeletePreNoticeConfirm []
  {:path [:delete-pre-notice-dialog]}
  (comm/post! "pre-notice/delete" (select-keys app #{::transit/id})
              {:on-success (tuck/send-async! ->DeletePreNoticeResponse)
               :on-failure (tuck/send-async! ->ServerError)})
  app)

(define-event DeletePreNoticeCancel []
  {}
  (dissoc app :delete-pre-notice-dialog))

(define-event TogglePreNoticeUsersDialog []
  {:path [:pre-notice-users-dialog?]}
  (not app))
