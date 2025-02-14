(ns ote.nap.users
  "Integrate into CKAN user and group information via database queries."
  (:require [jeesql.core :refer [defqueries]]
            [ote.db.utils :as db-utils]
            [taoensso.timbre :as log]
            [clojure.core.cache :as cache]))

;; FIXME:
;; CKAN tables are owned by the ckan user and access needs to be granted to them
;; for the OTE application user. Automate this in flyway.
;;
;; GRANT ALL PRIVILEGES ON "group" TO ote;
;; GRANT ALL PRIVILEGES ON "user" TO ote;
;; GRANT ALL PRIVILEGES ON "member" TO ote;

(defqueries "ote/nap/users.sql")

(declare list-users fetch-user-by-id delete-user! search-user-operators-and-members list-authority-users)

(defn find-user [db user-id]
  (let [rows (map db-utils/underscore->structure
                  (fetch-user-by-id db {:user-id user-id}))
        user (first rows)]
    (when user
      (-> user
          (assoc-in [:user :transit-authority?]
                    (has-group-attribute? db {:user-id         (get-in user [:user :id])
                                              :group-attribute "transit-authority?"}))
          (assoc-in [:user :authority-group-admin?]
                    (has-group-attribute? db {:user-id         (get-in user [:user :id])
                                              :group-attribute "authority-group-admin?"}))
          (assoc :groups (map :group rows))))))

(defn wrap-user-info
  "Ring middleware to fetch user info based on :user-id."
  [{:keys [db allow-unauthenticated?]} handler]
  (fn [{user-id :user-id :as req}]
    (if-let [user (when user-id
                    (find-user db user-id))]
      (handler (assoc req :user user))
      (if allow-unauthenticated?
        (handler req)
        (do (log/info "No user info, return 401")
            {:status 401
             :body "Unknown user"})))))

(defn fetch-authority-group-admin-id
  [db]
  (authority-group-admin-id db))