(ns ote.style.front-page
  "Front page styles related to hero images and other front page components"
  (:require
    [stylefy.core :as stylefy :refer [use-style use-sub-style]]))

(def width-xs 767)
(def width-sm 991)
(def width-md 1199)
;(def width-l 1199)



(def front-page-h1  {:position "static"
                      :display "flex"
                      :margin-top "100px"
                      :-webkit-box-orient "vertical"
                      :-webkit-box-direction "normal"
                      :-webkit-flex-direction "column"
                      :flex-direction "column"
                      :-webkit-box-pack "start"
                      :-ms-flex-pack "start"
                      :justify-content "flex-start"
                      :-webkit-box-align "center"
                      :-ms-flex-align "center"
                      :align-items "center"
                      :font-family "Montserrat, sans-serif"
                      :color "#fff"
                      :font-size "6em"
                      :font-weight "200"
                      :text-shadow "0 2px 10px rgba(0, 0, 0, .5)"
                      ::stylefy/media {{:max-width (str width-xs "px")} {:font-size "3em"}}})

(def front-page-hero-text {:display "flex"
                           :margin-top "60px"
                           :-webkit-box-orient "vertical"
                           :-webkit-box-direction "normal"
                           :-webkit-flex-direction "column"
                           :-ms-flex-direction "column"
                           :flex-direction "column"
                           :-webkit-box-pack "start"
                           :-webkit-justify-content "flex-start"
                           :-ms-flex-pack "start"
                           :justify-content "flex-start"
                           :v-webkit-box-align "center"
                           :-webkit-align-items "center"
                           :-ms-flex-align "center"
                           :align-items "center"
                           :font-family "Montserrat, sans-serif"
                           :color "#fafafa"
                           :font-size "2.25em"
                           :line-height "1.5em"
                           :font-weight "300"
                           :text-align "center"
                           :text-shadow "0 1px 5px rgba(0, 0, 0, .5)"
                           ::stylefy/media {{:max-width (str width-xs "px")} {:font-size "1.6em" }}})