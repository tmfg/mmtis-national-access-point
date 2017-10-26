-- Add sub-type for transport-service

CREATE TYPE transport_service_subtype AS ENUM (
  'taxi', 'request', 'schedule'
);


ALTER TABLE "transport-service"
  ADD COLUMN "sub-type" transport_service_subtype;
