(ns ote.services.pre-notice-test
  (:require [clojure.test :as t :refer [deftest testing is]]
            [ote.test :refer [system-fixture http-get http-post fetch-id-for-username]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [com.stuartsierra.component :as component]
            [ote.db.service-generators :as s-generators]
            [ote.services.pre-notices :as pre-notices]
            [ote.db.generators :as generators]))

(t/use-fixtures :each
                (system-fixture
                  :pre-notices (component/using (pre-notices/->PreNotices
                                                  (:pre-notices (read-string (slurp "config.edn")))) [:http :db])))

(defn- notice-count [notice-list type]
  (count (filter (fn [x]
                   (when (= type (:ote.db.transit/pre-notice-state x)) x)) notice-list)))

(deftest save-pre-notice
  (let [generated-notice (gen/generate s-generators/gen-pre-notice)
        response (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin")
                            "pre-notice"
                            generated-notice)
        notice (:transit response)
        id (get notice :ote.db.transit/id)]
    ;; Saved ok
    (is (pos? id))
    ;; State ok
    (is (= :draft (:ote.db.transit/pre-notice-state notice)))
    ;; Type ok
    (is (= [:new] (:ote.db.transit/pre-notice-type notice)))
    ;; Not modified
    (is (nil? (:ote.db.transit/modified notice)))
    ;; Created-by ok
    (is (not (nil? (:ote.db.modification/created-by notice))))
    ;; Created ok
    (is (not (nil? (:ote.db.modification/created notice))))
    ;; Not modified-by
    (is (nil? (:ote.db.transit/modified-by notice)))
    ;; Other type description
    (is (not (nil? (:ote.db.transit/other-type-description notice))))
    ;; Effective dates
    (is (not (nil? (:ote.db.transit/effective-dates notice))))
    ;; Route description
    (is (not (nil? (:ote.db.transit/route-description notice))))
    ;; Regions
    (is (not (nil? (:ote.db.transit/regions notice))))
    ;; Nil sent
    (is (nil? (:ote.db.transit/sent notice)))))

(deftest edit-pre-notice
  (let [generated-notice (gen/generate s-generators/gen-pre-notice)
        response (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin")
                            "pre-notice"
                            generated-notice)
        notice (:transit response)
        id (get notice :ote.db.transit/id)]

    ;; Saved ok
    (is (pos? id))

    ;; Edit data
    (let [generated-url (gen/generate generators/gen-url)
          modified-notice (assoc notice :ote.db.transit/url generated-url)
          edit-response (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin")
                                   "pre-notice"
                                   modified-notice)
          edited-notice (:transit edit-response)]
      (is (= generated-url (:ote.db.transit/url edited-notice))))))

(deftest operator-pre-notice-list-draft
  (let [generated-notice (gen/generate s-generators/gen-pre-notice)
        response (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin")
                            "pre-notice"
                            generated-notice)
        list (:transit (http-get (fetch-id-for-username (:db ote.test/*ote*) "admin")
                                 "pre-notices/list"))]

    ;; One draft
    (is (= 1 (notice-count list :draft)))
    (is (= 0 (notice-count list :sent)))

    ;; Save as sent
    (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin") "pre-notice" (assoc generated-notice :ote.db.transit/pre-notice-state :sent))
    ;; 1 sent
    (is (= 1 (notice-count (:transit (http-get (fetch-id-for-username (:db ote.test/*ote*) "admin")
                                               "pre-notices/list")) :sent)))))

(deftest operator-pre-notice-list-sent
  (let [generated-notice (gen/generate s-generators/gen-pre-notice)
        sent-notice (assoc generated-notice :ote.db.transit/pre-notice-state :sent)
        response (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin")
                            "pre-notice"
                            sent-notice)
        list (:transit (http-get (fetch-id-for-username (:db ote.test/*ote*) "admin")
                                 "pre-notices/list"))]

    ;; 0 draft
    (is (= 0 (notice-count list :draft)))
    ;; 1 sent
    (is (= 1 (notice-count list :sent)))))

(deftest authority-pre-notice-list
  (let [generated-notice (gen/generate s-generators/gen-pre-notice)
        sent-notice (assoc generated-notice :ote.db.transit/pre-notice-state :sent)
        response (http-post (fetch-id-for-username (:db ote.test/*ote*) "admin")
                            "pre-notice"
                            sent-notice)
        list (:transit (http-get (fetch-id-for-username (:db ote.test/*ote*) "admin")
                                 "pre-notices/authority-list"))]

    ;; 1 sent notice
    (is (= 1 (count list)))))
