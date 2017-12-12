(ns ote.views.theme
  (:require [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [ote.ui.debug :as debug]
            [stylefy.core :as stylefy]
            [ote.style.base :as style-base]
            [reagent.core :as r]
            [ote.localization :refer [tr tr-key]]
            [ote.app.controller.front-page :as fp-controller]))

(defn- flash-message-error [e! msg]
  [ui/snackbar {:open (boolean msg)
        :message (or msg "")
        :body-style style-base/error-flash-message-body
        :auto-hide-duration 4000
        :on-request-close #(e! (fp-controller/->ClearFlashMessage))}])

(defn- flash-message [e! msg ]
  [ui/snackbar {:open (boolean msg)
               :message (or msg "")
               :body-style style-base/success-flash-message-body
               :auto-hide-duration 4000
               :on-request-close #(e! (fp-controller/->ClearFlashMessage))}])

(defonce debug-visible? (r/atom false))
(defonce debug-state-toggle-listener
  (do (.addEventListener
       js/window "keypress"
       (fn [e]
         (when (or (and (.-ctrlKey e) (= "d" (.-key e)))
                   (and (.-ctrlKey e) (= "b" (.-key e))))
           (swap! debug-visible? not))))
      true))


(defn- debug-state [app]
  [:span
   (when @debug-visible?
     [:div.row
      [debug/debug app]])])

(defn on-before-unload []
  (let [state (atom {})]
    {:component-will-mount
     #(set! (.-onbeforeunload js/window)
            (fn []
              (let [{:keys [before-unload-message navigation-prompt-open?]} @state]
                ;; Don't show browser's onbeforeunload dialog if internal
                ;; prompt dialog is already open
                (when-not navigation-prompt-open?
                  before-unload-message))))
     :component-will-receive-props
     (fn [_ [_ _ app _]]
       (reset! state
               (select-keys app [:before-unload-message :navigation-prompt-open?])))}))

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
              error-msg :flash-message-error
              query :query
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

                       ;; Main text color
                       :text-color     (color :grey900)
                       }

           :button    {:labelColor "#fff"}
           ;; Change drop down list items selected color
           :menu-item {:selected-text-color (color :blue700)}})}
          [:span

             (when error-msg
               [flash-message-error e! error-msg])
             (when (or msg (:logged_in query))
                   [flash-message e! (or msg (tr [:common-texts :logged-in]))])
             content
             (when navigation-prompt-open?
               [navigation-prompt e! before-unload-message navigation-confirm])
             [debug-state app]]])})))
