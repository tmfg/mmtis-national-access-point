CREATE TYPE "company_sources" AS ENUM ('csv-file','csv-url','form','none' );

ALTER TABLE "transport-service"
  ADD COLUMN "company-source" company_sources;

-- Update "company-source" value for services that have companies-csv-url set.
UPDATE "transport-service" ts SET "company-source" = 'csv-url' WHERE ts."companies-csv-url" IS NOT NULL;
