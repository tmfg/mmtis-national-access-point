-- name: select-packages-without-finished-results
SELECT *
  FROM "gtfs_package"
 WHERE "tis-entry-public-id" IS NOT NULL
   AND "tis-result-links" IS NULL
   AND "tis-complete" IS NULL OR FALSE;

-- name: update-tis-results!
UPDATE "gtfs_package"
   SET "tis-result-links" = :tis-result-links,
       "tis-complete" = :tis-complete
WHERE "tis-entry-public-id" = :tis-entry-public-id;