(ns ote.ui.form-fields
  "UI components for different form fields."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [ote.localization :refer [tr]]
            [cljs-react-material-ui.icons :as ic]
            [stylefy.core :as stylefy]
            [ote.style.form-fields :as style-form-fields]
            [ote.style.base :as style-base]
            [ote.ui.validation :as valid]
            [ote.time :as time]
            [ote.ui.buttons :as buttons]))



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
      (and placeholder-fn (placeholder-fn row))))

(defmethod field :string [{:keys [update! label name max-length min-length regex
                                  focus on-focus form? error warning table?]
                           :as   field} data]
  [text-field
   {:floatingLabelText (when-not table?  label)
    :hintText          (placeholder field data)
    :on-change         #(let [v %2]
                          (if regex
                            (when (re-matches regex v)
                              (update! v))
                            (update! v)))
    :value             (or data "")
    :error-text        (or error warning "") ;; Show error text or warning text or empty string
    :error-style       (if error             ;; Error is more critical than required - showing it first
                        style-base/error-element
                        style-base/required-element)}])

(defmethod field :text-area [{:keys [update! label name rows error]
                              :as   field} data]
  [text-field
   {:floatingLabelText label
    :hintText          (placeholder field data)
    :on-change         #(update! %2)
    :value             (or data "")
    :multiLine         true
    :rows              rows
    :error-text        error}])

(def languages ["FI" "SV" "EN"])

(defmethod field :localized-text [{:keys [update! table? label name rows rows-max error]
                                   :as   field} data]
  (r/with-let [selected-language (r/atom (first languages))]
    (let [data (or data [])
          languages (or (:languages field) languages)
          language @selected-language
          language-data (some #(when (= language (:ote.db.transport-service/lang %)) %) data)]
      [:table
       [:tr
        [:td
         [text-field
          {:floatingLabelText (when-not table? label)
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
           :multiLine         true
           :rows rows
           :rows-max (or rows-max rows)
           :error-text        error}]]]
       [:tr
        [:td (stylefy/use-style style-form-fields/localized-text-language-links)
         (doall
          (for [lang languages]
            ^{:key lang}
            [:a (merge
                 (stylefy/use-style
                  (if (= lang language)
                    style-form-fields/localized-text-language-selected
                    style-form-fields/localized-text-language))
                 {:on-click #(reset! selected-language lang)})
             lang]))]]])))


(defmethod field :selection [{:keys [update! label name style show-option options form? error warning] :as field}
                             data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [option-idx (zipmap options (range))]
    [ui/select-field {:style style
                      :floating-label-text label
                      :value (option-idx data)
                      :on-change #(update! (nth options %2))
                      :error-text        (or error warning "") ;; Show error text or warning text or empty string
                      :error-style       (if error             ;; Error is more critical than required - showing it first
                                           style-base/error-element
                                           style-base/required-element)
                      }
     (doall
      (map-indexed
       (fn [i option]
         ^{:key i}
         [ui/menu-item {:value i :primary-text (show-option option)}])
       options))]))


(defmethod field :multiselect-selection
  [{:keys [update! label name style show-option show-option-short options form? error auto-width?]
    :as field}
   data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [selected-set (set (or data #{}))
        option-idx (zipmap options (range))]
    [ui/select-field
     {:auto-width (boolean auto-width?)
      :style style
      :floating-label-text label
      :multiple true
      :value (clj->js (map option-idx selected-set))
      :selection-renderer (fn [values]
                            (str/join ", " (map (comp (or show-option-short show-option) (partial nth options)) values)))
      :on-change (fn [event index values]
                   (update! (into #{}
                                  (map (partial nth options))
                                  values)))}
     ;; Add selected value to vector
     (doall
      (map-indexed
       (fn [i option]
         ^{:key i}
         [ui/menu-item {:value i
                        :primary-text (show-option option)
                        :inset-children true
                        :checked (boolean (selected-set option))}])
       options))]))


(def phone-regex #"\+?\d+")

(defmethod field :phone [opts data]
  [field (assoc opts
                :type :string
                :regex phone-regex)])

(def number-regex #"\d*([\.,]\d*)?")

(defmethod field :number [_  data]
  ;; Number field contains internal state that has the current
  ;; typed in text (which may be an incompletely typed number).
  ;;
  ;; The value updated to the app model is always a parsed number.
  (let [txt (r/atom (if data (.toFixed data 2) ""))]
    (fn [{:keys [update!] :as opts} data]
      [field (assoc opts
                    :type :string
                    :parse js/parseFloat
                    :regex number-regex
                    :update! #(do
                                (reset! txt %)
                                (update!
                                   (if (str/blank? %)
                                     nil
                                     (-> %
                                         (str/replace #"," ".")
                                         (js/parseFloat %))))))
       @txt])))

(def time-regex #"\d{0,2}(:\d{0,2})?")

(defmethod field :time [{:keys [update!] :as opts} data]
  ;; FIXME: material-ui timepicker doesn't allow simply writing a time
  ;; best would be both, writing plus an icon to open selector dialog
  (let [data (or (some-> data ::incomplete)
                 (and data (time/format-time data))
                 "")]
    [field (assoc opts
                  :update! (fn [string]
                             (update! (assoc (time/parse-time string)
                                             ::incomplete string)))
                  :type :string
                  :regex time-regex) data]))

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

(defmethod field :date-picker [{:keys [update! label ok-label cancel-label] :as opts} data]
  [ui/date-picker {:hint-text label
                   :value data
                   :on-change (fn [_ date]
                                (update! date))
                   :format-date time/format-date
                   :ok-label (or ok-label (tr [:buttons :save]))
                   :cancel-label (or cancel-label (tr [:buttons :cancel]))
                   :locale "fi-FI"
                   :Date-time-format js/Intl.DateTimeFormat}])

(defmethod field :default [opts data]
  [:div.error "Missing field type: " (:type opts)])


(defmethod field :table [{:keys [table-fields update! delete? add-label] :as opts} data]
  [:div
   [ui/table
    [ui/table-header {:adjust-for-checkbox false
                      :display-select-all false}
     [ui/table-row {:selectable false}
      (doall
       (for [{:keys [name label width] :as tf} table-fields]
         ^{:key name}
         [ui/table-header-column {:style
                                  {:width width
                                   :white-space "pre-wrap"}}
          label]))
      (when delete?
        [ui/table-header-column {:style {:width "70px"}}
         (tr [:buttons :delete])])]]

    [ui/table-body {:display-row-checkbox false}
     (map-indexed
      (fn [i row]
        ^{:key i}
        [ui/table-row {:selectable false :display-border false}
         (doall
          (for [{:keys [name read write width] :as tf} table-fields
                :let [update-fn (if write
                                  #(update data i write %)
                                  #(assoc-in data [i name] %))]]
            ^{:key name}
            [ui/table-row-column {:style {:width width}}
             [field (assoc tf
                           :table? true
                           :update! #(update! (update-fn %)))
              ((or read name) row)]]))
         (when delete?
           [ui/table-row-column {:style {:width "70px"}}
            [ui/icon-button {:on-click #(update! (vec (concat (when (pos? i)
                                                                (take i data))
                                                              (drop (inc i) data))))}
             [ic/action-delete]]])])
      data)]]
   (when add-label
     [buttons/save {:on-click #(update! (conj (or data []) {}))
                    :label add-label
                    :label-style style-base/button-label-style
                    :disabled false}])])

(defmethod field :checkbox [{:keys [update! label]} checked?]
  [ui/checkbox {:label label
                :checked checked?
                :on-check #(update! (not checked?))}])
