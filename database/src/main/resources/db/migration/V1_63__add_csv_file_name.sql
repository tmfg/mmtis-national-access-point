-- Add company-csv-filename to transport-service table
ALTER TABLE "transport-service"
  ADD COLUMN "company-csv-filename" VARCHAR(1024);