-- name: fetch-operator-service-counts
SELECT o.id, COUNT(t.id) AS services
  FROM "transport-operator" o
       JOIN "transport-service" t ON t."transport-operator-id" = o.id
 WHERE o.id IN (:operators)
GROUP BY o.id

-- name: count-matching-operators
-- single?: true
SELECT COUNT(id) FROM "transport-operator" WHERE name ILIKE :name

-- name: count-all-operators
-- single?: true
SELECT COUNT(id) FROM "transport-operator"
