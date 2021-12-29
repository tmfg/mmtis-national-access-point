(ns ote.theme.colors
  "Theming colors to be used across frontend. The colors are from Fintraffic's official style guide from December 2020.

  Whenever picking colors to use, use them in this order:
   1. Start with `primary-` prefixed colors. These are named semantically as per intended usage context.
   2. Prefer `accessible-` colors for highlights, area element backgrounds etc. These have the best contrast.
   3. Expanded palette is available through prefixes `basic-`, `light-` and `dark-`. Use these sparingly.

For extra specific reasons there's also `faint-` colors. Use this extremely sparingly!

  More details and guidance available in `Fintraffic_Brand_Book_Joulukuu2020.pdf` pages 17 to 21.")

;;;; Fintraffic official colors - USE THESE!!

;; Primary colors are black and white.
(def basic-black "#000000")
(def basic-white "#ffffff")

;; Basic colors
;  semantic names defined by designers, do not use these directly
(def ^:private sand "#c89b64")
;  Fintraffic basic colors
(def basic-blue "#0064eb")
(def basic-green "#25a794")
(def basic-purple "#73468c")
(def basic-red "#ff5a05")
(def basic-yellow "#ffd000")
(def basic-gray "#9696aa")
(def basic-brown sand)

;; Dark colors
;  semantic names defined by designers, do not use these directly
(def ^:private orange "#ff8c00")
(def ^:private brown "#784b28")
;  Fintraffic dark colors
(def dark-blue "#0034ac")
(def dark-green "#005f61")
(def dark-purple "#520076")
(def dark-red "#b40000")
(def dark-yellow orange)
(def dark-gray "#505064")
(def dark-brown brown)

;; Light colors
;  semantic names defined by designers, do not use these directly
(def beige "#dcc8aa")
;  Fintraffic light colors
(def light-blue "#5fb4f5")
(def light-green "#64e1b4")
(def light-purple "#af96dc")
(def light-red "#ff9b87")
(def light-yellow "#fff5aa")
(def light-gray "#cdcdd7")
(def light-brown beige)

;; Faint colors
;  !! NON-STANDARD COLORS !!
;  for use as non-important element background highlighting and similar places where accessability is not an issue
(def faint-gray "#eaeaea")

;; Semantic colors - prefer the use of these above everything else
;  Primary colors by usage context.
(def primary-background-color basic-black)
(def primary-text-color basic-white)
(def primary-button-background-color dark-blue)

; colors with just a single variant do not have the color intensity in the name
(def accessible-blue dark-blue)
(def accessible-green dark-green)
(def accessible-basic-purple basic-purple)
(def accessible-dark-purple dark-purple)
(def accessible-red dark-red)
(def accessible-gray dark-gray)
(def accessible-brown dark-brown)
(def accessible-black basic-black)

;;;; old colors - DO NOT USE THESE!!

(def ^:deprecated blue-light "#3385d6")
(def ^:deprecated blue-dark "#0048c2")
(def ^:deprecated blue-darker "#0029B8")

(def ^:deprecated gray950 "#191919")
(def ^:deprecated gray900 "#323232")
(def ^:deprecated gray800 "#4b4b4b")
(def ^:deprecated gray750 "#646464")
(def ^:deprecated gray700 "#7d7d7d")
(def ^:deprecated gray650 "#969696")
(def ^:deprecated gray550 "#c8c8c8")
(def ^:deprecated gray450 "#dcdcdc")
(def ^:deprecated gray400 "#e1e1e1")
(def ^:deprecated gray350 "#e6e6e6")
(def ^:deprecated gray300 "#ebebeb")
(def ^:deprecated gray200 "#f0f0f0")
(def ^:deprecated gray100 "#f5f5f5")
(def ^:deprecated gray50 "#fafafa")

(def ^:deprecated green-basic "#00AA00")
(def ^:deprecated red-basic "#DD0000")
(def ^:deprecated red-dark "#CF0000")
(def ^:deprecated red-darker "#C10000")
(def ^:deprecated orange-basic "#FF8800")
(def ^:deprecated white-basic "white")
(def ^:deprecated yellow-basic "#ddcc00")
(def ^:deprecated purple-darker "#7000D0")

(def ^:deprecated add-color green-basic)
(def ^:deprecated icon-disabled gray550)
(def ^:deprecated icon-gray gray900)
(def ^:deprecated negative-button red-basic)
(def ^:deprecated negative-text white-basic)
(def ^:deprecated negative-button-hover red-dark)
(def ^:deprecated primary dark-blue)
(def ^:deprecated primary-dark blue-dark)
(def ^:deprecated primary-darker blue-darker)
(def ^:deprecated primary-text white-basic)
(def ^:deprecated primary-disabled gray550)
(def ^:deprecated primary-light blue-light)
(def ^:deprecated secondary gray900)
(def ^:deprecated progress primary)
(def ^:deprecated remove-color red-darker)
(def ^:deprecated success green-basic)
(def ^:deprecated warning red-basic)

(def ^:deprecated calendar-day-font gray950)

(def ^:deprecated monitor-taxi-color "rgb(0,170,187)")
(def ^:deprecated monitor-request-color "rgb(102,204,102)")
(def ^:deprecated monitor-schedule-color "rgb(153,204,0)")
(def ^:deprecated monitor-terminal-color "rgb(221,204,0)")
(def ^:deprecated monitor-rental-color "rgb(255,136,0)")
(def ^:deprecated monitor-parking-color "rgb(255,102,153)")
(def ^:deprecated monitor-brokerage-color "rgb(153,0,221)")
