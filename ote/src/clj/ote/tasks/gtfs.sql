-- name: select-gtfs-urls-update
SELECT eid.id as id, (eid."external-interface").url, eid.format[1], eid.license,
       ts."transport-operator-id" as "operator-id", ts.id as "ts-id", eid."gtfs-imported" as "last-import-date"
  FROM "external-interface-description"  eid
  JOIN "transport-service" ts ON eid."transport-service-id" = ts.id
  JOIN "transport-operator" top ON  top.id = ts."transport-operator-id"
 WHERE ts."published?" = TRUE
   AND top.id NOT IN (:blacklist)
   AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
   AND ( "gtfs-imported" < (current_timestamp - '1 day'::interval) OR "gtfs-imported" IS NULL)
 ORDER BY "gtfs-imported" ASC LIMIT 1
   FOR UPDATE SKIP LOCKED;

-- name: refresh-nightly-transit-changes
SELECT refresh_nightly_transit_changes();
