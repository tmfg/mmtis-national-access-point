-- Add first_package column to gtfs_package

ALTER TABLE gtfs_package
ADD COLUMN first_package boolean DEFAULT false;

UPDATE gtfs_package SET first_package = TRUE WHERE ID IN (
SELECT DISTINCT ON ("external-interface-description-id") id from gtfs_package p ORDER BY "external-interface-description-id", created ASC);