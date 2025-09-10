(ns ote.tasks.taxiui
  (:require [chime :as chime]
            [clj-time.core :as t]
            [clj-time.periodic :as periodic]
            [com.stuartsierra.component :as component]
            [hiccup.core :as hiccup]
            [ote.db.transport-operator :as t-operator]
            [ote.db.transport-service :as t-service]
            [ote.email :as email]
            [ote.localization :as localization]
            [ote.services.taxiui-service :as taxiui]
            [ote.util.email-template :as email-template]
            [specql.core :as specql]
            [taoensso.timbre :as log]))

(defn ^:private once-every-month
  [day-of-month]
  (let [start (-> (t/today-at-midnight)
                  (.withDayOfMonth day-of-month))
        adjust (t/months 1)]
    (periodic/periodic-seq (if (t/before? start (t/now))
                             (t/plus start adjust)
                             start)
                           adjust)))

(defn send-outdated-taxiui-prices-emails
  [config db email]
  (doseq [outdated (taxiui/fetch-pricing-statistics db {:age-filter :over-year-ago})]
    (println "outdated" outdated)
    (let [{:keys [service-id]} outdated
          service (some-> (specql/fetch db ::t-service/transport-service
                                        #{::t-service/id
                                          ::t-service/contact-email
                                          ::t-service/transport-operator-id}
                                        {::t-service/id service-id})
                          first)
          operator (some-> (specql/fetch db ::t-operator/transport-operator
                                         #{::t-operator/id
                                           ::t-operator/email}
                                         {::t-operator/id (::t-service/transport-operator-id service)})
                           first)
          recipient (or (::t-service/contact-email service)
                        (::t-operator/email operator)
                        "nap@fintraffic.fi")]
      (when-not false #_(:testing-env? config)
        (log/info "Initiating sending of outdated taxiui prices email to" recipient "for service" service-id)
        (localization/with-language
          "fi"
          (email/send! email {:to      recipient
                              :subject (localization/tr [:email-templates :validation-report :title])
                              :body    [{:type    "text/html;charset=utf-8"
                                         :content (str email-template/html-header
                                                       (hiccup/html (email-template/outdated-taxi-prices
                                                                      [:email-templates :outdated-taxi-prices :title]
                                                                      operator
                                                                      service
                                                                      ; note: we could group by operator to reduce total number of sent emails, but in practice this is probably fine
                                                                      [outdated])))}]}))
        (log/info "Email of outdated taxiui prices sent")))))

(defrecord TaxiUITasks [config]
  component/Lifecycle
  (start [{db :db email :email :as this}]
    (when-not (satisfies? email/Send email)
      (log/warn "Email component does not satisfy email/Send protocol"))
    (assoc this
      ::taxiui-tasks [(chime/chime-at (once-every-month 11)
                                      (fn [_]
                                        (#'send-outdated-taxiui-prices-emails config db email)))]))
  (stop [{stop-tasks ::taxiui-tasks :as this}]
    (doseq [stop stop-tasks]
      (stop))
    (dissoc this ::taxiui-tasks)))

(defn taxiui-tasks [config]
  (->TaxiUITasks config))