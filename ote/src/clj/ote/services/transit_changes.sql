-- name: next-different-week-for-operator
SELECT date_trunc('week', x.dt::DATE) as "change-date",
       EXTRACT(YEAR FROM :date::DATE)::INTEGER as "current-year",
       EXTRACT(WEEK FROM :date::DATE)::INTEGER AS "current-week",
       gtfs_operator_week_hash(:operator-id::INTEGER, :date::DATE) as "current-weekhash",
       EXTRACT(YEAR FROM x.dt)::INTEGER AS "different-year",
       EXTRACT(WEEK FROM x.dt)::INTEGER AS "different-week",
       x.weekhash as "different-weekhash"
  FROM (SELECT s.dt::date, gtfs_operator_week_hash(:operator-id::INTEGER, s.dt::date) as weekhash
          FROM (SELECT generate_series((:date::date + '1 week'::interval)::timestamp,
                                       (:date::date + '1 year'::interval)::timestamp, '1 week') dt) s) x
 WHERE x.weekhash != gtfs_operator_week_hash(:operator-id::INTEGER, :date::date)
 LIMIT 1;

-- name: list-current-operators
WITH operators AS (
  SELECT DISTINCT "transport-operator-id"
    FROM gtfs_package
)
SELECT op.id as "transport-operator-id", op.name AS "transport-operator-name"
  FROM operators ops
  JOIN "transport-operator" op ON ops."transport-operator-id" = op.id
 WHERE gtfs_latest_package_for_date(ops."transport-operator-id", :date::date) IS NOT NULL;
