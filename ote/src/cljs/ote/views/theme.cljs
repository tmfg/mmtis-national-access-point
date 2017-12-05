(ns ote.views.theme
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [ote.ui.debug :as debug]
            [ote.style.base :as style-base]
            [reagent.core :as r]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.front-page :as fp-controller]))

(defn- flash-message [msg]
  [ui/snackbar {:open (boolean msg)
                :message (or msg "")
                :style style-base/flash-message
                :auto-hide-duration 5000}])

(defonce debug-state-toggle-listener (atom false))

(defn- debug-state [_]
  (let [visible? (r/atom false)]
    (when-not @debug-state-toggle-listener
      (reset! debug-state-toggle-listener true)
      (.addEventListener
       js/window "keypress"
       (fn [e]
         (when (or (and (.-ctrlKey e) (= "d" (.-key e)))
                   (and (.-ctrlKey e) (= "b" (.-key e))))
           (swap! visible? not)))))
    (fn [app]
      [:span
       (when @visible?
         [:div.row
          [debug/debug app]])])))

(defn on-before-unload []
  (let [before-unload-message (atom nil)]
    {:component-will-mount
     #(set! (.-onbeforeunload js/window)
            (fn []
              @before-unload-message))
     :component-will-receive-props
     (fn [_ [_ _ app _]]
       (reset! before-unload-message (:before-unload-message app)))}))

(defn navigation-prompt [e! msg confirm]
  (let [tr (tr-key [:dialog :navigation-prompt])]
    [ui/dialog {:title (tr :title)
                :modal true
                :open true
                :actions [(r/as-element
                           [ui/flat-button {:label (tr :stay)
                                            :primary true
                                            :on-click #(e! (fp-controller/->StayOnPage))}])
                          (r/as-element
                           [ui/flat-button {:label (tr :leave)
                                            :secondary true
                                            :on-click #(e! confirm)}])]}
     msg]))

(defn theme
  "App container that sets the theme and common elements like flash message."
  [e! app content]
  (r/create-class
   (merge
    (on-before-unload)
    {:reagent-render
     (fn [e! {msg :flash-message
              navigation-prompt-open? :navigation-prompt-open?
              navigation-confirm :navigation-confirm
              before-unload-message :before-unload-message
              :as app} content]
       [ui/mui-theme-provider
        {:mui-theme
         (get-mui-theme
          {:palette   {;; primary nav color - Also Focus color in text fields
                       :primary1-color (color :blue700)

                       ;; Hint color in text fields
                       :disabledColor  (color :grey900)

                       ;; Border color
                       :borderColor (color :grey600)

                       :shadowColor (color :grey900)

                       ;; canvas color
                       ;;:canvas-color  (color :lightBlue50)

                       ;; Main text color
                       :text-color     (color :grey900)
                       }

           :button    {:labelColor "#fff"}
           ;; Change drop down list items selected color
           :menu-item {:selected-text-color (color :blue700)}})}
        [:span

         (when msg
           [flash-message msg])
         content
         (when navigation-prompt-open?
           [navigation-prompt e! before-unload-message navigation-confirm])
         [debug-state app]]])})))
