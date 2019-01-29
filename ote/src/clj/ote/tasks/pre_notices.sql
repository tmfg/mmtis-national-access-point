-- name: fetch-pre-notices-by-interval-and-regions
SELECT id, "pre-notice-type", "route-description", created, modified, description, regions as "region-ids",
  (SELECT to_char(eds."effective-date", 'dd.mm.yyyy')
     FROM unnest(n."effective-dates") eds
     ORDER BY eds."effective-date" ASC
     LIMIT 1) as "first-effective-date",
  (SELECT array_agg(fr.nimi)
    FROM "finnish_regions" fr
   WHERE n.regions IS NOT NULL AND fr.numero = ANY(n.regions)) as "regions",
  (SELECT op.name
     FROM "transport-operator" op
    WHERE op.id = n."transport-operator-id") as "operator-name"
  FROM "pre_notice" n
 WHERE "pre-notice-state" = 'sent'
   AND sent IS NOT NULL
   AND ((:regions::char(2)[]) IS NULL OR (:regions::char(2)[]) && n.regions)
   AND (sent > (current_timestamp - :interval::interval));

-- name: fetch-current-detected-changes-by-regions
WITH changes_with_regions AS (
  SELECT chg.*,
         (SELECT array_agg(x.reg)
            FROM (SELECT DISTINCT unnest(p."finnish-regions") as reg
                    FROM gtfs_package p
                   WHERE p.id = ANY(chg."package-ids")
                     AND p."deleted?" = FALSE) x) AS "finnish-regions"

    FROM "gtfs-transit-changes" chg
    WHERE chg.date = CURRENT_DATE
      AND chg."different-week-date" IS NOT NULL
)
SELECT to_char(chg."different-week-date", 'dd.mm.yyyy') as "change-date",
       (chg."different-week-date" - CURRENT_DATE) AS "days-until-change",
       chg."added-routes", chg."removed-routes", chg."changed-routes", chg."no-traffic-routes",
       (SELECT array_agg(fr.nimi)
          FROM finnish_regions fr
         WHERE fr.numero = ANY(chg."finnish-regions")) AS regions,
       op.name AS "operator-name",
       ts.name AS "service-name",
       to_char(date,'yyyy-mm-dd') as date,
       ts.id AS "transport-service-id"
  FROM changes_with_regions chg
  JOIN "transport-service" ts ON ts.id = chg."transport-service-id" AND ts."sub-type" = 'schedule'
  JOIN "transport-operator" op ON op.id = ts."transport-operator-id"
 WHERE chg.date = CURRENT_DATE
   AND chg."change-date" IS NOT NULL
   AND (chg."finnish-regions" IS NULL OR
        :regions::CHAR(2)[] IS NULL OR
        :regions::CHAR(2)[] && chg."finnish-regions") ORDER BY chg."change-date" ASC;

-- name: fetch-current-detected-changes-by-regions-and-date
-- Added only for testing purposes. Isn't used in code, but will help investigate problems.
-- Usage: (fetch-current-detected-changes-by-regions-and-date (:db ote.main/ote) {:regions nil :date "2018-10-10"})
WITH changes_with_regions AS (
  SELECT chg.*,
         (SELECT array_agg(x.reg)
            FROM (SELECT DISTINCT unnest(p."finnish-regions") as reg
                    FROM gtfs_package p
                   WHERE p.id = ANY(chg."package-ids")
                     AND p."deleted?" = FALSE) x) AS "finnish-regions"
    FROM "gtfs-transit-changes" chg
)
SELECT to_char(chg."change-date", 'dd.mm.yyyy') as "change-date",
       ("change-date" - CURRENT_DATE) AS "days-until-change",
       chg."added-routes", chg."removed-routes", chg."changed-routes", chg."no-traffic-routes",
       (SELECT array_agg(fr.nimi)
          FROM finnish_regions fr
         WHERE fr.numero = ANY(chg."finnish-regions")) AS regions,
       op.name AS "operator-name",
       ts.name AS "service-name",
       to_char(date,'yyyy-mm-dd') as date,
       ts.id AS "transport-service-id"
  FROM changes_with_regions chg
  JOIN "transport-service" ts ON ts.id = chg."transport-service-id"
  JOIN "transport-operator" op ON op.id = ts."transport-operator-id"
 WHERE chg.date = :date::DATE
   AND chg."change-date" IS NOT NULL
   AND (chg."finnish-regions" IS NULL OR
        :regions::CHAR(2)[] IS NULL OR
        :regions::CHAR(2)[] && chg."finnish-regions") ORDER BY chg."change-date" ASC;