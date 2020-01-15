-- Create table to store s3 locations for transport-service company csv files
CREATE TABLE "transport_service_company_csv"
(
    id                     SERIAL PRIMARY KEY,
    "transport-service-id" INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE,
    "csv-file-name"        TEXT,
    "validation-warning"   TEXT,
    created                timestamp with time zone DEFAULT NOW(),
    "created-by"           TEXT REFERENCES "user" (id)
);