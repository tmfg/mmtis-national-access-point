-- name: fetch-operator-service-counts
SELECT t."transport-operator-id" as id, COUNT(t.id) AS services
  FROM "transport-service" t
 WHERE t."transport-operator-id" IN (:operators) AND
       t.published IS NOT NULL
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

-- name: fetch-users-within-same-business-id-family
-- We give user permissions for all users that have permissions for one or more operator with the same business-id.
SELECT u.id as "user-id"
  FROM "transport-operator" op
  JOIN "group" g ON g.id = op."ckan-group-id"
  JOIN "member" m ON g.id = m.group_id AND m.table_name = 'user'
  JOIN "user" u ON m.table_id = u.id
 WHERE op."business-id" = :business-id
   AND u.id != :user-id
 GROUP BY "user-id";

-- name: group-id-for-op
SELECT "ckan-group-id" FROM "transport-operator" op
 WHERE op."id" = :id
 LIMIT 1;

-- name: services-associated-to-operator
-- Fetch all services where given operator is associated.
SELECT tsc.name as "given-name",
       po.name as "operator-name",
       ts.name as "service-name",
       ts.id as "service-id",
       po."business-id" as "operator-business-id"
FROM "transport-service" ts
       JOIN LATERAL unnest(ts.companies) tsc ON "business-id" = :business-id
       JOIN "transport-operator" po
         ON ts."transport-operator-id" = po.id;


-- name: fetch-operator-users
SELECT u.id, u.name, u.fullname, u.email
  FROM member m
      JOIN "user" u ON m.table_id = u.id AND u.state = 'active' AND m.state = 'active'
WHERE m.group_id = :ckan-group-id;
