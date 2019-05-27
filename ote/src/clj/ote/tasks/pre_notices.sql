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

-- name: fetch-unsent-changes-by-regions
-- Fetch newest changes. Use package table to get regions
SELECT * FROM (
                SELECT DISTINCT ON (h.id) h.id AS "history-id",
                       (SELECT array_agg(fr.nimi)
                          FROM finnish_regions fr
                         WHERE fr.numero = ANY (p."finnish-regions")) AS regions,
                      to_char(h."different-week-date", 'dd.mm.yyyy') AS "different-week-date",
                      op.name AS "operator-name",
                      ts.name AS "service-name",
                      ts.id AS "transport-service-id",
                      to_char(h."change-detected", 'yyyy-mm-dd') AS date,
                      (h."different-week-date" - CURRENT_DATE) AS "days-until-change",
                      h."change-type",
                      h."route-hash-id"
                FROM "detected-change-history" h
                     JOIN gtfs_package p ON p.id = ANY (h."package-ids")
                     JOIN "detected-route-change" r ON h."change-key" = r."change-key"
                     JOIN "transport-service" ts
                            ON ts.id = h."transport-service-id" AND ts."sub-type" = 'schedule' AND
                               ts."commercial-traffic?" = TRUE
                     JOIN "transport-operator" op ON op.id = ts."transport-operator-id"
                WHERE h."email-sent" IS NULL
                  AND (p."finnish-regions" IS NULL OR
                       :regions::CHAR(2)[] IS NULL OR
                       :regions::CHAR(2)[] && p."finnish-regions")
                GROUP BY h.id,p."finnish-regions", op.name, ts.name,ts.id
              ) x
 ORDER BY x."different-week-date" ASC;


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
  JOIN "transport-service" ts ON ts.id = chg."transport-service-id" AND ts."sub-type" = 'schedule' AND ts."commercial-traffic?" = TRUE
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
  JOIN "transport-service" ts ON ts.id = chg."transport-service-id" AND ts."sub-type" = 'schedule' AND ts."commercial-traffic?" = TRUE
  JOIN "transport-operator" op ON op.id = ts."transport-operator-id"
 WHERE chg.date = :date::DATE
   AND chg."change-date" IS NOT NULL
   AND (chg."finnish-regions" IS NULL OR
        :regions::CHAR(2)[] IS NULL OR
        :regions::CHAR(2)[] && chg."finnish-regions") ORDER BY chg."change-date" ASC;