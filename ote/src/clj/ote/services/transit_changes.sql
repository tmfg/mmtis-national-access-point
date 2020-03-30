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
       ts."commercial-traffic?" AS "commercial?",
       ts.name AS "transport-service-name",
       op.name AS "transport-operator-name",
       c."current-added-routes" as "added-routes",
       c."current-removed-routes" as "removed-routes",
       c."current-no-traffic-routes" as "no-traffic-routes",
       c."current-changed-routes" as "changed-routes",
       CURRENT_DATE as "current-date",
       c."change-date",
       c."date",
       ("sent-emails"."email-sent" IS NOT NULL) AS "recent-change?",
       MIN(drc."different-week-date") as "different-week-date",
       MIN(drc."different-week-date") - CURRENT_DATE AS "days-until-change",
       (c."different-week-date" IS NOT NULL) AS "changes?",

       -- IF max-date is given, then there cannot be any errors
       CASE
           WHEN (SELECT (upper(gtfs_package_date_range(p.id)))::date
                  FROM gtfs_package p
                 WHERE p."transport-service-id" = ts.id AND p."deleted?" = FALSE
                 ORDER BY p.id DESC limit 1) > '1900-01-01'::date THEN false
           ELSE true
           END
           AS "interfaces-has-errors?",

       NOT EXISTS(SELECT id
                  FROM "external-interface-description" eid
                  WHERE eid."transport-service-id" = ts.id
                    AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                    AND 'route-and-schedule' = ANY(eid."data-content")) AS "no-interfaces?",
       NOT EXISTS(SELECT eid.id
                  FROM "external-interface-description" eid, "external-interface-download-status" eids
                  WHERE eid."transport-service-id" = ts.id and eids."external-interface-description-id" = eid.id
                    AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
                    AND 'route-and-schedule' = ANY(eid."data-content")) AS "no-interfaces-imported?",
       (SELECT string_agg(fr, ',')
        FROM gtfs_package p
               JOIN LATERAL unnest(p."finnish-regions") fr ON TRUE
        WHERE id = ANY(c."package-ids")) AS "finnish-regions",
       (SELECT (upper(gtfs_package_date_range(p.id)))::date
          FROM gtfs_package p
         WHERE p."transport-service-id" = ts.id AND p."deleted?" = FALSE
         ORDER BY p.id DESC limit 1) as "max-date"
FROM "transport-service" ts
     LEFT JOIN latest_transit_changes c ON ts.id = c."transport-service-id"
     LEFT JOIN (SELECT distinct drc."transit-service-id", drc."transit-change-date", drc."different-week-date"
                  FROM "detected-route-change" drc
                 WHERE drc."different-week-date" >= CURRENT_DATE
                 ORDER BY drc."different-week-date" ASC) drc ON drc."transit-service-id" = c."transport-service-id" AND drc."transit-change-date" = c.date
     JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
     LEFT JOIN (SELECT DISTINCT ON (dch."transport-service-id") *
                  FROM "detected-change-history" dch
                 WHERE dch."email-sent" = (SELECT MAX("email-sent") AS max
                                             FROM "detected-change-history" dc)) AS "sent-emails" ON ts.id = "sent-emails"."transport-service-id"
WHERE 'road' = ANY(ts."transport-type")
  AND 'schedule' = ts."sub-type"
  -- Get new changes or changes that can't be found because of invalid gtfs package which makes different-week-date as null
  AND (drc."different-week-date" >= CURRENT_DATE OR drc."different-week-date" IS NULL)
  AND ts.published IS NOT NULL
-- Group so that each group represents a distinct change date, allows summing up changes in SELECT section of this query
GROUP BY ts.id, c."date", op.name, c."change-date", c."package-ids", c."different-week-date", "sent-emails"."email-sent",
         c."current-added-routes", c."current-removed-routes", c."current-no-traffic-routes", c."current-changed-routes"

ORDER BY "different-week-date" ASC, "interfaces-has-errors?" DESC, "no-interfaces?" DESC, "no-interfaces-imported?" ASC,
         op.name ASC;

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

-- name: valid-detected-route-changes
-- Changes will expire and to keep statistic up to date return only changes that are not yet exipred
SELECT c."route-hash-id", c."different-week-date", c."change-type"
FROM "detected-route-change" c
       LEFT JOIN "detected-change-history" h ON h."transport-service-id" = c."transit-service-id" AND h."change-key" = c."change-key" AND h."different-week-date" >= :date
WHERE c."transit-change-date" = :date
  AND c."transit-service-id" = :service-id
  AND c."different-week-date" >= CURRENT_DATE;


-- name: fetch-gtfs-interface-for-service
-- Currently only in admin panel gtfs packages can be uploaded to server using a form.
-- We need to match package with interface so return possible interfaces.
SELECT i.id, i."transport-service-id", (i."external-interface").url as url, i.format, i."data-content"
  FROM "external-interface-description" i
 WHERE (('GTFS' = ANY(i.format)) OR ('Kalkati.net' = ANY(i.format)))
   AND 'route-and-schedule' = ANY(i."data-content")
   AND i."transport-service-id" = :service-id AND i.id = :interface-id
 ORDER BY id DESC;
