-- Add external csv url for companies
ALTER TABLE "transport-service"
  ADD COLUMN "companies-csv-url" VARCHAR(1024);
