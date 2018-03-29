-- Add service specific transport type

CREATE TYPE transport_type AS ENUM (
 'road', 'rail', 'sea', 'aviation'
 );

ALTER TABLE "transport-service"
  ADD COLUMN "transport-type" transport_type[];
