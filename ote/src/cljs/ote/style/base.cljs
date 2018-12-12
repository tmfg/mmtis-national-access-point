(ns ote.style.base
  "Base styles for OTE application. Everything that affects the overall look and feel of the app."
  (:require
    [stylefy.core :as stylefy :refer [use-style use-sub-style]]))

(def body {:margin 0
           :padding 0})

(def mobile-width-px 1000)

(def wrapper {:padding-top "20px"})

(def font {:font-family "'Roboto', sans-serif"})

(def divider {:margin "20px 0px 15px 0px"
              :border-top "1px solid rgb(217, 217, 217)"})

(def inline-block {:display "inline-block"})

(def action-button-container (merge inline-block
                                    {:margin-right "1em"}))

(def icon-small {:width 20 :height 20
                 :vertical-align "middle"})

(def icon-medium {:width 24 :height 24
                  :vertical-align "middle"})

(def icon-large {:width 32 :height 32
                 :vertical-align "middle"})

(def base-button {:padding-left "1.1em"
                  :padding-right "1.1em"
                  :text-transform "uppercase"
                  :color "#FFFFFF"
                  :background-color "#1565C0"
                  :font-size "12px"
                  :font-weight "bold"})

(def delete-button (merge base-button {:background-color "rgb(221,0,0)"}))


(def disabled-button (merge base-button {:background-color "#CCCCCC"}))

(def button-label-style {:font-size "12px"
                         :font-weight "bold"
                         :text-transform "uppercase"
                         :color "#FFFFFF"})
(def button-add-row {:margin "15px 0 20px 0"})

;; Form elements
(def required-element {:color "#B71C1C"
                       :font-size 14
                       :font-weight "600"})                 ;; currently same as error

(def error-element {:color "#B71C1C"
                    :font-weight "600"})

;; Front page

(def front-page-add-service {:border-right "1px solid grey"})
(def front-page-service-table {:a
                               {:text-decoration "none"
                                ::stylefy/mode {:visited {:text-decoration "none"}}}})
(def header-font {:font-size "18px"
                  :padding-top "20px"
                  :font-weight "600"})

(def success-flash-message-body {:background-color "rgba(128, 149, 50, 0.87)"})
(def error-flash-message-body {:max-width "800px" :background-color "rgba(190, 0, 0, 0.87)"})

(def title {:font-weight "bold"})


(defn flex-container [dir]
  {:display "flex" :flex-direction dir})

(defn flex-container2 []
  {:display "flex" :flex-direction "row" :width "100%"})

(def flex-child {:flex 1 })

(def item-list-container
  (merge (flex-container "row")
         {:flex-wrap "wrap"}))

(def item-list-row-margin
  {:margin-right "1em"})

(def item-list-item
  (merge inline-block
         {:position "relative"
          :margin-left "0.2em"}))

(def help {:border-radius "0.2em"
           :color "#666666"
           :background-color "#DAEDF7"
           :padding "10px"
           :margin-bottom "10px"
           :align-items "center"})

;; Full width generic help box
(def generic-help (merge help
                         {:background-color "#F5F5F5" :padding "15px"
                          :margin-left "-15px"
                          :margin-right "-15px"
                          :margin-top "-15px"}))

(def help-link-container {:padding "10px 0px 0px 0px"})
(def link-icon-container {:float "left" :padding-right "10px"})
(def link-icon {:color "#06c" :height 18 :width 18})

(def link-color "#2D75B4")

(def filters-form
  {:border "solid 1px #0046ad"})

(def language-selection-dropdown
  {:border-top "solid 1px white"
   :font-size "12px"
   :margin-top "5px"
   :color "#fff"
   :padding-top "5px"
   :text-align "center"})

(def language-selection-footer
  {:border-top "solid 1px white"
   :width "100%"
   :color "#fff"
   :display "inline-block"
   :margin-top "5px"
   :padding-top "5px"})

(def language-flag
  {:padding-right "10px"})

(def section-margin {:margin-top "1em"})

(def placeholder {:color "#a0a0a0"})

(def footer-copyright {:text-align "center"
                       :margin-top "24px"
                       ;; Decrement footer bottom padding from logo height
                       :margin-bottom (str (- 48 20) "px")
                       ::stylefy/sub-styles
                       {:logo {:height "48px"
                               :width "48px"
                               :margin-bottom "8px"}}})

(def disabled-color {:color "rgba(0, 0, 0, 0.247059)"})
(def checkbox-label {:float "left"
                     :position "relative"
                     :display "block"
                     :width "calc(100% - 38px)"
                     :line-height "24p";
                     :font-family "Roboto, sans-serif"})
(def checkbox-label-with-width (assoc checkbox-label :width "260px"))

(def mobile-extra-padding {::stylefy/media {{:max-width (str mobile-width-px "px")} {:padding-top "20px"}}})
