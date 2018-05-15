-- name: gtfs-trip-id-and-index
-- Select row id and index for updating stop times
SELECT tr."trip-id" AS "trip-id", gt.id as "trip-row-id", tr.ordinality as index
  FROM "gtfs-trip" gt
  LEFT JOIN LATERAL unnest(trips) WITH ORDINALITY AS tr ON TRUE
 WHERE gt."package-id" = :package-id;

-- name: update-stop-times!
UPDATE "gtfs-trip"
   SET trips[:index]."stop-times" = :stop-times::"gtfs-stop-time-info"[]
 WHERE "gtfs-trip".id = :trip-row-id

-- name: generate-date-hashes!
INSERT INTO "gtfs-date-hash" ("package-id", date, hash)
 SELECT :package-id::INTEGER, gtfs_package_dates,
        gtfs_hash_for_date(:package-id::INTEGER, gtfs_package_dates)
   FROM gtfs_package_dates(:package-id::INTEGER);
