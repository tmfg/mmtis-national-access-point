(ns ote.ui.mui-chip-input
  (:require material-ui-chip-input
            [cljs-react-material-ui.reagent :as ui]
            [reagent.core :as r]))

;; FIXME: Chips are always placed above input field in form group. Suspecting that flexbox set in the form-group is affecting this somehow.
;;        Correct behaviour is: place chips inside the input field until there is no horizontal space left, after that create a new line.
(def chip-input
  (let [component (r/adapt-react-class (aget js/window "MaterialUIChipInput"))
        default-props {:floating-label-fixed true

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
                       ; FIXME: For some reason, the text input underline is misplaced compared to other original mui-textfields.
                       ;        This change fixes those problems, in combination with conditional chipContainerStyle change.
                       :inputStyle {:margin-top 12
                                    :margin-bottom 14}}]
    (fn [props] [component (r/merge-props
                             (merge
                               default-props
                               ; Remove margin if there are no chips (works only in "controlled" case i.e. :value prop is used).
                               (when (empty? (:value props))
                                 {:chipContainerStyle {:margin-top 0}}))
                             props)])))