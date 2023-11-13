-- name: select-packages-without-finished-results
SELECT *
  FROM "gtfs_package"
 WHERE "tis-entry-public-id" IS NOT NULL
   AND "tis-result-links" IS NULL;

-- name: update-tis-results!
UPDATE "gtfs_package"
   SET "tis-result-links" = :tis-result-links
WHERE "tis-entry-public-id" = :tis-entry-public-id;