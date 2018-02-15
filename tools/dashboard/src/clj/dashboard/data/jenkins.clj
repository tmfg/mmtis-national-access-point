(ns dashboard.data.jenkins
  (:require [org.httpkit.client :as http-client]
            [cheshire.core :as cheshire]))

(def config (delay (read-string (slurp "jenkins.edn"))))

(defn- jenkins-get [api-path]
  (let [{:keys [url user api-token]} @config
        call-url (str url "/api/" api-path)
        {:keys [body status] :as response}
        @(http-client/get call-url
                          {:basic-auth [user api-token]
                           :timeout 15000})]
    (if (= 200 status)
      (cheshire/decode body keyword)
      (throw (ex-info "Jenkins Call failed" {:url call-url
                                             :response response})))))

(defn job-progress [{:keys [lastBuild] :as job}]
  (if (:result lastBuild)
    job
    (assoc job :progress (double (* 100 (/ (- (System/currentTimeMillis) (:timestamp lastBuild))
                                           (:estimatedDuration lastBuild)))))))
(defn jobs []
  (let [skip-jobs (or (:skip-jobs @config) #{})
        job-filter (complement (comp skip-jobs :name))]
    (map job-progress
         (filter job-filter
                 (:jobs (jenkins-get "json?tree=jobs[name,lastBuild[result,number,duration,timestamp,estimatedDuration]]"))))))
