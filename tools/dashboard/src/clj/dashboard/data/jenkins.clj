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
(defn jobs []
  (:jobs (jenkins-get "json?tree=jobs[name,lastBuild[result,number,duration,timestamp]]")))
