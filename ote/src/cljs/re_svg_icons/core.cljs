(ns re-svg-icons.core
  "Original from [tatut/re-svg-icons](https://github.com/tatut/re-svg-icons), copied under MIT license")

(defn icon* [opts svg-hiccup]
  ;; Merge the given opts to the SVG attrs
  ;; This allows setting class and other options
  (update svg-hiccup 1 merge opts))
