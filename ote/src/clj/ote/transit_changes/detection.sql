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
        stop."stop-name", stop."stop-lat", stop."stop-lon"
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
  JOIN LATERAL unnest(trip."stop-times") as stoptime ON TRUE
  JOIN "gtfs-stop" stop ON (stop."package-id" = r."package-id" AND stop."stop-id" = stoptime."stop-id")
 WHERE r."package-id" IN (SELECT unnest(gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)))
   AND ROW(r."package-id", t."service-id")::service_ref IN
       (SELECT * FROM gtfs_services_for_date(
        (SELECT gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE)), :date::DATE))
   AND COALESCE(r."route-short-name",'') = COALESCE(:route-short-name::TEXT,'')
   AND COALESCE(r."route-long-name",'') = COALESCE(:route-long-name::TEXT,'')
   AND COALESCE(trip."trip-headsign",'') = COALESCE(:trip-headsign::TEXT,'')
 ORDER BY t."package-id", trip."trip-id", stoptime."stop-sequence";
