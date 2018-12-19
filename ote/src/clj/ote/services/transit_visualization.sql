-- name: fetch-operator-date-hashes
SELECT date, hash::text
  FROM "gtfs-date-hash"
 WHERE hash IS NOT NULL
   AND "package-id" IN (SELECT id FROM gtfs_package WHERE "transport-operator-id" = :operator-id AND p."deleted?" = FALSE)
   -- Take dates from two months ago two 1 year in the future (but always full years)
   AND date >= make_date(EXTRACT(YEAR FROM (current_date - '2 months'::interval)::date)::integer, 1, 1)
   AND date <= make_date(EXTRACT(YEAR FROM (current_date + '1 year'::interval)::date)::integer, 12, 31);

-- name: fetch-routes-for-dates
-- Given a package id and two dates, fetch the routes operating on those days with
-- the amount of trips on each day.
WITH
date1_trips AS (
SELECT r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id") AS trips,
       string_agg(t.trips::TEXT,',') as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                           (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date1::date)),
                           :date1::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date1::date))
 GROUP BY r."route-short-name", r."route-long-name", trip."trip-headsign"
),
date2_trips AS (
SELECT r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id") AS trips,
       string_agg(t.trips::TEXT,',') as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                           (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date2::date)),
                           :date2::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date2::date))
 GROUP BY r."route-short-name", r."route-long-name", trip."trip-headsign"
)
SELECT x.* FROM (
 SELECT COALESCE(d1."route-short-name",d2."route-short-name") AS "route-short-name",
        COALESCE(d1."route-long-name",d2."route-long-name") AS "route-long-name",
        COALESCE(d1."trip-headsign",d2."trip-headsign") AS "trip-headsign",
        d1.trips as "date1-trips", d2.trips as "date2-trips",
        CASE
          WHEN d1.tripdata = d2.tripdata THEN false
          ELSE true
        END as "different?"
   FROM date1_trips d1 FULL OUTER JOIN date2_trips d2
        ON (COALESCE(d1."route-short-name",'') = COALESCE(d2."route-short-name", '') AND
            COALESCE(d1."route-long-name",'') = COALESCE(d2."route-long-name", '') AND
            COALESCE(d1."trip-headsign",'') = COALESCE(d2."trip-headsign",''))) x
ORDER BY x."route-short-name";

-- name: fetch-trip-stops-for-route-by-name-and-date
-- Fetch all trips with stop sequence
SELECT x."trip-id", x."trip-headsign",
       string_agg(concat(x."stop-name",'@',x."departure-time"), '->' ORDER BY x."trip-id", x."departure-time") as stops
  FROM (
SELECT trip."trip-id", trip."trip-headsign", stop."stop-name", stoptime."departure-time"
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (r."package-id" = t."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON TRUE
  JOIN LATERAL unnest(trip."stop-times") stoptime ON TRUE
  JOIN "gtfs-stop" stop ON (r."package-id" = stop."package-id" AND stoptime."stop-id" = stop."stop-id")
 WHERE COALESCE(:route-short-name::VARCHAR,'') = COALESCE(r."route-short-name",'')
   AND COALESCE(:route-long-name::VARCHAR,'') = COALESCE(r."route-long-name",'')
   AND COALESCE(:headsign::VARCHAR,'') = COALESCE(trip."trip-headsign",'')
   AND t."service-id" IN (SELECT gtfs_services_for_date(
                                (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date::DATE)),
                                :date::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date::DATE))
 ORDER BY stoptime."stop-sequence") x
 GROUP BY x."trip-id", x."trip-headsign";

-- name: fetch-route-trips-by-hash-and-date
-- Fetch geometries of route trips for given date by route hash-id
SELECT ST_AsGeoJSON(COALESCE(
          -- If there exists a "gtfs-shape" row for this package and trip shape id,
          -- use that to generate the detailed route line
          (SELECT ST_MakeLine(ST_MakePoint(rs."shape-pt-lon", rs."shape-pt-lat") ORDER BY rs."shape-pt-sequence") as routeline
             FROM "gtfs-shape" gs
             JOIN LATERAL unnest(gs."route-shape") rs ON TRUE
            WHERE gs."shape-id" = x."shape-id"
              AND gs."package-id" = x."package-id"),
          -- Otherwise use line generated from the stop sequence
          x."route-line")) as "route-line",
       array_agg(departure) as departures,
       string_agg(stops, '||') as stops
  FROM (SELECT (array_agg(stoptime."departure-time"))[1] as "departure",
               ST_MakeLine(ST_MakePoint(stop."stop-lon", stop."stop-lat") ORDER BY stoptime."stop-sequence") as "route-line",
               string_agg(CONCAT(stop."stop-lon", ',', stop."stop-lat", ',', stop."stop-name"), '||' ORDER BY stoptime."stop-sequence") as stops,
               trip."shape-id", r."package-id"
          FROM "detection-route" r
          JOIN "gtfs-trip" t ON (r."package-id" = t."package-id" AND r."route-id" = t."route-id")
          JOIN LATERAL unnest(t.trips) trip ON TRUE
          JOIN LATERAL unnest(trip."stop-times") stoptime ON TRUE
          JOIN "gtfs-stop" stop ON (r."package-id" = stop."package-id" AND stoptime."stop-id" = stop."stop-id")
         WHERE COALESCE(:route-hash-id::VARCHAR,'') = COALESCE(r."route-hash-id",'')
           AND ROW(r."package-id", t."service-id")::service_ref IN
               (SELECT * FROM gtfs_services_for_date(gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE),
                          :date::DATE))
           AND r."package-id" = ANY(gtfs_service_packages_for_date(:service-id::INTEGER, :date::DATE))
         GROUP BY trip."shape-id", r."package-id", trip."trip-id") x
 -- Group same route lines to single row (aggregate departures to array)
 GROUP BY "route-line", "shape-id", "package-id";

-- name: fetch-route-trip-info-by-name-and-date
-- Fetch listing of all trips by route name and date
SELECT trip."package-id", (trip.trip)."trip-id",
       array_agg(ROW(stoptime."stop-sequence",
                     s."stop-name",
                     stoptime."arrival-time",
                     stoptime."departure-time",
                     s."stop-lat", s."stop-lon")::gtfs_stoptime_display
                 ORDER BY stoptime."stop-sequence") AS "stoptimes"
 FROM gtfs_route_trips_for_date(gtfs_service_packages_for_date(:service-id::INTEGER,:date::date), :date::date) rt
 JOIN LATERAL unnest(rt.tripdata) trip ON TRUE
 JOIN LATERAL unnest((trip.trip)."stop-times") stoptime ON TRUE
 JOIN "gtfs-stop" s ON (s."package-id" = trip."package-id" AND s."stop-id" = stoptime."stop-id")
 WHERE COALESCE(:route-hash-id::VARCHAR,'') = COALESCE(rt."route-hash-id",'')
 GROUP BY trip."package-id", (trip.trip)."trip-id";

-- name: fetch-date-hashes-for-route-with-route-hash-id
-- Fetch the date/hash pairs for a given route using route-hash-id which isn't used for all services
WITH dates AS (
  -- Calculate a series of dates from beginning of last year
  -- to the end of the next year.
  SELECT ts::date AS date
    FROM generate_series(
            (date_trunc('year', CURRENT_DATE) - '1 year'::interval)::date,
            (date_trunc('year', CURRENT_DATE) + '2 years'::interval)::date,
            '1 day'::interval) AS g(ts)
)
SELECT x.date::text, string_agg(x.hash,' ' ORDER BY x.e_id asc) as hash
  FROM (SELECT d.date, package_id, rh.hash::text, p."external-interface-description-id" as e_id
          FROM dates d
          -- Join packages for each date
          JOIN LATERAL unnest(gtfs_service_packages_for_date(:service-id::INTEGER, d.date))
            AS ps (package_id) ON TRUE
          -- Join gtfs_package to get external-interface-description-id
          JOIN gtfs_package p ON p.id = package_id AND p."deleted?" = FALSE
          -- Join all date hashes for packages
          JOIN "gtfs-date-hash" dh ON (dh."package-id" = package_id AND dh.date = d.date)
          -- Join unnested per route hashes
          JOIN LATERAL unnest(dh."route-hashes") rh ON TRUE
         WHERE rh."route-hash-id" = :route-hash-id::VARCHAR) x
 GROUP BY x.date, x.e_id;


-- name: fetch-service-info
-- Fetch service info for display in the UI
SELECT ts.name AS "transport-service-name",
       ts.id AS "transport-service-id",
       op.name AS "transport-operator-name",
       op.id AS "transport-operator-id"
  FROM "transport-service" ts
  JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
 WHERE ts."published?" = TRUE
   AND ts.id = :service-id;

-- name: fetch-route-differences
-- single?: true
-- Fetch the differences in the given route for the given dates
SELECT gtfs_route_differences(
          :route-short-name, :route-long-name, :trip-headsign, :route-hash-id,
          (SELECT tripdata
                     FROM gtfs_route_trips_for_date(
                            gtfs_service_packages_for_date(:service-id::INTEGER, :date1::DATE), :date1::DATE) trips
            WHERE COALESCE(trips."route-short-name", '') = COALESCE(:route-short-name, '')
              AND COALESCE(trips."route-long-name", '') = COALESCE(:route-long-name, '')
              AND COALESCE(trips."trip-headsign", '') = COALESCE(:trip-headsign, '')),
          (SELECT tripdata
                     FROM gtfs_route_trips_for_date(
                            gtfs_service_packages_for_date(:service-id::INTEGER, :date2::DATE), :date2::DATE) trips
            WHERE COALESCE(trips."route-short-name", '') = COALESCE(trips."route-short-name", '')
              AND COALESCE(trips."route-long-name", '') = COALESCE(:route-long-name, '')
              AND COALESCE(trips."trip-headsign", '') = COALESCE(:trip-headsign, '')))::TEXT;


-- name: fetch-gtfs-packages-for-service
SELECT p.id, p.created,
       to_char(lower(dr.daterange), 'dd.mm.yyyy') as "min-date",
       to_char(upper(dr.daterange), 'dd.mm.yyyy') as "max-date",
       (eid."external-interface").url as "interface-url"
  FROM gtfs_package p
  JOIN LATERAL gtfs_package_date_range(p.id) as dr (daterange) ON TRUE
  JOIN "external-interface-description" eid ON p."external-interface-description-id" = eid.id
 WHERE p."transport-service-id" = :service-id AND p."deleted?" = FALSE
 ORDER BY p.created DESC;
