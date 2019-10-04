(ns ote.config.email-config
  "Configuration values for the emailing feature.
  This file contains constants which are not environment-specific, so they are defined here instead of hard-coding in
  implementation files. Motivation is to have constants in one place for better overview to the feature and its
  variability.")

(defn config []
  {:e2e-test-email "user.userson@example.com"                          ;; To detect which email address needs to be replaced by amazon simulator address
   :e2e-test-amazon-simulator-email "success@simulator.amazonses.com"  ;; Amazon simulator email where we can send e2e test emails
   })
