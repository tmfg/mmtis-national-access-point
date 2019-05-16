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
       (SELECT array_agg(dc)
        FROM "external-interface-description" eid
                 JOIN LATERAL unnest(eid."data-content") dc ON TRUE
        WHERE ts.id = eid."transport-service-id") as "interface-types"
FROM "transport-service" ts
         LEFT JOIN "external-interface-description" eid on (ts.id = eid."transport-service-id")
         LEFT JOIN LATERAL unnest("data-content") types ON true
WHERE ts."transport-operator-id" in (:operator-ids)
GROUP BY ts.id
ORDER BY ts."type" DESC, ts.modified DESC NULLS LAST;
