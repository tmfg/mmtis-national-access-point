-- link Netex conversion results to related GTFS import package to enable linking conversion reports to GTFS import
-- reports
ALTER TABLE "netex-conversion"
    ADD COLUMN package_id integer REFERENCES gtfs_package ("id");