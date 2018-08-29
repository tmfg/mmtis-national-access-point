-- name: next-different-week-for-operator
SELECT "change-date",
       "current-year", "current-week", "current-weekhash",
       "different-year", "different-week", "different-weekhash"
  FROM "nightly-transit-changes"
 WHERE "transport-operator-id" = :operator-id;

-- name: upcoming-changes
SELECT ts.id AS "transport-service-id",
       ts.name AS "transport-service-name",
       op.name AS "transport-operator-name",
       "added-routes", "removed-routes", "changed-routes",
       CURRENT_DATE as "current-date",
       "change-date",
       "change-date" - CURRENT_DATE AS "days-until-change",
       ("change-date" IS NOT NULL) AS "changes?",
       (SELECT array_to_string(array_agg("finnish-regions"), ',')
          FROM gtfs_package
         WHERE id = ANY(c."package-ids")) AS "finnish-regions"
  FROM "transport-service" ts
  JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
  LEFT JOIN "gtfs-transit-changes" c ON (c."transport-service-id" = ts.id AND date = CURRENT_DATE)
 ORDER BY "change-date" ASC;

-- name: vanha
SELECT c."transport-service-id",
       ts.name AS "transport-service-name",
       op.name AS "transport-operator-name",
       "added-routes", "removed-routes", "changed-routes",
       CURRENT_DATE as "current-date",
       "change-date",
       "change-date" - CURRENT_DATE AS "days-until-change"
  FROM "gtfs-transit-changes" c
  JOIN "transport-service" ts ON c."transport-service-id" = ts.id
  JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
 WHERE date = CURRENT_DATE;
