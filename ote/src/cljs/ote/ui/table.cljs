(ns ote.ui.table
  "Simple Material UI data table"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.common :as common]
            [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]))

(def tooltip-icon
  "A tooltip icon that shows balloon.css tooltip on hover."
  (let [wrapped (common/tooltip-wrapper ic/action-help {:style {:margin-left 8}})]
    (fn [opts]
      [wrapped {:style {:width 16 :height 16
                        :vertical-align "middle"
                        :color "gray"}}
       opts])))

(defn table [{:keys [height name->label key-fn
                     label-style
                     row-style show-row-hover?
                     on-select row-selected? no-rows-message class] :as opts} headers rows]
  [ui/table (merge
              {:wrapperStyle {:overflow "visible"}
               ;; FIXME: When we have tooltips in header labels, body does not need overflow: visible.
               ;;        But, if any row includes tooltips, the body must also have visible overflow.
               ;;        For now, leaving this commented out.
               ;:bodyStyle {:overflow "visible"}
               }
              (when on-select
                {:on-row-selection (fn [selected-rows]
                                     (let [rows (vec rows)]
                                       (on-select (map (partial nth rows) selected-rows))))})
              (when height
                {:height height
                 :fixed-header true})
              (when class
                {:class class}))
   [ui/table-header {:style {:overflow "visible"}
                     :adjust-for-checkbox false :display-select-all false}
    [ui/table-row {:style {:overflow "visible"}
                   :selectable false}
     (doall
       (for [{:keys [name width tooltip tooltip-pos tooltip-len]} headers]
         ^{:key (str name)}
         [ui/table-header-column {:style
                                  (merge
                                    (when width
                                      {:width width})
                                    {:white-space "pre-wrap" :overflow "visible"}
                                    label-style)}
          (name->label name)
          (when tooltip
            [tooltip-icon {:text tooltip :pos tooltip-pos :len tooltip-len}])]))]]
   [ui/table-body {:display-row-checkbox false
                   :show-row-hover (boolean show-row-hover?)}
    (if (empty? rows)
      [:tr [:td {:colSpan (count headers)}
            [common/help no-rows-message]]]
      (doall
        (map-indexed
          (fn [i row]
            ^{:key (if key-fn (key-fn row) i)}
            [ui/table-row (merge
                            {:selectable (boolean on-select)
                             :selected (if row-selected? (row-selected? row) false)
                             :display-border false}
                            (when row-style
                              {:style row-style}))
             (doall
               (for [{:keys [name width col-style format read]} headers
                     :let [value ((or format identity) (if read (read row) (get row name)))]]
                 ^{:key (str name)}
                 [ui/table-row-column {:style (merge
                                                {:white-space "pre-wrap" :overflow "visible"}
                                                (when width {:width width})
                                                (when col-style col-style))}
                  (cond
                    (vector? value)
                    (r/as-element value)

                    (string? value)
                    value

                    :else (str value))]))])
          rows)))]])
