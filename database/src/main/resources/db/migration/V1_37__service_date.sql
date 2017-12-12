ALTER TABLE "transport-service"
  ADD COLUMN "available-from" DATE,
  ADD COLUMN "available-to" DATE;

COMMENT ON COLUMN "transport-service"."available-from" IS
E'The earliest date when this service is available. NULL means it is already available at the time of entry.';

COMMENT ON COLUMN "transport-service"."available-to" IS
E'The last date on which this service is available. NULL means indefinitely (no known end date).';
