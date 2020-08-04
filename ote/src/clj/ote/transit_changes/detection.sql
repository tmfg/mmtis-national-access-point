-- name: service-route-hashes-for-date-range
-- Return all route hashes for routes of the given service for the given date range.
WITH dates AS (
  SELECT :start-date::DATE + d AS date
  FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT x.date AS DATE, string_agg(x.hash,' ' ORDER BY x.eid asc) AS hash, x."route-hash-id" AS "route-hash-id"
FROM (
       SELECT DISTINCT ON (concat(d.date, rh.hash)) concat(d.date, rh.hash) as ddd,
                                                    d.date, rh."route-short-name", rh."route-long-name", rh."trip-headsign",
              COALESCE(rh."route-hash-id", :route-hash-id) as "route-hash-id", e.id as eid,
              string_agg(rh.hash::text, ' ' ORDER BY e.id ASC) as hash
         FROM dates d
              LEFT JOIN "gtfs-date-hash" dh ON (dh."transport-service-id" = :service-id::INTEGER
                                                    AND dh.date = d.date
                                                    AND dh."package-id" = ANY(gtfs_service_packages_for_date(:service-id::INTEGER, d.date)))
              -- Join gtfs_package to get external-interface-description-id
              LEFT JOIN gtfs_package p ON p.id = dh."package-id" AND p."deleted?" = FALSE
              LEFT JOIN "external-interface-description" e ON e.id = p."external-interface-description-id"
              LEFT JOIN LATERAL unnest(dh."route-hashes") AS rh ON rh."route-hash-id" = :route-hash-id
        GROUP BY d.date, rh."route-short-name", rh."route-long-name", rh."trip-headsign", rh."route-hash-id", rh.hash, eid) x
GROUP BY x.date, x."route-hash-id"
ORDER BY x.date asc;

-- name: service-packages-for-date-range
WITH dates AS (
  SELECT :start-date::DATE + d AS date
    FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT DISTINCT pids.id
  FROM dates d
  JOIN LATERAL unnest(gtfs_service_packages_for_date(:service-id::INTEGER, d.date)) pids (id) ON TRUE;

-- name: service-packages-for-detection-date
WITH dates AS (
    SELECT :start-date::DATE + d AS date
    FROM generate_series(0, :end-date::DATE - :start-date::DATE) s (d)
)
SELECT DISTINCT pids.id
  FROM gtfs_package p,
       dates d
       JOIN LATERAL unnest(gtfs_packages_for_detection(:service-id::INTEGER, d.date)) pids (id) ON TRUE
 WHERE p."transport-service-id" = :service-id::INTEGER
   AND pids.id = p.id
   AND p.created <= :detection-date::DATE;

-- name: service-routes-with-date-range
SELECT * FROM gtfs_routes_for_change_detection(:service-id::INTEGER, :detection-date::DATE);

-- name: fetch-route-trips-for-date-in-detection
SELECT x."package-id", x."route-id", x."trip-id",
       x."stop-id", x."departure-time", x."stop-sequence",
       x."stop-name", x."stop-lat", x."stop-lon", x."stop-fuzzy-lat", x."stop-fuzzy-lon"
FROM (
         WITH routes AS (
             SELECT --DISTINCT ON (dr."route-id")
                    dr."route-id",
                    id, "package-id", "route-hash-id"
             FROM "detection-route" dr
             WHERE dr."package-id" in (SELECT unnest(gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)))
         )
         SELECT DISTINCT ON (concat(stoptime."stop-id",stoptime."departure-time", stoptime."stop-sequence"))
             concat(stoptime."stop-id",stoptime."departure-time", stoptime."stop-sequence") as ddd,
             t."package-id", r."route-id", trip."trip-id",
             stoptime."stop-id", stoptime."departure-time", stoptime."stop-sequence",
             stop."stop-name", stop."stop-lat", stop."stop-lon", stop."stop-fuzzy-lat", stop."stop-fuzzy-lon"
         FROM routes r
                  JOIN "gtfs_package" p ON p.id = r."package-id" AND p."deleted?" = FALSE
                  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
                  JOIN LATERAL unnest(t.trips) trip ON true
                  JOIN LATERAL unnest(trip."stop-times") as stoptime ON TRUE
                  JOIN "gtfs-stop" stop ON (stop."package-id" = r."package-id" AND stop."stop-id" = stoptime."stop-id")
         WHERE ROW(r."package-id", t."service-id")::service_ref IN
               (SELECT * FROM gtfs_services_for_date(
                       (SELECT gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)), :date::DATE))
           AND r."route-hash-id" = :route-hash-id
         ORDER BY ddd,p."external-interface-description-id", t."package-id", trip."trip-id", stoptime."stop-sequence") x
order BY x."trip-id",  x."stop-sequence",  x."departure-time";

-- name: fetch-route-trips-for-date-to-visualization
SELECT x."package-id", x."route-id", x."trip-id",
       x."stop-id", x."departure-time", x."stop-sequence",
       x."stop-name", x."stop-lat", x."stop-lon", x."stop-fuzzy-lat", x."stop-fuzzy-lon"
FROM (
    WITH routes AS (
        SELECT --DISTINCT ON (dr."route-id")
        dr."route-id", id, "package-id", "route-hash-id"
        FROM "detection-route" dr
        WHERE dr."package-id" in (SELECT unnest(gtfs_service_packages_for_detection_date(:service-id::INTEGER, :date::DATE,:detection-date::DATE)))
    )
    SELECT DISTINCT ON (concat(stoptime."stop-id",stoptime."departure-time", stoptime."stop-sequence"))
        concat(stoptime."stop-id",stoptime."departure-time", stoptime."stop-sequence") as ddd,
           t."package-id", r."route-id", trip."trip-id",
           stoptime."stop-id", stoptime."departure-time", stoptime."stop-sequence",
           stop."stop-name", stop."stop-lat", stop."stop-lon", stop."stop-fuzzy-lat", stop."stop-fuzzy-lon"
    FROM routes r
             JOIN "gtfs_package" p ON p.id = r."package-id" AND p."deleted?" = FALSE
             JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
             JOIN LATERAL unnest(t.trips) trip ON true
             JOIN LATERAL unnest(trip."stop-times") as stoptime ON TRUE
             JOIN "gtfs-stop" stop ON (stop."package-id" = r."package-id" AND stop."stop-id" = stoptime."stop-id")
    WHERE ROW(r."package-id", t."service-id")::service_ref IN
          (SELECT * FROM gtfs_services_for_date(
                  (SELECT gtfs_service_packages_for_detection_date(:service-id::INTEGER, :date::DATE, :detection-date::DATE)), :date::DATE))
      AND r."route-hash-id" = :route-hash-id
    ORDER BY ddd,p."external-interface-description-id", t."package-id", trip."trip-id", stoptime."stop-sequence") x
ORDER BY x."trip-id",  x."stop-sequence",  x."departure-time";

-- name: generate-date-hashes
SELECT gtfs_generate_date_hashes(:package-id::INTEGER, :transport-service-id::INTEGER);

-- name: generate-date-hashes-for-future
SELECT gtfs_generate_date_hashes_for_future(:package-id::INTEGER, :transport-service-id::INTEGER, :from-date::DATE);

-- name: fetch-services-packages
SELECT p.id as "package-id"
  FROM "external-interface-description" e, "gtfs_package" p
 WHERE e.id = p."external-interface-description-id"
   AND e."transport-service-id" = :service-id;

-- name: fetch-distinct-services-from-transit-changes
SELECT distinct t."transport-service-id" as id
  FROM "gtfs-transit-changes" t;

-- name: fetch-monthly-packages
SELECT MAX(p.id) as "package-id", p."transport-service-id"
  FROM gtfs_package p, "transport-service" t
 WHERE p."transport-service-id" = t.id
   AND t."commercial-traffic?" = TRUE
 GROUP BY concat(p."transport-service-id", to_char(p.created, '-YYYY-MM')), p."transport-service-id"
 ORDER BY concat(p."transport-service-id", to_char(p.created, '-YYYY-MM')) asc;

-- name: fetch-all-packages
SELECT p.id as "package-id", p."transport-service-id"
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
