-- name: fetch-transport-operator-ckan-description
-- single?: true
SELECT description FROM "group" WHERE id = :id;
