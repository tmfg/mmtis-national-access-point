(ns ote.transit-changes.stop-name-detection-test-values
  (:require [clojure.test :refer :all]))

(def first-common-stop "Joensuu")

(def trip1-test-values {:package-id 13776,
                        :trip-id "M-P, SS_7182844",
                        :headsign "Joensuu-Kuopio (S974)",
                        :stoptimes [#:gtfs{:stop-sequence 1,
                                           :stop-name "Joensuu",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}
                                    #:gtfs{:stop-sequence 2,
                                           :stop-name "Kauppatori L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 17, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 17, :seconds 0},
                                           :stop-lat 62.60010284595497,
                                           :stop-lon 29.76093412570076,
                                           :stop-fuzzy-lat 62.6001,
                                           :stop-fuzzy-lon 29.7609}
                                    #:gtfs{:stop-sequence 3,
                                           :stop-name "Yliopisto P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 19, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 19, :seconds 0},
                                           :stop-lat 62.60535348047568,
                                           :stop-lon 29.745916167948703,
                                           :stop-fuzzy-lat 62.6053,
                                           :stop-fuzzy-lon 29.7459}
                                    #:gtfs{:stop-sequence 4,
                                           :stop-name "Kaislakatu P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 20, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 20, :seconds 0},
                                           :stop-lat 62.613154541337316,
                                           :stop-lon 29.738927040057302,
                                           :stop-fuzzy-lat 62.6131,
                                           :stop-fuzzy-lon 29.7389}
                                    #:gtfs{:stop-sequence 5,
                                           :stop-name "Siilainen L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 21, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 21, :seconds 0},
                                           :stop-lat 62.61836475215888,
                                           :stop-lon 29.729402190537932,
                                           :stop-fuzzy-lat 62.6183,
                                           :stop-fuzzy-lon 29.7294}
                                    #:gtfs{:stop-sequence 6,
                                           :stop-name "Noljakka L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 23, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 23, :seconds 0},
                                           :stop-lat 62.62736342965559,
                                           :stop-lon 29.69551393699565,
                                           :stop-fuzzy-lat 62.6273,
                                           :stop-fuzzy-lon 29.6955}
                                    #:gtfs{:stop-sequence 7,
                                           :stop-name "Marjalantie L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
                                           :stop-lat 62.629325973884136,
                                           :stop-lon 29.691460723407655,
                                           :stop-fuzzy-lat 62.6293,
                                           :stop-fuzzy-lon 29.6914}
                                    #:gtfs{:stop-sequence 8,
                                           :stop-name "Kuusela L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
                                           :stop-lat 62.63110771792321,
                                           :stop-lon 29.685775934484685,
                                           :stop-fuzzy-lat 62.6311,
                                           :stop-fuzzy-lon 29.6857}
                                    #:gtfs{:stop-sequence 9,
                                           :stop-name "Marjala L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 25, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 25, :seconds 0},
                                           :stop-lat 62.63562673922048,
                                           :stop-lon 29.667408752067438,
                                           :stop-fuzzy-lat 62.6356,
                                           :stop-fuzzy-lon 29.6674}
                                    #:gtfs{:stop-sequence 10,
                                           :stop-name "Lepikkoranta L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 26, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 26, :seconds 0},
                                           :stop-lat 62.63553082738688,
                                           :stop-lon 29.651806409103003,
                                           :stop-fuzzy-lat 62.6355,
                                           :stop-fuzzy-lon 29.6518}
                                    #:gtfs{:stop-sequence 11,
                                           :stop-name "Joensuu",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 30, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 30, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}],
                        :first-common-stop "Joensuu",
                        :first-common-stop-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0}})

(def trip2-test-values {:package-id 13776,
                        :trip-id "M-P, SS_7182913",
                        :headsign "Joensuu-Kuopio (S974)",
                        :stoptimes [#:gtfs{:stop-sequence 1,
                                           :stop-name "Joensuu",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}
                                    #:gtfs{:stop-sequence 2,
                                           :stop-name "Kauppatori L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 17, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 17, :seconds 0},
                                           :stop-lat 62.60010284595497,
                                           :stop-lon 29.76093412570076,
                                           :stop-fuzzy-lat 62.6001,
                                           :stop-fuzzy-lon 29.7609}
                                    #:gtfs{:stop-sequence 3,
                                           :stop-name "Lyseo L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 18, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 18, :seconds 0},
                                           :stop-lat 62.60130580183765,
                                           :stop-lon 29.754837060741245,
                                           :stop-fuzzy-lat 62.6013,
                                           :stop-fuzzy-lon 29.7548}
                                    #:gtfs{:stop-sequence 4,
                                           :stop-name "Ystävyydenpuisto L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 18, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 18, :seconds 0},
                                           :stop-lat 62.602250756894286,
                                           :stop-lon 29.750062683927077,
                                           :stop-fuzzy-lat 62.6022,
                                           :stop-fuzzy-lon 29.7500}
                                    #:gtfs{:stop-sequence 5,
                                           :stop-name "Yliopisto P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 19, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 19, :seconds 0},
                                           :stop-lat 62.60535348047568,
                                           :stop-lon 29.745916167948703,
                                           :stop-fuzzy-lat 62.6053,
                                           :stop-fuzzy-lon 29.7459}
                                    #:gtfs{:stop-sequence 6,
                                           :stop-name "Kaislakatu P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 20, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 20, :seconds 0},
                                           :stop-lat 62.613154541337316,
                                           :stop-lon 29.738927040057302,
                                           :stop-fuzzy-lat 62.6131,
                                           :stop-fuzzy-lon 29.7389}
                                    #:gtfs{:stop-sequence 7,
                                           :stop-name "Siilainen L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 21, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 21, :seconds 0},
                                           :stop-lat 62.61836475215888,
                                           :stop-lon 29.729402190537932,
                                           :stop-fuzzy-lat 62.6183,
                                           :stop-fuzzy-lon 29.7294}
                                    #:gtfs{:stop-sequence 8,
                                           :stop-name "Noljakka S",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 23, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 23, :seconds 0},
                                           :stop-lat 62.62736342965559,
                                           :stop-lon 29.69551393699565,
                                           :stop-fuzzy-lat 62.6273,
                                           :stop-fuzzy-lon 29.6955}
                                    #:gtfs{:stop-sequence 9,
                                           :stop-name "Marjalantie L",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
                                           :stop-lat 62.629325973884136,
                                           :stop-lon 29.691460723407655,
                                           :stop-fuzzy-lat 62.6293,
                                           :stop-fuzzy-lon 29.6914}
                                    #:gtfs{:stop-sequence 11,
                                           :stop-name "Joensuu",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 30, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 30, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}],
                        :first-common-stop "Joensuu",
                        :first-common-stop-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0}})

(def combined-stop-sequence-result
  '(#:gtfs{:stop-sequence 0
           :stop-name "Joensuu",
          :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0},
          :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 15, :seconds 0}}
    #:gtfs{:stop-sequence 1
           :stop-name "Kauppatori L",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 17, :seconds 0},
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 17, :seconds 0}}
    #:gtfs{:stop-sequence 2
           :stop-name "Lyseo L",
           :departure-time-date1 nil,
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 18, :seconds 0}}
    #:gtfs{:stop-sequence 3
           :stop-name "Ystävyydenpuisto L",
           :departure-time-date1 nil,
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 18, :seconds 0}}
    #:gtfs{:stop-sequence 4
           :stop-name "Yliopisto P",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 19, :seconds 0},
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 19, :seconds 0}}
    #:gtfs{:stop-sequence 5
           :stop-name "Kaislakatu P",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 20, :seconds 0},
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 20, :seconds 0}}
    #:gtfs{:stop-sequence 6
           :stop-name "Siilainen L",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 21, :seconds 0},
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 21, :seconds 0}}
    #:gtfs{:stop-sequence 7
           :stop-name "Noljakka S->Noljakka L",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 23, :seconds 0},
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 23, :seconds 0}}
    #:gtfs{:stop-sequence 7.07
            :stop-name "Kuusela L",
            :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
            :departure-time-date2 nil}
    #:gtfs{:stop-sequence 8
            :stop-name "Marjalantie L",
            :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0},
            :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 24, :seconds 0}}
    #:gtfs{:stop-sequence 8.08
           :stop-name "Marjala L",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 25, :seconds 0},
           :departure-time-date2 nil}
    #:gtfs{:stop-sequence 8.1608
           :stop-name "Lepikkoranta L",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 26, :seconds 0},
           :departure-time-date2 nil}
    #:gtfs{:stop-sequence 12
           :stop-name "Joensuu",
           :departure-time-date1 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 30, :seconds 0},
           :departure-time-date2 #ote.time.Interval{:years 0, :months 0, :days 0, :hours 18, :minutes 30, :seconds 0}}))

(def v2-first-common-stop "Ylisenvaara P")
(def v2-trip1-test-values {:package-id 13776,
                        :trip-id "M-P, SS_7182844",
                        :headsign "Joensuu-Kuopio (S974)",
                        :stoptimes [#:gtfs{:stop-sequence 1,
                                           :stop-name "Ylisenvaara P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 54, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 54, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}
                                    #:gtfs{:stop-sequence 2,
                                           :stop-name "Tankapirtti P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 58, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 58, :seconds 0},
                                           :stop-lat 62.60010284595497,
                                           :stop-lon 29.76093412570076,
                                           :stop-fuzzy-lat 62.6001,
                                           :stop-fuzzy-lon 29.7609}
                                    #:gtfs{:stop-sequence 3,
                                           :stop-name "Kakslauttanen P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 04, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 04, :seconds 0},
                                           :stop-lat 62.60535348047568,
                                           :stop-lon 29.745916167948703,
                                           :stop-fuzzy-lat 62.6053,
                                           :stop-fuzzy-lon 29.7459}
                                    #:gtfs{:stop-sequence 4,
                                           :stop-name "Tieva I",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 05, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 05, :seconds 0},
                                           :stop-lat 62.613154541337316,
                                           :stop-lon 29.738927040057302,
                                           :stop-fuzzy-lat 62.6131,
                                           :stop-fuzzy-lon 29.7389}
                                    #:gtfs{:stop-sequence 5,
                                           :stop-name "Muotkan maja th I",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 05, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 05, :seconds 0},
                                           :stop-lat 62.61836475215888,
                                           :stop-lon 29.729402190537932,
                                           :stop-fuzzy-lat 62.6183,
                                           :stop-fuzzy-lon 29.7294}
                                    #:gtfs{:stop-sequence 6,
                                           :stop-name "Kopararova I",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 06, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 06, :seconds 0},
                                           :stop-lat 62.629325973884136,
                                           :stop-lon 29.691460723407655,
                                           :stop-fuzzy-lat 62.6293,
                                           :stop-fuzzy-lon 29.6914}
                                    #:gtfs{:stop-sequence 7,
                                           :stop-name "Kutturan th P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 8, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 8, :seconds 0},
                                           :stop-lat 62.62736342965559,
                                           :stop-lon 29.69551393699565,
                                           :stop-fuzzy-lat 62.6273,
                                           :stop-fuzzy-lon 29.6955}

                                    #:gtfs{:stop-sequence 8,
                                           :stop-name "Tunturikeskus Kiilopää",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 7, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 7, :seconds 0},
                                           :stop-lat 62.63562673922048,
                                           :stop-lon 29.667408752067438,
                                           :stop-fuzzy-lat 62.6356,
                                           :stop-fuzzy-lon 29.6674}

                                    #:gtfs{:stop-sequence 9,
                                           :stop-name "Rönkönvaara P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 9, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 9, :seconds 0},
                                           :stop-lat 62.63110771792321,
                                           :stop-lon 29.685775934484685,
                                           :stop-fuzzy-lat 62.6311,
                                           :stop-fuzzy-lon 29.6857}

                                    #:gtfs{:stop-sequence 10,
                                           :stop-name "Laanila th P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :stop-lat 62.63553082738688,
                                           :stop-lon 29.651806409103003,
                                           :stop-fuzzy-lat 62.6355,
                                           :stop-fuzzy-lon 29.6518}
                                    #:gtfs{:stop-sequence 11,
                                           :stop-name "Laanipalo P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}],
                        :first-common-stop "Ylisenvaara P",
                        :first-common-stop-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 54, :seconds 0}})

(def v2-trip2-test-values {:package-id 13776,
                        :trip-id "M-P, SS_7182913",
                        :headsign "Joensuu-Kuopio (S974)",
                        :stoptimes [#:gtfs{:stop-sequence 1,
                                           :stop-name "Ylisenvaara P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 54, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 54, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}
                                    #:gtfs{:stop-sequence 2,
                                           :stop-name "Tankapirtti P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 58, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 58, :seconds 0},
                                           :stop-lat 62.60010284595497,
                                           :stop-lon 29.76093412570076,
                                           :stop-fuzzy-lat 62.6001,
                                           :stop-fuzzy-lon 29.7609}
                                    #:gtfs{:stop-sequence 3,
                                           :stop-name "Kakslauttanen P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 04, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 04, :seconds 0},
                                           :stop-lat 62.60535348047568,
                                           :stop-lon 29.745916167948703,
                                           :stop-fuzzy-lat 62.6053,
                                           :stop-fuzzy-lon 29.7459}

                                    #:gtfs{:stop-sequence 4,
                                           :stop-name "Kutturan th P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 6, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 6, :seconds 0},
                                           :stop-lat 62.62736342965559,
                                           :stop-lon 29.69551393699565,
                                           :stop-fuzzy-lat 62.6273,
                                           :stop-fuzzy-lon 29.6955}

                                    #:gtfs{:stop-sequence 5,
                                           :stop-name "Rönkönvaara P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 7, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 7, :seconds 0},
                                           :stop-lat 62.63110771792321,
                                           :stop-lon 29.685775934484685,
                                           :stop-fuzzy-lat 62.6311,
                                           :stop-fuzzy-lon 29.6857}

                                    #:gtfs{:stop-sequence 6,
                                           :stop-name "Laanila th P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :stop-lat 62.63553082738688,
                                           :stop-lon 29.651806409103003,
                                           :stop-fuzzy-lat 62.6355,
                                           :stop-fuzzy-lon 29.6518}
                                    #:gtfs{:stop-sequence 7,
                                           :stop-name "Laanipalo P",
                                           :arrival-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :departure-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 15, :minutes 11, :seconds 0},
                                           :stop-lat 62.601131784912305,
                                           :stop-lon 29.77588953417734,
                                           :stop-fuzzy-lat 62.6011,
                                           :stop-fuzzy-lon 29.7758}],
                        :first-common-stop "Ylisenvaara P",
                        :first-common-stop-time #ote.time.Interval{:years 0, :months 0, :days 0, :hours 14, :minutes 54, :seconds 0}})

(def v2-combined-stop-sequences-result
  '(#:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 14 :minutes 54 :months 0 :seconds 0 :years 0}
          :departure-time-date2 #ote.time.Interval{:days 0 :hours 14 :minutes 54 :months 0 :seconds 0 :years 0}
          :stop-name "Ylisenvaara P"
          :stop-sequence 0}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 14 :minutes 58 :months 0 :seconds 0 :years 0}
           :departure-time-date2 #ote.time.Interval{:days 0 :hours 14 :minutes 58 :months 0 :seconds 0 :years 0}
           :stop-name "Tankapirtti P"
           :stop-sequence 1}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 4 :months 0 :seconds 0 :years 0}
           :departure-time-date2 #ote.time.Interval{:days 0 :hours 15 :minutes 4 :months 0 :seconds 0 :years 0}
           :stop-name "Kakslauttanen P"
           :stop-sequence 2}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 5 :months 0 :seconds 0 :years 0}
           :departure-time-date2 nil
           :stop-name "Tieva I"
           :stop-sequence 2.02}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 5 :months 0 :seconds 0 :years 0}
           :departure-time-date2 nil
           :stop-name "Muotkan maja th I"
           :stop-sequence 2.02}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 6 :months 0 :seconds 0 :years 0}
           :departure-time-date2 nil
           :stop-name "Kopararova I"
           :stop-sequence 2.0402}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 7 :months 0 :seconds 0 :years 0}
           :departure-time-date2 nil
           :stop-name "Tunturikeskus Kiilopää"
           :stop-sequence 2.060602}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 8 :months 0 :seconds 0 :years 0}
           :departure-time-date2 #ote.time.Interval{:days 0 :hours 15 :minutes 6 :months 0 :seconds 0 :years 0}
           :stop-name "Kutturan th P"
           :stop-sequence 5}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 9 :months 0 :seconds 0 :years 0}
           :departure-time-date2 #ote.time.Interval{:days 0 :hours 15 :minutes 7 :months 0 :seconds 0 :years 0}
           :stop-name "Rönkönvaara P"
           :stop-sequence 7}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 11 :months 0 :seconds 0 :years 0}
           :departure-time-date2 #ote.time.Interval{:days 0 :hours 15 :minutes 11 :months 0 :seconds 0 :years 0}
           :stop-name "Laanila th P"
           :stop-sequence 9}
    #:gtfs{:departure-time-date1 #ote.time.Interval{:days 0 :hours 15 :minutes 11 :months 0 :seconds 0 :years 0}
           :departure-time-date2 #ote.time.Interval{:days 0 :hours 15 :minutes 11 :months 0 :seconds 0 :years 0}
           :stop-name "Laanipalo P"
           :stop-sequence 10}))