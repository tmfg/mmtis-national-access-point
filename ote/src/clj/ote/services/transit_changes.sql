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
   WHERE "change-date" >= CURRENT_DATE
   ORDER BY "transport-service-id", date desc
)
SELECT ts.id AS "transport-service-id",
       ts.name AS "transport-service-name",
       op.name AS "transport-operator-name",
       "added-routes", "removed-routes", "changed-routes",
       CURRENT_DATE as "current-date",
       "change-date",
       "date",
       "change-date" - CURRENT_DATE AS "days-until-change",
       ("change-date" IS NOT NULL) AS "changes?",
       (SELECT string_agg(fr, ',')
          FROM gtfs_package p
          JOIN LATERAL unnest(p."finnish-regions") fr ON TRUE
         WHERE id = ANY(c."package-ids")) AS "finnish-regions"
  FROM "transport-service" ts
  JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
  LEFT JOIN latest_transit_changes c ON c."transport-service-id" = ts.id
 WHERE 'road' = ANY(ts."transport-type")
   AND 'schedule' = ts."sub-type"
 ORDER BY "change-date" ASC;
