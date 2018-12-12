(ns ote.app.controller.transport-operator
  "Transport operator controls "                            ;; FIXME: Move transport-service related stuff to other file
  (:require [ote.communication :as comm]
            [ote.ui.form :as form]
            [ote.localization :refer [tr tr-key]]
            [ote.db.transport-operator :as t-operator]
            [ote.app.routes :as routes]
            [tuck.core :refer [define-event send-async! Event]]
            [ote.app.controller.common :refer [->ServerError]]
            [ote.app.controller.flags :as flags]
            [ote.db.common :as common]))

;; TODO: serialize ytj fetches? What if user back-forwards on UI, would old resp complete and mix up app state?

(if (flags/enabled? :open-ytj-integration) ;;;;;;;;;;;;;;;;;;; TODO: remove condition check when feature is approved
(do

(defn- strip-ytj-metadata [app] (dissoc app :ytj-response :ytj-company-names :ytj-flags :transport-operator-save-q))

(defn- address-of-type [type addresses]
  "Returns from a vector the first map whose type key matches to YTJ address type."
  (let [item (first (filter #(= type (:type %)) addresses))]
    {::common/post_office (:city item)
     ::common/postal_code (:postCode item)
     ::common/street      (:street item)}))

(defn- filter-coll-type [type collection]
  "Returns a filtered a collection of maps based on :type key"
  (first (filter #(some (fn [pred] (pred %)) [(comp #{type} :type)]) collection)))

(defn- preferred-ytj-contact [types contacts]
  "Takes 'types' vector of key names as strings in order of preference, 'contacts' collection of maps
  and finds the first contacts map which has a key with a matching name.
  Key names are searched in the order they are in `types`.
  Returns the value of the forst matching key from the first matching map"
  (loop [[type & remaining] types
         result []]
    (if (and type (empty? result))
      (let [match (:value (filter-coll-type type contacts))]
        ;(.debug js/console "Contact match=" (clj->js match))
        (recur remaining (if match (conj result match) result)))
      (do
        ;(.debug js/console "Contact result=" (clj->js result))
        (first result)))))                                  ;; Return first because expected value is string not vector

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
  ;(.debug js/console "name->nap-operator: " "ytj-name=" (clj->js ytj-name) "nap-names=" (clj->js nap-operators))
  (let [nap-item (some  #(when (= (get-in % [:transport-operator ::t-operator/name]) ytj-name) %) ;; Return whole item or nil
                   nap-operators)]
    (:transport-operator nap-item)))

(defn- ytj->nap-companies [operators-ytj operators-nap]
  "Takes `operators-ytj` and if there's a name match to an item in `operators-nap`,
  copies into the ytj item nap keys which are not shared with other nap companies which have the same business-id."
  ;(.debug js/console "ytj->nap-companies operators-ytj=" (clj->js operators-ytj) " \n operators-nap=" (clj->js operators-nap))
  (doall
    (map (fn [ytj]
           (let [nap-item (name->nap-operator (:name ytj) operators-nap)
                 nap-id (::t-operator/id nap-item)]
             (cond-> ytj
                     true (assoc ::t-operator/name (:name ytj)) ;; All operators must have name set to allow updating operator and group data if necessary
                     true (dissoc :name)                        ;; Remove ytj namespace key
                     (some? nap-id) (merge (take-update-op-keys nap-item)) ;; nap fields user is allowed to edit
                     ;(some? nap-id) (assoc ::t-operator/id nap-id) ;; Set id of existing operators so they get updated and not created by nap service
                     ;; Remove keys not supported by nap service
                     true (take-operator-api-keys))))
         operators-ytj)))

;; Use-cases:
;; Create new op, business-id found in YTJ
;; Create new op, business-id not found in YTJ
;; Edit op, business id found in YTJ
;; Edit op, business id not found in YTJ
(defn- process-ytj-data [app response]
  ;(.debug js/console "process-ytj-data response=" (clj->js response))
  (let [ytj-business-id-hit? (= 200 (:status response))
        ytj-address-billing (address-of-type 1 (:addresses response))
        use-ytj-addr-billing? ytj-business-id-hit?
        ytj-address-visiting (address-of-type 2 (:addresses response))
        use-ytj-addr-visiting? ytj-business-id-hit?
        ytj-contact-phone (first (preferred-ytj-contact ["Puhelin" "Telefon" "Telephone"] (:contactDetails response)))
        use-ytj-phone? (and (not-empty ytj-contact-phone) (empty? (::t-operator/phone app)))
        ytj-contact-gsm (preferred-ytj-contact ["Matkapuhelin" "Mobiltelefon" "Mobile phone"] (:contactDetails response))
        use-ytj-gsm? (and (not-empty ytj-contact-gsm) (empty? (::t-operator/gsm app)))
        ;ytj-contact-email (first (preferred-ytj-contact ["Matkapuhelin" "Mobiltelefon" "Mobile phone"] (:contactDetails response))) ;TODO: check ytj field types, does it return email?
        use-ytj-email? false
        ytj-contact-web (first (preferred-ytj-contact ["Kotisivun www-osoite" "www-adress" "Website address"] (:contactDetails response)))
        use-ytj-web? (and (not-empty ytj-contact-web) (empty? (::t-operator/homepage app)))
        ytj-company-names (when (some? (:name response)) (ytj->nap-companies
                                                           (into [{:name (:name response)}] (:auxiliaryNames response)) ; Insert company name first to checkbox list before aux names
                                                           (:transport-operators-with-services app)))]
    (.debug js/console "process-ytj-data ytj-company-names=" (clj->js ytj-company-names) " app=" (clj->js app))
    (cond-> app
            true (assoc
                   :ytj-response response
                   :ytj-response-loading false
                   :transport-operator-loaded? true)
            true (assoc-in [:transport-operator :transport-operators-to-save] []) ;; Init to empty vector to allow populating it in different scenarios
            ;; Set data sources for form fields and if user allowed to edit
            use-ytj-addr-billing? (assoc-in [:transport-operator ::t-operator/billing-address] ytj-address-billing)
            true (assoc-in [:ytj-flags :use-ytj-addr-billing?] use-ytj-addr-billing?)
            use-ytj-addr-visiting? (assoc-in [:transport-operator ::t-operator/visiting-address] ytj-address-visiting)
            true (assoc-in [:ytj-flags :use-ytj-addr-visiting?] use-ytj-addr-visiting?)
            use-ytj-phone? (assoc-in [:transport-operator ::t-operator/phone] ytj-contact-phone)
            use-ytj-phone? (assoc-in [:ytj-flags :use-ytj-phone?] true)
            use-ytj-gsm? (assoc-in [:transport-operator ::t-operator/gsm] ytj-contact-gsm)
            use-ytj-gsm? (assoc-in [:ytj-flags :use-ytj-gsm?] true)
            ;use-ytj-email? (assoc-in [:transport-operator ::t-operator/email] ytj-contact-email) ;TODO: check ytj field types, does it return email?
            use-ytj-email? (assoc-in [:ytj-flags :use-ytj-email?] true)
            use-ytj-web? (assoc-in [:transport-operator ::t-operator/homepage] ytj-contact-web)
            use-ytj-web? (assoc-in [:ytj-flags :use-ytj-homepage?] true)
            ;; Set data source for company selection list
            (not-empty ytj-company-names) (assoc :ytj-company-names ytj-company-names)
            ;; Set checkbox list items selected if they have an ytj name match in nap:
            (not-empty ytj-company-names) (assoc-in [:transport-operator :transport-operators-to-save] (filterv ::t-operator/id ytj-company-names)))))

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
      (dissoc :ytj-response)
      (assoc :ytj-response-loading true)))

(define-event FetchYtjOperator [id]
              {}
              (send-fetch-ytj app id))

(define-event CancelTransportOperator []
              {}
              (.debug js/console "transport-operator/CancelTransportOperator")
              (routes/navigate! :own-services)
              (update-in app [:transport-operator] dissoc :new?))

(define-event VerifyCreateState []
              {}
              ;; To avoid app state problems redirect to own services if user refreshes on operator creation view
              (when-not (get-in app [:transport-operator :new?])
                (routes/navigate! :own-services))
              app)

(define-event ToggleTransportOperatorDeleteDialog []
              {:path [:transport-operator :show-delete-dialog?]
               :app show?}
              (not show?))

(define-event DeleteTransportOperatorResponse [response]
              {}
              (routes/navigate! :own-services)
              (-> app
                  (assoc-in [:transport-operator :show-delete-dialog?] false)
                  (assoc :flash-message (tr [:common-texts :delete-operator-success])
                         :services-changed? true)))

(define-event DeleteTransportOperator [id]
              {}
              (comm/post! "transport-operator/delete"  {:id id}
                          {:on-success (send-async! ->DeleteTransportOperatorResponse)
                           :on-failure (send-async! ->ServerError)})
              app)

(defrecord SelectOperator [data])
(defrecord SelectOperatorForTransit [data])
(defrecord EditTransportOperator [id])
(defrecord EditTransportOperatorResponse [response])
(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperator [])
(defrecord SaveTransportOperatorResponse [data])
(defrecord FailedTransportOperatorResponse [response])

(defrecord TransportOperatorResponse [response])
(defrecord CreateTransportOperator [])

;; Takes `app`, POSTs the next transport operator in queue and updates the queue.
;; Returns a new app state.
(defn- save-next-operator! [app]
  (let [ops-to-save (:transport-operator-save-q app)
        op-next (first ops-to-save)
        ops-rest (rest ops-to-save)]
    (.debug js/console "save-next-operator! app=" (clj->js app) " \n next=" (clj->js op-next) " \n in queue=" (count ops-rest)) ;; TODO: disable from production

    (if (some? op-next)
      (do (comm/post! "transport-operator" op-next
                      {:on-success (send-async! ->SaveTransportOperatorResponse)
                       :on-failure (send-async! ->FailedTransportOperatorResponse)})
          (assoc app :transport-operator-save-q ops-rest))
      (dissoc app :transport-operator-save-q))))

(defn transport-operator-by-ckan-group-id[id]
  (comm/get! (str "transport-operator/" id) {:on-success (send-async! ->TransportOperatorResponse)}))

(extend-protocol Event

  CreateTransportOperator
  (process-event [_ app]
    (.debug js/console "transport-operator: CreateTransportOperator")
    (let [res (-> app
                    (strip-ytj-metadata)
                    (assoc
                      :transport-operator {:new? true}
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
    ;(.debug js/console "EditTransportOperator id=" (clj->js id))
    (if id
      (do
        (comm/get! (str "t-operator/" id)
                   {:on-success (send-async! ->EditTransportOperatorResponse)})
        (assoc app :transport-operator-loaded? false))
      (assoc app :transport-operator-loaded? true)))

  EditTransportOperatorResponse                             ;; todo: pois
  (process-event [{response :response} app]
    ;(.debug js/console "EditTransportOperatorResponse response=" (clj->js response))

    (let [state (assoc app :transport-operator response
                           :transport-operator-loaded? true)
          nap-business-id (get-in state [:transport-operator ::t-operator/business-id])
          op-ytj-cache-miss? (or (empty? (:ytj-company-names state)) (not= nap-business-id (get-in state [:ytj-response :businessId])))]

      (if op-ytj-cache-miss?
        (send-fetch-ytj state nap-business-id)
        (process-ytj-data state (:ytj-response state))
        )
      ))

  EditTransportOperatorState
  (process-event [{data :data} app]
    (dissoc
      (update app :transport-operator merge data)
      :transport-operator-save-q))

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
      ;(.debug js/console "SaveTransportOperator data-fields=" (clj->js data-fields) " \n ytj-ops-selected=" (clj->js ytj-ops-selected) " \n operators-to-save=" (clj->js operators-to-save) )
      (save-next-operator!
        (assoc app :transport-operator-save-q operators-to-save))))

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
    (.debug js/console "SaveTransportOperatorResponse: sending?=" (clj->js (:transport-operator-save-q app)))

    (let [sending-done? (empty? (:transport-operator-save-q app))
          state (cond-> app
                        true (assoc
                               :flash-message (tr [:common-texts :transport-operator-saved]))
                        sending-done? (assoc
                                        :transport-operator data
                                        :transport-operators-with-services (map (fn [{:keys [transport-operator] :as operator-with-services}]
                                                                                  (if (= (::t-operator/id data) (::t-operator/id transport-operator))
                                                                                    (assoc operator-with-services :transport-operator data)
                                                                                    operator-with-services))
                                                                                (:transport-operators-with-services app))
                                        :services-changed? true)
                        true (save-next-operator!))]
      ;; Stay on page as long as there's items to send - otherwise there will be app state inconsistency problems
      (when sending-done?
        (routes/navigate! :own-services))

      state))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app
      :transport-operator (assoc response
                            :loading? false))))

(defmethod routes/on-navigate-event :transport-operator [{id :params}]
  (.debug js/console "transport-operator/on-navigate-event: id=" (clj->js id))
  (if (some? (:id id))
    (->EditTransportOperator (:id id))
    (->VerifyCreateState)
    ))

)
(do  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TODO: remove this no-YTJ branch when feature is approved

(define-event ToggleTransportOperatorDeleteDialog []
  {:path [:transport-operator :show-delete-dialog?]
   :app show?}
  (not show?))

(define-event DeleteTransportOperatorResponse [response]
  {}
  (routes/navigate! :own-services)
  (-> app
    (assoc-in [:transport-operator :show-delete-dialog?] false)
    (assoc :flash-message (tr [:common-texts :delete-operator-success])
           :services-changed? true)))

(define-event DeleteTransportOperator [id]
  {}
  (comm/post! "transport-operator/delete"  {:id id}
            {:on-success (send-async! ->DeleteTransportOperatorResponse)
             :on-failure (send-async! ->ServerError)})
  app)

(defrecord SelectOperator [data])
(defrecord SelectOperatorForTransit [data])
(defrecord EditTransportOperator [id])
(defrecord EditTransportOperatorResponse [response])
(defrecord EditTransportOperatorState [data])
(defrecord SaveTransportOperator [])
(defrecord SaveTransportOperatorResponse [data])
(defrecord FailedTransportOperatorResponse [response])

(defrecord TransportOperatorResponse [response])
(defrecord CreateTransportOperator [])


(defn transport-operator-by-ckan-group-id[id]
  (comm/get! (str "transport-operator/" id) {:on-success (send-async! ->TransportOperatorResponse)}))

(extend-protocol Event

  CreateTransportOperator
  (process-event [_ app]
    (routes/navigate! :transport-operator)
    (assoc app
           :transport-operator {:new? true}
           :services-changed? true))

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
    (comm/get! (str "t-operator/" id)
               {:on-success (send-async! ->EditTransportOperatorResponse)})
    (assoc app :transport-operator-loaded? false))

  EditTransportOperatorResponse
  (process-event [{response :response} app]
       (assoc app
              :transport-operator-loaded? true
              :transport-operator response))

  EditTransportOperatorState
  (process-event [{data :data} app]
    (update app :transport-operator merge data))

  SaveTransportOperator
  (process-event [_ app]
    (let [operator-data (-> app
                            :transport-operator
                            form/without-form-metadata)]
      (comm/post! "transport-operator" operator-data {:on-success (send-async! ->SaveTransportOperatorResponse)
                                                      :on-failure (send-async! ->FailedTransportOperatorResponse)})
      app))

  FailedTransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app :flash-message-error (tr [:common-texts :save-failed])))

  SaveTransportOperatorResponse
  (process-event [{data :data} app]
    (routes/navigate! :own-services)
    (assoc app
           :flash-message (tr [:common-texts :transport-operator-saved ])
           :transport-operator data
           :transport-operators-with-services (map (fn [{:keys [transport-operator] :as operator-with-services}]
                                                     (if (= (::t-operator/id data)
                                                            (::t-operator/id transport-operator))
                                                       (assoc operator-with-services
                                                              :transport-operator data)
                                                       operator-with-services))
                                                   (:transport-operators-with-services app))
           :services-changed? true))

  TransportOperatorResponse
  (process-event [{response :response} app]
    (assoc app
      :transport-operator (assoc response
                            :loading? false))))))
