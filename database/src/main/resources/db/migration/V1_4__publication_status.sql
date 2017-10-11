-- Add "published?" boolean to transport-service

ALTER TABLE "transport-service"
  ADD "published?" BOOLEAN DEFAULT FALSE;
