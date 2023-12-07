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