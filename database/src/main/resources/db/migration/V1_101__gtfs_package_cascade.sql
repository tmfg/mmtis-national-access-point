-- Add cascade constraints when deleting gtfs_package

ALTER TABLE "gtfs-agency" DROP CONSTRAINT "gtfs-agency_package-id_fkey";
ALTER TABLE "gtfs-agency" ADD CONSTRAINT "gtfs-agency_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-calendar" DROP CONSTRAINT "gtfs-calendar_package-id_fkey";
ALTER TABLE "gtfs-calendar" ADD CONSTRAINT "gtfs-calendar_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-calendar-date" DROP CONSTRAINT "gtfs-calendar-date_package-id_fkey";
ALTER TABLE "gtfs-calendar-date" ADD CONSTRAINT "gtfs-calendar-date_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-date-hash" DROP CONSTRAINT "gtfs-date-hash_package-id_fkey";
ALTER TABLE "gtfs-date-hash" ADD CONSTRAINT "gtfs-date-hash_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-route" DROP CONSTRAINT "gtfs-route_package-id_fkey";
ALTER TABLE "gtfs-route" ADD CONSTRAINT "gtfs-route_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-shape" DROP CONSTRAINT "gtfs-shape_package-id_fkey";
ALTER TABLE "gtfs-shape" ADD CONSTRAINT "gtfs-shape_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-stop" DROP CONSTRAINT "gtfs-stop_package-id_fkey";
ALTER TABLE "gtfs-stop" ADD CONSTRAINT "gtfs-stop_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-transfer" DROP CONSTRAINT "gtfs-transfer_package-id_fkey";
ALTER TABLE "gtfs-transfer" ADD CONSTRAINT "gtfs-transfer_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;

ALTER TABLE "gtfs-trip" DROP CONSTRAINT "gtfs-trip_package-id_fkey";
ALTER TABLE "gtfs-trip" ADD CONSTRAINT "gtfs-trip_package-id_fkey" FOREIGN KEY ("package-id") REFERENCES gtfs_package (id) ON DELETE CASCADE;
