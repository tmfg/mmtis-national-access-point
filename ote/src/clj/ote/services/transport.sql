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
       ts."validate",
       ts."parent-id",
       CASE WHEN (select service."parent-id" as "child-parent-id" FROM "transport-service" service WHERE service."parent-id" = ts.id)
            IS NOT NULL THEN true
           ELSE false
       END as "has-child?",
       ts."created",
       ts."modified",
       ts."transport-type",
       (SELECT array_agg(dc)
          FROM "external-interface-description" eid
               JOIN LATERAL unnest(eid."data-content") dc ON TRUE
         WHERE ts.id = eid."transport-service-id") as "interface-types"
  FROM "transport-service" ts
 WHERE ts."transport-operator-id" in (:operator-ids)
 ORDER BY ts."type" DESC, ts.modified DESC NULLS LAST;
