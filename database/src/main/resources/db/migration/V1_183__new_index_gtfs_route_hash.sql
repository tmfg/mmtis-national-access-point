-- To speed up queries we need one more index to filter down results
create index "gtfs-date-hash_package-id_transport-service-id_date_index"
    on "gtfs-date-hash" ("package-id", "transport-service-id", date);

-- This should be added using code - But ensure that when the code is changed old values are repaired in db
UPDATE "gtfs-date-hash" SET "transport-service-id" = p."transport-service-id"
FROM "gtfs_package" p
WHERE p.id = "gtfs-date-hash"."package-id" AND "gtfs-date-hash"."transport-service-id" IS NULL;