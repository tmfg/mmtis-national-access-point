(ns ote.style.service-viewer
  "Styles for service viewer (GeoJSON view)"
  (:require [stylefy.core :as stylefy]
            [ote.theme.screen-sizes :refer [width-xxs width-xs width-sm width-md width-l width-xl]]))

(def properties-table {:text-align "left"})

(def striped-even {:background-color "#f2f2f2"})
(def striped-odd {:background-color "white"})

(def striped-styles [striped-even striped-odd])

(def th {:vertical-align "top"
         :padding-top "0.2em"
         :padding-right "1.5em"})

(def value {:padding-right "0.5em"})

(def info-container {:display "flex"
                     ::stylefy/media {{:max-width (str width-xs "px")}
                                      {:flex-direction "column"}}
                     ::stylefy/sub-styles {:left-block {:flex 1
                                                        :margin-right "0.5rem"}
                                           :middle-block {:flex 1
                                                          :margin "0 0.5rem"}
                                           :right-block {:flex 1
                                                         :margin-left "0.5rem"
                                                         ::stylefy/media {{:max-width (str width-xs "px")}
                                                                          {:padding-top "1rem"
                                                                           :margin-left 0}}}}})

(def info-row
  {:display "flex"
   :flex-direction "row"
   ::stylefy/media {{:max-width (str width-xs "px")}
                    {:flex-direction "column"
                     :padding-top "1rem"}}})

(def info-seqment
  {::stylefy/sub-styles {:left {:margin-right "0.5rem"
                                :flex 1
                                ::stylefy/media {{:max-width (str width-xs "px")}
                                                 {:margin-right 0}}}
                         :mid {:margin-right "0.5rem"
                               :flex 1
                               ::stylefy/media {{:max-width (str width-xs "px")}
                                                {:margin-right 0}}}
                         :right {:flex 1}}})

(def service-header
  {::stylefy/media {{:max-width (str width-sm "px")}
                    {:margin-top "2rem"}}})