-- link each report entry to either external interface id or package id and enforce it to make sure there is some
-- backtracking available
ALTER TABLE "gtfs_import_report"
    ADD COLUMN external_interface_id INTEGER REFERENCES napote.public."external-interface-description" ("id"),
    ALTER package_id DROP NOT NULL,
    ADD CONSTRAINT require_either_linking_ids
        CHECK ( package_id IS NOT NULL OR external_interface_id IS NOT NULL );