CREATE TYPE PARKING_CAPACITY AS
(
  "parking-facility" PARKING_FACILITY,
  "capacity" INTEGER
);

-- Recreate type, add missing parking provider attributes that are mentioned in the geojson example.
ALTER TYPE PARKING_PROVIDER_INFORMATION
 DROP ATTRIBUTE "parking-areas",
  ADD ATTRIBUTE "office-hours" service_hours[],
  ADD ATTRIBUTE "service-hours" service_hours[],
  ADD ATTRIBUTE "payment-methods" payment_method[],
  ADD ATTRIBUTE "information-service-accessibility" accessibility_info_facility[],
  ADD ATTRIBUTE accessibility accessibility_facility[],
  ADD ATTRIBUTE mobility mobility_facility[],
  ADD ATTRIBUTE "accessibility-description" localized_text[], -- Free text of accessibility
  ADD ATTRIBUTE "charging-points" localized_text[], -- Free text of possible charging points
  ADD ATTRIBUTE "additional-service-links" service_link[],-- Support multiple additional services instead of just one.
  ADD ATTRIBUTE "parking-capacities" parking_capacity[],
  ADD ATTRIBUTE "booking-service" service_link,
  ADD ATTRIBUTE "price-classes" price_class[],
  ADD ATTRIBUTE "real-time-information" service_link,
  ADD ATTRIBUTE "maximum-stay" INTERVAL,
  ADD ATTRIBUTE "office-hours-exceptions" service_exception[],
  ADD ATTRIBUTE "service-exceptions" service_exception[];

-- Drop now obsolete type.
DROP TYPE parking_area;
