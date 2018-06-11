(ns ote.ui.table
  "Simple Material UI data table"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.common :as common]
            [reagent.core :as r]))

(defn table [{:keys [height name->label key-fn
                     row-style show-row-hover?
                     on-select row-selected? no-rows-message] :as opts} headers rows]
  [ui/table (merge
             (when on-select
               {:on-row-selection (fn [selected-rows]
                                    (let [rows (vec rows)]
                                      (on-select (map (partial nth rows) selected-rows))))})
             (when height
               {:height height
                :fixed-header true}))
   [ui/table-header {:adjust-for-checkbox false :display-select-all false}
    [ui/table-row {:selectable false}
     (doall
      (for [{:keys [name width]} headers]
        ^{:key (str name)}
        [ui/table-header-column (when width
                                  {:style {:width width}})
         (name->label name)]))]]
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
              [ui/table-row-column {:style (merge (when width {:width width})
                                                  (when col-style col-style))}
               (cond
                 (vector? value)
                 (r/as-element value)

                 (string? value)
                 value

                 :else (str value))]))])
        rows)))]])
