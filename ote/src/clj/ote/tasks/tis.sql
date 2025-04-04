-- name: select-packages-without-finished-results
-- Select only newest package for each interface.
WITH latest_packagees AS (
    SELECT distinct on ("external-interface-description-id") "external-interface-description-id", *
    FROM "gtfs_package"
    ORDER BY  "external-interface-description-id" DESC, id DESC
    LIMIT 100)
SELECT * from latest_packagees
WHERE "tis-complete" IS FALSE
  AND "tis-entry-public-id" IS NOT NULL
  AND created > (NOW() - INTERVAL '1 week') IS TRUE;

-- name: update-tis-results!
UPDATE "gtfs_package"
   SET "tis-complete" = :tis-complete,
       "tis-success" = :tis-success,
       "tis-magic-link" = :tis-magic-link,
       tis_polling_completed = NOW()
WHERE "tis-entry-public-id" = :tis-entry-public-id;

-- name: tis-polling-started!
UPDATE "gtfs_package"
   SET tis_polling_started = NOW()
 WHERE id = :package-id
   AND tis_polling_started IS NULL;

-- name: tis-polling-completed!
UPDATE "gtfs_package" SET tis_polling_completed = NOW() WHERE id = :package-id;

-- name: tis-submit-started!
UPDATE "gtfs_package"
   SET tis_submit_started = NOW()
 WHERE id = :package-id
   AND tis_submit_started IS NULL;

-- name: tis-submit-completed!
UPDATE "gtfs_package" SET tis_submit_completed = NOW() WHERE id = :package-id;

-- name: fetch-count-service-packages
SELECT COUNT(p.id) as "package-count" FROM gtfs_package p WHERE p."transport-service-id" = :service-id;

-- name: list-all-external-interfaces
SELECT DISTINCT
       tso.id                         AS "operator-id",
       tso.name                       AS "operator-name",
       tse.id                         AS "service-id",
       eid.id                         AS "external-interface-description-id",
       eid.license                    AS "license",
       trim(lower(eid.format[1]))     AS "format",
       trim((eid."external-interface").url) AS url,
       COALESCE(tso.email, u.email)   AS "contact-email"
  FROM "external-interface-description" eid
       LEFT JOIN "transport-service" tse ON tse.id = eid."transport-service-id"
       LEFT JOIN "transport-operator" tso ON tso.id = tse."transport-operator-id"
       LEFT JOIN member m ON m.group_id = tso."ckan-group-id"
       LEFT JOIN "user" u ON u.id = m.table_id
 WHERE tse.published IS NOT NULL
   AND m.capacity = 'admin'
   AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format) OR 'NeTEx' = ANY(eid.format))
ORDER BY tso.id DESC;

-- name: fetch-external-interface-for-package
-- For admin panel. Use this when submitting a single package to vaco
SELECT eid.id,
       eid."transport-service-id",
       (eid."external-interface").url as url,
       (eid."external-interface").url,
       eid.format[1],
       eid.license,
       eid."data-content"
from "external-interface-description" eid
WHERE eid.id = (SELECT "external-interface-description-id" from gtfs_package where id = :package-id);