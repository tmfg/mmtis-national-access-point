-- Add "notice-external-interfaces?" boolean to transport-service

ALTER TABLE "transport-service"
  ADD "notice-external-interfaces?" BOOLEAN DEFAULT FALSE;