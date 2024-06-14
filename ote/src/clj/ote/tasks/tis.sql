-- name: select-packages-without-finished-results
SELECT *
  FROM "gtfs_package"
 WHERE "tis-entry-public-id" IS NOT NULL
   AND "tis-complete" IS FALSE
   AND created > (NOW() - INTERVAL '1 week') IS TRUE
 LIMIT 100;

-- name: update-tis-results!
UPDATE "gtfs_package"
   SET "tis-complete" = :tis-complete,
       "tis-success" = :tis-success,
       "tis-magic-link" = :tis-magic-link
WHERE "tis-entry-public-id" = :tis-entry-public-id;

-- name: fetch-count-service-packages
SELECT COUNT(p.id) as "package-count" FROM gtfs_package p WHERE p."transport-service-id" = :service-id;

-- name: list-all-external-interfaces
SELECT tso.id                         AS "operator-id",
       tso.name                       AS "operator-name",
       tse.id                         AS "service-id",
       eid.id                         AS "external-interface-description-id",
       eid.license                    AS "license",
       trim(lower(eid.format[1]))     AS "format",
       (eid."external-interface").url AS url,
       COALESCE(tso.email, u.email)   AS "contact-email"
  FROM "external-interface-description" eid
       LEFT JOIN "transport-service" tse ON tse.id = eid."transport-service-id"
       LEFT JOIN "transport-operator" tso ON tso.id = tse."transport-operator-id"
       LEFT JOIN member m ON m.group_id = tso."ckan-group-id"
       LEFT JOIN "user" u ON u.id = m.table_id
 WHERE tse.published IS NOT NULL
   AND m.capacity = 'admin';