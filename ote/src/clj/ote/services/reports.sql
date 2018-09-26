
-- This is just a placeholder report for testing. Real queries coming later.
-- name: fetch-transport-operators-report
SELECT op.name, op.id,
       op.phone, COALESCE(NULLIF(op.email,''), u.email) as "email"
  FROM "transport-operator" op
         JOIN "user" u ON u.name = (SELECT author
                                      FROM "revision" r
                                             JOIN "group" g ON op."ckan-group-id" = g.id
                                     WHERE r.id = g."revision_id"
                                     LIMIT 1)
 WHERE (SELECT COUNT(*) FROM "transport-service" ts WHERE ts."transport-operator-id" = op.id) = 0
 ORDER BY op.name ASC;
