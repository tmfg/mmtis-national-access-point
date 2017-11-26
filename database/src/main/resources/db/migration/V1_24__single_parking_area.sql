DROP TYPE parking_provider_information CASCADE;


CREATE TYPE PARKING_CAPACITY AS
(
  "parking-facility" PARKING_FACILITY,
  "capacity" INTEGER
);


ALTER TYPE parking_area
  RENAME TO parking_provider_information;

-- Add missing parking provider attributes.
ALTER TYPE parking_provider_information
  ADD ATTRIBUTE "parking-capacities" PARKING_CAPACITY [],
  ADD ATTRIBUTE "booking-service" SERVICE_LINK,
  ADD ATTRIBUTE "price-classes" PRICE_CLASS [],
  ADD ATTRIBUTE "real-time-information" SERVICE_LINK,
  ADD ATTRIBUTE "maximum-stay" INTERVAL;

-- Support multiple additional services instead of just one.
ALTER TYPE parking_provider_information
  DROP ATTRIBUTE "additional-service",
  ADD ATTRIBUTE "additional-services" SERVICE_LINK [];

ALTER TABLE "transport-service"
  ADD COLUMN parking parking_provider_information;
