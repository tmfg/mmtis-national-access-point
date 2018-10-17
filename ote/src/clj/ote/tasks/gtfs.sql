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

-- name: service-route-hashes-for-date-range
-- Return all route hashes for routes of the given service for the given date range.
WITH dates AS (
  SELECT :start-date::DATE + d AS date
    FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT d.date, rh."route-short-name", rh."route-long-name", rh."trip-headsign",
       string_agg(rh.hash::text, ' ') as hash
  FROM dates d
  LEFT JOIN "gtfs-date-hash" dh
    ON (dh.date = d.date AND
        dh."package-id" = ANY(gtfs_service_packages_for_date(:service-id::INTEGER, d.date)))
  LEFT JOIN LATERAL unnest(dh."route-hashes") AS rh ON TRUE
 GROUP BY d.date, rh."route-short-name", rh."route-long-name", rh."trip-headsign", dh."package-id"
 ORDER BY d.date;


-- name: service-routes-with-date-range
SELECT * FROM gtfs_service_routes_with_daterange(:service-id::INTEGER);

-- name: route-trips-for-dateXXX
SELECT trip."package-id", (trip.trip).*
  FROM gtfs_route_tripdata_for_date(
                 gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE),
                 :date::DATE,
                 :route-short-name, :route-long-name, :trip-headsign) g (tripdata)
  JOIN LATERAL unnest(g.tripdata) AS trip ON TRUE;

-- name: fetch-route-trips-for-date
SELECT t."package-id", trip."trip-id",
       stoptime."stop-id", stoptime."departure-time", stoptime."stop-sequence"
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
  JOIN LATERAL unnest(trip."stop-times") as stoptime ON TRUE
 WHERE r."package-id" IN (SELECT unnest(gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)))
   AND ROW(r."package-id", t."service-id")::service_ref IN
       (SELECT * FROM gtfs_services_for_date(
        (SELECT gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)), :date::DATE))
   AND COALESCE(r."route-short-name",'') = COALESCE(:route-short-name::TEXT,'')
   AND COALESCE(r."route-long-name",'') = COALESCE(:route-long-name::TEXT,'')
   AND COALESCE(trip."trip-headsign",'') = COALESCE(:trip-headsign::TEXT,'')
 ORDER BY t."package-id", trip."trip-id", stoptime."stop-sequence";
