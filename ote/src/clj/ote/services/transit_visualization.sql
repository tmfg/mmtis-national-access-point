-- name: fetch-operator-date-hashes
SELECT date, hash::text
  FROM "gtfs-date-hash"
 WHERE hash IS NOT NULL AND
       "package-id" IN (SELECT id FROM gtfs_package WHERE "transport-operator-id" = :operator-id);
