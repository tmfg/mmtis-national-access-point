(ns ote.config.email-config
  "Configuration values for email related thingies")

(defn config []
  {:e2e-test-email "user.userson@example.com"                          ;; To detect which email address needs to be replaced by amazon simulator address
   :e2e-test-amazon-simulator-email "success@simulator.amazonses.com"  ;; Amazon simulator email where we can send e2e test emails
   })
