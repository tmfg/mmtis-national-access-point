(ns ote.ui.form
  "Generic form component.
  Forms are automatically generated from schemas that describe the fields.
  Fields can be grouped with a label to give the form more structure."
  (:require [ote.ui.validation :as validation]
            [ote.ui.form-fields :as form-fields]
            [cljs-time.core :as t]
            [clojure.string :as str]
            [cljs-react-material-ui.reagent :as ui]
            [reagent.core :as r]
            [stylefy.core :as stylefy]
            [ote.style.form :as style-form]))

(defrecord Group [label options schemas])

(defn group
  "Create a group of form fields. The first argument
  is the label of the group (or an options map containing a label).
  The rest of the arguments are field schemas for the fields in the group."
  [label-or-options & schemas]
  (if-let [options (and (map? label-or-options)
                       label-or-options)]
    (->Group (:label options)
             (merge {:layout :default}
                    options)
             schemas)
    (->Group label-or-options
             {:layout :default} schemas)))

(defn row
  "Creates an unlabeled group with all fields side-by-side in a row."
  [& schemas]
  (->Group nil {:row? true} schemas))

(defn group? [x]
  (instance? Group x))

(defn modified?
  "Check if any fields have been modified."
  [data]
  (not (empty? (::modified data))))

(defn missing-required-fields
  "Returns a (possibly empty) set of required fields that have no value in the input form data."
  [data]
  (::missing-required-fields data))

(defn required-fields-missing?
  "Returns true if any required field is missing a value."
  [data]
  (not (empty? (missing-required-fields data))))

(defn errors?
  "Returns true if there are any validation errors in the input form data."
  [data]
  (not (empty? (::errors data))))

(defn valid?
  "Check if input form data is valid. Returns true if there are no validation errors and
  all required fields have a value."
  [data]
  (and (not (errors? data))
       (not (required-fields-missing? data))))

(defn can-save-and-modified?
  "Check if form can be saved and that it has been modified."
  [data]
  (and (modified? data)
       (valid? data)))

(defn can-save?
  "Check if form can be saved."
  [data]
  (valid? data))

(defn disable-save?
  "Check if form save button should be disabled.
  Form save should be disabled if there are validation errors,
  some required fields are missing values or the form hasn't been
  modified at all."
  [data]
  (not (can-save-and-modified? data)))

(defn without-form-metadata
  "Returns form data map without form metadata keys"
  [data]
  (dissoc data
          ::modified
          ::errors
          ::warnings
          ::notices
          ::missing-required-fields
          ::first-modification
          ::latest-modification
          ::schema
          ::closed-groups))

(defrecord ^:private Label [label])
(defn- label? [x]
  (instance? Label x))

(defn- unpack-groups
  "Unpack schemas from groups to a single flat vector without nil values.
  Group labels are interleaved with schemas as Label record instances."
  [schemas]
  (loop [acc []
         [s & schemas] (remove nil? schemas)]
    (if-not s
      acc
      (cond
        (label? s)
        (recur acc schemas)

        (group? s)
        (recur acc
               (concat (remove nil? (:schemas s)) schemas))

        :default
        (recur (conj acc s)
               schemas)))))


(defn validate [data schemas]
  (let [all-schemas (unpack-groups schemas)
        all-errors (validation/validate-row nil data all-schemas :validate)
        all-warnings (validation/validate-row nil data all-schemas :warn)
        all-notices (validation/validate-row nil data all-schemas :notice)
        missing-required-fields (into #{}
                                      (map :name)
                                      (validation/missing-required-fields data all-schemas))]
    (assoc data
      ::errors all-errors
      ::warnings all-warnings
      ::notices all-notices
      ::missing-required-fields missing-required-fields)))

(defn- modification-time [{first ::first-modification
                           latest ::latest-modification :as data}]
  (assoc data
    ::first-modification (or first (t/now))
    ::latest-modification (t/now)))


(defn field-ui
  "UI for a single form field"
  [{:keys [columns name label type read fmt col-class required?
           component] :as s}
   data update-fn editable? update-form
   modified? errors warnings notices]
  (if (= type :component)
    [:div.component (component {:update-form! #(update-form s)
                                :data data})]
    (if editable?
      [form-fields/field (assoc s
                                :form? true
                                :update! update-fn
                                :error (when (not (empty? errors))
                                         (str/join " " errors))) data]
      [:div.form-control-static
       (if fmt
         (fmt ((or read #(get % name)) data))
         (form-fields/show-value s data))])))

;; Grid column classes for columns spanned
(def col-classes {1 ["col-xs-12" "col-md-4" "col-lg4-"]
                  2 ["col-xs-12" "col-md-6" "col-lg-6"]
                  3 ["col-xs-12" "col-md-12" "col-lg-12"]})

(defn group-ui
  "UI for a group of fields in the form"
  [style schemas data update-fn can-edit? current-focus set-focus!
   modified errors warnings notices update-form]
  [:div.form-group (stylefy/use-style style)
   (doall
    (for [{:keys [name editable? read write] :as s} schemas
          :let [editable? (and can-edit?
                               (or (nil? editable?)
                                   (editable? data)))]]
      ^{:key name}
      [:div.form-field (stylefy/use-sub-style style :form-field)
       [field-ui (assoc s
                                        ;:col-class col-class
                        :focus (= name current-focus)
                        :on-focus #(set-focus! name))
        ((or read name) data)
        #(update-fn (or write name) %)
        editable? update-form
        (get modified name)
        (get errors name)
        (get warnings name)
        (get notices name)]]))])

(defn- with-automatic-labels
  "Add an automatically generated `:label` to fields with the given `:name->label` function.
  If a schema has a manually given label, it is not overwritten."
  [name->label schemas]
  (if-not name->label
    schemas
    (mapv (fn [{:keys [name label type] :as s}]
            (let [schema (assoc s :label (or label
                                             (name->label name)))]
              (if (= type :table)
                ;; Also translate all the table fields for tables
                (update schema :table-fields (partial with-automatic-labels name->label))
                schema)))
          schemas)))

(defn- toggle [set value]
  (let [set (or set #{})]
    (if (set value)
      (disj set value)
      (conj set value))))

(defn form
  "Generic form component that takes `options`, a vector of field `schemas` and the
  current state of the form `data`.

  Supported options:

  :update!      Function to call when the form data changes
  :footer-fn    Optional function to create a footer component that is shown under the form.
                Receives the current form state with validation added as parameter.
  :class        Optional extra CSS classes for the form
  :name->label  Optional function to automatically generate a `:label` for field schemas that
                is based on the `:name` of the field. This is useful to automatically take
                a translation as the label.
  "
  [_ _ _]
  (let [focus (atom nil)]
    ;; FIXME: change layout to material-ui grid when upgrading to material-ui v1
    (fn [{:keys [update! class footer-fn can-edit? label name->label] :as options}
         schemas
         {modified ::muokatut
          :as data}]
      (let [{::keys [errors warnings notices] :as validated-data} (validate data schemas)
            can-edit? (if (some? can-edit?)
                        can-edit?
                        true)
            update-form (fn [new-data]
                          (assert update! (str ":update! missing, options:" (pr-str options)))
                          (-> new-data
                              modification-time
                              (validate schemas)
                              (assoc ::modified (conj (or (::modified new-data) #{}) name))
                              update!))
            update-field-fn (fn [name-or-write value]
                              (let [new-data (if (keyword? name-or-write)
                                               (assoc data name-or-write value)
                                               (name-or-write data value))]
                                (update-form new-data)))
            closed-groups (::closed-groups data #{})]
        [:div.form
         {:class class}
         (when label
           [:h3.form-label label])
         (doall
          (map-indexed
           (fn [i {:keys [label schemas options] :as group}]
             (let [columns (or (:columns options) 1)
                   classes (get col-classes columns)
                   schemas (with-automatic-labels name->label schemas)
                   style (if (= :row (:layout options))
                           style-form/form-group-row
                           style-form/form-group-column)
                   group-component [group-ui
                                    style
                                    schemas
                                    validated-data
                                    update-field-fn
                                    can-edit?
                                    @focus
                                    #(reset! focus %)
                                    modified
                                    errors warnings notices
                                    update-form]]
               ^{:key i}
               [:div.form-group-container (stylefy/use-style style-form/form-group-container
                                           {::stylefy/with-classes classes})
                [ui/card {:z-depth 1
                          :expanded (not (closed-groups label))
                          :on-expand-change #(update! (update data ::closed-groups toggle label))}
                 (when label
                   [ui/card-header {:title label
                                    :style {:padding-bottom "0px"}
                                    :title-style {:font-weight "bold"}
                                    :acts-as-expander true
                                    :show-expandable-button true}])
                 [ui/card-text {:style {:padding-top "0px"} :expandable true}
                  group-component]
                 (when-let [actions (:actions options)]
                   [ui/card-actions
                    (r/as-element actions)])]]))
           schemas))

         (when-let [footer (when footer-fn
                             (footer-fn (assoc validated-data
                                               ::schema schemas)))]
           [:div.form-footer.row
            [:div.col-md-12 footer]])]))))
