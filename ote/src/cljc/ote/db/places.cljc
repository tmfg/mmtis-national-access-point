(ns ote.db.places
  (:require [ote.tietokanta.specql-db :refer [define-tables]]))

(define-tables
  ["finnish_municipalities" ::finnish-municipalities])
