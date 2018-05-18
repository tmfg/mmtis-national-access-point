-- name: fetch-operator-date-hashes
SELECT date, hash::text
  FROM "gtfs-date-hash"
 WHERE hash IS NOT NULL AND
       "package-id" IN (SELECT id FROM gtfs_package WHERE "transport-operator-id" = :operator-id);

-- name: fetch-routes-for-dates
-- Given a package id and two dates, fetch the routes operating on those days with
-- the amount of trips on each day.
WITH
date1_trips AS (
SELECT r."route-id", r."route-short-name", r."route-long-name", SUM(array_length(t.trips, 1)) as trips
  FROM "gtfs-route" r
       JOIN "gtfs-trip" t ON r."route-id" = t."route-id"
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                           (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date1::date)),
                           :date1::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(:operator-id::INTEGER, :date1::date))
 GROUP BY r."route-id", r."route-short-name", r."route-long-name"
),
date2_trips AS (
SELECT r."route-id", r."route-short-name", r."route-long-name", SUM(array_length(t.trips, 1)) as trips
  FROM "gtfs-route" r
       JOIN "gtfs-trip" t ON r."route-id" = t."route-id"
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                           (SELECT gtfs_latest_package_for_date(1, :date2::date)),
                           :date2::date))
   AND r."package-id" = (SELECT gtfs_latest_package_for_date(1, :date2::date))
 GROUP BY r."route-id", r."route-short-name", r."route-long-name"
)
SELECT x.* FROM (
 SELECT COALESCE(d1."route-id",d2."route-id") AS "route-id",
        COALESCE(d1."route-short-name", d2."route-short-name") AS "route-short-name",
        COALESCE(d1."route-long-name", d2."route-long-name") AS "route-long-name",
        d1.trips as "date1-trips", d2.trips as "date2-trips"
   FROM date1_trips d1 FULL OUTER JOIN
        date2_trips d2 ON (d1."route-id" = d2."route-id" AND
                           d1."route-short-name" = d2."route-short-name" AND
                           d1."route-long-name" = d2."route-long-name")) x
ORDER BY x."route-short-name";
