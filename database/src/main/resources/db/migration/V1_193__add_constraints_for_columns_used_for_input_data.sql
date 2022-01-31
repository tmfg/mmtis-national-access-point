ALTER TABLE "transport_service_company_csv"
    DROP CONSTRAINT IF EXISTS "csv-file-name-max-length",
    ADD CONSTRAINT "csv-file-name-max-length" CHECK (CHAR_LENGTH("csv-file-name") <= 100);

ALTER TABLE "transport_service_company_csv"
    DROP CONSTRAINT IF EXISTS "validation-warning-max-length",
    ADD CONSTRAINT "validation-warning-max-length" CHECK (CHAR_LENGTH("validation-warning") <= 100);

ALTER TABLE "transport_service_company_csv"
    DROP CONSTRAINT IF EXISTS "created-by-max-length",
    ADD CONSTRAINT "created-by-max-length" CHECK (CHAR_LENGTH("created-by") <= 100);

ALTER TABLE "transport_service_company_csv_temp"
    DROP CONSTRAINT IF EXISTS "csv-file-name-max-length",
    ADD CONSTRAINT "csv-file-name-max-length" CHECK (CHAR_LENGTH("csv-file-name") <= 100);

ALTER TABLE "transport_service_company_csv_temp"
    DROP CONSTRAINT IF EXISTS "validation-warning-max-length",
    ADD CONSTRAINT "validation-warning-max-length" CHECK (CHAR_LENGTH("validation-warning") <= 100);

ALTER TABLE "transport_service_company_csv_temp"
    DROP CONSTRAINT IF EXISTS "created-by-max-length",
    ADD CONSTRAINT "created-by-max-length" CHECK (CHAR_LENGTH("created-by") <= 100);

ALTER TABLE "netex-conversion"
    DROP CONSTRAINT IF EXISTS "filename-max-length",
    ADD CONSTRAINT "filename-max-length" CHECK (CHAR_LENGTH("filename") <= 100);

ALTER TABLE "netex-conversion"
    DROP CONSTRAINT IF EXISTS "input-file-error-max-length",
    ADD CONSTRAINT "input-file-error-max-length" CHECK (CHAR_LENGTH("input-file-error") <= 50000);

ALTER TABLE "netex-conversion"
    DROP CONSTRAINT IF EXISTS "validation-file-error-max-length",
    ADD CONSTRAINT "validation-file-error-max-length" CHECK (CHAR_LENGTH("validation-file-error") <= 100000);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-id-max-length",
    ADD CONSTRAINT "agency-id-max-length" CHECK (CHAR_LENGTH("agency-id") <= 100);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-name-max-length",
    ADD CONSTRAINT "agency-name-max-length" CHECK (CHAR_LENGTH("agency-name") <= 500);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-url-max-length",
    ADD CONSTRAINT "agency-url-max-length" CHECK (CHAR_LENGTH("agency-url") <= 500);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-fare-url-max-length",
    ADD CONSTRAINT "agency-fare-url-max-length" CHECK (CHAR_LENGTH("agency-fare-url") <= 500);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-timezone-max-length",
    ADD CONSTRAINT "agency-timezone-max-length" CHECK (CHAR_LENGTH("agency-timezone") <= 100);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-lang-max-length",
    ADD CONSTRAINT "agency-lang-max-length" CHECK (CHAR_LENGTH("agency-lang") <= 100);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-phone-max-length",
    ADD CONSTRAINT "agency-phone-max-length" CHECK (CHAR_LENGTH("agency-phone") <= 100);

ALTER TABLE "gtfs-agency"
    DROP CONSTRAINT IF EXISTS "agency-email-max-length",
    ADD CONSTRAINT "agency-email-max-length" CHECK (CHAR_LENGTH("agency-email") <= 100);

ALTER TABLE "gtfs-calendar"
    DROP CONSTRAINT IF EXISTS "service-id-max-length",
    ADD CONSTRAINT "service-id-max-length" CHECK (CHAR_LENGTH("service-id") <= 200);

ALTER TABLE "gtfs-calendar-date"
    DROP CONSTRAINT IF EXISTS "service-id-max-length",
    ADD CONSTRAINT "service-id-max-length" CHECK (CHAR_LENGTH("service-id") <= 200);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "route-id-max-length",
    ADD CONSTRAINT "route-id-max-length" CHECK (CHAR_LENGTH("route-id") <= 100);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "agency-id-max-length",
    ADD CONSTRAINT "agency-id-max-length" CHECK (CHAR_LENGTH("agency-id") <= 100);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "route-short-name-max-length",
    ADD CONSTRAINT "route-short-name-max-length" CHECK (CHAR_LENGTH("route-short-name") <= 200);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "route-long-name-max-length",
    ADD CONSTRAINT "route-long-name-max-length" CHECK (CHAR_LENGTH("route-long-name") <= 500);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "route-desc-max-length",
    ADD CONSTRAINT "route-desc-max-length" CHECK (CHAR_LENGTH("route-desc") <= 200);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "route-color-max-length",
    ADD CONSTRAINT "route-color-max-length" CHECK (CHAR_LENGTH("route-color") <= 100);

ALTER TABLE "gtfs-route"
    DROP CONSTRAINT IF EXISTS "route-text-color-max-length",
    ADD CONSTRAINT "route-text-color-max-length" CHECK (CHAR_LENGTH("route-text-color") <= 100);

ALTER TABLE "gtfs-shape"
    DROP CONSTRAINT IF EXISTS "shape-id-max-length",
    ADD CONSTRAINT "shape-id-max-length" CHECK (CHAR_LENGTH("shape-id") <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-id-max-length",
    ADD CONSTRAINT "stop-id-max-length" CHECK (CHAR_LENGTH("stop-id") <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-code-max-length",
    ADD CONSTRAINT "stop-code-max-length" CHECK (CHAR_LENGTH("stop-code") <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-name-max-length",
    ADD CONSTRAINT "stop-name-max-length" CHECK (CHAR_LENGTH("stop-name") <= 200);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-desc-max-length",
    ADD CONSTRAINT "stop-desc-max-length" CHECK (CHAR_LENGTH("stop-desc") <= 500);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "zone-id-max-length",
    ADD CONSTRAINT "zone-id-max-length" CHECK (CHAR_LENGTH("zone-id") <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-url-max-length",
    ADD CONSTRAINT "stop-url-max-length" CHECK (CHAR_LENGTH("stop-url") <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "parent-station-max-length",
    ADD CONSTRAINT "parent-station-max-length" CHECK (CHAR_LENGTH("parent-station") <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-timezone-max-length",
    ADD CONSTRAINT "stop-timezone-max-length" CHECK (CHAR_LENGTH("stop-timezone") <= 100);

ALTER TABLE "gtfs-trip"
    DROP CONSTRAINT IF EXISTS "route-id-max-length",
    ADD CONSTRAINT "route-id-max-length" CHECK (CHAR_LENGTH("route-id") <= 100);

ALTER TABLE "gtfs-trip"
    DROP CONSTRAINT IF EXISTS "service-id-max-length",
    ADD CONSTRAINT "service-id-max-length" CHECK (CHAR_LENGTH("service-id") <= 200);

ALTER TABLE "gtfs_package"
    DROP CONSTRAINT IF EXISTS "etag-max-length",
    ADD CONSTRAINT "etag-max-length" CHECK (CHAR_LENGTH("etag") <= 100);

ALTER TABLE "gtfs_package"
    DROP CONSTRAINT IF EXISTS "license-max-length",
    ADD CONSTRAINT "license-max-length" CHECK (CHAR_LENGTH("license") <= 100);

ALTER TABLE "pre_notice"
    DROP CONSTRAINT IF EXISTS "created-by-max-length",
    ADD CONSTRAINT "created-by-max-length" CHECK (CHAR_LENGTH("created-by") <= 100);

ALTER TABLE "pre_notice"
    DROP CONSTRAINT IF EXISTS "modified-by-max-length",
    ADD CONSTRAINT "modified-by-max-length" CHECK (CHAR_LENGTH("modified-by") <= 100);

ALTER TABLE "pre_notice"
    DROP CONSTRAINT IF EXISTS "other-type-description-max-length",
    ADD CONSTRAINT "other-type-description-max-length" CHECK (CHAR_LENGTH("other-type-description") <= 500);

ALTER TABLE "pre_notice"
    DROP CONSTRAINT IF EXISTS "description-max-length",
    ADD CONSTRAINT "description-max-length" CHECK (CHAR_LENGTH("description") <= 2000);

ALTER TABLE "pre_notice_attachment"
    DROP CONSTRAINT IF EXISTS "attachment-file-name-max-length",
    ADD CONSTRAINT "attachment-file-name-max-length" CHECK (CHAR_LENGTH("attachment-file-name") <= 200);

ALTER TABLE "pre_notice_attachment"
    DROP CONSTRAINT IF EXISTS "created-by-max-length",
    ADD CONSTRAINT "created-by-max-length" CHECK (CHAR_LENGTH("created-by") <= 100);

ALTER TABLE "netex-conversion"
    DROP CONSTRAINT IF EXISTS "data-content-max-length",
    ADD CONSTRAINT "data-content-max-length" CHECK (ARRAY_LENGTH("data-content", 1) <= 100);

ALTER TABLE "gtfs-date-hash"
    DROP CONSTRAINT IF EXISTS "route-hashes-max-length",
    ADD CONSTRAINT "route-hashes-max-length" CHECK (ARRAY_LENGTH("route-hashes", 1) <= 2000);

ALTER TABLE "gtfs-shape"
    DROP CONSTRAINT IF EXISTS "route-shape-max-length",
    ADD CONSTRAINT "route-shape-max-length" CHECK (ARRAY_LENGTH("route-shape", 1) <= 50000);

ALTER TABLE "gtfs-transit-changes"
    DROP CONSTRAINT IF EXISTS "route-changes-old-max-length",
    ADD CONSTRAINT "route-changes-old-max-length" CHECK (ARRAY_LENGTH("route-changes-old", 1) <= 5000);

ALTER TABLE "gtfs-transit-changes"
    DROP CONSTRAINT IF EXISTS "package-ids-max-length",
    ADD CONSTRAINT "package-ids-max-length" CHECK (ARRAY_LENGTH("package-ids", 1) <= 100);

ALTER TABLE "gtfs-trip"
    DROP CONSTRAINT IF EXISTS "trips-max-length",
    ADD CONSTRAINT "trips-max-length" CHECK (ARRAY_LENGTH("trips", 1) <= 1000);

ALTER TABLE "gtfs_package"
    DROP CONSTRAINT IF EXISTS "finnish-regions-max-length",
    ADD CONSTRAINT "finnish-regions-max-length" CHECK (ARRAY_LENGTH("finnish-regions", 1) <= 100);

ALTER TABLE "pre_notice"
    DROP CONSTRAINT IF EXISTS "effective-dates-max-length",
    ADD CONSTRAINT "effective-dates-max-length" CHECK (ARRAY_LENGTH("effective-dates", 1) <= 100);

ALTER TABLE "pre_notice"
    DROP CONSTRAINT IF EXISTS "pre-notice-type-max-length",
    ADD CONSTRAINT "pre-notice-type-max-length" CHECK (ARRAY_LENGTH("pre-notice-type", 1) <= 100);

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-lat-value-range",
    ADD CONSTRAINT "stop-lat-value-range" CHECK (("stop-lat" >= -90) AND ("stop-lat" <= 90));

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-lon-value-range",
    ADD CONSTRAINT "stop-lon-value-range" CHECK (("stop-lon" >= -180) AND ("stop-lon" <= 180));

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-fuzzy-lat-value-range",
    ADD CONSTRAINT "stop-fuzzy-lat-value-range" CHECK (("stop-fuzzy-lat" >= -90) AND ("stop-fuzzy-lat" <= 90));

ALTER TABLE "gtfs-stop"
    DROP CONSTRAINT IF EXISTS "stop-fuzzy-lon-value-range",
    ADD CONSTRAINT "stop-fuzzy-lon-value-range" CHECK (("stop-fuzzy-lon" >= -180) AND ("stop-fuzzy-lon" <= 180));

ALTER TABLE "gtfs-date-hash"
    DROP CONSTRAINT IF EXISTS "hash-max-length",
    ADD CONSTRAINT "hash-max-length" CHECK (LENGTH("hash") <= 100);
