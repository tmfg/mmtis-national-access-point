-- name: select-gtfs-urls-update
SELECT eid.id as id, (eid."external-interface").url, eid.format[1], eid.license,
       ts."transport-operator-id" as "operator-id", ts.id as "ts-id", eid."gtfs-imported" as "last-import-date"
  FROM "transport-service"  ts, "external-interface-description"  eid
 WHERE ts."published?" = TRUE
   AND ts.id = eid."transport-service-id"
   AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
   AND ( "gtfs-imported" < (current_timestamp - '1 day'::interval) OR "gtfs-imported" IS NULL)
 ORDER BY "gtfs-imported" ASC LIMIT 1
   FOR UPDATE SKIP LOCKED;
