-- name: next-stop-sequence-number
-- single?: true
SELECT nextval('ote_user_stop_code');

-- name: fetch-services-with-route
SELECT ts.id as id
  FROM "transport-service" ts
       JOIN "external-interface-description" e ON e."transport-service-id" = ts.id
 WHERE ts."transport-operator-id" = :operator-id
   AND (e."external-interface").url = :url