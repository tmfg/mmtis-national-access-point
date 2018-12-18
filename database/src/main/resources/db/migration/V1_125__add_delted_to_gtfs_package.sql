-- Add possibility to mark gtfs_package as deleted
ALTER TABLE gtfs_package
  ADD COLUMN "deleted?" bool default FALSE;

-- Remove foreign key to external-interface-description
ALTER TABLE gtfs_package
  DROP CONSTRAINT "gtfs_package_external-interface-description-id_fkey";

-- Drop foreign key from detection-service-route-type to transport-service
ALTER TABLE "detection-service-route-type"
  DROP CONSTRAINT "detection-service-route-type_transport-service-id_fkey";

-- Add dropped foreign key and add delete cascade and update cascade
ALTER TABLE "detection-service-route-type"
  ADD CONSTRAINT "detection-service-route-type_transport-service-id_fkey"
  FOREIGN KEY ("transport-service-id") REFERENCES "transport-service" (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- Drop foreign key
ALTER TABLE "gtfs-transit-changes"
  DROP CONSTRAINT "gtfs-transit-changes_transport-service-id_fkey";

-- Add dropped foreign key and add delete cascade and update cascade
ALTER TABLE "gtfs-transit-changes"
  ADD CONSTRAINT "gtfs-transit-changes_transport-service-id_fkey"
  FOREIGN KEY ("transport-service-id") REFERENCES "transport-service" (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- Drop foreign key from detection-route to gtfs_package and gtfs-route because package will stay in database even if routes and service are removed
ALTER TABLE "detection-route" DROP CONSTRAINT "detection-route_package-id_fkey";
ALTER TABLE "detection-route" DROP CONSTRAINT "detection-route_gtfs-route-id_fkey";