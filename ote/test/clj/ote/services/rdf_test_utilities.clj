(ns ote.services.rdf-test-utilities
  "Test data and utilities for RDF generation tests")

(def test-small-taxi-service
  {:service
   {:ote.db.transport-service/contact-email "tero.testinen@sahkoposti.fi",
    :ote.db.transport-service/sub-type :taxi,
    :ote.db.transport-service/id 123456789,
    :ote.db.modification/created
    #inst "2016-12-19T18:45:33.294000000-00:00",
    :ote.db.transport-service/notice-external-interfaces? false,
    :ote.db.transport-service/companies [],
    :municipality "https://w3id.org/stirdata/resource/lau/item/FI_092",
    :ote.db.transport-service/description
    [#:ote.db.transport-service{:lang "FI", :text "henkilöliikenne"}],
    :ote.db.transport-service/brokerage? false,
    :ote.db.transport-service/external-interfaces nil,
    :ote.db.transport-service/rentals {},
    :ote.db.transport-service/contact-address
    #:ote.db.common{:street "Testikatu 1",
                    :post_office "Vantaa",
                    :postal_code "01230"},
    :ote.db.modification/created-by
    "some_uuid",
    :ote.db.transport-service/name "Taksi",
    :ote.db.transport-service/type :passenger-transportation,
    :ote.db.transport-service/transport-operator-id 1,
    :ote.db.transport-service/contact-phone "0401234567",
    :ote.db.transport-service/commercial-traffic? true,
    :ote.db.transport-service/passenger-transportation
    #:ote.db.transport-service{:additional-services [],
                               :price-classes [],
                               :guaranteed-accessibility-tool [],
                               :booking-service
                               #:ote.db.transport-service{:description
                                                          []},
                               :guaranteed-info-service-accessibility [],
                               :guaranteed-accessibility-description [],
                               :limited-transportable-aid [],
                               :guaranteed-vehicle-accessibility [],
                               :pricing
                               #:ote.db.transport-service{:description
                                                          []},
                               :payment-methods [],
                               :limited-vehicle-accessibility [],
                               :real-time-information
                               #:ote.db.transport-service{:description
                                                          []},
                               :guaranteed-transportable-aid [],
                               :payment-method-description [],
                               :limited-accessibility-description [],
                               :service-exceptions [],
                               :limited-info-service-accessibility [],
                               :service-hours [],
                               :luggage-restrictions
                               [#:ote.db.transport-service{:lang "FI"}],
                               :limited-accessibility-tool []},
    :ote.db.transport-service/transport-type [:road]},
   :operation-areas
   [{:geojson "{\"type\":\"Polygon\",\"coordinates\":[]}",
     :primary? true,
     :feature-id "Vantaa"}],
   :operator
   #:ote.db.transport-operator{:gsm "0401234567",
                               :business-id "1234567-5",
                               :name "Tero Testinen",
                               :ckan-group-id
                               "some_uuid",
                               :id 123456789,
                               :billing-address {},
                               :visiting-address
                               #:ote.db.common{:street "Testikatu 1",
                                               :post_office "Vantaa",
                                               :postal_code "01230"},
                               :phone "0401234567",
                               :deleted? false,
                               :email "tero.testinen@example.com"},
   :validation-data {},
   :latest-publication #inst "2025-11-17T06:43:13.005000000-00:00"})

(def test-large-bus-service
  {:service
   {:ote.db.transport-service/available-from
    #inst "2022-12-31T22:00:00.000-00:00",
    :ote.db.transport-service/contact-email "info@superonnikka.com",
    :ote.db.transport-service/available-to
    #inst "2024-12-28T22:00:00.000-00:00",
    :ote.db.transport-service/sub-type :schedule,
    :ote.db.modification/modified-by
    "some_uuid4",
    :ote.db.transport-service/id 111,
    :ote.db.modification/created
    #inst "2018-11-11T10:10:10.708000000-00:00",
    :ote.db.transport-service/notice-external-interfaces? true,
    :ote.db.transport-service/companies
    [#:ote.db.transport-service{:name "SuperOnnikka Oy",
                                :business-id "7654321-0"}],
    :municipality "https://w3id.org/stirdata/resource/lau/item/FI_564",
    :ote.db.transport-service/published
    #inst "2025-04-01T05:42:11.109000000-00:00",
    :ote.db.transport-service/description
    [#:ote.db.transport-service{:lang "FI",
                                :text
                                "Kuvaus suomeksi SuperOnnikka-linjaliikenteestä"}
     #:ote.db.transport-service{:lang "EN",
                                :text
                                "Description in English about SuperOnnikka coach service"}],
    :ote.db.transport-service/brokerage? false,
    :ote.db.transport-service/external-interfaces
    [#:ote.db.transport-service{:ckan-resource-id
                                "some_uuid2",
                                :gtfs-imported
                                #inst "2025-10-15T00:00:00.981000000-00:00",
                                :license
                                "https://www.link.to/license",
                                :external-interface
                                #:ote.db.transport-service{:url
                                                           "https://fakeapi.fi/gtfs/111/gtfs.zip",
                                                           :description
                                                           [#:ote.db.transport-service{:lang
                                                                                       "FI",
                                                                                       :text
                                                                                       "Aikataulu- ja reittitiedot GTFS-muodossa"}
                                                            #:ote.db.transport-service{:lang
                                                                                       "EN",
                                                                                       :text
                                                                                       "Schedule and route information in GTFS format"}]},
                                :format ["GTFS"],
                                :data-content [:route-and-schedule],
                                :transport-service-id 111,
                                :id 111001}
     #:ote.db.transport-service{:ckan-resource-id
                                "some_uuid3",
                                :license
                                "https://www.link.to/license",
                                :external-interface
                                #:ote.db.transport-service{:url
                                                           "https://fakeapi.fi/json/111",
                                                           :description
                                                           [#:ote.db.transport-service{:lang
                                                                                       "FI",
                                                                                       :text
                                                                                       "Lippu- ja myyntirajapinta, vain suomeksi"}]},
                                :format ["JSON"],
                                :data-content [:payment-interface],
                                :transport-service-id 111,
                                :id 111002}],
    :ote.db.transport-service/rentals
    #:ote.db.transport-service{:rental-additional-services [],
                               :vehicle-classes [],
                               :booking-service
                               #:ote.db.transport-service{:description
                                                          []},
                               :pick-up-locations [],
                               :guaranteed-accessibility-description [],
                               :limited-transportable-aid [],
                               :guaranteed-vehicle-accessibility [],
                               :payment-methods [],
                               :limited-vehicle-accessibility [],
                               :real-time-information
                               #:ote.db.transport-service{:description
                                                          []},
                               :accessibility-description [],
                               :guaranteed-transportable-aid [],
                               :limited-accessibility-description [],
                               :luggage-restrictions []},
    :ote.db.transport-service/contact-address
    #:ote.db.common{:street "Onnikkatu 1",
                    :post_office "Oulu",
                    :postal_code "90100"},
    :ote.db.modification/modified
    #inst "2025-03-27T12:52:01.031000000-00:00",
    :ote.db.modification/created-by
    "some_uuid4",
    :ote.db.transport-service/homepage "www.superonnikka.com",
    :ote.db.transport-service/name "SuperOnnikka reittiliikenne",
    :ote.db.transport-service/type :passenger-transportation,
    :ote.db.transport-service/company-source :form,
    :ote.db.transport-service/transport-operator-id 112,
    :ote.db.transport-service/contact-phone "060002010",
    :ote.db.transport-service/commercial-traffic? true,
    :ote.db.transport-service/passenger-transportation
    #:ote.db.transport-service{:additional-services [],
                               :price-classes [],
                               :guaranteed-accessibility-tool [],
                               :booking-service
                               #:ote.db.transport-service{:url
                                                          "www.superonnikka.com",
                                                          :description
                                                          [#:ote.db.transport-service{:lang
                                                                                      "FI",
                                                                                      :text
                                                                                      "SuperOnnikka.com verkkokauppa"}
                                                           #:ote.db.transport-service{:lang
                                                                                      "EN",
                                                                                      :text
                                                                                      "SuperOnnikka.com Webstore"}]},
                               :guaranteed-info-service-accessibility [],
                               :guaranteed-accessibility-description [],
                               :limited-transportable-aid [],
                               :guaranteed-vehicle-accessibility
                               [:boarding-assistance],
                               :pricing
                               #:ote.db.transport-service{:url
                                                          "api.superonnikka.com",
                                                          :description
                                                          [#:ote.db.transport-service{:lang
                                                                                      "FI",
                                                                                      :text
                                                                                      "Lippu voi maksaa paljon tai vähän"}
                                                           #:ote.db.transport-service{:lang
                                                                                      "EN",
                                                                                      :text
                                                                                      "The ticket price may vary"}]},
                               :advance-reservation :possible,
                               :payment-methods
                               [:debit-card :credit-card],
                               :limited-vehicle-accessibility
                               [:low-floor
                                :step-free-access
                                :suitable-for-wheelchairs
                                :assistance-dog-space],
                               :real-time-information
                               #:ote.db.transport-service{:description
                                                          []},
                               :guaranteed-transportable-aid
                               [:wheelchair
                                :crutches
                                :walker
                                :walking-stick],
                               :payment-method-description
                               [#:ote.db.transport-service{:lang "FI",
                                                           :text
                                                           "Bussista voi ostaa oravannahkoilla, api.superonnikka.com:in kautta Bitcoineilla."}
                                #:ote.db.transport-service{:lang "EN",
                                                           :text
                                                           "From bus with squirrel hides, via api.superonnikka.com with Bitcoins."}],
                               :limited-accessibility-description
                               [#:ote.db.transport-service{:lang "FI",
                                                           :text
                                                           "Linjan varustelu mahdollistaa esteettömän matkustamisen pyörätuolilla, kävelykepeillä tai -tukien kanssa. Matkustajille on varattu avustuskoiran paikka."}],
                               :service-hours-info
                               [#:ote.db.transport-service{:lang "FI",
                                                           :text
                                                           "Palvelemme ympäri vuorokauden vuoden jokaisena päivänä."}],
                               :service-exceptions
                               [#:ote.db.transport-service{:from-date
                                                           #inst "2020-12-24T20:00:00.000-00:00",
                                                           :to-date
                                                           #inst "2020-12-24T20:00:00.000-00:00",
                                                           :description
                                                           [#:ote.db.transport-service{:lang
                                                                                       "FI",
                                                                                       :text
                                                                                       "Jouluaatto 2020"}
                                                            #:ote.db.transport-service{:lang
                                                                                       "EN",
                                                                                       :text
                                                                                       "Christmas Eve 2020"}]}
                                #:ote.db.transport-service{:from-date
                                                           #inst "2021-12-25T22:00:00.000-00:00",
                                                           :to-date
                                                           #inst "2021-12-25T22:00:00.000-00:00",
                                                           :description
                                                           [#:ote.db.transport-service{:lang
                                                                                       "EN",
                                                                                       :text
                                                                                       "Christmas day 2021"}
                                                            #:ote.db.transport-service{:lang
                                                                                       "FI",
                                                                                       :text
                                                                                       "Joulupäivä 2021"}]}],
                               :accessibility-info-url
                               "http://www.superonnikka.com/FI/accessibility",
                               :limited-info-service-accessibility
                               [:audio-information],
                               :service-hours
                               [#:ote.db.transport-service{:week-days
                                                           [:MON
                                                            :TUE
                                                            :WED
                                                            :THU
                                                            :FRI
                                                            :SAT
                                                            :SUN],
                                                           :from
                                                           {:hours 0,
                                                            :minutes 0,
                                                            :seconds 0},
                                                           :to
                                                           {:hours 24,
                                                            :minutes 0,
                                                            :seconds 0},
                                                           :description
                                                           [],
                                                           :all-day
                                                           true}],
                               :luggage-restrictions
                               [#:ote.db.transport-service{:lang "FI",
                                                           :text
                                                           "Vain matka-arkut sallittuja."}
                                #:ote.db.transport-service{:lang "EN",
                                                           :text
                                                           "Only travel trunks allowed."}],
                               :limited-accessibility-tool []},
    :ote.db.transport-service/transport-type [:road]},
   :operation-areas
   [{:geojson "{\"type\":\"Polygon\",\"coordinates\":[]}",
     :primary? true,
     :feature-id "Suomi"}],
   :operator
   #:ote.db.transport-operator{:gsm "0402222222",
                               :business-id "7654321-0",
                               :name "SuperOnnikka Oy",
                               :ckan-group-id
                               "some_uuid1",
                               :id 112,
                               :billing-address
                               #:ote.db.common{:street
                                               "Onnikkatu 1",
                                               :post_office "Oulu",
                                               :postal_code "90100",
                                               :country_code "FI"},
                               :visiting-address
                               #:ote.db.common{:street
                                               "Onnikkatu 1",
                                               :post_office "Oulu",
                                               :postal_code "90100",
                                               :country_code "FI"},
                               :homepage "www.superonnikka.com",
                               :phone "0401111111",
                               :deleted? false,
                               :email "info@superonnikka.com"},
   :validation-data
   {111
    {:tis-magic-link
     "https://magiclink.fi",
     :tis-success false,
     :created #inst "2025-11-16T15:37:04.004000000-00:00",
     :download-status nil,
     :tis_polling_completed nil,
     :tis-complete false,
     :id 765644,
     :tis_submit_completed #inst "2025-11-16T22:58:37.234128000-00:00",
     :tis-entry-public-id "public-id-111"},
    112 nil},
   :latest-publication #inst "2025-11-17T06:43:13.005000000-00:00"})
