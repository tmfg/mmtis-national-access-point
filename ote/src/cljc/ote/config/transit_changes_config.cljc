(ns ote.config.transit-changes-config
  "Configuration values for the transit change detection feature.
  This file contains constants which are not environment-specific, so they are defined here instead of hard-coding in
  implementation files. Motivation is to have constants in one place for better overview to the feature and its
  variability.")

(defn config []
  {:detection-threshold-no-traffic-days 16                  ;; For reporting a no-traffic change, the length of no-traffic period must exceed this value
   :detection-threshold-route-end-days 90                   ;; Route will be reported as ending if the difference of it's last day with traffic in future and the analysis days is less than this value
   :detection-interval-service-days 14                      ;; The next transit change detection run for a service will be scheduled this many days into the future
   :detection-window-days 216                               ;; How many days into the future the detection task analyzes traffic for a service
   })
