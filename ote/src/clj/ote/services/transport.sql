-- name: fetch-transport-operator-ckan-description
-- single?: true
SELECT description FROM "group" WHERE id = :id;

-- name: fetch-transport-services
SELECT ts.id,
    ts."transport-operator-id",
    ts."name",
    ts."type",
    ts."sub-type",
    ts."published",
    ts."created",
    ts."modified",
    ts."transport-type",
    eid."data-content" as "interface-types"
FROM "transport-service" ts LEFT OUTER JOIN "external-interface-description" eid on (ts.id = eid."transport-service-id")
WHERE ts."transport-operator-id" in (:operator-ids)
ORDER BY ts."type" DESC, ts.modified DESC NULLS LAST;
