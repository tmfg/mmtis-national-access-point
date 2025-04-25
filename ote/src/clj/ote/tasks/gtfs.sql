-- name: select-gtfs-urls-update
SELECT eid.id as id, TRIM((eid."external-interface").url) as url, eid.format[1] , eid.license,
       ts."transport-operator-id" as "operator-id", top."name" as "operator-name", ts.id as "ts-id",
       eid."gtfs-imported" as "last-import-date",
       eid."data-content" as "data-content"
  FROM "external-interface-description" eid
  JOIN "transport-service" ts ON eid."transport-service-id" = ts.id
  JOIN "transport-operator" top ON top.id = ts."transport-operator-id"
 WHERE ts.published IS NOT NULL
   AND top.id NOT IN (:blacklist)
   AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format) OR 'NeTEx' = ANY(eid.format))
   AND ( "gtfs-imported" < (current_timestamp - '1 day'::interval) OR "gtfs-imported" IS NULL)
 ORDER BY "gtfs-imported" ASC LIMIT 1
   FOR UPDATE SKIP LOCKED;

-- name: select-gtfs-url-for-service
SELECT eid.id as id, (eid."external-interface").url, eid.format[1], eid.license,
       ts."transport-operator-id" as "operator-id", top."name" as "operator-name", ts.id as "ts-id",
       eid."gtfs-imported" as "last-import-date",
       eid."data-content" as "data-content"
  FROM "external-interface-description" eid
  JOIN "transport-service" ts ON eid."transport-service-id" = ts.id
  JOIN "transport-operator" top ON top.id = ts."transport-operator-id"
 WHERE ts.published IS NOT NULL
   AND ts.id = :service-id
   AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format));

-- name: select-gtfs-url-for-interface
SELECT eid.id as id, (eid."external-interface").url, eid.format[1], eid.license,
       ts."transport-operator-id" as "operator-id", top."name" as "operator-name", ts.id as "ts-id",
       eid."gtfs-imported" as "last-import-date",
       eid."data-content" as "data-content"
FROM "external-interface-description" eid
         JOIN "transport-service" ts ON eid."transport-service-id" = ts.id
         JOIN "transport-operator" top ON top.id = ts."transport-operator-id"
WHERE ts.published IS NOT NULL
  AND ts.id = :service-id
  AND eid.id = :interface-id
  AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format) OR 'NeTEx' = ANY(eid.format));

-- name: services-for-nightly-change-detection
SELECT ts.id
  FROM "transport-service" ts,
       "transport-operator" top
 WHERE ts."sub-type" = 'schedule'
   AND ts."transport-operator-id" = top.id
   AND top.id NOT IN (:blacklist)
   AND EXISTS(SELECT id
                FROM "external-interface-description" eid
               WHERE ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                 AND 'route-and-schedule' = ANY(eid."data-content")
                 AND eid."transport-service-id" = ts.id)
   AND (:force = TRUE OR gtfs_should_calculate_transit_change(ts.id));

-- name: fetch-latest-gtfs-vaco-status
SELECT gp.id, gp."tis-entry-public-id", gp."tis-complete", gp."tis-success", gp."tis-magic-link",
       gp.tis_submit_completed, gp.tis_polling_completed, es."download-status", gp.created
  FROM gtfs_package gp
      LEFT JOIN "external-interface-download-status" es ON gp.id = es."package-id"
 WHERE gp."transport-service-id" = :service-id
   AND gp."external-interface-description-id" = :interface-id
   AND (gp.tis_entry_status is null OR (gp.tis_entry_status is not null and gp.tis_entry_status != 'cancelled'))
ORDER BY gp.created DESC
 LIMIT 1;

-- name: fetch-latest-netex-conversion
SELECT nc.filename, nc.status, nc."input-file-error", nc."validation-file-error"
    FROM "netex-conversion" nc
WHERE nc."transport-service-id" = :service-id
  AND nc."external-interface-description-id" = :interface-id
ORDER BY nc.modified DESC, nc.created DESC
 LIMIT 1;