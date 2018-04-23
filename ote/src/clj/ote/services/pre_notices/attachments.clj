(ns ote.services.pre-notices.attachments
  (:require [amazonica.aws.s3 :as s3]
            [taoensso.timbre :as log]
            [ote.authorization :as authorization]
            [specql.op :as op]
            [ring.util.io :as ring-io]
            [clojure.java.io :as io]
            [compojure.core :refer [routes POST GET]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [specql.core :as specql]
            [ote.db.transit :as transit]
            [ote.db.modification :as modification]
            [ote.components.http :as http]
            [ote.nap.users :as users]))

(defn upload-attachment [db {bucket :bucket :as config} {user :user :as req}]
  (let [uploaded-file (get-in req [:multipart-params "file"])
        _ (assert (and (:filename uploaded-file)
                       (:tempfile uploaded-file))
                  "No uploaded file")
        file (specql/insert! db ::transit/pre-notice-attachment
                             (modification/with-modification-timestamp-and-user
                               {::transit/attachment-file-name (:filename uploaded-file)}
                               ::transit/id user))]

    (s3/put-object bucket (str (::transit/id file) "_" (:filename uploaded-file))
                   (:tempfile uploaded-file))
    (http/transit-response file)))

(defn- attachment-info [db user pre-notice-attachment-id]
  (let [can-view-all-attachments?
        (or (authorization/admin? user)
            (users/is-transit-authority-user? db {:user-id (authorization/user-id user)}))]
    (first
     (specql/fetch db ::transit/pre-notice-attachment
                   #{::transit/id ::transit/attachment-file-name}
                   (op/and
                    {::transit/id pre-notice-attachment-id}
                    (when-not can-view-all-attachments?
                      {::transit/created-by (authorization/user-id user)}))))))

(defn download-attachment [db {bucket :bucket :as config} {user :user :as req}]
  (let [attachment (attachment-info db user (Long/parseLong (get-in req [:params :id])))
        s3-file (when attachment
                  (s3/get-object bucket (str (::transit/id attachment) "_"
                                             (::transit/attachment-file-name attachment))))]
    (if-not s3-file
      {:status 404
       :body "No such attachment"}
      {:status 200
       :body (ring-io/piped-input-stream
              (fn [out]
                (io/copy (:input-stream s3-file) out)))})))

(defn attachment-routes [db config]
  (if-not (:bucket config)
    (do (log/error "No S3 bucket configured, attachment upload/download disabled")
        nil)
    (wrap-multipart-params
     (routes
      (POST "/pre-notice/upload" req
            (upload-attachment db config req))
      (GET "/pre-notice/attachment/:id" req
           (download-attachment db config req))))))
