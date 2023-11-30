-- track whether the related TIS entry processing is complete separately to avoid cases where complete entry
-- without usable results gets polled over and over and over... again
ALTER TABLE "gtfs_package" ADD COLUMN "tis-complete" BOOLEAN DEFAULT FALSE;