(ns ote.views.admin.company-csv
  "CSV files that are uploaded by users."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [stylefy.core :as stylefy]
            [ote.db.transport-service :as t-service]
            [ote.db.modification :as modification]
            [ote.localization :refer [tr tr-key]]
            [ote.time :as time]
            [ote.style.base :as style-base]
            [ote.ui.common :refer [linkify]]
            [ote.app.controller.admin :as admin]))

(defn page-controls [e! app]
  [:div])

(defn company-csv-list [e! app]
  (r/create-class
    {:component-will-mount #(e! (admin/->FetchCompanyCsvs))
     :reagent-render
     (fn [e! app-state]
       (let [csv-files (get-in app [:admin :company-csv :results])
             loading? (get-in app [:admin :company-csv :loading?])]
         [:div.row
          (when loading?
            [:p "Ladataan csv tiedostoja..."])
          (when csv-files
           [:span
            [ui/table {:selectable false}
             [ui/table-header {:class "table-header-wrap"
                               :adjust-for-checkbox false
                               :display-select-all false}

              [ui/table-row
               [ui/table-header-column {:class "table-header-wrap" :style {:width "20%"}} "S3 tiedostoavain"]
               [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Tiedoston nimi"]
               [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Validointivaroitukset"]
               [ui/table-header-column {:class "table-header-wrap" :style {:width "15%"}} "Toimivat rivit"]
               [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Hajonneet rivit"]
               [ui/table-header-column {:class "table-header-wrap" :style {:width "10%"}} "Ladattu"]]]
             [ui/table-body {:display-row-checkbox false}
              (doall
                (for [{::t-service/keys [id file-key csv-file-name validation-warning]
                       ::modification/keys [created] :as r} csv-files]
                  ^{:key id}
                  [ui/table-row {:selectable false}
                   [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "20%"})
                    file-key]
                   [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                    csv-file-name]
                   [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                    validation-warning]
                   [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "15%"})
                    "xx"]
                   [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                    "yy"]
                   [ui/table-row-column (merge (stylefy/use-style style-base/table-col-style-wrap) {:width "10%"})
                    (time/format-timestamp-for-ui created)]]))]]])]))}))
