-- Type and procedure for calculating transit changes between two days

---- Copy functions we need from R__GTFS_query (for fresh instances)
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



CREATE TYPE "transit-day-difference" AS (
  "routes-added" INTEGER,
  "routes-removed" INTEGER,
  "trip-count-difference" INTEGER,
  "stop-sequence-changes" INTEGER,
  "stop-time-changes" INTEGER
);

CREATE OR REPLACE FUNCTION gtfs_operator_route_names_for_date(op_ INTEGER, date DATE)
RETURNS SETOF TEXT
AS $$
SELECT DISTINCT COALESCE(r."route-short-name", r."route-long-name") as name
  FROM "gtfs-trip" t
  JOIN "gtfs-route" r ON t."route-id" = r."route-id"
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(gtfs_latest_package_for_date(op_,date), date));
$$ LANGUAGE SQL STABLE;


CREATE OR REPLACE FUNCTION gtfs_operator_unique_stop_sequences_for_date(op_ INTEGER, date DATE)
RETURNS SETOF TEXT
AS $$
SELECT DISTINCT string_agg(stop."stop-name", '->' order by stops."stop-sequence") as "stop-seq"
  FROM "gtfs-trip" t
  JOIN LATERAL unnest(t.trips) trips ON true
  JOIN LATERAL unnest(trips."stop-times") stops ON TRUE
  JOIN "gtfs-stop" stop ON (t."package-id" = stop."package-id" AND stops."stop-id" = stop."stop-id")
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                          (SELECT gtfs_latest_package_for_date(op_, date)), date))
 GROUP BY trips."trip-id";
$$ LANGUAGE SQL STABLE;


CREATE OR REPLACE FUNCTION gtfs_operator_stop_times_for_date(op_ INTEGER, date DATE)
RETURNS SETOF TEXT
AS $$
SELECT DISTINCT concat(stop."stop-name", '@', stops."departure-time"::text) as "stop-time"
  FROM "gtfs-trip" t
  JOIN LATERAL unnest(t.trips) trips ON true
  JOIN LATERAL unnest(trips."stop-times") stops ON TRUE
  JOIN "gtfs-stop" stop ON (t."package-id" = stop."package-id" AND stops."stop-id" = stop."stop-id")
 WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                          (SELECT gtfs_latest_package_for_date(op_, date)), date))
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_calculate_day_difference(op_ INTEGER, date1 DATE, date2 DATE)
RETURNS "transit-day-difference"
AS $$
DECLARE
  routes_added INTEGER;
  routes_removed INTEGER;
  date1_trips INTEGER;
  date2_trips INTEGER;
  trip_count_diff INTEGER;
  stop_seq_added INTEGER;
  stop_seq_removed INTEGER;
  stop_seq_changes INTEGER;
  stop_time_added INTEGER;
  stop_time_removed INTEGER;
  stop_time_changes INTEGER;
BEGIN
  -- Count how many routes have been added/removed
  SELECT COUNT(*)
    INTO routes_added
    FROM gtfs_operator_route_names_for_date(op_, date2) rn
   WHERE rn NOT IN (SELECT gtfs_operator_route_names_for_date(op_, date1));

  SELECT COUNT(*)
    INTO routes_removed
    FROM gtfs_operator_route_names_for_date(op_, date1) rn
   WHERE rn NOT IN (SELECT gtfs_operator_route_names_for_date(op_, date2));

  -- Count the difference in total amount of trips
  SELECT SUM(array_length(t.trips,1))
    INTO date1_trips
    FROM "gtfs-trip" t
   WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                            (SELECT gtfs_latest_package_for_date(op_, date1)),
                            date1));

  SELECT SUM(array_length(t.trips,1))
    INTO date2_trips
    FROM "gtfs-trip" t
   WHERE t."service-id" IN (SELECT gtfs_services_for_date(
                            (SELECT gtfs_latest_package_for_date(op_, date2)),
                            date2));

  trip_count_diff := COALESCE(date2_trips,0) - COALESCE(date1_trips,0);

  -- Count how many unique stop sequences are added/removed
  SELECT COUNT(*)
    INTO stop_seq_added
    FROM gtfs_operator_unique_stop_sequences_for_date(op_, date2) ss
   WHERE ss NOT IN (SELECT gtfs_operator_unique_stop_sequences_for_date(op_, date1));

  SELECT COUNT(*)
    INTO stop_seq_removed
    FROM gtfs_operator_unique_stop_sequences_for_date(op_, date1) ss
   WHERE ss NOT IN (SELECT gtfs_operator_unique_stop_sequences_for_date(op_, date2));

  stop_seq_changes := stop_seq_added + stop_seq_removed;

  -- Count how many stop times are added/removed
  SELECT COUNT(*)
    INTO stop_time_added
    FROM gtfs_operator_stop_times_for_date(op_, date2) ss
   WHERE ss NOT IN (SELECT gtfs_operator_stop_times_for_date(op_, date1));

  SELECT COUNT(*)
    INTO stop_time_removed
    FROM gtfs_operator_stop_times_for_date(op_, date1) ss
   WHERE ss NOT IN (SELECT gtfs_operator_stop_times_for_date(op_, date2));

  stop_time_changes := stop_time_added + stop_time_removed;

  -- Return everything in one record
  RETURN ROW(routes_added,
             routes_removed,
             trip_count_diff,
             stop_seq_changes,
             stop_time_changes)::"transit-day-difference";
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION gtfs_nightly_changes_date_pair(cdate DATE, chash TEXT, ddate DATE, dhash TEXT)
RETURNS DATE[]
AS $$
DECLARE
 chashes TEXT[];
 dhashes TEXT[];
 i INTEGER;
 date1 DATE;
 date2 DATE;
BEGIN
 -- split week hashes to days
 chashes := regexp_split_to_array(chash, ',');
 dhashes := regexp_split_to_array(dhash, ',');

 FOR i IN 1 .. 7 LOOP
   IF chashes[i] != dhashes[i] THEN
     date1 := cdate + CONCAT((i-1)::TEXT, ' days')::INTERVAL;
     date2 := ddate + CONCAT((i-1)::TEXT, ' days')::INTERVAL;
   END IF;
 END LOOP;
 RETURN ARRAY[date1, date2]::DATE[];

END;
$$ LANGUAGE plpgsql;


-- Recreate nightly-transit-changes view with new change information

DROP FUNCTION refresh_nightly_transit_changes ();
DROP MATERIALIZED VIEW "nightly-transit-changes";

CREATE MATERIALIZED VIEW "nightly-transit-changes" AS
WITH operators AS (
  SELECT DISTINCT "transport-operator-id", "transport-service-id"
    FROM gtfs_package
)
SELECT datepair.p[1] as "current-week-date", datepair.p[2] as "different-week-date", changes.*, diff.*
  FROM (
        SELECT op.id AS "transport-operator-id",
               op.name AS "transport-operator-name",
               ts.name AS "transport-service-name",
               date_trunc('week', x.dt::DATE) as "change-date",
               EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER as "current-year",
               EXTRACT(WEEK FROM CURRENT_DATE)::INTEGER AS "current-week",
               gtfs_operator_week_hash(op.id, CURRENT_DATE) as "current-weekhash",
               EXTRACT(YEAR FROM x.dt)::INTEGER AS "different-year",
               EXTRACT(WEEK FROM x.dt)::INTEGER AS "different-week",
               x.weekhash as "different-weekhash"
          FROM operators ops
          JOIN "transport-operator" op ON ops."transport-operator-id" = op.id
          JOIN "transport-service" ts ON ops."transport-service-id" = ts.id
          JOIN LATERAL (SELECT x.*
                          FROM (SELECT s.dt::date, gtfs_operator_week_hash(op.id, s.dt::date) as weekhash
                                  FROM (SELECT generate_series((CURRENT_DATE + '1 week'::interval)::timestamp,
                                                               (CURRENT_DATE + '1 year'::interval)::timestamp, '1 week') dt) s) x
                         WHERE x.weekhash != gtfs_operator_week_hash(op.id, CURRENT_DATE)
                         LIMIT 1) x ON TRUE
         WHERE gtfs_latest_package_for_date(ops."transport-operator-id", CURRENT_DATE) IS NOT NULL) changes
  JOIN LATERAL (SELECT gtfs_nightly_changes_date_pair(
                         date_trunc('week', CURRENT_DATE)::DATE, "current-weekhash",
                         "change-date"::DATE, "different-weekhash") as p) datepair ON TRUE
  JOIN LATERAL (SELECT *
                  FROM gtfs_calculate_day_difference(
                         "transport-operator-id", datepair.p[1], datepair.p[2])) diff ON TRUE;

CREATE FUNCTION refresh_nightly_transit_changes ()
RETURNS VOID
AS $$
BEGIN
 REFRESH MATERIALIZED VIEW "nightly-transit-changes";
 RETURN;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
