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

-- name: update-child-parent-interfaces
SELECT child_parent_interfaces(:parent-id::INTEGER, :child-id::INTEGER);

-- name: update-old-package-interface-ids!
UPDATE gtfs_package p SET "external-interface-description-id" = :new-interface-id WHERE p."transport-service-id" = :service-id AND p."external-interface-description-id" NOT IN (:ids);

-- name: select-old-packages
select * from gtfs_package p WHERE p."transport-service-id" = :service-id AND p."external-interface-description-id" NOT IN (:ids);

-- name: fetch-child-service-interfaces
SELECT e.id, (e."external-interface").url as url
  FROM "external-interface-description" e
 WHERE e."transport-service-id" = :service-id
   AND 'route-and-schedule' = ANY(e."data-content");
