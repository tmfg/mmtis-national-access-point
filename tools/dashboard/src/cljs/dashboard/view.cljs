(ns dashboard.view)

(def radiator-item-style
  {:border "solid 1px black"
   :border-radius 5
   :margin 5
   :padding 3})

(defn job-style [result]
  (merge radiator-item-style
         {:width 175 :height 50
          :background (case result
                        "FAILURE" "red"
                        "SUCCESS" "green"
                        "wheat")}))

(defn jenkins-jobs [jobs]
  [:div.jenkins-jobs {:style {:display "flex" :flex-direction "column" :justify-content "center"}}
   [:div "Jenkins build status"]
   (doall
    (for [{:keys [name lastBuild] :as job} jobs]
      [:div {:style (job-style (:result lastBuild))}
       [:div name]
       [:div (.toLocaleString (js/Date. (:timestamp lastBuild)))]]))])

(defn published-services [service-count]
  [:div {:style (merge radiator-item-style {:width 300 :height 150})}
   "Published service count"
   [:div {:style {:font-size "250%"}}
    service-count]])

(defn dashboard-view [e! app]
  [:div.dashboard {:style {:display "flex" :flex-direction "row"}}
   [jenkins-jobs (:jenkins app)]
   [published-services (:published-services app)]
   [:div (pr-str app)]])
