(ns ote.nap.users
  "Integrate into CKAN user and group information via database queries."
  (:require [jeesql.core :refer [defqueries]]
            [ote.db.utils :as db-utils]
            [taoensso.timbre :as log]))

;; FIXME:
;; CKAN tables are owned by the ckan user and access needs to be granted to them
;; for the OTE application user. Automate this in flyway.
;;
;; GRANT ALL PRIVILEGES ON "group" TO ote;
;; GRANT ALL PRIVILEGES ON "user" TO ote;
;; GRANT ALL PRIVILEGES ON "member" TO ote;

(defqueries "ote/nap/users.sql")

(defn find-user [db username]
  (let [rows (map db-utils/underscore->structure
                  (fetch-user-by-username db {:username username}))
        user (first rows)]
    (when user
      (-> user
          (assoc-in [:user :transit-authority?]
                    (is-transit-authority-user? db {:user-id (get-in user [:user :id])}))
          (assoc :groups (map :group rows))))))



(defn wrap-user-info
  "Ring middleware to fetch user info based on :user-id (username)."
  [db handler]
  ;; Cache fetched user info for 15 minutes
  (fn [{username :user-id :as req}]
    (if-let [user (when username
                    (find-user db username))]
      (do (log/debug "User found: " (pr-str user))
          (handler (assoc req :user user)))
      (do (log/info "No user info, return 401")
          {:status 401
           :body "Unknown user"}))))
