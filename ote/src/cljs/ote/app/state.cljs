(ns ote.app.state
  "Contains the frontend application `app` database.
  Everything that is in the current state of the frontend is in the app atom."
  (:require [reagent.core :as r]))

(defonce app
         (r/atom {:place-search       {}
                  :page               :operator             ;; :operator = Transport Operator, :passenger-transportation = Passenger transport info

                  ;; Currently selected / edited transport operator (company basic info)
                  :transport-operator #:ote.db.transport-operator {:name        "Foo"
                                                                   :business-id "1234567-8"
                                                                   :visiting-address
                                                                                #:ote.db.common {:street      "Street 1"
                                                                                                 :postal-code "90100"
                                                                                                 :post-office "Oulu"}}

                  ;; Currently selected / edited transport service
                  :transport-service  {:add-price-class false ;; State of price class adding list (true = open)
                                       }

                  :data               {:operator         {}
                                       :service-provider {:transport-operator-id    0
                                                          :type                     "passenger-transportation" ;;:terminal :passenger-transportation :rentals :parking :brokerage
                                                          :terminal                 {}
                                                          :passenger-transportation {}
                                                          :rental                   {}
                                                          :parking                  {}
                                                          :brokerage                {}}}}))
