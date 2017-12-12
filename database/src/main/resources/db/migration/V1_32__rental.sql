-- New type for rental additional services
CREATE TYPE rental_additional_service AS (
  "additional-service-type" additional_services,
  "additional-service-price" price_class
);

ALTER TYPE rental_provider_information
 DROP ATTRIBUTE "additional-services";

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "rental-additional-services" rental_additional_service[];

-- New type for vehicle 
CREATE TYPE rental_vehicle AS (
  "vehicle-type" VARCHAR(128),
  "license-required" VARCHAR(20),
  "minimum-age" INTEGER,
  "vehicle-prices" price_class[]
);

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "rental-vehicle" rental_vehicle[];

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "usage-area" VARCHAR(500);
