-- Drop foreign key from detected-route-change
ALTER TABLE "detected-route-change"
  DROP CONSTRAINT IF EXISTS "detected-route-change_transit-service-id_fkey";


-- Add delete on cascade on detected gtfs-transit-changes
ALTER TABLE "detected-route-change"
  DROP CONSTRAINT IF EXISTS "detected-route-change_transit-change-date_fkey",
  ADD CONSTRAINT "detected-route-change_transit-change-date_fkey"
      FOREIGN KEY ("transit-change-date", "transit-service-id")
      REFERENCES "gtfs-transit-changes"
      ON DELETE CASCADE;


-- Drop foreign key from gtfs_packages
ALTER TABLE gtfs_package
  DROP CONSTRAINT IF EXISTS  "gtfs_package_transport-service-id_fkey";
