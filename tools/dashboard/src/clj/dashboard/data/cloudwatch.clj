(ns dashboard.data.cloudwatch
  (:require [amazonica.aws.cloudwatch :as cloudwatch]))

(defn last-five-minutes []
  {:start-time (java.util.Date. (- (System/currentTimeMillis)
                                   (* 1000 60 5)))
   :end-time (java.util.Date.)
   :period 300})

(defn fetch-metric
  "Fetch last five minutes' min/max/avg of given metric"
  [namespace metric-name dimensions]
  (cloudwatch/get-metric-statistics (merge (last-five-minutes)
                                           {:statistics ["Average" "Minimum" "Maximum"]
                                            :namespace namespace
                                            :metric-name metric-name
                                            :dimensions dimensions})))

(defn finap-db-load []
  (-> (fetch-metric "AWS/RDS"
                    "CPUUtilization"
                    [{:name "DBInstanceIdentifier" :value "napote-db-prod"}])
      :datapoints first (select-keys #{:minimum :maximum :average})))
