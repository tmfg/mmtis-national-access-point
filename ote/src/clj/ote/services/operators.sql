-- name: fetch-operator-service-counts
SELECT t."transport-operator-id" as id, COUNT(t.id) AS services
  FROM "transport-service" t
 WHERE t."transport-operator-id" IN (:operators) AND
       t."published?" = TRUE
GROUP BY t."transport-operator-id"

-- name: count-matching-operators
-- single?: true
SELECT COUNT(id) FROM "transport-operator" WHERE name ILIKE :name

-- name: count-all-operators
-- single?: true
SELECT COUNT(id) FROM "transport-operator"
