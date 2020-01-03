(ns ote.services.places
  "Provides a service for the frontend to search for geographical places.
  Returns data as transit in as Clojure data (see `ote.geo` namespace for
  a definition of the geographical data)."
  (:require [ote.components.http :as http]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET]]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [ote.util.functor :refer [fmap]]
            [specql.core :as specql]
            [ote.db.places :as places]
            [specql.op :as op]
            [jeesql.core :refer [defqueries]]
            [ote.db.transport-service :as t-service])
  (:import (java.net URLEncoder)))

(defqueries "ote/services/places.sql")


(defn place-completions
  "Return a list of completions that match the given search term."
  [db term]
  (vec (fetch-operation-area-search db {:name (str "%" term "%")})))

(defn place-by-id [db id]
  (first
    (specql/fetch db ::places/places
                  #{::places/namefin ::places/id ::places/type ::places/location}
                  {::places/id id})))

(defn save-transport-service-operation-area!
  "Clear old place links and insert new links for the given transport service.
  Should be called within a transaction."
  [db transport-service-id places delete-existing?]

  (let [stored (into #{}
                     (comp (filter #(= (::places/type %) "stored"))
                           (map ::places/id))
                     places)]
    ;; Remove linked geometries, except drawn geometries that were not removed
    (when delete-existing?
      (specql/delete! db ::t-service/operation_area
                      (merge
                        {::t-service/transport-service-id transport-service-id}
                        (when-not (empty? stored)
                          {::t-service/id (op/not (op/in stored))})))))

  (doseq [{::places/keys [id namefin type primary?] :as place} places]
    (case type
      "drawn"
      (let [{id :id}
            (insert-geojson-for-transport-service<! db {:transport-service-id transport-service-id
                                                        :name namefin
                                                        :geojson (:geojson place)
                                                        :primary? primary?})]
        (insert-spatial-search-custom-area! db {:operation-area-id id}))

      ;; Stored geometry, update name
      "stored"
      (specql/update! db ::t-service/operation_area
                      {::t-service/description [{::t-service/lang "FI" ::t-service/text namefin}]}
                      {::t-service/id id
                       ::t-service/transport-service-id transport-service-id})

      ;; default, link new geometry by reference
      (link-transport-service-place! db {:transport-service-id transport-service-id
                                         :place-id id
                                         :name namefin
                                         :primary? primary?}))))

(defn fetch-transport-service-operation-area
  "Fetch operation area for the given transport service id."
  [db transport-service-id]
  (specql/fetch db ::t-service/operation_area_geojson
                #{::t-service/id ::t-service/description ::t-service/location-geojson
                  ::t-service/primary?}
                {::t-service/transport-service-id transport-service-id}))

(defn duplicate-operation-area [db old-service-id new-service-id places-from-ui]
  (let [;; Places with int id are coming fron database
        places-in-db (filter #(integer? (:ote.db.places/id %)) places-from-ui)
        ;; Places with string id's are made by user in ui
        created-places (filter #(not (integer? (:ote.db.places/id %))) places-from-ui)
        ;; Copy all places that are stored in db to the new service
        copied-places (copy-operation-area db {:old-service-id old-service-id :new-service-id new-service-id
                                               :ids (map :ote.db.places/id places-in-db)})

        ;; Save new places
        new-places (save-transport-service-operation-area! db new-service-id created-places false)]
    nil))

(defrecord Places [sources]
  component/Lifecycle
  (start [{db :db http :http :as this}]
    (assoc this ::stop
                (http/publish!
                  http
                  {:authenticated? false}
                  (routes
                    (GET "/place-completions/:term" [term]
                      (http/transit-response
                        (place-completions db term)))
                    (GET "/place/:id" [id]
                      {:status 200
                       :headers {"Content-Type" "application/json"}
                       :body (fetch-place-geojson-by-id db {:id id})})))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))
