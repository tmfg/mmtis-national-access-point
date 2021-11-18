(ns ote.style.front-page
  "Front page styles related to hero images and other front page components"
  (:require
    [stylefy.core :as stylefy :refer [use-style use-sub-style]]
    [ote.theme.screen-sizes :refer [width-xxs width-xs width-sm width-md width-l width-xl]]
    [ote.theme.colors :as colors]))

(def hero-img {:height "540px"
               :margin-top "-20px"
               :background "url(/img/hero-2000.png)"
               :background-size "cover"
               ::stylefy/media {{:max-width (str width-xl "px")} {:background "url(/img/hero-2000.png)"}
                                {:max-width (str width-l "px")} {:background "url(/img/hero-1600.png)"}
                                {:max-width (str width-sm "px")} {:background "url(/img/hero-1080.png)"
                                                                  :background-size "cover"
                                                                  :height "450px"}
                                {:max-width (str width-xs "px")} {:background "url(/img/hero-800.png)"
                                                                  :background-size "cover"
                                                                  :height "400px"}}})

(def hero-btn-container {:padding-top "0.5rem"
                         ::stylefy/media {{:max-width (str width-xl "px")} {:padding-top "0.5rem"}
                                          {:max-width (str width-l "px")} {:padding-top "0.5rem"}
                                          {:max-width (str width-sm "px")} {:padding-top "0.5rem"}
                                          {:max-width (str width-xs "px")} {:padding-top "0"}}})

(def h2 {:font-size "2.25em"
         ::stylefy/media {{:max-width (str width-xs "px")} {:font-size "1.5em"}}})

(def fp-btn-blue {:box-shadow "3px 3px 8px 0 rgba(0, 0, 0, .2)"
                  :cursor "pointer"
                  :background-image "linear-gradient(90deg, #06c, #0029b8)"})
(def fp-btn-hover {::stylefy/mode {:hover {:box-shadow "1px 1px 4px 0 rgba(0, 0, 0, .2)"
                                           :transform "scale(0.98)"}
                                   ::stylefy/vendors ["webkit" "moz" "ms"]
                                   ::stylefy/auto-prefix #{:transform}}})
(def fp-btn-blue-hover {:background-image "linear-gradient(45deg, #06c, #0029b8)"})
(def fp-btn-gray {:background-image "linear-gradient(90deg, #ccc, #ccc)"})
(def fp-btn {:display "flex"
             :padding "20px 20px 20px 10px"
             :box-pack "center"                             ;; Old flex standard
             :flex-pack "center"                            ;; Old flex standard
             :justify-content "center"
             :backface-visibility "visible"
             :transform-origin "50% 50% 0px"
             :transition "all 300ms ease"
             :font-family "Public Sans, sans-serif"
             :font-weight "400"
             :font-size "1.1rem"
             :text-align "center"
             :color "#fff"
             :border 0
             ::stylefy/vendors ["webkit" "moz" "ms"]
             ::stylefy/auto-prefix #{:box-pack :justify-content :flex-pack :backface-visibility :transform-origin :transition}})

(def front-page-button (merge
                         fp-btn-blue
                         fp-btn
                         fp-btn-hover
                         fp-btn-blue-hover))

(def hero-btn (merge {:margin-top "1rem"
                      :margin-left "auto"
                      :margin-right "auto"
                      :text-decoration "none"
                      ::stylefy/media {{:max-width (str width-xl "px")} {:margin-top "1rem"}
                                       {:max-width (str width-l "px")} {:margin-top "1rem"}
                                       {:max-width (str width-sm "px")} {:margin-top "0.5rem"}
                                       {:max-width (str width-xs "px")} {:margin-top "0.5rem"}}}
                     front-page-button))

(def front-page-button-disabled (merge
                                  fp-btn-gray
                                  fp-btn))

(def row-media {:margin-top "20px"
                :margin-bottom "20px"
                ::stylefy/media {{:max-width (str width-xs "px")}
                                 {:display "flex" :flex-direction "column"}}})

(def large-text-container {:padding-left "60px"
                           ::stylefy/media {{:max-width (str width-xs "px")} {:padding-left "10px"
                                                                              :order 2}}})

(def large-icon-container {:display "flex"
                           :flex-direction "column"
                           :align-items "center"
                           ::stylefy/vendors ["webkit" "moz" "ms"]
                           ::stylefy/auto-prefix #{:display :flex-direction :align-items}
                           ::stylefy/media {{:max-width (str width-xs "px")} {:order 1}}})

(def large-font-icon {:font-size "14rem"
                      :color "#969696"
                      :text-shadow "0 4px 4px rgba(0, 0, 0, .2)"
                      ::stylefy/media {{:max-width (str width-xs "px")} {:font-size "10rrem"}}})

(def front-page-h1 {:position "static"
                    :display "flex"
                    :padding-top "2rem"
                    :box-orient "vertical"                  ;; Old flex standard
                    :box-direction "normal"                 ;; Old flex standard
                    :flex-direction "column"
                    :box-pack "start"                       ;; Old flex standard
                    :flex-pack "start"                      ;; Old flex standard
                    :justify-content "flex-start"
                    :box-align "center"                     ;; Old flex standard
                    :flex-align "center"                    ;; Old flex standard
                    :align-items "center"
                    :font-family "Public Sans, sans-serif"
                    :color "#fff"
                    :font-size "6rem"
                    :font-weight "200"
                    :text-shadow "0 2px 10px rgba(0, 0, 0, .5)"
                    ::stylefy/vendors ["webkit" "moz" "ms"]
                    ::stylefy/auto-prefix #{:box-orient :box-direction :flex-direction :box-pack :flex-pack :justify-content :box-align :flex-align}
                    ::stylefy/media {{:max-width (str width-sm "px")} {:padding-top "20px"
                                                                       :font-size "4rem"
                                                                       :font-weight "300"}
                                     {:max-width (str width-xs "px")} {:padding-top "20px"
                                                                       :font-size "3rem"
                                                                       :font-weight "400"}
                                     {:min-width "0px" :max-width (str width-xxs "px")} {:padding-top "20px"
                                                                                         :font-size "2rem"
                                                                                         :font-weight "400"}}})

(def front-page-hero-text {:display "block"
                           :margin-top "2.5rem"
                           :height "100px"
                           :align-items "cexnter"
                           :font-family "Public Sans, sans-serif"
                           :color "#fafafa"
                           :font-size "2.25rem"
                           :line-height "3rem"
                           :font-weight "300"
                           :text-align "center"
                           :text-shadow "0 1px 5px rgba(0, 0, 0, .5)"
                           ::stylefy/vendors ["webkit" "moz" "ms"]
                           ::stylefy/media {{:max-width (str width-sm "px")} {:margin-top "2rem"
                                                                              :line-height "1.6rem"
                                                                              :font-size "1.6rem"
                                                                              :font-weight "400"}
                                            {:max-width (str width-xs "px")} {:margin-top "20px"
                                                                              :line-height "1.4rem"
                                                                              :font-size "1.4rem"
                                                                              :font-weight "400"}
                                            {:min-width "0px" :max-width (str width-xxs "px")} {:margin-top "0rem"
                                                                                                :line-height "1.2rem"
                                                                                                :font-size "1.2rem"
                                                                                                :font-weight "400"}}})

(def third-column-text {
                        :color "#c8c8c8"
                        :font-size "0.875rem"
                        :font-weight 300
                        :box-align "center"                 ;; Old flex standard
                        :flex-align "center"                ;; Old flex standard
                        :align-items "flex-start"
                        ::stylefy/vendors ["webkit" "moz" "ms"]
                        ::stylefy/auto-prefix #{:box-align :align-items :flex-align}
                        ::stylefy/media {
                                         {:max-width (str width-xs "px")} {:margin-bottom "20px"}
                                         {:min-width (str width-xs "px")} {:margin-bottom "40px"}}})

(def lower-section {:display "flex"
                    :padding-top "80px"
                    :padding-bottom "100px"
                    :background-image "linear-gradient(45deg, #ddd, #f8f8f8 46%, #f1f1f1)"
                    :box-shadow "4px 0 50px 0 rgba(0, 0, 0, .2), 4px 0 20px 0 #fff"})

(def media-transport-service {::stylefy/media {{:max-width (str width-xs "px")} {:padding-top "60px"}}})

(def lower-section-data-container {:display "flex"
                                   :box-orient "vertical"   ;; Old flex standard
                                   :box-direction "normal"  ;; Old flex standard
                                   :flex-direction "column"
                                   :box-align "center"      ;; Old flex standard
                                   :flex-align "center"     ;; Old flex standard
                                   :align-items "center"
                                   ::stylefy/vendors ["webkit" "moz" "ms"]
                                   ::stylefy/auto-prefix #{:box-orient :box-direction :flex-direction :box-align
                                                           :align-items :flex-align}})

(def lower-section-title {:font-size "1.5rem"
                          :font-weight 600
                          ::stylefy/media {{:max-width (str width-xs "px")} {:font-size "1.25rem"
                                                                             :font-weight 500}}})

(def lower-section-font-icon {:font-size "8rem"
                              :color "#969696"
                              :text-shadow "0 4px 4px rgba(0, 0, 0, .2)"
                              ::stylefy/media {{:max-width (str width-xs "px")} {:font-size "6rem"}
                                               {:min-width "0px" :max-width (str width-xxs "px")} {:font-size "5rem"}}})

(def lower-section-text {:text-align "center"
                         :font-size "1rem"
                         :font-weight 400
                         :line-height "1.5rem"
                         :min-height "95px"
                         :width "100%"                      ;; Required by IE11 for some odd reason (here be dragons!).
                         })

(def footer-logo {:width "160px"
                  ::stylefy/media {{:max-width (str width-xs "px")} {:width "90px"}}})

(def footer-small-icon {:position "relative" :height 22 :width 30 :top 5 :color "#fff" :padding-right "10px"})

(def footer-3-container {:padding-top "60px"
                         ::stylefy/media {{:max-width (str width-xs "px")} {:padding-top "40px"}}})

(def footer-gray-info-text {:color colors/gray550
                            :margin-left "30px"})
