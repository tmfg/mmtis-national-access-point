(ns ote.ui.mui-chip-input
  (:require material-ui-chip-input
            [cljs-react-material-ui.reagent :as ui]
            [reagent.core :as r]))

;; TODO: Todo implement custom cljs version of mui chip-input component mirroring the amazing features of the component.

;; FIXME: Chips are always placed above input field in form group. Suspecting that flexbox set in the form-group is affecting this somehow.
;;        Correct behaviour is: place chips inside the input field until there is no horizontal space left, after that create a new line.
(def default-props {:ref "chip-input"
                    :floating-label-fixed true

                    ;; == Autocomplete options ==
                    :filter (or filter (aget js/MaterialUI "AutoComplete" "caseInsensitiveFilter"))
                    :max-search-results 10
                    :open-on-focus true
                    :clear-on-blur true

                    ;; == Chip options ==
                    :allow-duplicates false
                    ; Vector of key-codes for triggering new chip creation, 13 => enter, 32 => space
                    :new-chip-key-codes [13]

                    ;; == Styling ==
                    :full-width false
                    :full-width-input false
                    ; Modify original style
                    ; For some reason, the text input underline is misplaced compared to other original mui-textfields.
                    ;        This change fixes those problems, in combination with conditional chipContainerStyle change.
                    :inputStyle {:margin-top 12
                                 :margin-bottom 14}})

(def chip-input* (r/adapt-react-class (aget js/window "MaterialUIChipInput")))

(defn chip-input [props]
  (let [ref (atom nil)]
    (r/create-class
      {:component-did-mount
       (fn [this]
         (let [c (reset! ref (aget this "refs" "chip-input"))
               handleAddChip* (aget c "handleAddChip")
               auto-select? (:auto-select? props)]
           ;; Autoselect first matching suggestion if :auto-select prop is true after pressing :new-chip-key-code key.
           ;; This will override the normal handleAddChip member function of chip-input with our custom function.
           (aset @ref "handleAddChip"
                 (fn [val]
                   (if auto-select?
                     (let [autocomplete (aget @ref "autoComplete")
                           ;; RequestLists contains filtered suggestions from AutoComplete
                           first-match (first (js->clj (aget autocomplete "requestsList") :keywordize-keys true))
                           suggestions (js->clj (aget @ref "props" "dataSource") :keywordize-keys true)

                           ;; Note: Suggestion can contain map or plain string.
                           ;; RequestsList will be empty if there is only one suggestion. Pick the first suggestion in that case.
                           chip (first (if first-match
                                         (filter #(= (or (:text %) %) (:text first-match)) suggestions)
                                         suggestions))]

                       ;; Call the original ChipInput.handleAddChip function
                       (when chip (.call handleAddChip* c chip)))
                     (.call handleAddChip* c val))))))

       :reagent-render
       (fn [props]
         [chip-input* (merge
                        default-props
                        ; Remove margin if there are no chips (works only in "controlled" case i.e. :value prop is used).
                        (when (empty? (:value props))
                          {:chipContainerStyle {:margin-top 0}})

                        ;; Remove custom prop to prevent Reactunknown prop warning
                        (dissoc props :auto-select?))])})))