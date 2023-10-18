-- add column for storing reference to TIS VACO entry to link between FINAP and TIS VACO processed data
ALTER TABLE "gtfs_package" ADD COLUMN "tis-entry-public-id" VARCHAR(42);