CREATE TYPE service_exception AS (
  "from-date" DATE,
  "to-date" DATE,
  description localized_text[]
);

COMMENT ON TYPE service_exception IS
E'Service hours controls when the service is normally open. Service exception marks days when the service is unavailable (like bank holidays or other service disruption).';

ALTER TYPE terminal_information
  ADD ATTRIBUTE "service-exceptions" service_exception[];

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "service-exceptions" service_exception[];

ALTER TYPE pick_up_location
  ADD ATTRIBUTE "service-exceptions" service_exception[];

ALTER TYPE parking_area
  ADD ATTRIBUTE "office-hours-exceptions" service_exception[],
  ADD ATTRIBUTE "service-exceptions" service_exception[];
