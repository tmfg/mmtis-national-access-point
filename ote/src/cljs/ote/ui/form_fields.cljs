(ns ote.ui.form-fields
  "UI components for different form fields."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.form-fields :as style-form-fields]
            [ote.style.base :as style-base]
            [ote.ui.validation :as valid]
            [ote.time :as time]
            [ote.ui.buttons :as buttons]
            [ote.ui.common :as common]
            [ote.style.form :as style-form]
            [ote.util.values :as values]
            [goog.string :as gstr]))



(def text-field
  "Temporary Material-ui reagent TextField fix. Requires Reagent v0.8.0-alpha2.
  Fixes caret positioning when editing TextField input.
  See: https://github.com/madvas/cljs-react-material-ui/issues/17"

  (r/adapt-react-class
    (aget js/MaterialUI "TextField")
    {:synthetic-input
     ;; A valid map value for `synthetic-input` does two things:
     ;; 1) It implicitly marks this component class as an input type so that interactive
     ;;    updates will work without cursor jumping.
     ;; 2) Reagent defers to its functions when it goes to set a value for the input component,
     ;;    or signal a change, providing enough data for us to decide which DOM node is our input
     ;;    node to target and continue processing with that (or any arbitrary behaviour...); and
     ;;    to handle onChange events arbitrarily.
     ;;
     ;;    Note: We can also use an extra hook `on-write` to execute more custom behaviour
     ;;    when Reagent actually writes a new value to the input node, from within `on-update`.
     ;;
     ;;    Note: Both functions receive a `next` argument which represents the next fn to
     ;;    execute in Reagent's processing chain.
     {:on-update (fn [next root-node rendered-value dom-value component]
                   (let [input-node (.querySelector root-node "input")
                         textarea-nodes (array-seq (.querySelectorAll root-node "textarea"))
                         textarea-node (when (= 2 (count textarea-nodes))
                                         ;; We are dealing with EnhancedTextarea (i.e.
                                         ;; multi-line TextField)
                                         ;; so our target node is the second <textarea>...
                                         (second textarea-nodes))
                         target-node (or input-node textarea-node)]
                     (when target-node
                       ;; Call Reagent's input node value setter fn (extracted from input-set-value)
                       ;; which handles updating of a given <input> element,
                       ;; now that we have targeted the correct <input> within our component...
                       (next target-node rendered-value dom-value component
                             ;; Also hook into the actual value-writing step,
                             ;; since `input-node-set-value doesn't necessarily update values
                             ;; (i.e. not dirty).
                             {:on-write
                              (fn [new-value]
                                ;; `blank?` is effectively the same conditional as Material-UI uses
                                ;; to update its `hasValue` and `isClean` properties, which are
                                ;; required for correct rendering of hint text etc.
                                (if (clojure.string/blank? new-value)
                                  (.setState component #js {:hasValue false :isClean false})
                                  (.setState component #js {:hasValue true :isClean false})))}))))
      :on-change (fn [next event]
                   ;; All we do here is continue processing but with the event target value
                   ;; extracted into a second argument, to match Material-UI's existing API.
                   (next event (-> event .-target .-value)))}}))

(defn read-only-atom [value]
  (r/wrap value
          #(assert false (str "Can't write to a read-only atom: " (pr-str value)))))

(defmulti field
  "Create an editable form field UI component. Dispatches on `:type` keyword.
  A field must always have an `:update!` callback the component calls to update a new value."
  (fn [t _] (:type t)))

(defmulti show-value
  "Create a read-only display for a value. Dispatches on `:type` keyword.
  This is not meant to be a 'disabled' input field, but for showing a readable value.
  Default implementation just converts input value to string."
  (fn [t _] (:type t)))

(defmethod show-value :default [_ data]
  [:span (str data)])

(defmethod show-value :component [skeema data]
  (let [komponentti (:component skeema)]
    [komponentti data]))


(defn placeholder [{:keys [placeholder placeholder-fn row] :as field} data]
  (or placeholder
      (and placeholder-fn (placeholder-fn row))
      ""))

(defmethod field :string [{:keys [update! label name max-length min-length regex
                                  focus on-focus form? error warning table? full-width?
                                  style input-style hint-style]
                           :as   field} data]
  [text-field
   (merge
    {:name name
     :floating-label-text (when-not table?  label)
     :floating-label-fixed true
     :hintText          (placeholder field data)
     :on-change         #(let [v %2]
                           (if regex
                             (when (re-matches regex v)
                               (update! v))
                             (update! v)))
     :value             (or data "")
     :error-text        (or error warning "") ;; Show error text or warning text or empty string
     :error-style       (if error ;; Error is more critical than required - showing it first
                          style-base/error-element
                          style-base/required-element)
     :hint-style (merge style-base/placeholder
                        hint-style)}
    (when max-length
      {:max-length max-length})
    (when full-width?
      {:full-width true})
    (when style
      {:style style})
    (when input-style
      {:input-style input-style}))])

(defmethod field :text-area [{:keys [update! label name rows error]
                              :as   field} data]
  [text-field
   {:name name
    :floating-label-text label
    :floating-label-fixed true
    :hintText          (placeholder field data)
    :on-change         #(update! %2)
    :value             (or data "")
    :multi-line        true
    :rows              rows
    :error-text        error}])

(def languages ["FI" "SV" "EN"])

(defmethod field :localized-text [{:keys [update! table? label name rows rows-max warning error full-width?]
                                   :as   field} data]
  (r/with-let [selected-language (r/atom (first languages))]
    (let [data (or data [])
          languages (or (:languages field) languages)
          language @selected-language
          language-data (some #(when (= language (:ote.db.transport-service/lang %)) %) data)
          rows (or rows 1)]
      [:div.table-responsive
        [:table.table {:style (when full-width? style-form/full-width)}
         [:tbody
           [:tr
            [:td
             [text-field
              (merge
               {:name name
                :floating-label-text (when-not table? label)
                :floating-label-fixed true
                :hintText          (placeholder field data)
                :on-change         #(let [updated-language-data
                                          {:ote.db.transport-service/lang language
                                           :ote.db.transport-service/text %2}]
                                      (update!
                                       (if language-data
                                         (mapv (fn [lang]
                                                 (if (= (:ote.db.transport-service/lang lang) language)
                                                   updated-language-data
                                                   lang)) data)
                                         (conj data updated-language-data))))
                :value             (or (:ote.db.transport-service/text language-data) "")
                :multi-line         true
                :rows rows
                :rows-max (or rows-max 200)
                :error-text       (or error "")}
               (when full-width?
                 {:full-width true}))]]]
           [:tr
            [:td
             [:div (stylefy/use-style style-form-fields/localized-text-language-container)
             (doall
              (for [lang languages]
                ^{:key lang}
                [:a  (merge
                      (stylefy/use-style
                         (if (= lang language)
                           style-form-fields/localized-text-language-selected
                           style-form-fields/localized-text-language))
                      {:href "#" :on-click #(do (.preventDefault %)
                                                (reset! selected-language lang))})
                 lang]))]]]
            (when (or error warning)
              [:tr
               [:td
                [:div (stylefy/use-style style-base/required-element)
                 (if error error warning)]]])]]])))


(defmethod field :selection [{:keys [update! table? label name style show-option options form? error warning auto-width? disabled?] :as field}
                             data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [option-idx (zipmap options (range))]
    [ui/select-field
     (merge
      {:auto-width (boolean auto-width?)
                      :style style
                      :floating-label-text (when-not table? label)
                      :floating-label-fixed true
                      :value (option-idx data)
                      :on-change #(update! (nth options %2))
                      :error-text        (or error warning "") ;; Show error text or warning text or empty string
                      :error-style       (if error             ;; Error is more critical than required - showing it first
                                           style-base/error-element
                                           style-base/required-element)
                      }
      (when disabled?
        {:disabled true}))
     (doall
      (map-indexed
       (fn [i option]
         (if (= :divider option)
           ^{:key i}
           [ui/divider]
           ^{:key i}
           [ui/menu-item {:value i :primary-text (show-option option)}]))
       options))]))


(defmethod field :multiselect-selection
  [{:keys [update! label name style show-option show-option-short options form? error warning
           auto-width? full-width?]
    :as field}
   data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [selected-set (set (or data #{}))
        option-idx (zipmap options (range))]
    [:div
      [ui/select-field
       (merge
        {:style style
         :floating-label-text label
         :floating-label-fixed true
         :multiple true
         :value (clj->js (map option-idx selected-set))
         :selection-renderer (fn [values]
                               (str/join ", " (map (comp (or show-option-short show-option) (partial nth options)) values)))
         :on-change (fn [event index values]
                      (cond
                        ;; Select all - if :ALL option is present, if first element is selected and if all options aren't selected
                        (and (some #(= :ALL %) options) (some #(= 0 %) values) (<= (count values) (count (drop 1 options))))
                          (update! (drop 1 options))
                        ;;Deselect all
                        (and (some #(= :ALL %) options) (some #(= 0 %) values) (= (count options) (count values)))
                          (update! (into #{} nil))
                        ;; Select one
                        :else (update! (into #{}
                                             (map (partial nth options))
                                             values))))}
        (when auto-width?
          {:auto-width true})
        (when full-width?
          {:full-width true}))
       ;; Add selected value to vector
       (doall
        (map-indexed
         (fn [i option]
           ^{:key i}
           [ui/menu-item {:value i
                          :primary-text (show-option option)
                          :inset-children true
                          :checked (boolean (selected-set option))}])
         options))]
    (when (or error warning)
      [:div (stylefy/use-style style-base/required-element)
       (if error error warning)])]))

(defmethod field :checkbox-group [{:keys [update! label show-option options help]} data]
  (let [selected (set (or data #{}))]
    [:div.checkbox-group
     [:h4 (stylefy/use-style style-form-fields/checkbox-group-label) label]
     (when help
       [common/help help])
     (doall
      (map-indexed
       (fn [i option]
         (let [checked? (boolean (selected option))]
           [ui/checkbox {:key i
                         :label (show-option option)
                         :checked checked?
                         :on-check #(update! ((if checked? disj conj) selected option))}]))
       options))]))

(defmethod field :checkbox [{:keys [update! label]} data]
  (let [checked? (boolean data)]
    [ui/checkbox {:label label
                  :checked checked?
                  :on-check #(update! (not checked?))}]))


(def phone-regex #"\+?\d+")

(defmethod field :phone [opts data]
  [field (assoc opts
                :type :string
                :regex phone-regex)])

(def number-regex #"\d*([\.,]\d{0,2})?")

(defmethod field :number [_  data]
  ;; Number field contains internal state that has the current
  ;; typed in text (which may be an incompletely typed number).
  ;;
  ;; The value updated to the app model is always a parsed number.
  (let [fmt #(if % (str/replace (.toFixed % 2) #"(,|\.)00" "") "")
        state (r/atom {:value data
                       :txt (fmt data)})]
    (r/create-class
     {:component-will-receive-props
      (fn [_ [_ _ new-value]]
        (swap! state
               (fn [{:keys [value txt] :as state}]
                 (if (not= value new-value)
                   {:value new-value
                    :txt (fmt new-value)}
                   state))))
      :reagent-render
      (fn [{:keys [update! currency?] :as opts} data]
        [:span [field (assoc opts
                             :type :string
                             :regex number-regex
                             :update! #(let [new-value (if (str/blank? %)
                                                         nil
                                                         (-> %
                                                             (str/replace #"," ".")
                                                             (js/parseFloat %)))]
                                         (reset! state {:value new-value
                                                        :txt %})
                                         (update! new-value)))
                (:txt @state)]
         (when currency? "€")])})))

;; Matches empty or any valid hour (0 (or 00) - 23)
(def hour-regex #"^(^$|0?[0-9]|1[0-9]|2[0-3])$")

;; Matches empty or any valid minute (0 (or 00) - 59)
(def minute-regex #"^(^$|0?[0-9]|[1-5][0-9])$")

(defmethod field :time [{:keys [update! error warning] :as opts}
                        {:keys [hours hours-text minutes minutes-text] :as data}]
  [:div (stylefy/use-style style-base/inline-block)
   [field (merge
           {:type :string
            :regex hour-regex
            :style {:width 30}
            :input-style {:text-align "right"}
            :hint-style {:position "absolute" :right "0"}
            :update! (fn [hour]
                       (let [h (if (str/blank? hour)
                                 nil
                                 (js/parseInt hour))]
                         (update! (assoc (time/->Time h minutes nil)
                                         :hours-text hour))))}
           (when (not hours)
             {:placeholder (tr [:common-texts :hours-placeholder])}))
    (if (not hours)
      ""
      (or hours-text (str hours)))]
   "."
   [field (merge
           {:type :string
            :regex minute-regex
            :style {:width 30}
            :update! (fn [minute]
                       (let [m (if (str/blank? minute)
                                 nil
                                 (js/parseInt minute))]
                         (update! (assoc (time/->Time hours m nil)
                                         :minutes-text minute))))}
           (when (not minutes)
             {:placeholder (tr [:common-texts :minutes-placeholder])}))
    (if (not minutes)
      ""
      (or minutes-text (gstr/format "%02d" minutes)))]

   (when warning
     [:div (stylefy/use-style style-base/error-element) warning])
   ])

(defmethod field :interval [{:keys [update!] :as opts} data]
  (let [[unit amount] (or (some (fn [[unit amount]]
                                  (when (not (zero? amount)) [unit amount])) data) [:hours 0])]
    [:div
     [field (assoc opts
              :update! (fn [num]
                         (update! (time/interval (or num 0) unit)))
              :type :number) (unit data)]
     [field (assoc opts
              :update! (fn [unit]
                         (update! (time/interval amount unit)))
              :label (tr [:common-texts :time-unit])
              :name :maximum-stay-unit
              :type :selection
              :show-option (tr-key [:common-texts :time-units])
              ;; TODO: Days not supported yet in ote.time
              :options [:minutes :hours])
      unit]]))

(defmethod field :time-picker [{:keys [update! ok-label cancel-label default-time] :as opts} data]
  (let [time-picker-time (if (= nil? data) default-time data)]
  [ui/time-picker
   {:format "24hr"
    :cancel-label cancel-label
    :ok-label ok-label
    :minutes-step 1
    :default-time (time/to-js-time time-picker-time)
    :on-change (fn [event value]
                 (update! (time/parse-time (time/format-js-time value))))}]))

(defmethod field :date-picker [{:keys [update! label ok-label cancel-label
                                       show-clear? hint-text] :as opts} data]
  [:div (stylefy/use-style style-base/inline-block)
   [ui/date-picker {:style {:display "inline-block"}
                    :text-field-style {:width "150px"}
                    :hint-text (or hint-text "")
                    :floating-label-text label
                    :floating-label-fixed true
                    :auto-ok true
                    :value data
                    :on-change (fn [_ date]
                                 (update! date))
                    :format-date time/format-date
                    :ok-label (or ok-label (tr [:buttons :save]))
                    :cancel-label (or cancel-label (tr [:buttons :cancel]))
                    :locale "fi-FI"
                    :Date-time-format js/Intl.DateTimeFormat}]
   (when show-clear?
     [ui/icon-button {:on-click #(update! nil)
                      :disabled (not data)
                      :style {:width 16 :height 16
                              :position "relative"
                              :padding 0
                              :left -15
                              :top "5px"}
                      :icon-style {:width 16 :height 16}}
      [ic/content-clear {:style {:width 16 :height 16}}]])])

(defmethod field :default [opts data]
  [:div.error "Missing field type: " (:type opts)])


(defmethod field :table [{:keys [table-fields update! delete? add-label error-data] :as opts} data]
  (let [data (if (or (empty? data)
                     (and delete?
                          (every? true? (map :deleted? data))))
               ;; have table always contain at least one (not deleted) row
               [{}]
               data)]
    [:div
     [ui/table
      [ui/table-header (merge {:adjust-for-checkbox false :display-select-all false}
                              {:style style-form/table-header-style})
       [ui/table-row (merge {:selectable false}
                            {:style style-form/table-header-style})
        (doall
         (for [{:keys [name label width] :as tf} table-fields]
           ^{:key name}
           [ui/table-header-column {:style
                                    (merge {:width width :white-space "pre-wrap"}
                                           style-form/table-header-style)}
            label]))
        (when delete?
          [ui/table-header-column {:style (merge {:width "70px"} style-form/table-header-style)}
           (tr [:buttons :delete])])]]

      [ui/table-body {:display-row-checkbox false}
       (doall
        (map-indexed
         (fn [i row]           
           (let [{:keys [errors missing-required-fields]} (and error-data
                                                               (< i (count error-data))
                                                               (nth error-data i))]
             (when (not (:deleted? row))
               
               ^{:key i}
               [ui/table-row (merge {:selectable false :display-border false}
                                    ;; If there are errors or missing fields, make the
                                    ;; row taller to show error messages
                                    (when (or errors missing-required-fields)
                                      {:style {:height 65}}))
                (doall 
                 (for [{:keys [name read write width type component] :as tf} table-fields
                       :let [field-error (get errors name)
                             missing? (get missing-required-fields name)
                             update-fn (if write
                                         #(update data i write %)
                                         #(assoc-in data [i name] %))
                             value ((or read name) row)]]
                   ^{:key name}
                   [ui/table-row-column {:style {:width width}}                    
                    (if (= :component type)
                      (component {:update-form! #(update! (update-fn %))
                                  :data value})
                      [field (merge (assoc tf
                                           :table? true
                                           :update! #(update! (update-fn %)))
                                    (when missing?
                                      {:warning (tr [:common-texts :required-field])})
                                    (when field-error
                                      {:error field-error}))
                       value])]))
                (when delete?
                  [ui/table-row-column {:style {:width "70px"}}
                   [ui/icon-button {:on-click #(update!
                                                (when (>= i 0)                                                  
                                                  (assoc-in data [i :deleted?] true)
                                        ))}                  
                    [ic/action-delete]]])])))
         data))]]
     (when add-label
       [:div (stylefy/use-style style-base/button-add-row)
        [buttons/save {:on-click #(update! (conj (or data []) {}))
                       :label add-label
                       :label-style style-base/button-label-style
                       :disabled (values/effectively-empty? (last data))}]])]))

(defmethod field :checkbox [{:keys [update! label warning error style]} checked?]
  [:div (when error (stylefy/use-style style-base/required-element))
    [ui/checkbox {:label label
                  :checked checked?
                  :on-check #(update! (not checked?))
                  :style style}]
    (when error
        (tr [:common-texts :required-field]))])
