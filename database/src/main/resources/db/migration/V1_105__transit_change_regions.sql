-- Add region information to nightly transit changes

CREATE OR REPLACE FUNCTION gtfs_package_finnish_regions(package_id INTEGER) RETURNS CHAR(2)[] AS $$
SELECT array_agg(x.numero) AS "finnish-regions"
  FROM (SELECT DISTINCT r.numero
          FROM "gtfs-stop" s
          JOIN finnish_regions r ON ST_Contains(r.location, ST_SetSRID(ST_MakePoint("stop-lon", "stop-lat"), 4326))
         WHERE "package-id" = package_id) x;
$$ LANGUAGE SQL STABLE;

-- Recreate nightly-transit-changes view with new change information

DROP FUNCTION refresh_nightly_transit_changes ();
DROP MATERIALIZED VIEW "nightly-transit-changes";

CREATE MATERIALIZED VIEW "nightly-transit-changes" AS
WITH operators AS (
  SELECT DISTINCT "transport-operator-id", "transport-service-id"
    FROM gtfs_package
)
SELECT datepair.p[1] as "current-week-date", datepair.p[2] as "different-week-date",
       changes.*, diff.*
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
               x.weekhash as "different-weekhash",
               (SELECT array_agg(regs.num)
                  FROM (SELECT unnest(gtfs_package_finnish_regions(gtfs_latest_package_for_date(ops."transport-operator-id", CURRENT_DATE))) num
                         UNION
                        SELECT unnest(gtfs_package_finnish_regions(gtfs_latest_package_for_date(ops."transport-operator-id", x.dt::DATE))) num) regs) AS "finnish-regions"
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
