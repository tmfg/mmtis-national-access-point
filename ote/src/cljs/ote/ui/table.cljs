(ns ote.ui.table
  "Simple Material UI data table"
  (:require [cljs-react-material-ui.reagent :as ui]
            [ote.ui.common :as common]))

(defn table [{:keys [height name->label key-fn
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
   [ui/table-body {:display-row-checkbox false}
    (if (empty? rows)
      [:tr [:td {:colSpan (count headers)}
            [common/help no-rows-message]]]
      (doall
       (map-indexed
        (fn [i row]
          ^{:key (if key-fn (key-fn row) i)}
          [ui/table-row {:selectable (boolean on-select)
                         :selected (if row-selected? (row-selected? row) false)
                         :display-border false}
           (doall
            (for [{:keys [name width format read]} headers
                  :let [value (if read (read row) (get row name))]]
              ^{:key (str name)}
              [ui/table-row-column (when width {:style {:width width}})
               (if format (format value) value)]))])
        rows)))]])
