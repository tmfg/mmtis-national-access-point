ALTER TABLE "gtfs-date-hash"
  ADD COLUMN "transport-service-id" INTEGER;

UPDATE "gtfs-date-hash" SET "transport-service-id" = p."transport-service-id" FROM "gtfs_package" p WHERE p.id = "gtfs-date-hash"."package-id";
