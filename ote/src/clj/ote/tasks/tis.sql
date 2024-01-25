-- name: select-packages-without-finished-results
SELECT *
  FROM "gtfs_package"
 WHERE "tis-entry-public-id" IS NOT NULL
   AND "tis-complete" IS FALSE
   AND created > (NOW() - INTERVAL '1 week') IS TRUE;

-- name: update-tis-results!
UPDATE "gtfs_package"
   SET "tis-complete" = :tis-complete,
       "tis-success" = :tis-success
WHERE "tis-entry-public-id" = :tis-entry-public-id;

-- name: fetch-count-service-packages
SELECT COUNT(p.id) as "package-count" FROM gtfs_package p WHERE p."transport-service-id" = :service-id;

-- name: list-all-external-interfaces
SELECT tso.id                         AS "operator-id",
       tso.name                       AS "operator-name",
       tse.id                         AS "service-id",
       eid.id                         AS "external-interface-description-id",
       eid.license                    AS "license",
       (eid."external-interface").url AS url
  FROM "external-interface-description" eid
           LEFT JOIN "transport-service" tse ON tse.id = eid."transport-service-id"
           LEFT JOIN "transport-operator" tso ON tso.id = tse."transport-operator-id"
 WHERE tse.published IS NOT NULL
   AND ('GTFS' = ANY (eid.format) OR 'gtfs' = ANY (eid.format));