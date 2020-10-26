(ns ote.views.transit-visualization.gtfs-package-info
  "Visualization of transit data (GTFS)."
  (:require [reagent.core :as r]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as ui]
            [clojure.string :as str]
            [ote.time :as time]
            [stylefy.core :as stylefy]
            [ote.app.routes :as routes]
            [ote.localization :refer [tr]]
            [ote.ui.table :as table]
            [ote.ui.common :as common]
            [ote.style.base :as style-base]
            [ote.style.transit-changes :as style]
            [ote.style.dialog :as style-dialog]
            [ote.app.controller.transit-visualization :as tv]))

(defn gtfs-package-info [e! open-sections transit-visualization service-id]
  (let [packages (:gtfs-package-info transit-visualization)
        transit-changes (:transit-changes transit-visualization)
        selected-route (get-in transit-visualization [:selected-route :route-hash-id])
        show-transit-changes-modal? (:show-transit-changes-modal? transit-visualization)
        grouped-packages (group-by :interface-url packages)
        group-keys (keys grouped-packages)
        latest-packages (mapv
                           (fn [k]
                             (first (get grouped-packages k)))
                           group-keys)
        previous-packages (apply concat
                                 (mapv (fn [k]
                                         (when-not (empty? (rest (get grouped-packages k)))
                                           (rest (get grouped-packages k))))
                                       group-keys))
        open? (get open-sections :gtfs-package-info false)
        pkg (fn [{:keys [created min-date max-date interface-url download-status]} show-link?]
              (when created
                [:div.gtfs-package (stylefy/use-style (style-base/flex-container "row"))
                 [:span interface-url " Ladattu NAPiin "
                  (if show-link?
                    (common/linkify
                      (str "/#/transit-visualization/" service-id "/" (time/format-date-iso-8601 created) "/all/")
                      (str (time/format-timestamp-for-ui created)))
                    (str (time/format-timestamp-for-ui created)))] ". "
                 "Sisältää tietoa liikennöinnistä ajanjaksolle  " min-date " - " max-date "."
                 (case download-status
                   "success" [:div {:style {:flex "1"}} " "]
                   "failure" [:div {:title "Aineiston tiedoissa virheitä. Aineistoa ei voitu ladata NAP:iin."
                                    :style {:flex "1"}}
                              [ic/alert-warning {:style style/gtfs-package-info-icons}]]
                   nil " "
                   :default " ")]))]

    [:div
     (when show-transit-changes-modal?
       [ui/dialog
        {:open true
         :actionsContainerStyle style-dialog/dialog-action-container
         :title "Viimeisimmät tunnistukset (max 50kpl)"
         :autoScrollBodyContent true
         :actions [(r/as-element
                     [ui/flat-button
                      {:label (tr [:buttons :close])
                       :primary true
                       :on-click (fn [^SyntheticMouseEvent event]
                                   (.preventDefault event)
                                   (e! (tv/->ToggleTransitChangesModal)))}])]}

        [:div
         [table/html-table
          (vector "Tunnistus ajettu" "Uusia reittejä" "Poistuvia reittejä" "Muuttuvia reittejä" "Tauollisia reittejä" "Käytetyt gtfs paketit")
          (mapv
            (fn [c]
              (let [package-info-rows (when-not (nil? (:package-info c))
                                        (str/split (:package-info c) ";"))
                    package-infos (when-not (nil? package-info-rows)
                                    (map
                                      #(str/split % #",")
                                      package-info-rows))
                    info-links (when-not (nil? package-info-rows)
                                 (map (fn [row]
                                        (let [link-key (str (:date c) "-" (first row))]
                                          ^{:key (str "link-key-" link-key)}
                                          [:a {:id link-key
                                               :href "#"
                                               :on-click (fn [event]
                                                           (do
                                                             (.preventDefault event)
                                                             (.stopPropagation event)
                                                             (e! (tv/->OpenPackageInfoPackageIdLink link-key))))}
                                           (if (= (:package-id-link transit-visualization) link-key)
                                             [:div
                                              "Id " (first row) [:br]
                                              "Paketti ladattu: " (second row) [:br]
                                              "Paketin url: " (nth row 2)]
                                             (str (first row) " "))]))
                                      package-infos))]

                {:on-click #(routes/navigate! :transit-visualization
                                              {:date (time/format-date-iso-8601 (:date c))
                                               :scope "all"
                                               :service-id service-id
                                               :route-hash-id selected-route})
                 :data (vector
                         (time/format-date (:date c))
                         (if-not (nil? (:added-routes c)) (:added-routes c) "")
                         (if-not (nil? (:removed-routes c)) (:removed-routes c) "")
                         (if-not (nil? (:changed-routes c)) (:changed-routes c) "")
                         (if-not (nil? (:no-traffic-routes c)) (:no-traffic-routes c) "")
                         (if-not (nil? info-links) info-links ""))}))
            transit-changes)]]])

     [:div (stylefy/use-style style/infobox)
      [:div (stylefy/use-style style/infobox-text)
       [:div
        [:b "Viimeisin aineisto"]
        [:span {:style {:float "right"}} [:a {:href "#"
                                              :on-click (fn [^SyntheticMouseEvent event]
                                                          (.preventDefault event)
                                                          (e! (tv/->ToggleTransitChangesModal)))}
                                          [ic/image-compare]]]]
       (doall
         (for [p latest-packages]
           ^{:key (str "latest-package-id-" (:id p))}
           [pkg p true]))]
      (when (seq previous-packages)
        [:div
         [common/linkify "#" "Näytä tiedot myös aiemmista aineistoista"
          {:icon (if open?
                   [ic/navigation-expand-less]
                   [ic/navigation-expand-more])
           :on-click (fn [^SyntheticMouseEvent event]
                       (.preventDefault event)
                       (e! (tv/->ToggleSection :gtfs-package-info)))
           :style style/infobox-more-link}]
         (when open?
           [:div
            (doall
              (for [{id :id :as p} previous-packages]
                ^{:key (str "gtfs-package-info-" id)}
                [pkg p true]))])])]]))