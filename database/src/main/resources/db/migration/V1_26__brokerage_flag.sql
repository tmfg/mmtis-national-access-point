ALTER TABLE "transport-service"
  ADD COLUMN "brokerage?" BOOLEAN;

UPDATE "transport-service" SET "brokerage?" = FALSE;
