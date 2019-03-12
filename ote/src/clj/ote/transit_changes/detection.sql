-- name: service-route-hashes-for-date-range
-- Return all route hashes for routes of the given service for the given date range.
WITH dates AS (
  SELECT :start-date::DATE + d AS date
    FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT d.date, rh."route-short-name", rh."route-long-name", rh."trip-headsign", rh."route-hash-id",
       string_agg(rh.hash::text, ' ' ORDER BY p."external-interface-description-id" ASC) as hash
  FROM dates d
  LEFT JOIN "gtfs-date-hash" dh
    ON (dh.date = d.date AND
        dh."package-id" = ANY(gtfs_service_packages_for_date(:service-id::INTEGER, d.date)))
  -- Join gtfs_package to get external-interface-description-id
  JOIN gtfs_package p ON p.id = dh."package-id" AND p."deleted?" = FALSE
  LEFT JOIN LATERAL unnest(dh."route-hashes") AS rh ON TRUE
 GROUP BY d.date, rh."route-short-name", rh."route-long-name", rh."trip-headsign", rh."route-hash-id"
 ORDER BY d.date;

-- name: service-packages-for-date-range
WITH dates AS (
  SELECT :start-date::DATE + d AS date
    FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT DISTINCT pids.id
  FROM dates d
  JOIN LATERAL unnest(gtfs_service_packages_for_date(:service-id::INTEGER, d.date)) pids (id) ON TRUE;

-- name: service-routes-with-date-range
SELECT * FROM gtfs_service_routes_with_daterange(:service-id::INTEGER);

-- name: fetch-route-trips-for-date
SELECT t."package-id", trip."trip-id",
       stoptime."stop-id", stoptime."departure-time", stoptime."stop-sequence",
        stop."stop-name", stop."stop-lat", stop."stop-lon", stop."stop-fuzzy-lat", stop."stop-fuzzy-lon"
  FROM "detection-route" r
  JOIN "gtfs_package" p ON p.id = r."package-id" AND p."deleted?" = FALSE
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
  JOIN LATERAL unnest(trip."stop-times") as stoptime ON TRUE
  JOIN "gtfs-stop" stop ON (stop."package-id" = r."package-id" AND stop."stop-id" = stoptime."stop-id")
 WHERE r."package-id" IN (SELECT unnest(gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)))
   AND ROW(r."package-id", t."service-id")::service_ref IN
       (SELECT * FROM gtfs_services_for_date(
        (SELECT gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)), :date::DATE))
   AND r."route-hash-id" = :route-hash-id
 ORDER BY p."external-interface-description-id", t."package-id", trip."trip-id", stoptime."stop-sequence";

-- name: generate-date-hashes
SELECT gtfs_generate_date_hashes(:package-id::INTEGER);

-- name: generate-date-hashes-for-future
SELECT gtfs_generate_date_hashes_for_future(:package-id::INTEGER);

-- name: fetch-services-packages
SELECT p.id as "package-id"
  FROM "external-interface-description" e, "gtfs_package" p
 WHERE e.id = p."external-interface-description-id"
   AND e."transport-service-id" = :service-id;

-- name: fetch-distinct-services-from-transit-changes
SELECT distinct t."transport-service-id" as id
  FROM "gtfs-transit-changes" t;

-- name: fetch-monthly-packages
SELECT MAX(p.id) as "package-id"
  FROM gtfs_package p, "transport-service" t
 WHERE p."transport-service-id" = t.id
   AND t."commercial-traffic?" = TRUE
 GROUP BY concat(p."transport-service-id", to_char(p.created, '-YYYY-MM'))
 ORDER BY concat(p."transport-service-id", to_char(p.created, '-YYYY-MM')) asc;

-- name: fetch-all-packages
SELECT p.id as "package-id"
  FROM gtfs_package p, "transport-service" t
 WHERE p."transport-service-id" = t.id
   AND t."commercial-traffic?" = TRUE
 ORDER BY p.id asc;

-- name: fetch-contract-packages
SELECT p.id as "package-id"
  FROM gtfs_package p, "transport-service" t
 WHERE p."transport-service-id" = t.id
   AND t."commercial-traffic?" = FALSE
 ORDER BY p.id asc;
