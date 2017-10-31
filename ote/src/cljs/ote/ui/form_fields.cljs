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
            [ote.time :as time]))


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
                                  focus on-focus form? error warning table? required?]
                           :as   field} data]
  [ui/text-field
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
  [ui/text-field
   {:floatingLabelText label
    :hintText          (placeholder field data)
    :on-change         #(update! %2)
    :value             (or data "")
    :multiLine         true
    :rows              rows
    :error-text        error}])

(def languages ["FI" "SV" "EN"])

(defmethod field :localized-text [{:keys [update! label name rows rows-max error]
                                   :as   field} data]
  (let [data (or data [])
        languages (or (:languages field) languages)
        selected-language (or (-> data meta :selected-language) (first languages))
        language-data (some #(when (= selected-language (:ote.db.transport-service/lang %)) %) data)]
    [:table
     [:tr
      [:td
       [ui/text-field
        {:floatingLabelText label
         :hintText          (placeholder field data)
         :on-change         #(let [updated-language-data
                                   {:ote.db.transport-service/lang selected-language
                                    :ote.db.transport-service/text %2}]
                               (update!
                                (with-meta
                                  (if language-data
                                    (mapv (fn [lang]
                                            (if (= (:ote.db.transport-service/lang lang) selected-language)
                                              updated-language-data
                                              lang)) data)
                                    (conj data updated-language-data))
                                  {:selected-language selected-language})))
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
                (if (= lang selected-language)
                  style-form-fields/localized-text-language-selected
                  style-form-fields/localized-text-language))
               {:on-click #(update! (with-meta data {:selected-language lang}))})
           lang]))]]]))


(defmethod field :selection [{:keys [update! label name style show-option options form? error] :as field}
                             data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [option-idx (zipmap options (range))]
    [ui/select-field {:style style
                      :floating-label-text label
                      :value (option-idx data)
                      :on-change #(update! (nth options %2))}
     (doall
      (map-indexed
       (fn [i option]
         ^{:key i}
         [ui/menu-item {:value i :primary-text (show-option option)}])
       options))]))


(defmethod field :multiselect-selection [{:keys [update! label name style show-option show-option-short options form? error] :as field} data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [selected-set (set (or data #{}))
        option-idx (zipmap options (range))]
    [ui/select-field {:style style
                      :floating-label-text label
                      :multiple true
                      :value (clj->js (map option-idx selected-set))
                      :selection-renderer (fn [values]
                                            (str/join ", " (map (comp (or show-option-short show-option) (partial nth options)) values)))
                      :on-change (fn [event index values]
                                   (update! (into #{}
                                                  (map (partial nth options))
                                                  values)))} ;; Add selected value to vector
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
  (let [data (or (some-> data meta ::incomplete)
                 (and data (time/format-time data))
                 "")]
    [field (assoc opts
                  :update! (fn [string]
                             (update! (with-meta (time/parse-time string)
                                        {::incomplete string})))
                  :type :string
                  :regex time-regex) data]))

(defmethod field :default [opts data]
  [:div.error "Missing field type: " (:type opts)])


(defmethod field :table [{:keys [table-fields update! delete?] :as opts} data]
  [ui/table
   [ui/table-header {:adjust-for-checkbox false
                     :display-select-all false}
    [ui/table-row {:selectable false}
     (doall
      (for [{:keys [name label width] :as tf} table-fields]
        ^{:key name}
        [ui/table-header-column {:style {:width width}} label]))
     (when delete?
       [ui/table-header-column {:style {:width "70px"}}
        (tr [:buttons :delete])])]]

   [ui/table-body {:display-row-checkbox false}
    (map-indexed
     (fn [i row]
       ^{:key i}
       [ui/table-row {:selectable false :display-border false}
        (doall
         (for [{:keys [name read write width] :as tf} table-fields]
           ^{:key name}
           [ui/table-row-column {:style {:width width}}
            [field (assoc tf
                          :table? true
                          :update! #(update! (assoc-in data [i name] %)))
             ((or read name) row)]]))
        (when delete?
          [ui/table-row-column {:style {:width "70px"}}
           [ui/icon-button {:on-click #(update! (vec (concat (when (pos? i)
                                                               (take i data))
                                                             (drop (inc i) data))))}
            [ic/action-delete]]])])
     data)]])
