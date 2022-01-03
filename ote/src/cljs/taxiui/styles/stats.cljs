(ns taxiui.styles.stats
  (:require [ote.theme.colors :as colors]
            [stylefy.core :as stylefy]
            [taxiui.theme :as theme]))

(def table-row {:height "2.5em"
                ::stylefy/mode {":nth-child(odd)" {:background-color colors/faint-gray}}})

(def table-cell {::stylefy/mode {:first-child {:padding-left  ".75em"}
                                 :last-child  {:padding-right ".75em"}}})

(def table-headers {:height "2.5em"})

(def table-header {:text-align "left"
                   :vertical-align "middle"
                   ::stylefy/mode {:first-child {:padding-left  ".75em"}
                                   :last-child  {:padding-right ".75em"}}})

(def table-header-title
  "This is worth an explanation; to vertically align items in a <th>...
   - The parent tr cannot be set to flex, as that causes invalid flow; either headers are listed vertically or without
     autoadjusting to table cell widths
   - individual th elements cannot be set to flex, see above
   - individual th elements cannot be set to inline-flex, also see above
   - adding a wrapping span which has inline-flex will position all elements vertically by default without losing the
   cell spacing"
  {:display     "inline-flex"
   :align-items "center"})

(def table-header-sorts {:display        "flex"
                         :flex-direction "column"})