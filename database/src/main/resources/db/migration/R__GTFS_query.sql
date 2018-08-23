CREATE OR REPLACE FUNCTION gtfs_package_date_range(package_id INTEGER) RETURNS daterange AS $$
SELECT daterange(MIN(pd.start), MAX(pd.end), '[]')
  FROM (SELECT MIN(date) as start, MAX(date) as end
          FROM "gtfs-calendar-date"
         WHERE "exception-type" = 1
           AND "package-id" = package_id
         UNION ALL
        SELECT MIN("start-date") AS start, MAX("end-date") AS end
          FROM "gtfs-calendar"
         WHERE "package-id" = package_id) pd
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_package_date_range (INTEGER) IS E'Returns a daterange the GTFS package has data for';

CREATE OR REPLACE FUNCTION gtfs_package_dates(package_id INTEGER) RETURNS SETOF DATE AS $$
SELECT series.ts::date AS date
  FROM (SELECT generate_series(lower(x.dr)::timestamp, upper(x.dr)::timestamp, '1 day') ts
          FROM (SELECT gtfs_package_date_range(package_id) AS dr) x) series
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_package_dates (INTEGER) IS E'Returns the series of dates the package has data for.';

CREATE OR REPLACE FUNCTION gtfs_services_for_date(package_id INTEGER, dt DATE) RETURNS SETOF TEXT AS $$
SELECT DISTINCT c."service-id"
  FROM "gtfs-calendar" c
  LEFT JOIN "gtfs-calendar-date" cd ON (c."package-id" = cd."package-id" AND c."service-id" = cd."service-id")
 WHERE c."package-id" = package_id
   AND (dt BETWEEN c."start-date" AND c."end-date")
   AND ((EXTRACT(DOW FROM dt) = 0 AND c.sunday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 1 AND c.monday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 2 AND c.tuesday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 3 AND c.wednesday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 4 AND c.thursday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 5 AND c.friday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 6 AND c.saturday = TRUE))
   AND (cd."exception-type" IS NULL OR cd."exception-type" != 2)
UNION
SELECT cd."service-id"
  FROM "gtfs-calendar-date" cd
 WHERE cd."package-id" = package_id
   AND cd.date = dt
   AND cd."exception-type" = 1;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_services_for_date (INTEGER, DATE) IS
E'Returns the service ids of all services having trips on the given date';

CREATE OR REPLACE FUNCTION gtfs_hash_for_date(package_id INTEGER, date DATE) RETURNS bytea AS $$
SELECT digest(string_agg(concat(d.route, ':', d.times), '|'), 'sha256')
  FROM (SELECT x.route, string_agg(concat(x."stop-name", '@', x."departure-time"), '->') as times
          FROM (SELECT COALESCE(r."route-short-name", r."route-long-name") as route, stops."departure-time", s."stop-name"
                  FROM "gtfs-trip" t
                  LEFT JOIN "gtfs-route" r ON (r."package-id" = t."package-id" AND r."route-id" = t."route-id")
                  LEFT JOIN LATERAL unnest(t.trips) trip ON TRUE
                  LEFT JOIN LATERAL unnest(trip."stop-times") stops ON TRUE
                  JOIN "gtfs-stop" s ON (s."package-id" = t."package-id" AND stops."stop-id" = s."stop-id")
                 WHERE t."package-id" = package_id
                   AND t."service-id" IN (SELECT gtfs_services_for_date(package_id, date))
                 ORDER BY route, stops."trip-id", "stop-sequence") x
  GROUP BY x.route) d;
$$ LANGUAGE SQL STABLE;


CREATE OR REPLACE FUNCTION gtfs_latest_package_for_date(operator_id INTEGER, date DATE) RETURNS INTEGER AS $$
SELECT p.id FROM gtfs_package p
 WHERE p."transport-operator-id" = operator_id
   AND p.created < date
   AND gtfs_package_date_range(p.id) @> date
 ORDER BY p.id DESC
 LIMIT 1;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_latest_package_for_date(INTEGER,DATE) IS
E'Returns the id of the latest package of the given transport-operator that has data for the given date.';

CREATE OR REPLACE FUNCTION gtfs_operator_week_hash(operator_id INTEGER, dt DATE) RETURNS VARCHAR AS $$
SELECT string_agg(concat(x.weekday,'=',x.hash::TEXT),',') as weekhash
  FROM (SELECT EXTRACT(ISODOW FROM date) as weekday,
               EXTRACT(YEAR FROM date) as year,
               EXTRACT(WEEK FROM date) as week,
               hash,
               row_number() OVER (PARTITION BY h.date ORDER BY p.created DESC)
          FROM "gtfs-date-hash" h
          JOIN "gtfs_package" p ON h."package-id" = p.id
         WHERE p."transport-operator-id" = operator_id
           AND (EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM dt) AND
                EXTRACT(WEEK FROM date) = EXTRACT(WEEK FROM dt))
         ORDER BY date ASC) x
 WHERE x.row_number = 1
 GROUP by year, week
 ORDER BY year, week
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_operator_week_hash(INTEGER,DATE) IS
E'Returns a string description of a week''s traffic hash for an operator.';

CREATE OR REPLACE FUNCTION gtfs_package_finnish_regions(package_id INTEGER) RETURNS CHAR(2)[] AS $$
SELECT array_agg(x.numero) AS "finnish-regions"
  FROM (SELECT DISTINCT r.numero
          FROM "gtfs-stop" s
          JOIN finnish_regions r ON st_contains(r.location, st_setsrid(st_makepoint("stop-lon", "stop-lat"), 4326))
         WHERE "package-id" = package_id) x;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_package_finnish_regions(INTEGER) IS
E'Returns an array of the finnish region numbers the given GTFS package operates in.
The package operates in the area if any of its stops are contained in the region geometry.';

CREATE OR REPLACE FUNCTION gtfs_route_trips_for_date(package_id INTEGER, dt DATE)
RETURNS SETOF RECORD
AS $$
SELECT r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id") AS trips,
       string_agg(t.trips::TEXT,',') as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(package_id, dt))
   AND r."package-id" = package_id
 GROUP BY r."route-short-name", r."route-long-name", trip."trip-headsign"
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_route_trips_for_date(package_id INTEGER, dt DATE)
RETURNS SETOF RECORD
AS $$
WITH
date_trips AS (

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
$$ LANGUAGE SQL STABLE;
