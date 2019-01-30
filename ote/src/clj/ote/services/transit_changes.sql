-- name: next-different-week-for-operator
SELECT "change-date",
       "current-year", "current-week", "current-weekhash",
       "different-year", "different-week", "different-weekhash"
  FROM "nightly-transit-changes"
 WHERE "transport-operator-id" = :operator-id;

-- name: upcoming-changes
WITH latest_transit_changes AS (
  SELECT DISTINCT ON ("transport-service-id") *
    FROM "gtfs-transit-changes"
   ORDER BY "transport-service-id", date desc
)
SELECT ts.id AS "transport-service-id",
       ts.name AS "transport-service-name",
       op.name AS "transport-operator-name",
       "added-routes", "removed-routes", "changed-routes", "no-traffic-routes",
       CURRENT_DATE as "current-date",
       "different-week-date", "change-date",
       "date",
       "change-date" - CURRENT_DATE AS "days-until-change",
       ("change-date" IS NOT NULL) AS "changes?",
       EXISTS(SELECT id
                FROM "external-interface-description" eid
               WHERE eid."transport-service-id" = ts.id
                 AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                 AND 'route-and-schedule' = ANY(eid."data-content")
                 AND ("gtfs-db-error" IS NOT NULL OR "gtfs-import-error" IS NOT NULL)) AS "interfaces-has-errors?",
       NOT EXISTS(SELECT id
                    FROM "external-interface-description" eid
                   WHERE eid."transport-service-id" = ts.id
                     AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                     AND 'route-and-schedule' = ANY(eid."data-content")) AS "no-interfaces?",
       NOT EXISTS(SELECT id
                    FROM "external-interface-description" eid
                   WHERE eid."transport-service-id" = ts.id
                     AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                     AND 'route-and-schedule' = ANY(eid."data-content") AND eid."gtfs-imported" IS NOT NULL) AS "no-interfaces-imported?",
       (SELECT string_agg(fr, ',')
          FROM gtfs_package p
          JOIN LATERAL unnest(p."finnish-regions") fr ON TRUE
         WHERE id = ANY(c."package-ids")) AS "finnish-regions",
       (SELECT (upper(gtfs_package_date_range(p.id)) - '1 day'::interval)::date
          FROM gtfs_package p
         WHERE p."transport-service-id" = ts.id AND p."deleted?" = FALSE
         ORDER BY p.id DESC limit 1) as "max-date"
  FROM "transport-service" ts
  JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
  LEFT JOIN latest_transit_changes c ON c."transport-service-id" = ts.id
 WHERE 'road' = ANY(ts."transport-type")
   AND 'schedule' = ts."sub-type"
   AND ts."published?" = TRUE
 ORDER BY "different-week-date" ASC, "interfaces-has-errors?" DESC, "no-interfaces?" DESC, "no-interfaces-imported?" ASC;

-- name: calculate-routes-route-hashes-using-headsign
SELECT calculate_route_hash_id_using_headsign(:package-id::INTEGER);
-- name: calculate-routes-route-hashes-using-short-and-long
SELECT calculate_route_hash_id_using_short_long(:package-id::INTEGER);
-- name: calculate-routes-route-hashes-using-route-id
SELECT calculate_route_hash_id_using_route_id(:package-id::INTEGER);
-- name: calculate-routes-route-hashes-using-long-headsign
SELECT calculate_route_hash_id_using_long_headsign(:package-id::INTEGER);
-- name: calculate-routes-route-hashes-using-long
SELECT calculate_route_hash_id_using_long(:package-id::INTEGER);

-- name: fetch-services-with-route-hash-id
SELECT ts.id as "service-id", ts.name as service, op.name as operator, d."route-hash-id-type" as type
  FROM "detection-service-route-type" d, "transport-service" ts, "transport-operator" op
 WHERE d."transport-service-id" = ts.id
   AND op.id = ts."transport-operator-id"
   ORDER BY d."route-hash-id-type" , ts.id;