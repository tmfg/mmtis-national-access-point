(ns ote.style.transit-changes
  "Transit changes styling"
  (:require [stylefy.core :as stylefy]
            [ote.theme.screen-sizes :refer [width-xxs width-xs width-sm width-md width-l width-xl]]))

(def add-color "rgb(0,170,0)")
(def remove-color "rgb(221,0,0)")
(def no-change-color "rgb(235,235,235)")

(def transit-changes-legend
  {:margin-top "20px"
   :display "block"
   :padding "1em"
   :background-color "rgb(235,235,235)"})

(def transit-changes-legend-icon
  {:display "inline-block"
   :margin-right "1rem"})

(def change-icon-value
  {:display "inline-block"
   :position "relative"
   :top "-0.5rem"
   :left "0.2rem"})

(def date1-highlight-color "rgba(53,140,217,1)")
(def date1-highlight-color-hover "rgba(53,140,217,0.5)")
(def date2-highlight-color "rgba(219,25,169,1)")
(def date2-highlight-color-hover "rgba(219,25,169,0.5)")

(defn date1-highlight-style
  ([]
   (date1-highlight-style "rgba(0,0,0,0)"))
  ([hash-color]
   (date1-highlight-style hash-color date1-highlight-color))
  ([hash-color highlight-color]
   {:background (str "radial-gradient(circle at center, " highlight-color " 50%, " (or hash-color "#FFF") " 40%) 0px 0px")
    :color "#E1E1F9"}))

(defn date2-highlight-style
  ([]
   (date2-highlight-style "rgba(0,0,0,0)"))
  ([hash-color]
   (date2-highlight-style hash-color date2-highlight-color))
  ([hash-color highlight-color]
   {:background (str "radial-gradient(circle at center, " highlight-color " 50%, " (or hash-color "#FFF") " 40%) 0px 0px")
    :color "#F6C6EA"}))

(def section
  {:border "solid 1px #646464"
   :padding-bottom "1.25rem"
   :margin-bottom "2.5rem"})

(def section-closed
  (dissoc section :padding-bottom))

(def section-title
  {:background-color "#646464"
   :color "white"
   :font-size "1.125rem"
   :font-family "Montserrat"
   :padding "0.875rem 0.875rem 0.875rem 0.625rem"
   :line-height 2
   :font-weight "600"})

(def infobox {:background-color "#FAFAFA"
              :border "solid 1px #e1e1e1"
              :margin-bottom "1.25rem"
              :padding "0.7rem 0.875rem 0.75rem"
              :font-size "0.875rem"
              :font-family "Roboto, sans-serif"
              :color "#505050"})

(def infobox-text {:margin-bottom "0.25rem"
                   :line-height "1.5em"})

(def infobox-more-link {:color "#06c"
                        :font-weight 400
                        :text-decoration "none"
                        :display "flex"
                        :flex-direction "row"
                        :align-items "flex-start"
                        :justify-content "flex-start"})

(def section-header
  {:padding "1rem"
   :background-color "#F0F0F0"
   :line-height 1.5})

(def section-body
  {:padding-left "1.25rem"
   :padding-right "1.25rem"})

(def map-checkbox-container {:margin "1.25rem 0"
                             :display "flex"
                             :flex-direction "row-reverse"
                             :flex-wrap "nowrap"
                             :justify-content "space-between"
                             ::stylefy/media {{:max-width (str width-xs "px")} {:flex-direction "column"}}})
