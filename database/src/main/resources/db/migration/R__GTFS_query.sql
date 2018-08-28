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

CREATE OR REPLACE FUNCTION gtfs_set_package_geometry(package_id INTEGER) RETURNS VOID AS $$
UPDATE gtfs_package
   SET envelope = (SELECT ST_Envelope(ST_Collect(ST_SetSRID(ST_MakePoint(s."stop-lon",s."stop-lat"), 4326)))
                     FROM "gtfs-stop" s
                    WHERE s."package-id" = package_id),
       "finnish-regions" = gtfs_package_finnish_regions(package_id)
 WHERE id = package_id;
$$ LANGUAGE SQL;

-------------------------------------
-- New per service query functions.
-- A service can have multiple packages (different external interfaces) operating on the same day
-- so we use an INTEGER[] to pass in all package ids to use.
--------

CREATE OR REPLACE FUNCTION gtfs_should_calculate_transit_change(service_id INTEGER)
RETURNS BOOLEAN
AS $$
SELECT NOT EXISTS(
  SELECT date
    FROM "gtfs-transit-changes" gtc
   WHERE "change-date" > CURRENT_DATE
     AND "transport-service-id" = service_id
     AND NOT EXISTS(SELECT id
                      FROM gtfs_package p
                     WHERE p."transport-service-id" = gtc."transport-service-id"
                       AND p.created > gtc.date));
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_should_calculate_transit_change(INTEGER) IS
E'Check if transit changes should be calculated for the given transport-service-id.';

CREATE OR REPLACE FUNCTION gtfs_services_for_date(package_ids INTEGER[], dt DATE)
RETURNS SETOF service_ref AS $$
SELECT DISTINCT ROW(c."package-id", c."service-id")::service_ref
  FROM "gtfs-calendar" c
  LEFT JOIN "gtfs-calendar-date" cd ON (c."package-id" = cd."package-id" AND c."service-id" = cd."service-id")
 WHERE c."package-id" = ANY(package_ids)
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
SELECT ROW(cd."package-id", cd."service-id")::service_ref
  FROM "gtfs-calendar-date" cd
 WHERE cd."package-id" = ANY(package_ids)
   AND cd.date = dt
   AND cd."exception-type" = 1;
$$ LANGUAGE SQL STABLE;


CREATE OR REPLACE FUNCTION gtfs_route_trips_for_date(package_ids INTEGER[], dt DATE)
RETURNS SETOF route_trips_for_date
AS $$
SELECT r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id")::INTEGER AS trips,
       array_agg(ROW(t."package-id",trip)::"gtfs-package-trip-info") as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE r."package-id" = ANY(package_ids)
   AND ROW(r."package-id", t."service-id")::service_ref IN (SELECT * FROM gtfs_services_for_date(package_ids, dt))
 GROUP BY r."route-short-name", r."route-long-name", trip."trip-headsign"
$$ LANGUAGE SQL STABLE;


CREATE OR REPLACE FUNCTION gtfs_service_packages_for_date(service_id INTEGER, dt DATE)
RETURNS INTEGER[]
AS $$
SELECT array_agg(x.id)
  FROM (SELECT DISTINCT ON ("external-interface-description-id") p.id
          FROM gtfs_package p
         WHERE "transport-service-id" = service_id
           AND created <= dt
         ORDER BY "external-interface-description-id", created DESC) x
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_trip_stop_departure_time(trip "gtfs-package-trip-info", stopname TEXT)
RETURNS interval
AS $$
SELECT st."departure-time"
  FROM unnest((trip.trip)."stop-times") st
  JOIN "gtfs-stop" s ON (s."package-id" = (trip)."package-id" AND st."stop-id" = s."stop-id")
 WHERE s."stop-name" = stopname
 LIMIT 1;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_trip_stop_sequence_by_name(trip "gtfs-package-trip-info", stopname TEXT)
RETURNS INTEGER
AS $$
SELECT st."stop-sequence"
  FROM unnest((trip.trip)."stop-times") st
  JOIN "gtfs-stop" s ON (s."package-id" = (trip)."package-id" AND st."stop-id" = s."stop-id")
 WHERE s."stop-name" = stopname
 LIMIT 1;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_trip_changes(d1_trip "gtfs-package-trip-info", d2_trip "gtfs-package-trip-info", first_common_stop TEXT)
RETURNS "gtfs-trip-change-info"
AS $$
DECLARE
 d1_stop_seq_of_fcs INTEGER;
 d2_stop_Seq_of_fcs INTEGER;
 chg "gtfs-trip-change-info";
 row RECORD;
BEGIN
  chg."trip-stop-sequence-changes" := 0;
  chg."trip-stop-time-changes" := 0;

  -- Determine the positions of the first common stop
  d1_stop_seq_of_fcs := gtfs_trip_stop_sequence_by_name(d1_trip, first_common_stop);
  d2_stop_seq_of_fcs := gtfs_trip_stop_sequence_by_name(d2_trip, first_common_stop);

  FOR row in
      SELECT COALESCE(d1."stop-name", d2."stop-name") AS stopname,
             d1."arrival-time" as d1_arr, d1."departure-time" as d1_dep, d1."stop-id" as d1_stop_id,
             d2."arrival-time" as d2_arr, d2."departure-time" as d2_dep, d2."stop-id" as d2_stop_id
        FROM (SELECT s."stop-name", st."arrival-time", st."departure-time",
                     st."stop-sequence" - d1_stop_seq_of_fcs as "stop-sequence",
                     st."stop-id"
                FROM unnest((d1_trip.trip)."stop-times") st
                JOIN "gtfs-stop" s ON (s."package-id" = (d1_trip)."package-id" AND st."stop-id" = s."stop-id")) d1
        FULL OUTER JOIN
             (SELECT s."stop-name", st."arrival-time", st."departure-time",
                     st."stop-sequence" - d2_stop_seq_of_fcs as "stop-sequence",
                     st."stop-id"
                FROM unnest((d2_trip.trip)."stop-times") st
                JOIN "gtfs-stop" s ON (s."package-id" = (d2_trip)."package-id" AND st."stop-id" = s."stop-id")) d2
          ON (d1."stop-sequence" = d2."stop-sequence")
  LOOP
    RAISE NOTICE '%  (% / %) => (% / %)', row.stopname, row.d1_arr,row.d1_dep, row.d2_arr,row.d2_dep;
    IF row.d1_stop_id IS NULL OR row.d2_stop_id IS NULL THEN
      -- If either side is NULL, this is a stop sequence change
      chg."trip-stop-sequence-changes" := chg."trip-stop-sequence-changes" + 1;
    ELSIF row.d1_dep != row.d2_dep THEN
      -- Otherwise, if the departure times are not the same, this is a stop time change
      chg."trip-stop-time-changes" := chg."trip-stop-time-changes" + 1;
    END IF;
  END LOOP;

  RETURN chg;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION gtfs_route_differences("route-short-name" TEXT, "route-long-name" TEXT, "trip-headsign" TEXT, d1_trips "gtfs-package-trip-info"[], d2_trips "gtfs-package-trip-info"[])
RETURNS "gtfs-route-change-info"
AS $$
DECLARE
  first_common_stop TEXT;
  chg "gtfs-route-change-info";
  row RECORD;
  all_trips TEXT[];
  d1_trip_ids TEXT[];
  d2_trip_ids TEXT[];
  trip_chg "gtfs-trip-change-info";
  trip_stop_sequence_changes INTEGER;
  trip_stop_time_changes INTEGER;
BEGIN
  trip_stop_sequence_changes := 0;
  trip_stop_time_changes := 0;

  -- Select all trips as array of "package-id:trip-id" strings
  all_trips := (SELECT array_agg(x.t)
                  FROM (SELECT CONCAT(d1t."package-id",':',(d1t.trip)."trip-id") t
                          FROM unnest(d1_trips) d1t
                         UNION
                        SELECT CONCAT(d2t."package-id",':',(d2t.trip)."trip-id") t
                          FROM unnest(d2_trips) d2t) x);

  -- Detect first common stop in the trips
  RAISE NOTICE '% % % -- d1_trips % -- d2_trips %, trips: %', "route-short-name", "route-long-name", "trip-headsign", array_length(d1_trips, 1), array_length(d2_trips,1), all_trips::text;

  SELECT INTO first_common_stop x."stop-name"
    FROM (SELECT s."stop-name", MIN(st."stop-sequence") AS min_stop_seq,
                 array_agg(CONCAT(dt."package-id", ':', (dt.trip)."trip-id")) as trips
            FROM unnest(array_cat(d1_trips,d2_trips)) dt
            JOIN LATERAL unnest((dt.trip)."stop-times") st ON TRUE
            JOIN "gtfs-stop" s ON (dt."package-id" = s."package-id" AND st."stop-id" = s."stop-id")
           GROUP BY s."stop-name") x
           WHERE x.trips @> all_trips
           ORDER BY min_stop_seq
           LIMIT 1;

   RAISE NOTICE '% / % / %   1. yhteinen pysäkki: %', "route-short-name", "route-long-name", "trip-headsign", first_common_stop;

   --------------------------
   -- Combine d1 and d2 trips based on the least amount of difference in departure for the 1st common stop
   -- using maximum threshold of 30 minutes

   -- Extract all trip ids for both days
   SELECT array_agg((d.trip)."trip-id")
     INTO d1_trip_ids
     FROM unnest(d1_trips) d;

   SELECT array_agg((d.trip)."trip-id")
     INTO d2_trip_ids
     FROM unnest(d2_trips) d;

   FOR row IN
       SELECT x.*
         FROM (SELECT d1t."package-id" as "d1-package-id", d1t.trip as "d1-trip",
                      d2t."package-id" AS "d2-package-id", d2t.trip as "d2-trip",
                      ABS(EXTRACT(EPOCH FROM gtfs_trip_stop_departure_time(d1t, first_common_stop)) -
                          EXTRACT(EPOCH FROM gtfs_trip_stop_departure_time(d2t, first_common_stop))) as timediff
                 FROM unnest(d1_trips) d1t CROSS JOIN unnest(d2_trips) d2t) x
         WHERE timediff < 1800 -- only consider differences less than 30 minutes
         ORDER BY timediff ASC
   LOOP
      IF (row."d1-trip")."trip-id" = ANY(d1_trip_ids) AND
         (row."d2-trip")."trip-id" = ANY(d2_trip_ids)
      THEN
         -- Both trips are still unconsumed, mark this pair as the same
         RAISE NOTICE '% = % aikaerolla %', (row."d1-trip")."trip-id", (row."d2-trip")."trip-id", row.timediff;
         d1_trip_ids := array_remove(d1_trip_ids, (row."d1-trip")."trip-id");
         d2_trip_ids := array_remove(d2_trip_ids, (row."d2-trip")."trip-id");

         -- Calculate stop time and stop sequence changes
         trip_chg := gtfs_trip_changes(ROW(row."d1-package-id", row."d1-trip")::"gtfs-package-trip-info",
                                       ROW(row."d2-package-id", row."d2-trip")::"gtfs-package-trip-info",
                                       first_common_stop);
         trip_stop_sequence_changes := trip_stop_sequence_changes + trip_chg."trip-stop-sequence-changes";
         trip_stop_time_changes := trip_stop_time_changes + trip_chg."trip-stop-time-changes";
      END IF;
   END LOOP;

   RAISE NOTICE 'yli jäi d1: % ja d2: % vuoroa', array_length(d1_trip_ids, 1), array_length(d2_trip_ids, 2);

  chg."route-short-name" := "route-short-name";
  chg."route-long-name" := "route-long-name";
  chg."trip-headsign" := "trip-headsign";
  chg."added-trips" := COALESCE(array_length(d2_trip_ids,1), 0);
  chg."removed-trips" := COALESCE(array_length(d1_trip_ids,1), 0);
  chg."trip-stop-sequence-changes" := trip_stop_sequence_changes;
  chg."trip-stop-time-changes" := trip_stop_time_changes;
  RETURN chg;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION gtfs_date_differences(service_id INTEGER, date1 DATE, date2 DATE)
RETURNS VOID
AS $$
DECLARE
  added_routes INTEGER;
  removed_routes INTEGER;
  added_trips INTEGER;
  removed_trips INTEGER;
  stop_seq_changes INTEGER;
  stop_time_changes INTEGER;
  route_change "gtfs-route-change-info";
  route_changes "gtfs-route-change-info"[];
  row RECORD;
  package_ids INTEGER[];
BEGIN
  added_routes := 0;
  removed_routes := 0;
  added_trips := 0;
  removed_trips := 0;
  stop_seq_changes := 0;
  stop_time_changes := 0;
  route_changes := ARRAY[]::"gtfs-route-change-info"[];

  -- Take all package ids used in calculation without duplicate values
  package_ids := (SELECT array_agg(x.p) FROM (
                   SELECT unnest(gtfs_service_packages_for_date(service_id, date1)) p
                    UNION
                   SELECT unnest(gtfs_service_packages_for_date(service_id, date2)) p) x);


  FOR row IN
      SELECT COALESCE(l."route-short-name",r."route-short-name") as "route-short-name",
             COALESCE(l."route-long-name", r."route-long-name") as "route-long-name",
             COALESCE(l."trip-headsign",r."trip-headsign") as "trip-headsign",
             l."tripdata" as "date1-trips",
             r."tripdata" as "date2-trips",
             COALESCE(l.tripdata,ARRAY[]::"gtfs-package-trip-info"[]) != COALESCE(r.tripdata,ARRAY[]::"gtfs-package-trip-info"[]) as "different?"
        FROM gtfs_route_trips_for_date(gtfs_service_packages_for_date(service_id, date1), date1) l
        FULL OUTER JOIN gtfs_route_trips_for_date(gtfs_service_packages_for_date(service_id, date2), date2) r ON
             (l."route-short-name"=r."route-short-name" AND l."route-long-name"=r."route-long-name" AND l."trip-headsign"=r."trip-headsign")
  LOOP
    IF row."date1-trips" IS NULL AND row."date2-trips" IS NOT NULL THEN
      added_routes := added_routes + 1;
    ELSIF row."date1-trips" IS NOT NULL AND row."date2-trips" IS NULL THEN
      removed_routes := removed_routes + 1;
    ELSIF row."different?" THEN
        route_change := gtfs_route_differences(row."route-short-name",row."route-long-name",row."trip-headsign", row."date1-trips", row."date2-trips");
        route_changes := route_changes || route_change;
        added_trips := added_trips + route_change."added-trips";
        removed_trips := removed_trips + route_change."removed-trips";
        stop_seq_changes := stop_seq_changes + route_change."trip-stop-sequence-changes";
        stop_time_changes := stop_time_changes + route_change."trip-stop-time-changes";
    END IF;
  END LOOP;

  -- Upsert detected change information
  INSERT INTO "gtfs-transit-changes"
         (date,"transport-service-id",
          "current-week-date","different-week-date","change-date",
          "added-routes","removed-routes","added-trips","removed-trips",
          "trip-stop-sequence-changes","trip-stop-time-changes",
          "route-changes","package-ids")
  VALUES (CURRENT_DATE, service_id,
          date1, date2, date_trunc('week',date2),
          added_routes, removed_routes, added_trips, removed_trips,
          stop_seq_changes, stop_time_changes,
          route_changes, package_ids)
  ON CONFLICT (date,"transport-service-id") DO
  UPDATE SET "current-week-date" = date1,
          "different-week-date" = date2,
          "change-date" = date_trunc('week',date2),
          "added-routes" = added_routes,
          "removed-routes" = removed_routes,
          "added-trips" = added_trips,
          "removed-trips" = removed_trips,
          "trip-stop-sequence-changes" = stop_seq_changes,
          "trip-stop-time-changes" = stop_time_changes,
          "route-changes" = route_changes,
          "package-ids" = package_ids;
END
$$ LANGUAGE plpgsql;
