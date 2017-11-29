ALTER TABLE "transport-service"
  ADD COLUMN "brokerage?" BOOLEAN;

UPDATE "transport-service" SET "brokerage?" = FALSE;

ALTER TABLE "transport-service"
ALTER COLUMN "brokerage?" SET DEFAULT FALSE,
ALTER COLUMN "brokerage?" SET NOT NULL;
