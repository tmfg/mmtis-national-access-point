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

-------------------------------------
-- New per service query functions.
-- A service can have multiple packages (different external interfaces) operating on the same day
-- so we use an INTEGER[] to pass in all package ids to use.
--------


CREATE TYPE route_trips_for_date AS (
 "route-short-name" TEXT,
 "route-long-name" TEXT,
 "trip-headsign" TEXT,
 trips INTEGER,
 tripdata "gtfs-trip-info"[]
);

CREATE TYPE service_ref AS (
  "package-id" INTEGER,
  "service-id" TEXT
);

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

COMMENT ON FUNCTION gtfs_services_for_date(INTEGER[], DATE) IS
E'Given package ids and a date, fetch all services that are operating on that date. Returns service
references which contain the package-id and the service-id.';

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

COMMENT ON FUNCTION gtfs_service_packages_for_date(INTEGER, DATE) IS
E'Returns an array of package ids that are in effect for the given service and date.';

CREATE OR REPLACE FUNCTION gtfs_route_trips_for_date(package_ids INTEGER[], dt DATE)
RETURNS SETOF route_trips_for_date
AS $$
SELECT r."route-short-name", r."route-long-name", trip."trip-headsign",
       COUNT(trip."trip-id")::INTEGER AS trips,
       array_agg(trip) as tripdata
  FROM "gtfs-route" r
  JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
  JOIN LATERAL unnest(t.trips) trip ON true
 WHERE r."package-id" = ANY(package_ids)
   AND ROW(r."package-id", t."service-id")::service_ref IN (SELECT * FROM gtfs_services_for_date(package_ids, dt))
 GROUP BY r."route-short-name", r."route-long-name", trip."trip-headsign"
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_route_trips_for_date(INTEGER[], DATE) IS
E'Return trips from given packages for the given date';

CREATE OR REPLACE FUNCTION gtfs_service_routes_with_trips_for_date(service_id INTEGER, dt DATE)
RETURNS SETOF route_trips_for_date
AS $$
SELECT gtfs_route_trips_for_date((SELECT gtfs_service_packages_for_date(service_id, dt)), dt);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION gtfs_route_differences("route-short-name" TEXT, "route-long-name" TEXT, "trip-headsign" TEXT, d1_trips "gtfs-trip-info"[], d2_trips "gtfs-trip-info"[])
RETURNS "gtfs-route-change-info"
AS $$
DECLARE
  chg "gtfs-route-change-info";
BEGIN
  chg."route-short-name" := "route-short-name";
  chg."route-long-name" := "route-long-name";
  chg."trip-headsign" := "trip-headsign";
  chg."added-trips" := 666;
  chg."removed-trips" := 42;
  chg."trip-stop-sequence-changes" := 1;
  chg."trip-stop-time-changes" := 44;
  RETURN chg;
END
$$ LANGUAGE plpgsql;

--select gtfs_route_differences('fpp','bar','61',ARRAY[]::"gtfs-trip-info"[], ARRAY[]::"gtfs-trip-info"[]);

COMMENT ON FUNCTION gtfs_route_differences(TEXT,TEXT,TEXT,"gtfs-trip-info"[],"gtfs-trip-info"[]) IS
E'Calculate differences in daily trips for route. Takes in two trip arrays for different dates.';

CREATE OR REPLACE FUNCTION gtfs_date_differences(service_id INTEGER, date1 DATE, date2 DATE)
RETURNS SETOF TEXT
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
             COALESCE(l.tripdata,ARRAY[]::"gtfs-trip-info"[]) != COALESCE(r.tripdata,ARRAY[]::"gtfs-trip-info"[]) as "different?"
        FROM gtfs_route_trips_for_date(gtfs_service_packages_for_date(service_id, date1), date1) l
        FULL OUTER JOIN gtfs_route_trips_for_date(gtfs_service_packages_for_date(service_id, date2), date2) r ON
             (l."route-short-name"=r."route-short-name" AND l."route-long-name"=r."route-long-name" AND l."trip-headsign"=r."trip-headsign")
  LOOP
    IF row."date1-trips" IS NULL AND row."date2-trips" IS NOT NULL THEN
      RETURN NEXT CONCAT('uusi reitti: ', row."route-long-name");
      added_routes := added_routes + 1;
    ELSIF row."date1-trips" IS NOT NULL AND row."date2-trips" IS NULL THEN
      RETURN NEXT CONCAT('poistunut reitti: ', row."route-long-name");
      removed_routes := removed_routes + 1;
    ELSE
      IF row."different?" THEN
        route_change := gtfs_route_differences(row."route-short-name",row."route-long-name",row."trip-headsign", row."date1-trips", row."date2-trips");
        route_changes := route_changes || route_change;
        added_trips := added_trips + route_change."added-trips";
        removed_trips := removed_trips + route_change."removed-trips";
        stop_seq_changes := stop_seq_changes + route_change."trip-stop-sequence-changes";
        stop_time_changes := stop_time_changes + route_change."trip-stop-time-changes";
        RETURN NEXT CONCAT('muuttunut: ', to_json(route_change));
      ELSE
        RETURN NEXT CONCAT('sama: ', row."route-long-name");
      END IF;
    END IF;
  END LOOP;
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
          route_changes, package_ids);
END
$$ LANGUAGE plpgsql;
