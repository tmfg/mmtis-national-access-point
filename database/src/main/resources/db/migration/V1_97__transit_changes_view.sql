------- BEGIN Copied from R__GTFS_query (we need these to initialize db) --------
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

CREATE OR REPLACE FUNCTION gtfs_latest_package_for_date(operator_id INTEGER, date DATE) RETURNS INTEGER AS $$
SELECT p.id FROM gtfs_package p
 WHERE p."transport-operator-id" = operator_id
   AND gtfs_package_date_range(p.id) @> date
 ORDER BY p.id DESC
 LIMIT 1;
$$ LANGUAGE SQL STABLE;

-------- END copied functions

CREATE MATERIALIZED VIEW "nightly-transit-changes" AS
WITH operators AS (
  SELECT DISTINCT "transport-operator-id", "transport-service-id"
    FROM gtfs_package
)
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
 WHERE gtfs_latest_package_for_date(ops."transport-operator-id", CURRENT_DATE) IS NOT NULL
;

COMMENT ON MATERIALIZED VIEW "nightly-transit-changes" IS
E'This view is refreshed each night and contains upcoming changes determined from GTFS packages.';
