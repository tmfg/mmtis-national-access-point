-- name: fetch-operator-service-counts
SELECT t."transport-operator-id" as id, COUNT(t.id) AS services
  FROM "transport-service" t
 WHERE t."transport-operator-id" IN (:operators) AND
       t."published?" = TRUE
GROUP BY t."transport-operator-id"

-- name: count-matching-operators
-- single?: true
SELECT COUNT(id)
  FROM "transport-operator" o
 WHERE o.name ILIKE :name
   AND o."deleted?" = FALSE


-- name: count-all-operators
-- single?: true
SELECT COUNT(id)
  FROM "transport-operator" o
 WHERE o."deleted?" = FALSE

-- name: delete-transport-operator
-- Delete all operator data except published external interface data from ckan
SELECT del_operator(:operator-group-name);

-- name: does-business-id-exists
-- We need to prevent users to create multiple companies with the same business-id, so check if business id is already added
SELECT id FROM "transport-operator" op
 WHERE op."business-id" = :business-id
 LIMIT 1;
