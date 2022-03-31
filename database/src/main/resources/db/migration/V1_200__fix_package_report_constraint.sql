-- Fixes faulty constraint preventing deleting packages
ALTER TABLE gtfs_import_report
    DROP CONSTRAINT gtfs_import_report_package_id_fkey
    , ADD  CONSTRAINT gtfs_import_report_package_id_fkey
    FOREIGN KEY (package_id) REFERENCES gtfs_package (id) ON DELETE CASCADE;
