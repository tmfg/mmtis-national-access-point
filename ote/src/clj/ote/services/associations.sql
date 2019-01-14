-- name: fetch-operators-associated-services
SELECT ts.name as "service-name",
       ts.id as "service-id",
       top."business-id" as "operator-business-id",
       top.id as "operator-id",
       top.name as "operator-name",
       CONCAT(ts.name, ' - ', top.name) as "service-operator"
FROM "associated-service-operators" aso
       LEFT JOIN "transport-service" ts ON aso."service-id" = ts.id
       LEFT JOIN "transport-operator" top ON ts."transport-operator-id" = top.id
WHERE aso."operator-id" = :operator-id;