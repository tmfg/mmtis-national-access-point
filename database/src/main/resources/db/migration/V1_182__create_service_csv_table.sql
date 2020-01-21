-- Create table to store s3 locations temporarily for transport-service company csv files
CREATE TABLE "transport_service_company_csv_temp"
(
    id                          SERIAL PRIMARY KEY,
    "transport-service-id"      INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE,
    "file-key"                  TEXT,
    "csv-file-name"             TEXT,
    "validation-warning"        TEXT,
    "failed-companies-count"    INTEGER,
    "valid-companies-count"     INTEGER,
    created                     timestamp with time zone DEFAULT NOW(),
    "created-by"                TEXT REFERENCES "user" (id)
);
-- Final table to store company-csv files
CREATE TABLE "transport_service_company_csv"
(
    id                          SERIAL PRIMARY KEY,
    "transport-service-id"      INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE,
    "file-key"                  TEXT,
    "csv-file-name"             TEXT,
    "validation-warning"        TEXT,
    "failed-companies-count"    INTEGER,
    "valid-companies-count"     INTEGER,
    created                     timestamp with time zone DEFAULT NOW(),
    "created-by"                TEXT REFERENCES "user" (id)
);

-- Add indexes or indices if you are from England
create unique index "transport_service_company_csv_temp_file-key_uindex"
    on transport_service_company_csv_temp ("file-key");

create unique index "transport_service_company_csv_file-key_uindex"
    on transport_service_company_csv ("file-key");