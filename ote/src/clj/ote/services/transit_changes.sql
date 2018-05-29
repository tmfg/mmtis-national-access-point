-- name: next-different-week-for-package
SELECT date_trunc('week', x.dt::DATE) as "change-date",
       EXTRACT(YEAR FROM :date::DATE)::INTEGER as "current-year",
       EXTRACT(WEEK FROM :date::DATE)::INTEGER AS "current-week",
       gtfs_package_week_hash(:package-id::INTEGER, :date::DATE) as "current-weekhash",
       EXTRACT(YEAR FROM x.dt)::INTEGER AS "different-year",
       EXTRACT(WEEK FROM x.dt)::INTEGER AS "different-week",
       x.weekhash as "different-weekhash"
  FROM (SELECT s.dt::date, gtfs_package_week_hash(:package-id::INTEGER, s.dt::date) as weekhash
          FROM (SELECT generate_series((:date::date + '1 week'::interval)::timestamp, (:date::date + '1 year'::interval)::timestamp, '1 week') dt) s) x
 WHERE x.weekhash != gtfs_package_week_hash(:package-id::INTEGER, :date::date)
 LIMIT 1;

-- name: list-current-packages
SELECT p.id AS "package-id", p."transport-operator-id", p."transport-service-id",
       op.name AS "transport-operator-name",
       ts.name AS "transport-service-name"
  FROM gtfs_package p
  JOIN "transport-operator" op ON p."transport-operator-id"=op.id
  JOIN "transport-service" ts ON p."transport-service-id"=ts.id
 WHERE gtfs_package_date_range(p.id) @> :date::date;
