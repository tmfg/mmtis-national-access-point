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

-- name: services-for-nightly-change-detection
SELECT ts.id
  FROM "transport-service" ts
 WHERE EXISTS(SELECT id
                FROM "external-interface-description" eid
               WHERE ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                 AND 'route-and-schedule' = ANY(eid."data-content")
                 AND eid."transport-service-id" = ts.id)
   AND (:force = TRUE OR gtfs_should_calculate_transit_change(ts.id));

-- name: upsert-service-transit-change
SELECT gtfs_upsert_service_transit_changes(:service-id::INTEGER);

-- name: service-routes-with-hashes
-- Return all routes for the given service with date hashes aggregated for
-- the given date range.
WITH dates AS (
  -- Return all dates between :start-date and :end-date (both inclusive)
  SELECT :start-date::DATE + d AS date
    FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT rd.*,
       (SELECT string_agg(COALESCE(gtfs_service_route_date_hash(
                   :service-id::INTEGER, d.date,
                   rd."route-short-name", rd."route-long-name", rd."trip-headsign"), 'N/A'), ',')
          FROM dates d) AS hashes
  FROM gtfs_service_routes_with_daterange(:service-id::INTEGER) rd;
