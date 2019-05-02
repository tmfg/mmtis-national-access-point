(ns ote.app.controller.transport-operator
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require  [tuck.core :as tuck :refer-macros [define-event]]
             [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transport-operator :as t-operator]
            [ote.app.routes :as routes]
            [tuck.core :refer [define-event send-async! Event]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.controller.flags :as flags]
            [ote.db.common :as common]
            [ote.ui.validation :as validation]))

(defrecord SelectOperator [data])
(defrecord SelectOperatorForTransit [data])
(defrecord EditTransportOperator [id])
(defrecord EditTransportOperatorResponse [response])
(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperator [])
(defrecord SaveTransportOperatorResponse [data])
(defrecord FailedTransportOperatorResponse [response])
(defrecord EnsureUniqueBusinessId [business-id])
(defrecord EnsureUniqueBusinessIdResponse [response])
(defrecord OperatorRename [operator selection])
(defrecord OperatorRenameResponse [response previous payload])
(defrecord UserCloseMergeSection [data])
(defrecord OperatorRefreshResponse [response])

(defrecord TransportOperatorResponse [response])
(defrecord CreateTransportOperator [])

(defn- strip-ytj-metadata [app] (dissoc app :ytj-response :ytj-company-names :ytj-flags :transport-operator-save-q))

(defn- address-of-type [type addresses]
  "Returns from a vector the first map whose type key matches to YTJ address type."
  (let [item (first (filter #(= type (:type %)) addresses))]
    {::common/post_office (:city item)
     ::common/postal_code (:postCode item)
     ::common/street (:street item)}))

(defn- filter-coll-type [type collection]
  "Returns a filtered a collection of maps based on :type key"
  (first (filter #(some (fn [pred] (pred %)) [(comp #{type} :type)]) collection)))

(defn- preferred-ytj-field [types contacts]
  "Takes 'types' vector of key names as strings in order of preference, 'contacts' collection of maps
  and finds the first contacts map which has a key with a matching name.
  Key names are searched in the order they are in `types`.
  Returns the value of the first matching key from the first matching map"
  (loop [[type & remaining] types
         result []]
    (if (and type (empty? result))
      (let [match (:value (filter-coll-type type contacts))]
        (recur remaining (if match (conj result match) result)))
      (first result))))                                     ;; Return first because expected value is string not vector

;; Keys for saving fields common with all companies sharing the same Y-tunnus/business-id
(defn- take-common-op-keys [coll] (select-keys coll [::t-operator/business-id
                                                     ::t-operator/billing-address
                                                     ::t-operator/visiting-address
                                                     ::t-operator/phone
                                                     ::t-operator/gsm
                                                     ::t-operator/email
                                                     ::t-operator/homepage]))

;; Keys unique to operator when creating a company
(defn- take-new-op-keys [coll] (select-keys coll [::t-operator/name]))

; Keys unique to an operator when editing existing company. Extra to `take-common-op-keys`
(defn- take-update-op-keys [coll] (select-keys coll [::t-operator/id ::t-operator/ckan-description ::t-operator/ckan-group-id]))

;; Take keys supported by backend transport-operator API
(defn take-operator-api-keys [op] (merge (take-new-op-keys op) (take-update-op-keys op) (take-common-op-keys op)))

;; Takes 'ytj-name' and finds the first from `nap-operators` whose name is a match, or nil.
(defn- name->nap-operator [ytj-name nap-operators]
  (let [nap-item (some #(when (= (get-in % [:transport-operator ::t-operator/name]) ytj-name) %) ;; Return whole item or nil
                       nap-operators)]
    (:transport-operator nap-item)))

;; Marks :disabled? those operators, which already have a nap id. Used for filtering UI selection menu.
(defn- mark-menuitems [ytj-names]
  (mapv #(assoc % :disabled? (int? (::t-operator/id %))) ytj-names))

(defn- ytj->nap-companies [operators-ytj operators-nap]
  "Function creates ytj operator items by combining metadata from YTJ response and NAP operators.
   Takes `operators-ytj` and if there's a name match to its name in `operators-nap`,
  copies into the ytj item nap keys which are not shared with other nap companies which have the same business-id."
  (doall
    (map (fn [ytj-item]
           (let [nap-item (name->nap-operator (:name ytj-item) operators-nap)
                 nap-id (::t-operator/id nap-item)]
             (cond-> ytj-item
                     true (assoc ::t-operator/name (:name ytj-item)) ;; All operators must have name set to allow updating operator and group data if necessary
                     true (dissoc :name)                    ;; Remove ytj namespace key
                     (some? nap-id) (merge (take-update-op-keys nap-item)) ;; nap fields user is allowed to edit
                     ;; Remove keys not supported by nap service
                     true (take-operator-api-keys))))
         operators-ytj)))

(defn- update-nap-keys-to-ytj-names [operators-ytj operators-nap]
  "Function updates nap metadata to existing ytj operator items, previously created by `ytj->nap-companies`.
  Takes `operators-ytj` and if there's a name match to its name in `operators-nap`, copies certain keys to matching operators-ytj item."
  (doall
    (map (fn [ytj-item]
           (let [nap-item (name->nap-operator (::t-operator/name ytj-item) operators-nap)
                 nap-id (::t-operator/id nap-item)]
             (cond-> ytj-item
                     true (dissoc ::t-operator/id)          ;; Nap id removed just in case there's no more NAP operator match
                     (some? nap-id) (merge (take-update-op-keys nap-item)))))
         operators-ytj)))

;; Takes `app`, POSTs the next transport operator in queue and updates the queue.
;; Returns a new app state.
(defn- save-next-operator! [app]
  (let [ops-to-save (:transport-operator-save-q app)
        op-next (form/without-form-metadata (first ops-to-save))
        ops-rest (rest ops-to-save)]
    (if (some? op-next)
      (do (comm/post! "transport-operator" op-next
                      {:on-success (send-async! ->SaveTransportOperatorResponse)
                       :on-failure (send-async! ->FailedTransportOperatorResponse)})
          (assoc app :transport-operator-save-q ops-rest))
      (dissoc app :transport-operator-save-q))))

;; Takes nap orphan operators without a matching ytj name and marks if renaming was successful and if item should be disabled on UI merging section.
;; response: response of renaming an orphan nap operator
;; previous: nap operator which was merged to ytj name
;; newname: new ytj name to which operator was updated
;; Returns new vector for :ytj-orphan-nap-operators
(defn- update-nap-orphans [orphans response previous newname]
  (map
    (fn [orphan]
      (if (= (::t-operator/id (:transport-operator orphan)) (::t-operator/id previous))
        (-> orphan
            (assoc-in
              [:transport-operator :save-success?] (pos-int? response))
            ;; Below handles the case where user renames orphan operator back to original, i.e. reverts the UI dropdown selection
            (assoc-in
              [:transport-operator :merge-handled?] (and
                                                      (pos-int? response)
                                                      (not= (::t-operator/name (:transport-operator orphan)) newname))))
        orphan))
    orphans))

(defn- operators-of-user [app]
  (if (and (get-in app [:user :admin?]) (:admin-transport-operators app))
    (:admin-transport-operators app)
    (:transport-operators-with-services app)))

(defn- compose-orphan-nap-operators [bid ytj-ops nap-ops]
  ;; Function takes bid (business-id) and nap-ops (vector of nap operator maps)
  ;; and returns a vector of those nap-ops items whose business-id matches to bid but which don not have a name match it ytj-ops.
  (let [nap-ops-for-bid (filter #(= (get-in % [:transport-operator ::t-operator/business-id]) bid) nap-ops)]
    (filter
      (fn [ytj-item]
        (not-any? (fn [nap-item]
                    (= (::t-operator/name nap-item) (::t-operator/name (:transport-operator ytj-item)))) ytj-ops))
      nap-ops-for-bid)))

;; Resolves nap metadata for ytj-fetched companies, e.g. what already exist in nap
;; Returns a vector of enriched ytj operator maps
(defn- compose-ytj-company-names [app response]
  (let [companies (when (some? (:name response))
                    (ytj->nap-companies
                      (into [{:name (:name response)}] (sort-by :name (:auxiliaryNames response))) ; Insert company name first to checkbox list before aux names
                      (operators-of-user app)))]
    ;; Add :show-delete-dialog? false to all companies to make :transport-operators-to-save values constant when adding them to checkboxes and checking if option is selected.
    (map #(assoc % :show-delete-dialog? false) companies)))

;; Resolves which ytj operator naems exist already in nap. Returns a filtered vector of ytj operators.
(defn- ytj-ops-already-in-nap [ytj-company-names]
  (filterv ::t-operator/id ytj-company-names))

;; Use-cases:
;; Create new op, business-id found in YTJ
;; Create new op, business-id not found in YTJ
;; Edit op, business id found in YTJ
;; Edit op, business id not found in YTJ
(defn- process-ytj-data [app response]
  (let [t-op (:transport-operator app)
        ytj-business-id-hit? (= 200 (:status response))
        ytj-address-billing (address-of-type 1 (:addresses response))
        use-ytj-addr-billing? ytj-business-id-hit?
        ytj-address-visiting (address-of-type 2 (:addresses response))
        use-ytj-addr-visiting? ytj-business-id-hit?
        ytj-contact-phone (preferred-ytj-field ["Puhelin" "Telefon" "Telephone"] (:contactDetails response))
        use-ytj-phone? (seq ytj-contact-phone)
        ytj-contact-gsm (preferred-ytj-field ["Matkapuhelin" "Mobiltelefon" "Mobile phone"] (:contactDetails response))
        use-ytj-gsm? (seq ytj-contact-gsm)
        ;ytj-contact-email Not read because not known in what field it is available
        use-ytj-email? false
        ytj-contact-web (preferred-ytj-field ["Kotisivun www-osoite" "www-adress" "Website address"] (:contactDetails response))
        use-ytj-web? (seq ytj-contact-web)
        ytj-company-names (mark-menuitems (compose-ytj-company-names app response))
        ytj-changed-contact-input-fields? (and ytj-business-id-hit?
                                               (or (not= ytj-address-billing (::t-operator/billing-address t-op))
                                                   (not= ytj-address-visiting (::t-operator/visiting-address t-op))
                                                   (not= ytj-contact-phone (::t-operator/phone t-op))
                                                   (not= ytj-contact-gsm (::t-operator/gsm t-op))
                                                   ;(not= ytj-contact-email (::t-operator/email t-op)) ;; TODO: take email into account when it's clear if YTJ api provides that
                                                   (not= ytj-contact-web (::t-operator/homepage t-op))
                                                   ;;(not= ytj-contact-name (::t-operator/name t-op)) Name not taken into account because that is handled in the name resolution wizard and name list
                                                   (not= ytj-address-billing (::t-operator/billing-address t-op))))]
    (cond-> app
            true (assoc
                   :ytj-response response
                   :ytj-response-loading false
                   :transport-operator-loaded? true)
            ;; Enable saving when YTJ changed a relevant field
            (and (not (:new? t-op))
                 ytj-changed-contact-input-fields?) (assoc-in [:transport-operator ::form/modified] #{::t-operator/name})
            ;; Set data sources for form fields and if user allowed to edit
            use-ytj-addr-billing? (assoc-in [:transport-operator ::t-operator/billing-address] ytj-address-billing)
            true (assoc-in [:ytj-flags :use-ytj-addr-billing?] use-ytj-addr-billing?)
            use-ytj-addr-visiting? (assoc-in [:transport-operator ::t-operator/visiting-address] ytj-address-visiting)
            true (assoc-in [:ytj-flags :use-ytj-addr-visiting?] use-ytj-addr-visiting?)
            use-ytj-phone? (assoc-in [:transport-operator ::t-operator/phone] ytj-contact-phone)
            use-ytj-phone? (assoc-in [:ytj-flags :use-ytj-phone?] true)
            use-ytj-gsm? (assoc-in [:transport-operator ::t-operator/gsm] ytj-contact-gsm)
            use-ytj-gsm? (assoc-in [:ytj-flags :use-ytj-gsm?] true)
            ;use-ytj-email? (assoc-in [:transport-operator ::t-operator/email] ytj-contact-email) ; Not set because not known in what field it is available
            use-ytj-email? (assoc-in [:ytj-flags :use-ytj-email?] true)
            use-ytj-web? (assoc-in [:transport-operator ::t-operator/homepage] ytj-contact-web)
            use-ytj-web? (assoc-in [:ytj-flags :use-ytj-homepage?] true)
            ;; Set data source for company selection list
            true (assoc-in [:transport-operator :ytj-orphan-nap-operators]
                           (sort-by #(get-in % [:transport-operator ::t-operator/name]) (compose-orphan-nap-operators (get-in app [:transport-operator ::t-operator/business-id])
                                                                                                                      ytj-company-names
                                                                                                                      (:transport-operators-with-services app))))
            true (assoc :ytj-company-names ytj-company-names)
            true (assoc-in [:transport-operator :transport-operators-to-save] (ytj-ops-already-in-nap ytj-company-names)))))

(define-event FetchYtjOperatorResponse [response]
              {}
              (process-ytj-data app response))

(defn- send-fetch-ytj [app id]
  "Takes app state and business id, initiates details fetch for id from YTJ. Returns a new app state."
  {:pre [(some? id)]}
  (comm/get! (str "fetch/ytj?company-id=" id)
             {:on-success (send-async! ->FetchYtjOperatorResponse)
              :on-failure (send-async! ->FetchYtjOperatorResponse)})
  (-> app
      (strip-ytj-metadata)
      (assoc :ytj-response-loading true)))

(define-event FetchYtjOperator [id]
              {}
              (send-fetch-ytj app id))

(define-event CancelTransportOperator []
              {}
              (routes/navigate! :own-services)
              (update-in app [:transport-operator] dissoc :new?))

(define-event VerifyCreateState []
              {}
              ;; To avoid app state problems redirect to own services if user refreshes on operator creation view
              (when-not (get-in app [:transport-operator :new?])
                (routes/navigate! :own-services))
              app)

(define-event ToggleListTransportOperatorDeleteDialog [operator]
              {}
              (update-in app [:transport-operator :transport-operators-to-save]
                         (fn [coll]
                           (for [c coll]
                             (update c :show-delete-dialog?
                                     #(if (and
                                            (not (true? (:show-delete-dialog? operator)))
                                            (= (::t-operator/id operator) (::t-operator/id c)))
                                        true
                                        false))))))

(define-event ToggleSingleTransportOperatorDeleteDialog []
              {:path [:transport-operator :show-delete-dialog?]
               :app show?}
              (not show?))

(define-event DeleteTransportOperatorResponse [response]
              {}
              (let [app (-> app
                            ;; Remove deleted operator from app-state (:transport-operators-to-save :transport-operators-with-services and :ytj-company-names)
                            (update-in [:transport-operator :transport-operators-to-save]
                                       (fn [operator-collection]
                                         (keep (fn [o]
                                                 (when-not (= response (::t-operator/id o)) o))
                                               operator-collection)))
                            (update :transport-operators-with-services
                                    (fn [operator-collection]
                                      (keep (fn [o]
                                              (when-not (= response (get-in o [:transport-operator ::t-operator/id])) o))
                                            operator-collection)))
                            (update :ytj-company-names
                                    (fn [operator-collection]
                                      (map (fn [o]
                                             (if (= response (::t-operator/id o))
                                               (dissoc o ::t-operator/id ::t-operator/ckan-group-id ::t-operator/ckan-description)
                                               o))
                                           operator-collection)))
                            (assoc-in [:transport-operator :show-delete-dialog?] false)
                            (assoc :flash-message (tr [:common-texts :delete-operator-success])
                                   :services-changed? true))]

                ;; Leave page if company wasn't found from ytj or if it was the last company in :transport-operators-to-save
                (when (or
                        (empty? (get app :ytj-company-names))
                        (empty? (get-in app [:transport-operator :transport-operators-to-save])))
                  (routes/navigate! :own-services))
                app))

(define-event DeleteTransportOperator [id]
              {}
              (comm/post! "transport-operator/delete" {:id id}
                          {:on-success (send-async! ->DeleteTransportOperatorResponse)
                           :on-failure (send-async! ->ServerError)})
              app)

(defn transport-operator-by-ckan-group-id [id]
  (comm/get! (str "transport-operator/" id) {:on-success (send-async! ->TransportOperatorResponse)}))

(defn- refresh-transport-operator [app]
  (if (:transport-operator-data-loaded? app true)
    (do
      (comm/post! "transport-operator/data" {}
                  {:on-success (tuck/send-async! ->OperatorRefreshResponse)
                   :on-failure (tuck/send-async! ->OperatorRefreshResponse)})
      (-> app
          (assoc :services-changed? false)
          (dissoc :transport-operators-with-services)))
    app))

(extend-protocol Event

  CreateTransportOperator
  (process-event [_ app]
    (let [res (-> app
                  (strip-ytj-metadata)
                  (assoc :transport-operator {:new? true}
                         :services-changed? true))]
      (routes/navigate! :transport-operator)
      res))

  SelectOperator
  (process-event [{data :data} app]
    (let [id (get data ::t-operator/id)
          service-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                    %)
                                 (:transport-operators-with-services app))
          route-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                  %)
                               (:route-list app))]
      (assoc app
        :transport-operator (:transport-operator service-operator)
        :transport-service-vector (:transport-service-vector service-operator)
        :routes-vector (:routes route-operator))))

  SelectOperatorForTransit
  (process-event [{data :data} app]
    (let [id (get data ::t-operator/id)
          selected-operator (some #(when (= id (get-in % [:transport-operator ::t-operator/id]))
                                     %)
                                  (:route-list app))]
      (assoc app
        :transport-operator (:transport-operator selected-operator)
        :routes-vector (:routes selected-operator))))

  EditTransportOperator
  (process-event [{id :id} app]
    (if id
      (do
        (comm/get! (str "t-operator/" id)
                   {:on-success (send-async! ->EditTransportOperatorResponse)})
        (assoc app :transport-operator-loaded? false))
      (assoc app :transport-operator-loaded? true)))

  EditTransportOperatorResponse
  (process-event [{response :response} app]
    (let [state (assoc app :transport-operator response
                           :transport-operator-loaded? true
                           :ytj-response {})
          nap-business-id (get-in state [:transport-operator ::t-operator/business-id])
          op-ytj-cache-miss? (or (empty? (:ytj-company-names state)) (not= nap-business-id (get-in state [:ytj-response :businessId])))]
      (if (flags/enabled? :open-ytj-integration)
        (if op-ytj-cache-miss?
          (send-fetch-ytj state nap-business-id)
          (process-ytj-data state (:ytj-response state)))
        state)))

  EditTransportOperatorState
  (process-event [{data :data} app]
    (-> app
        (update :transport-operator merge data)
        (dissoc :transport-operator-save-q)
        (assoc :before-unload-message [:dialog :navigation-prompt :unsaved-data])))

  ;; Start saving sequence after user action.
  ;; Two categories of operators:
  ;; 1) Those affected directly: name or description updated/created
  ;; 2) Those affected indirectly: update fields shared with all companies using the same business-id/y-tunnus. In practise contact fields.
  ;; Creates an operator map queue to save each affected operator, but select fields based on which category operator falls in. Backend should save fields found under operator map which it receives.
  SaveTransportOperator
  (process-event [_ app]
    (let [ytj-ops-selected (get-in app [:transport-operator :transport-operators-to-save])
          op-new? (nil? (get-in app [:params :id]))
          op-nap? (empty? ytj-ops-selected)
          op-update-nap? (and (not op-new?) op-nap?)
          data-fields (-> app
                          :transport-operator
                          (dissoc :new?)
                          form/without-form-metadata)
          operators-to-save (if op-nap?
                              ;; Copy all user's fields to operator map because user sets all fields for non-ytj operator.
                              (vector (merge (take-common-op-keys data-fields)
                                             (take-new-op-keys data-fields)
                                             (when op-update-nap?) (take-update-op-keys data-fields)))
                              ;; Copy only fields allowed for user to edit to ytj-matching operator maps because other values are already set
                              (mapv #(merge % (take-common-op-keys data-fields)) ytj-ops-selected))]
      (-> app
          (dissoc :before-unload-message [:dialog :navigation-prompt :unsaved-data])
          (assoc :transport-operator-save-q operators-to-save)
          (save-next-operator!))))

  FailedTransportOperatorResponse
  (process-event [{response :response} app]
    (let [sending-done? (empty? (:transport-operator-save-q app))
          state (-> app
                    (assoc :flash-message-error (tr [:common-texts :save-failed]))
                    (save-next-operator!))]

      ;; Stay on page as long as there's items to send - otherwise there will be app state inconsistency problems
      (when sending-done?
        (routes/navigate! :own-services))

      state))

  SaveTransportOperatorResponse
  (process-event [{data :data} app]
    (let [sending-done? (empty? (:transport-operator-save-q app))
          state (cond-> app
                        true (assoc
                               :flash-message (tr [:common-texts :transport-operator-saved]))
                        sending-done? (assoc
                                        :transport-operators-with-services (map (fn [{:keys [transport-operator] :as operator-with-services}]
                                                                                  (if (= (::t-operator/id data) (::t-operator/id transport-operator))
                                                                                    (assoc operator-with-services :transport-operator data)
                                                                                    operator-with-services))
                                                                                (:transport-operators-with-services app))
                                        :services-changed? true)
                        ;; Select last operator as selected operator
                        sending-done? (assoc :transport-operator data)
                        true (save-next-operator!))]
      ;; Stay on page as long as there's items to send - otherwise there will be app state inconsistency problems
      (when sending-done?
        (routes/navigate! :own-services))
      state))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app
      :transport-operator (assoc response
                            :loading? false)))

  OperatorRefreshResponse
  (process-event [{response :response} {:keys [ytj-company-names] :as app}]
    (let [app (assoc app :transport-operators-with-services (:transport-operators response))
          ytj-ops (update-nap-keys-to-ytj-names ytj-company-names (operators-of-user app))
          ytj-ops-marked (mark-menuitems ytj-ops)]
      (-> app
          (assoc :transport-operator-data-loaded? true
                 :ytj-company-names ytj-ops-marked)
          (assoc-in [:transport-operator :transport-operators-to-save] (ytj-ops-already-in-nap ytj-ops-marked)))))

  OperatorRenameResponse
  (process-event [{response :response previous :previous payload :payload :as all} app]
    (let [nap-orphans (update-nap-orphans (get-in app [:transport-operator :ytj-orphan-nap-operators]) response previous (::t-operator/name payload))]
      (cond-> app
              true (assoc-in [:transport-operator :ytj-orphan-nap-operators] nap-orphans)
              ;; Refresh app state only if saving was ok, fail means data was not changed. Service returns on fail a status object on error or on success the count of changed records
              (pos-int? response) (refresh-transport-operator)))) ;; Issues a request, handled in OperatorRefreshResponse

  OperatorRename
  (process-event [{operator :operator sel :selection :as data} app]
    (let [selection (:current sel)
          ;; Set only required keys to payload eliminate any chance for modifying any other keys than what is wanted
          payload {::t-operator/id (::t-operator/id operator)
                   ;; When user reverts renaming by re-selecting the default item, rename operator back to original.
                   ;; That allows displaying the freed name as a selection option in other operator name dropdowns.
                   ::t-operator/name (if (:placeholder selection)
                                       (::t-operator/name operator)
                                       (::t-operator/name selection))}]
      (if-not (empty? (::t-operator/name payload))
        (comm/post! "transport-operator" payload
                    {:on-success (send-async! ->OperatorRenameResponse operator payload)
                     :on-failure (send-async! ->OperatorRenameResponse operator payload)})
        (.warning js/console "OperatorRename: Bad operator name"))
      app))

  UserCloseMergeSection
  (process-event [_ app]
    (assoc-in app [:transport-operator :ytj-orphan-nap-operators] nil))

  EnsureUniqueBusinessId
  (process-event [{business-id :business-id} app]
    (let [bid-validation-error (validation/validate-rule :business-id nil business-id)
          resolve-bid-uniqueness-state (fn [state bid-validation-error]
                                         (if (nil? bid-validation-error)
                                           (do
                                             (comm/get! (str "transport-operator/ensure-unique-business-id/" business-id)
                                                        {:on-success (send-async! ->EnsureUniqueBusinessIdResponse)})
                                             state)
                                           (assoc-in app [:transport-operator :business-id-exists?] nil)))]
      (-> app
          (resolve-bid-uniqueness-state bid-validation-error)
          (dissoc :ytj-response))))

  EnsureUniqueBusinessIdResponse
  (process-event [{response :response} app]
    (assoc-in app [:transport-operator :business-id-exists?] (:business-id-exists response))))

(defmethod routes/on-navigate-event :transport-operator [{id :params}]
  (if (some? (:id id))
    (->EditTransportOperator (:id id))
    (->VerifyCreateState)))
