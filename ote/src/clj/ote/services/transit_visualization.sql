-- name: fetch-operator-date-hashes
SELECT date, hash::text
  FROM "gtfs-date-hash"
 WHERE hash IS NOT NULL
   AND "package-id" IN (SELECT id FROM gtfs_package WHERE "transport-operator-id" = :operator-id)
   -- Take dates from two months ago two 1 year in the future (but always full years)
   AND date >= make_date(EXTRACT(YEAR FROM (current_date - '2 months'::interval)::date)::integer, 1, 1)
   AND date <= make_date(EXTRACT(YEAR FROM (current_date + '1 year'::interval)::date)::integer, 12, 31);

-- name: fetch-routes-for-dates
-- Given a package id and two dates, fetch the routes operating on those days with
-- the amount of trips on each day.
WITH
date1_trips AS (
SELECT r."route-id", r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id") AS trips,
       string_agg(t.trips::TEXT,',') as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                           (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date1::date)),
                           :date1::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date1::date))
 GROUP BY r."route-id", r."route-short-name", r."route-long-name", trip."trip-headsign"
),
date2_trips AS (
SELECT r."route-id", r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id") AS trips,
       string_agg(t.trips::TEXT,',') as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                           (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date2::date)),
                           :date2::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date2::date))
 GROUP BY r."route-id", r."route-short-name", r."route-long-name", trip."trip-headsign"
)
SELECT x.* FROM (
 SELECT COALESCE(d1."route-id",d2."route-id") AS "route-id",
        d1."route-short-name", d1."route-long-name", d1."trip-headsign",
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

-- name: fetch-route-trips-by-name-and-date
-- Fetch geometries of route trips for given date by route short and long name
SELECT x."route-line", array_agg(departure) as departures
  FROM (SELECT
               (array_agg(stoptime."departure-time"))[1] as "departure",
               st_asgeojson(ST_MakeLine(ST_MakePoint(stop."stop-lon", stop."stop-lat") ORDER BY stoptime."stop-sequence")) as "route-line"
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
         GROUP BY trip."trip-id") x
 -- Group same route lines to single row (aggregate departures to array)
 GROUP BY "route-line";
