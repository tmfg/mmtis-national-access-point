(ns ote.ui.select_field
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.style.base :as style-base]
            [reagent.core :as r]))

(defn select-field [{:keys [update! table? label style show-option options
                                error warning auto-width? disabled?
                                option-value class-name ] :as field}]
  ;; Because material-ui selection value can't be an arbitrary JS object, use index
  (let [int-value-atom (r/atom 0)
        int-cb (fn [ix]
                 (reset! int-value-atom ix)
                 (update! (nth options ix)))]
    ;; Wrapper fn required to get reagent re-render element after changes
    (fn []
      [ui/select-field
       (merge
         {:auto-width (boolean auto-width?)
          :style style
          :floating-label-text (when-not table? label)
          :floating-label-fixed true
          :value @int-value-atom
          :on-change #(int-cb %2)
          :error-text (or error warning "")                 ;; Show error text or warning text or empty string
          :error-style (if error                            ;; Error is more critical than required - showing it first
                         style-base/error-element
                         style-base/required-element)}
         (when class-name {:className class-name})
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
           options))])))

