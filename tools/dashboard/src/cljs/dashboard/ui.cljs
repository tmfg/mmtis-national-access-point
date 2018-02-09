(ns dashboard.ui)

(defn polar->cartesian [cx cy radius angle-deg]
  (let [rad (/ (* js/Math.PI (- angle-deg 90)) 180.0)]
    [(+ cx (* radius (js/Math.cos rad)))
     (+ cy (* radius (js/Math.sin rad)))]))


(defn arc [cx cy r db de color width]
  (let [[ax ay] (polar->cartesian cx cy r db)
        [lx ly] (polar->cartesian cx cy r de)
        large? (if (< (- de db) 180) 0 1)
        dir? 1]
    [:path {:d (str "M " ax " " ay " "
                    "A" r " " r " 0 " large? " " dir? " " lx " " ly)
            :fill "none"
            :stroke color
            :stroke-width width}]))


(defn gauge [percentage]
  (let [angle (- (* 180.0 (/ percentage 100.0)) 90)
        cx 50
        cy 50
        r 40]
    [:svg {:width 100 :height 80}
     [arc cx cy r -90 90 "lightGray" 10]
     [arc cx cy r -90 angle "red" 5]
     (let [[x y] (polar->cartesian cx cy (- r 5) angle)]
       [:line {:x1 cx :y1 cy :x2 x :y2 y :style {:stroke "black" :stroke-width 2}}])
     [:text {:text-anchor "middle" :x cx :y (+ cy 20) :style {:font-size 20}}
      (str (.toFixed percentage 1) "%")]]))
