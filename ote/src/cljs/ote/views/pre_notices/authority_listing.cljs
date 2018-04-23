(ns ote.views.pre-notices.authority-listing
  "Listing of pre notices for transit authority (city, ELY center) users"
  (:require [reagent.core :as r]
            [ote.db.transit :as transit]
            [ote.localization :refer [tr tr-key]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [ote.app.controller.pre-notices :as pre-notice]
            [ote.ui.table :as table]
            [clojure.string :as str]
            [ote.time :as time]
            [ote.db.modification :as modification]
            [ote.db.transport-operator :as t-operator]
            [ote.db.user :as user]
            [ote.ui.common :as common]
            [stylefy.core :as stylefy]
            [ote.style.pre-notice :as styles]
            [ote.ui.form-fields :as form-fields]))

(defn format-notice-types [types]
  (str/join ", " (map (tr-key [:enums ::transit/pre-notice-type]) types)))

(defn comment-list [comments]
  [:div
   (doall
    (for [{::transit/keys [id author comment]
           timestamp ::modification/created} comments]
      ^{:key id}
      [:div.comment (stylefy/use-style styles/comment-style)
       [common/gravatar {:size 32 :default "mm"} (::user/email author)]
       (time/format-timestamp-for-ui timestamp) " "
       [common/tooltip {:text (::user/email author)
                        :len "long"}
        [:span (::user/fullname author)]] ": "
       comment]))])

(defn- format-effective-dates [effective-dates]
  [:div
   (map-indexed
    (fn [i {::transit/keys [effective-date effective-date-description]}]
      [:div (when effective-date (time/format-date effective-date)) " " effective-date-description])
    effective-dates)])

(defn- attachment-list [attachments]
  [:div
   (for [{::transit/keys [id attachment-file-name]} attachments]
     ^{:key id}
     [:div [common/linkify
            (str "pre-notice/attachment/" id) attachment-file-name
            {:target "_blank"}]])])

(defn pre-notice-view [e! pre-notice]
  (let [tr* (tr-key [:field-labels :pre-notice]
                    [:pre-notice-list-page :headers])]
    [ui/dialog
     {:open true
      :modal true
      :auto-scroll-body-content true
      :title   (tr [:pre-notice-list-page :pre-notice-dialog :label])
      :actions [(r/as-element
                 [ui/flat-button
                  {:label     (tr [:buttons :close])
                   :secondary true
                   :primary   true
                   :on-click  #(e! (pre-notice/->ClosePreNotice))}])]}

     [:div.pre-notice-dialog
      (into [common/table2]
            (mapcat
             (fn [[key fmt]]
               [[:b (str (tr* key) ": ")] (fmt (get pre-notice key))])
             [[::modification/created time/format-timestamp-for-ui]
              [::t-operator/transport-operator ::t-operator/name]
              [::transit/pre-notice-type format-notice-types]
              [::transit/route-description str]
              [:region-names str]
              [::transit/effective-dates format-effective-dates]
              [::transit/url str]
              [::transit/attachments attachment-list]]))
      [:div.pre-notice-comments (stylefy/use-style styles/comment-container)
       [:h3 (tr [:pre-notice-list-page :pre-notice-dialog :comments-label])]
       [comment-list (::transit/comments pre-notice)]
       [form-fields/field {:type :string
                           :input-style {:height 50}
                           :update! #(e! (pre-notice/->UpdateNewCommentText %))
                           :label (tr [:pre-notice-list-page :pre-notice-dialog :new-comment-label])
                           :on-enter #(e! (pre-notice/->AddComment))}
        (:new-comment pre-notice)]]]]))

(defn pre-notices-listing [e! pre-notices]
  (if (= :loading pre-notices)
    [:div.loading [:img {:src "/base/images/loading-spinner.gif"}]]
    [:div
     [:div.row
      [:div.col-xs-12.col-sm-12.col-md-12
       [:h1 (tr [:pre-notice-list-page :header-authority-pre-notice-list])]]]
     [:div.row

      [table/table {:name->label     (tr-key [:pre-notice-list-page :headers])
                    :key-fn          ::transit/id
                    :no-rows-message (tr [:pre-notice-list-page :no-pre-notices-for-operator])
                    :on-select #(e! (pre-notice/->ShowPreNotice (::transit/id (first %))))}
       [{:name ::modification/created :format (comp str time/format-timestamp-for-ui)}
        {:name ::transit/pre-notice-type
         :format format-notice-types}
        {:name ::transit/route-description}
        {:name ::t-operator/transport-operator :read (comp ::t-operator/name ::t-operator/transport-operator)}]

       pre-notices]]]))

(defn pre-notices [e! {:keys [pre-notices pre-notice-dialog] :as app}]
  [:div.authority-pre-notices
   [pre-notices-listing e! pre-notices]
   (when pre-notice-dialog
     [pre-notice-view e! pre-notice-dialog])])
