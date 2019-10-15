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
 WHERE c."package-id" = package_id
   AND (dt BETWEEN c."start-date" AND c."end-date")
   AND ((EXTRACT(DOW FROM dt) = 0 AND c.sunday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 1 AND c.monday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 2 AND c.tuesday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 3 AND c.wednesday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 4 AND c.thursday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 5 AND c.friday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 6 AND c.saturday = TRUE))
   AND NOT EXISTS(SELECT id
                    FROM "gtfs-calendar-date" cd
                   WHERE cd."exception-type" = 2
                     AND cd."service-id" = c."service-id"
                     AND cd."package-id" = package_id
                     AND cd.date = dt)
UNION
SELECT cd."service-id"
  FROM "gtfs-calendar-date" cd
 WHERE cd."package-id" = package_id
   AND cd.date = dt
   AND cd."exception-type" = 1;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_services_for_date (INTEGER, DATE) IS
E'Returns the GTFS calendar service ids of all services having trips on the given date';

CREATE OR REPLACE FUNCTION gtfs_services_for_date(package_ids INTEGER[], dt DATE)
  RETURNS SETOF service_ref AS $$
SELECT DISTINCT ROW(c."package-id", c."service-id")::service_ref
  FROM "gtfs-calendar" c
 WHERE c."package-id" = ANY(package_ids)
   AND (dt BETWEEN c."start-date" AND c."end-date")
   AND ((EXTRACT(DOW FROM dt) = 0 AND c.sunday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 1 AND c.monday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 2 AND c.tuesday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 3 AND c.wednesday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 4 AND c.thursday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 5 AND c.friday = TRUE) OR
        (EXTRACT(DOW FROM dt) = 6 AND c.saturday = TRUE))
   AND NOT EXISTS (SELECT id
                     FROM "gtfs-calendar-date" cd
                    WHERE cd."exception-type" = 2
                      AND cd."service-id" = c."service-id"
                      AND cd."package-id" = c."package-id" AND cd.date = dt)

 UNION
SELECT ROW(cd."package-id", cd."service-id")::service_ref
  FROM "gtfs-calendar-date" cd
 WHERE cd."package-id" = ANY(package_ids)
   AND cd.date = dt
   AND cd."exception-type" = 1;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_services_for_date(INTEGER[],DATE) IS
  E'Return set of (package-id, calendar service-id) tuples of services operated by the given packages for the given date.';

CREATE OR REPLACE FUNCTION gtfs_latest_package_for_date(operator_id INTEGER, date DATE) RETURNS INTEGER AS $$
SELECT p.id FROM gtfs_package p
 WHERE p."transport-operator-id" = operator_id
   AND p.created::date <= date
   AND p."deleted?" = FALSE
   AND gtfs_package_date_range(p.id) @> date
 ORDER BY p.id DESC
 LIMIT 1;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_latest_package_for_date(INTEGER,DATE) IS
E'Returns the id of the latest package of the given transport-operator that has data for the given date.';

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
SELECT EXISTS(
  SELECT gtc.date
    FROM "gtfs-transit-changes" gtc
   WHERE (gtc."change-date" IS NOT NULL
     AND gtc."change-date" = CURRENT_DATE
     AND gtc."transport-service-id" = service_id)
     OR EXISTS(SELECT id
                      FROM gtfs_package p
                     WHERE p."deleted?" = FALSE
                       AND p."transport-service-id" = service_id
                       AND p.created > CURRENT_DATE - interval '4 hours'));
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_should_calculate_transit_change(INTEGER) IS
E'Check if transit changes should be calculated for the given transport-service-id. New calculation is required if earliest change-date is in the past or if service have got new package. 4 hour window is due to utc times vs server time';

CREATE OR REPLACE FUNCTION gtfs_route_trips_for_date(package_ids INTEGER[], dt DATE)
RETURNS SETOF route_trips_for_date
AS $$
SELECT r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id")::INTEGER AS trips,
       array_agg(ROW(t."package-id",trip)::"gtfs-package-trip-info") as tripdata, r."route-hash-id"
  FROM "detection-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE r."package-id" = ANY(package_ids)
   AND ROW(r."package-id", t."service-id")::service_ref IN (SELECT * FROM gtfs_services_for_date(package_ids, dt))
 GROUP BY r."route-short-name", r."route-long-name", trip."trip-headsign", r."route-hash-id"
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_route_tripdata_for_date(
  package_ids INTEGER[],
  dt DATE,
  route_short_name TEXT,
  route_long_name TEXT,
  trip_headsign TEXT,
  route_hash_id TEXT)
RETURNS "gtfs-package-trip-info"[]
AS $$
SELECT array_agg(ROW(t."package-id",trip)::"gtfs-package-trip-info") as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE r."package-id" = ANY(package_ids)
   AND ROW(r."package-id", t."service-id")::service_ref IN (SELECT * FROM gtfs_services_for_date(package_ids, dt))
   AND COALESCE(r."route-short-name",'') = COALESCE(route_short_name,'')
   AND COALESCE(r."route-long-name",'') = COALESCE(route_long_name,'')
   AND COALESCE(trip."trip-headsign",'') = COALESCE(trip_headsign,'');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_service_packages_for_date(service_id INTEGER, dt DATE)
RETURNS INTEGER[]
AS $$
SELECT array_agg(x.id)
  FROM (SELECT DISTINCT ON ("external-interface-description-id") p.id
          FROM gtfs_package p
         WHERE "transport-service-id" = service_id
           AND p."deleted?" = FALSE
           AND ( p.created::DATE <= dt OR p.first_package = TRUE)
         ORDER BY "external-interface-description-id", created DESC) x
$$ LANGUAGE SQL STABLE;

-- New sproc for getting packages is need when calendar wants to show what kind of traffic was back
-- in the day. We
CREATE OR REPLACE FUNCTION gtfs_service_packages_for_detection_date(service_id INTEGER, dt DATE)
    RETURNS INTEGER[]
AS $$
SELECT array_agg(x.id)
FROM (SELECT DISTINCT ON ("external-interface-description-id") p.id
      FROM gtfs_package p
      WHERE "transport-service-id" = service_id
        AND p."deleted?" = FALSE
        AND  p.created::DATE <= dt
        AND p.created::DATE <= detection_date
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

  -- Determine the positions of the first common stop.
  -- The stop seq position is used to "line up" the stops in both trips
  -- by substracting it from the actual stop sequence number. This makes
  -- the first common stop have the sequence number of 0 on both sides
  -- and is used in the JOIN clause.
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
          ON (d1."stop-sequence" = d2."stop-sequence" AND d1."stop-name" = d2."stop-name")
  LOOP
    --RAISE NOTICE '%  (% / %) => (% / %)', row.stopname, row.d1_arr,row.d1_dep, row.d2_arr,row.d2_dep;
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

CREATE OR REPLACE FUNCTION gtfs_service_date_hash(service_id INTEGER, dt DATE)
RETURNS TEXT
AS $$
SELECT string_agg(h.hash::TEXT,' ' ORDER BY h."package-id")
  FROM "gtfs-date-hash" h
 WHERE h.date = dt
   AND h."package-id" = ANY(gtfs_service_packages_for_date(service_id, dt))
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_service_week_hash(service_id INTEGER, dt DATE)
RETURNS TEXT
AS $$
WITH week_dates AS (
SELECT date_trunc('week', dt) + (CONCAT(days,' days'))::interval as date
  FROM generate_series(0, 6) days
)
SELECT string_agg(concat(EXTRACT(ISODOW FROM date),'=', gtfs_service_date_hash(service_id, date::date)),',') as weekhash
  FROM week_dates;
$$ LANGUAGE SQL STABLE;


CREATE OR REPLACE FUNCTION gtfs_package_date_hashes(package_id INTEGER, dt DATE)
RETURNS VOID
AS $$
DECLARE
  route_hashes "gtfs-route-hash"[];
  date_hash bytea;
BEGIN
  SELECT array_agg(ROW(d."route-short-name", d."route-long-name", d."trip-headsign", digest(d.times, 'sha256'), d."route-hash-id")::"gtfs-route-hash"
                   ORDER BY d."route-short-name",d."route-long-name",d."trip-headsign")
    INTO route_hashes
    FROM (SELECT x."route-short-name", x."route-long-name", x."trip-headsign", x."route-hash-id",
                 string_agg(x.trip_times, ',' ORDER BY x.trip_times) as times
            FROM (SELECT COALESCE(r."route-short-name", '') as "route-short-name",
                         COALESCE(r."route-long-name", '') as "route-long-name",
                         COALESCE(r."trip-headsign",'') AS "trip-headsign",
                         string_agg(concat(s."stop-fuzzy-lat",'-',s."stop-fuzzy-lon",'@',stops."departure-time"), '->' ORDER BY stops."stop-sequence") as trip_times,
                         COALESCE(r."route-hash-id", '') as "route-hash-id"
                    FROM "gtfs-trip" t
                    LEFT JOIN "detection-route" r ON (r."package-id" = t."package-id" AND r."route-id" = t."route-id")
                    LEFT JOIN LATERAL unnest(t.trips) trip ON TRUE
                    LEFT JOIN LATERAL unnest(trip."stop-times") stops ON TRUE
                    JOIN "gtfs-stop" s ON (s."package-id" = t."package-id" AND stops."stop-id" = s."stop-id")
                   WHERE t."package-id" = package_id
                     AND t."service-id" IN (SELECT gtfs_services_for_date(package_id, dt))
                   GROUP BY "route-short-name", "route-long-name", r."trip-headsign", "route-hash-id", stops."trip-id") x
    GROUP BY x."route-short-name", x."route-long-name", x."trip-headsign", x."route-hash-id") d;


    SELECT digest(string_agg(rh.hash::text, ','), 'sha256')
      INTO date_hash
      FROM unnest(route_hashes) rh;

    INSERT INTO "gtfs-date-hash" ("package-id", date, hash, "route-hashes", "created")
    VALUES (package_id, dt, date_hash, route_hashes, now())
    ON CONFLICT ("package-id", date) DO
    UPDATE SET "package-id" = package_id,
               date = dt,
               hash = date_hash,
               "route-hashes" = route_hashes,
               modified = now();
END
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION gtfs_package_date_hashes (INTEGER,DATE) IS
E'Calculate and store per route and per day hashes for the given package for the given date.';

-- Generate date hashes for given package
CREATE OR REPLACE FUNCTION gtfs_generate_date_hashes (package_id INTEGER) RETURNS VOID AS $$
DECLARE
 row RECORD;
 allowed_range tsrange;
BEGIN
  allowed_range := tsrange(CURRENT_DATE - '1 years'::interval,
                           CURRENT_DATE + '2 years'::interval);
  FOR row IN
      SELECT * FROM gtfs_package_dates(package_id)
       WHERE allowed_range @> gtfs_package_dates::timestamp
  LOOP
    PERFORM gtfs_package_date_hashes(package_id, row.gtfs_package_dates);
  END LOOP;
END
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION gtfs_generate_date_hashes (INTEGER) IS
E'Calculate and store per route and per day hashes for every day in the given package.';

-- Generate date hashes for future only - to speed up calculations
CREATE OR REPLACE FUNCTION gtfs_generate_date_hashes_for_future(package_id INTEGER) RETURNS VOID AS $$
DECLARE
 row RECORD;
 allowed_range tsrange;
BEGIN
  allowed_range := tsrange(CURRENT_DATE - '1 day'::interval,
                           CURRENT_DATE + '1 year'::interval);
  FOR row IN
      SELECT * FROM gtfs_package_dates(package_id)
       WHERE allowed_range @> gtfs_package_dates::timestamp
  LOOP
    PERFORM gtfs_package_date_hashes(package_id, row.gtfs_package_dates);
  END LOOP;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION gtfs_service_route_date_hash(
    service_id INTEGER, dt DATE,
    route_short_name TEXT, route_long_name TEXT, trip_headsign TEXT)
RETURNS TEXT
AS $$
SELECT string_agg(rh.hash::TEXT,' ' ORDER BY h."package-id")
  FROM "gtfs-date-hash" h
  JOIN LATERAL unnest(h."route-hashes") rh
    ON (COALESCE(rh."route-short-name",'') = COALESCE(route_short_name,'') AND
        COALESCE(rh."route-long-name",'') = COALESCE(route_long_name,'') AND
        COALESCE(rh."trip-headsign",'') = COALESCE(trip_headsign,''))
 WHERE h.date = dt
   AND h."package-id" = ANY(gtfs_service_packages_for_date(service_id, dt))
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_service_route_date_hash(INTEGER,DATE,TEXT,TEXT,TEXT) IS
E'Fetch the traffic hash of the given route by date and service.';

CREATE OR REPLACE FUNCTION gtfs_service_route_week_hash(
    service_id INTEGER, dt DATE,
    route_short_name TEXT, route_long_name TEXT, trip_headsign TEXT)
RETURNS TEXT
AS $$
WITH week_dates AS (
SELECT date_trunc('week', dt) + (CONCAT(days,' days'))::interval as date
  FROM generate_series(0, 6) days
)
SELECT string_agg(concat(EXTRACT(ISODOW FROM date),'=', gtfs_service_route_date_hash(service_id, date::date, route_short_name, route_long_name, trip_headsign)),',') as weekhash
  FROM week_dates;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_service_route_week_hash(INTEGER,DATE,TEXT,TEXT,TEXT) IS
E'Fetch the combined hash of the whole week''s days for the given route by date and service.';


CREATE OR REPLACE FUNCTION gtfs_service_routes(service_id INTEGER)
RETURNS SETOF RECORD
AS $$
SELECT r."route-id", r."route-short-name", r."route-long-name", trip."trip-headsign"
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE r."package-id" = ANY(gtfs_service_packages_for_date(service_id, CURRENT_DATE))
 GROUP BY r."route-id", r."route-short-name", r."route-long-name", trip."trip-headsign"
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_service_routes (INTEGER) IS
E'Return all routes in packages for the given service. Returns set of (route-id, route-short-name, route-long-name, trip-headsign) tuples.';


CREATE OR REPLACE FUNCTION gtfs_first_different_day(weekhash1 TEXT, weekhash2 TEXT)
RETURNS INTEGER
AS $$
SELECT COALESCE(
 -- First try to find a different day where both weeks have traffic
 (SELECT ((string_to_array(c.day,'='))[1])::integer - 1
    FROM unnest(string_to_array(weekhash1, ',')) AS c (day)
    JOIN unnest(string_to_array(weekhash2, ',')) AS d (day)
      ON (string_to_array(c.day,'='))[1] = (string_to_array(d.day,'='))[1]
   WHERE (string_to_array(c.day,'='))[2] != ''
     AND (string_to_array(d.day,'='))[2] != ''
     AND c.day != d.day
   LIMIT 1),
 -- Fallback: consider difference where other day has no traffic
 (SELECT ((string_to_array(c.day,'='))[1])::integer - 1
    FROM unnest(string_to_array(weekhash1, ',')) AS c (day)
    JOIN unnest(string_to_array(weekhash2, ',')) AS d (day)
      ON (string_to_array(c.day,'='))[1] = (string_to_array(d.day,'='))[1]
   WHERE c.day != d.day
   LIMIT 1));
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_first_different_day(TEXT,TEXT) IS
E'Compare two week hashes and return the first different day of week index (monday is zero).';

CREATE OR REPLACE FUNCTION gtfs_compare_weeks_excluding_no_traffic(wh1 TEXT, wh2 TEXT)
RETURNS BOOLEAN
AS $$
SELECT NOT EXISTS(
  SELECT d1.day
    FROM (SELECT (string_to_array(w1.dh, '='))[1] as day,
                 (string_to_Array(w1.dh, '='))[2] as dayhash
           FROM unnest(string_to_array(wh1, ',')) AS w1 (dh)) d1
    JOIN (SELECT (string_to_array(w2.dh, '='))[1] as day,
                 (string_to_Array(w2.dh, '='))[2] as dayhash
           FROM unnest(string_to_array(wh2, ',')) AS w2 (dh)) d2
      ON d1.day = d2.day
   WHERE d1.dayhash != '' AND d2.dayhash != ''
     AND d1.dayhash != d2.dayhash
);
$$ LANGUAGE SQL IMMUTABLE;

COMMENT ON FUNCTION gtfs_compare_weeks_excluding_no_traffic(TEXT,TEXT) IS
E'Returns TRUE if all days where both given weekhashes have traffic, are the same';

CREATE OR REPLACE FUNCTION gtfs_route_next_different_week(
    service_id INTEGER,
    route_short_name TEXT, route_long_name TEXT, trip_headsign TEXT)
RETURNS gtfs_week_diff
AS $$
WITH weeks AS (
 SELECT (date_trunc('week',CURRENT_DATE) + (CONCAT(w,' weeks'))::interval)::date AS "beginning-of-week"
   FROM generate_series(0, 15) w
)
SELECT date_trunc('week', CURRENT_DATE)::date AS "beginning-of-current-week",
       gtfs_service_route_week_hash(service_id, CURRENT_DATE, route_short_name, route_long_name, trip_headsign) AS "current-weekhash",
       chg."beginning-of-different-week", chg."different-weekhash"
  FROM (SELECT wh."beginning-of-week" AS "beginning-of-different-week",
               wh.weekhash AS "different-weekhash",
               gtfs_service_route_week_hash(service_id, CURRENT_DATE, route_short_name, route_long_name, trip_headsign) AS curwh,
               LEAD(wh.weekhash, 2) OVER w AS nextwh
          FROM (SELECT w."beginning-of-week",
                       gtfs_service_route_week_hash(service_id, w."beginning-of-week", route_short_name, route_long_name, trip_headsign) as weekhash
                  FROM weeks w
                 ORDER BY "beginning-of-week") wh
         WINDOW w AS (ROWS BETWEEN CURRENT ROW AND 2 FOLLOWING)) chg
 WHERE (-- Find first week with a different hash than current week
        not gtfs_compare_weeks_excluding_no_traffic(chg."different-weekhash", chg.curwh) AND
        -- But skip over two different weeks (like bank holiday, christmas vacation)
        not gtfs_compare_weeks_excluding_no_traffic(chg.curwh, chg.nextwh))
 LIMIT 1;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_route_next_different_week(INTEGER,TEXT,TEXT,TEXT) IS
E'Returns the next different week for the given service and route.';

CREATE OR REPLACE FUNCTION gtfs_service_routes_with_daterange(service_id INTEGER)
RETURNS SETOF gtfs_route_with_daterange AS $$
WITH dates AS (
  -- Calculate a series of dates from beginning of last year
  -- to the end of the next year.
  SELECT ts::date AS date
    FROM generate_series(
            (date_trunc('year', CURRENT_DATE) - '1 year'::interval)::date,
            (date_trunc('year', CURRENT_DATE) + '2 years'::interval)::date,
            '1 day'::interval) AS g(ts)
)
SELECT COALESCE(rh."route-short-name",'') AS "route-short-name",
       COALESCE(rh."route-long-name",'') AS "route-long-name",
       COALESCE(rh."trip-headsign", '') AS "trip-headsign",
       MIN(d.date) AS "min-date",
       MAX(d.date) AS "max-date",
       COALESCE(rh."route-hash-id", '') AS "route-hash-id"
  FROM dates d
  -- Join packages for each date
  JOIN LATERAL unnest(gtfs_service_packages_for_date(service_id::INTEGER, d.date))
    AS ps (package_id) ON TRUE
  -- Join all date hashes for packages
  JOIN "gtfs-date-hash" dh ON (dh."package-id" = package_id AND dh.date = d.date)
  -- Join unnested per route hashes
  JOIN LATERAL unnest(dh."route-hashes") rh ON TRUE
 WHERE rh.hash IS NOT NULL
  AND rh."route-hash-id" IS NOT NULL
  AND rh."route-hash-id" != ''
 GROUP BY rh."route-short-name", rh."route-long-name", rh."trip-headsign", rh."route-hash-id"
 -- Remove routes that do not have traffic anymore
 HAVING MAX(d.date) >= current_date;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION calculate_route_hash_id_using_headsign(package_id INTEGER)
RETURNS VOID
AS $$
BEGIN

  DELETE FROM "detection-route" WHERE "package-id" = package_id;

  INSERT INTO "detection-route" ("gtfs-route-id", "package-id", "route-id", "route-short-name", "route-long-name", "route-hash-id", "trip-headsign")
    SELECT r.id, r."package-id", r."route-id", r."route-short-name", r."route-long-name",
           concat(trim(r."route-short-name"), '-', trim(r."route-long-name"), '-', trim(trip."trip-headsign")), trip."trip-headsign"
     FROM "gtfs-route" r
     JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
     JOIN LATERAL unnest(t.trips) trip ON true
    WHERE r."package-id" = package_id
    GROUP BY r.id, trip."trip-headsign"
       ON CONFLICT DO NOTHING;

END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_route_hash_id_using_short_long(package_id INTEGER)
RETURNS VOID
AS $$
BEGIN

  DELETE FROM "detection-route" WHERE "package-id" = package_id;

  INSERT INTO "detection-route" ("gtfs-route-id", "package-id", "route-id", "route-short-name", "route-long-name", "route-hash-id", "trip-headsign")
    SELECT r.id, r."package-id", r."route-id", r."route-short-name", r."route-long-name", concat(trim(r."route-short-name"), '-', trim(r."route-long-name")), trip."trip-headsign"
    FROM "gtfs-route" r
    JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
    JOIN LATERAL unnest(t.trips) trip ON true
   WHERE r."package-id" = package_id
   GROUP BY r.id, trip."trip-headsign"
      ON CONFLICT DO NOTHING;

END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_route_hash_id_using_route_id(package_id INTEGER)
RETURNS VOID
AS $$
BEGIN

  DELETE FROM "detection-route" WHERE "package-id" = package_id;

  INSERT INTO "detection-route" ("gtfs-route-id", "package-id", "route-id", "route-short-name", "route-long-name", "route-hash-id", "trip-headsign")
    SELECT r.id, r."package-id", r."route-id", r."route-short-name", r."route-long-name", r."route-id", trip."trip-headsign"
      FROM "gtfs-route" r
      JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
      JOIN LATERAL unnest(t.trips) trip ON true
     WHERE r."package-id" = package_id
     GROUP BY r.id, trip."trip-headsign"
        ON CONFLICT DO NOTHING;

END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_route_hash_id_using_long_headsign(package_id INTEGER)
RETURNS VOID
AS $$
BEGIN

  DELETE FROM "detection-route" WHERE "package-id" = package_id;

  INSERT INTO "detection-route" ("gtfs-route-id", "package-id", "route-id", "route-short-name", "route-long-name", "route-hash-id", "trip-headsign")
    SELECT r.id, r."package-id", r."route-id", r."route-short-name", r."route-long-name", concat(trim(r."route-long-name"), '-', trim(trip."trip-headsign")), trip."trip-headsign"
      FROM "gtfs-route" r
      JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
      JOIN LATERAL unnest(t.trips) trip ON true
     WHERE r."package-id" = package_id
     GROUP BY r.id, trip."trip-headsign"
        ON CONFLICT DO NOTHING;

END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_route_hash_id_using_long(package_id INTEGER)
RETURNS VOID
AS $$
BEGIN

  DELETE FROM "detection-route" WHERE "package-id" = package_id;

  INSERT INTO "detection-route" ("gtfs-route-id", "package-id", "route-id", "route-short-name", "route-long-name", "route-hash-id", "trip-headsign")
    SELECT r.id, r."package-id", r."route-id", r."route-short-name", r."route-long-name", r."route-long-name", trip."trip-headsign"
      FROM "gtfs-route" r
      JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
      JOIN LATERAL unnest(t.trips) trip ON true
     WHERE r."package-id" = package_id
     GROUP BY r.id, trip."trip-headsign"
        ON CONFLICT DO NOTHING;

END
$$ LANGUAGE plpgsql;
