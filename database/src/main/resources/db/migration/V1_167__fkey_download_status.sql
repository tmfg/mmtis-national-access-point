ALTER TABLE "external-interface-download-status"
    DROP CONSTRAINT "external-interface-download-status_package-id_fkey";

ALTER TABLE "external-interface-download-status"
    ADD CONSTRAINT "external-interface-download-status_package-id_fkey"
        FOREIGN KEY ("package-id") REFERENCES "gtfs_package"
            ON DELETE CASCADE;
