




(defn header [title add-button operator-selection]

  [:div.row
   [:div.col-xs-12.col-sm-6.col-md-9
    [:h1 (tr [:pre-notice-list-page :header-pre-notice-list])]]
   [:div.col-xs-12.col-sm-6.col-md-3
    [ui/raised-button {:label (tr [:buttons :add-new-pre-notice])
                       :style {:float "right"}
                       :on-click #(do
                                    (.preventDefault %)
                                    (e! (pre-notice/->CreateNewPreNotice)))
                       :primary true
                       :icon (ic/content-add)}]]]

  )