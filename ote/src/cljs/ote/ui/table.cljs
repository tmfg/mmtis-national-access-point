(ns ote.ui.table
  "Simple Material UI data table and more simple html table"
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [color]]
            [ote.ui.common :as common]
            [ote.style.table :as table-style]
            [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [ote.theme.colors :as colors]
            [ote.theme.transitions :as transitions]
            [stylefy.core :as stylefy]))

(def tooltip-icon
  "A tooltip icon that shows balloon.css tooltip on hover."
  (let [wrapped (common/tooltip-wrapper ic/action-help {:style {:margin-left "8px"}})]
    (fn [opts]
      [wrapped {:style {:width "16px"
                        :height "16px"
                        :vertical-align "middle"
                        :color "gray"}}
       opts])))

(defn table [{:keys [table-name height name->label key-fn label-style row-style
                     on-select row-selected? no-rows-message class no-outline-on-selection?] :as opts} headers rows]
  (let [table-id (or table-name (str (rand-int 9999)))
        table-row-color "#FFFFFF"
        table-row-color-alt colors/gray200
        table-row-hover-color colors/gray400]
    [ui/table (merge
                {:wrapperStyle {:overflow "visible"}
                 :body-style {:padding "3px"}
                 ;; FIXME: When we have tooltips in header labels, body does not need overflow: visible.
                 ;;        But, if any row includes tooltips, the body must also have visible overflow.
                 ;;        For now, leaving this commented out.
                 ;:bodyStyle {:overflow "visible"}
                 }
                (when no-outline-on-selection?
                  {:body-style {:padding 0}})
                (when on-select
                  {:on-row-selection (fn [selected-rows]
                                       (when (seq selected-rows)
                                         (let [rows (vec rows)]
                                           (on-select (map (partial nth rows) selected-rows)))))})
                (when height
                  {:height height
                   :fixed-header true})
                (when class
                  {:class class}))
     [ui/table-header {:key (str table-id "-hdr")
                       :style {:overflow "visible"
                               :background-color (color :grey300)
                               :border-bottom (str "1px solid " colors/gray650 " !important")}
                       :adjust-for-checkbox false
                       :display-select-all false}
      [ui/table-row {:key (str table-id "-hdr-row")         ;; When adding more rows remember to add their id to key
                     :style {:overflow "visible"
                             :border-bottom (str "1px solid " colors/gray650 " !important")}
                     :selectable false}
       (doall
         (map-indexed
           (fn [i {:keys [name width tooltip tooltip-pos tooltip-len]}]
             ;; Note, add i to key if name is not enough, for now avoiding index because it's not optimal for react rendering
             ^{:key (str table-id "-hdr-col-" name)}
             [ui/table-header-column {:style
                                      (merge
                                        (when width
                                          {:width width})
                                        {:white-space "pre-line"
                                         :overflow "visible"
                                         :color (color :grey900)
                                         :font-size "1em"
                                         :font-weight "bold"}
                                        label-style)}
              (name->label name)
              (when tooltip
                [tooltip-icon {:text tooltip :pos tooltip-pos :len tooltip-len}])])
           headers))]]
     [ui/table-body {:display-row-checkbox false}
      (if (empty? rows)
        [:tr [:td {:colSpan (count headers)}
              [:div (stylefy/use-style table-style/no-rows-message) no-rows-message]]]
        (doall
          (map-indexed
            (fn [i row]
              ^{:key (str table-id "-" i "-b-row-" (if (and key-fn (key-fn row))
                                               (key-fn row)
                                               row))}
              [ui/table-row (merge
                              (stylefy/use-style (merge {:background-color (if (even? i)
                                                                             table-row-color
                                                                             table-row-color-alt)} ; Add stripes
                                                        {:border-bottom (str "1px solid " colors/gray650 " !important")}
                                                        (when on-select
                                                          {:cursor "pointer"
                                                           :transition (str "background-color " transitions/fast-ease-in-out)
                                                           ::stylefy/mode {:hover {:background-color table-row-hover-color}}})
                                                        (when (if row-selected? (row-selected? row) false)
                                                          {:outline (str "solid 3px " colors/primary-dark)
                                                           ::stylefy/mode {:hover {:background-color (if (even? i)
                                                                                                       table-row-color
                                                                                                       table-row-color-alt)}}})
                                                        (when row-style row-style)))
                              {:selectable (boolean on-select)})

               (doall
                 (map-indexed
                   (fn [i {:keys [name width col-style format read]}]
                     (let [value ((or format identity) (if read (read row) (get row name)))]
                       ;; Note: index i not added to key, because assuming one row won't have duplicate value-name pairs
                       ;; Assumption ok when each row is in a separate key namespace
                       ^{:key (str table-id "-b-row-col-" value "-" name)}
                       [ui/table-row-column {:style (merge
                                                      {:white-space "pre-line"
                                                       :overflow "visible"}
                                                      (when width {:width width})
                                                      (when col-style col-style))}
                        (cond
                          (vector? value)
                          (r/as-element value)

                          (string? value)
                          value

                          :else (str value))]))
                   headers))])
            rows)))]]))

(defonce selected-table-row (r/atom 0))
(defn html-table
  "Headers are a simple vector [text,text,text].
  Add rows as a mapv e.g. [{:on-click xxx :data},{:on-click xxx :data}]
  Available options are currently related only to fixed header: #{:max-height-px :fixed-header :overflow}"
  [headers rows options]
  (let [row-index (r/atom 0)]
    [:div {:style
           (merge
             (when (:max-height-px options) {:max-height (str (:max-height-px options) "px")}))}
     ;; Create separate table for sticky headers
     (when (:fixed-header options)
       [:table.nap-table
        [:thead
         [:tr
          (doall
            (for [h headers
                  :let [width (when (map? h)
                                (:width h))
                        text (if (string? h)
                               h
                               (:text h))]]
              ^{:key (str "html-table-header" h)}
              [:th (when width {:style {:width width}}) text]))]]])
     ;; When sticky headers are not needed, create only one table
     [:div {:style
            {:max-height (str (- (:max-height-px options) 58) "px")
             :overflow (:overflow options)}}

      [:table.nap-table (when (:overflow options) {:style
                                                   {:max-height (:max-height options)
                                                    :overflow (:overflow options)}})
       (when-not (:fixed-header options)
         [:thead
          [:tr
           (doall
             (for [h headers
                   :let [width (when (map? h)
                                 (:width h))
                         text (if (string? h)
                                h
                                (:text h))]]
               ^{:key (str "html-table-header" text)}
               [:th (when width {:style {:width width}}) text]))]])
       [:tbody
        (doall
          (for [row rows
                :let [data (:data row)
                      on-click (:on-click row)
                      _ (swap! row-index inc)
                      column-inx (r/atom 0)]]
            ^{:key (str @row-index "-html-table-row" data)}
            [:tr
             {:style (merge {:color "yellow"} (when
                                                (= (str @selected-table-row) (str @row-index))
                                                {:outline (str "solid 3px " colors/primary-dark)
                                                 :outline-offset "-3px"}))
              :data-value @row-index
              :data @row-index
              :on-click (fn [event]
                          (let [new-index (.getAttribute (-> event .-currentTarget) "data-value")]
                            (reset! selected-table-row new-index)
                            (on-click)))}
             (doall
               (for [column data
                     :let [_ (swap! column-inx inc)
                           width (when (map? column)
                                   (:width column))
                           text (if (map? column)
                                  (:text column)
                                  column)]]
                 ^{:key (str @column-inx "-row-column-" text)}
                 [:td (when width {:style {:width width}}) text]))]))]]]]))
