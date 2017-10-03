(ns ote.ui.form-fields
  "UI components for different form fields."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]))


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
                                   focus on-focus form? error]
                            :as   field} data]
  [ui/text-field
   {:floatingLabelText label
    :hintText          (placeholder field data)
    :on-change         #(update! %2)
    :value             (or data "")
    :error-text        error}])


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

(defmethod field :localized-text [{:keys [update! label name rows error]
                                   :as   field} data]
  [ui/text-field
   {:floatingLabelText label
    :hintText          (placeholder field data)
    :on-change         #(update! [{:ote.db.transport-service/lang "FI" :ote.db.transport-service/text %2}])
    :value             (get-in data [0 :ote.db.transport-service/text])
    :multiLine         true
    :rows              rows
    :error-text        error}]

  )


(defmethod field :selection [{:keys [update! label name show-option options form? error] :as field}
                             data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [option-idx (zipmap options (range))]
    [ui/select-field {:floating-label-text label
                      :value (option-idx data)
                      :on-change #(update! (nth options %2))}
     (map-indexed
      (fn [i option]
        ^{:key i}
        [ui/menu-item {:value i :primary-text (show-option option)}])
      options)]))


(defmethod field :multiselect-selection [{:keys [update! label name show-option options form? error] :as field} data]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [selected-set (or data #{})
        option-idx (zipmap options (range))]
    [ui/select-field {:floating-label-text label
                      :multiple true
                      :value (clj->js (map option-idx selected-set))
                      :selection-renderer (fn [values]
                                            (str/join ", " (map (comp show-option (partial nth options)) values)))
                      :on-change (fn [event index values]
                                   (update! (into #{}
                                                  (map (partial nth options))
                                                  values)))} ;; Add selected value to vector
     (map-indexed
       (fn [i option]
         ^{:key i}
         [ui/menu-item {
                        :value i
                        :primary-text (show-option option)
                        :inset-children true
                        :checked (boolean (selected-set option))
                        }])
       options)]))


(def phone-regex #"\+?\d+")

(defmethod field :phone [field data]
  [field (assoc field
                :type :string
                :regex phone-regex)])

(defmethod field :default [opts data]
  [:div.error "Missing field type: " (:type opts)])
