-- Transport operator information

CREATE TYPE localized_text AS (
  lang CHAR(2),
  text TEXT
);

CREATE TYPE address AS (
  street VARCHAR(128),
  postal_code CHAR(5),
  post_office VARCHAR(64)
);

CREATE TABLE "transport-operator" (
  id SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL, -- PRH doesn't tell maximum length
  "business-id" CHAR(9),
  homepage VARCHAR(1024),
  "visiting-address" address,
  "billing-address" address,
  phone VARCHAR(16),
  gsm VARCHAR(16),
  email VARCHAR(200)
);

CREATE TYPE week_day AS ENUM ('MON','TUE','WED','THU','FRI','SAT','SUN');

CREATE TYPE service_hours AS (
  "week-days" week_day[],
  "from" TIME,
  "to" TIME,
  description localized_text[]
);

CREATE TYPE transport_provider_type AS ENUM (
  'terminal', -- Ports, stations and terminals
  'passenger-transportation', -- passenger transportation services
  'rentals', -- Renting and community use
  'parking', -- Parking services
  'brokerage' -- Brokerage services
);

CREATE TYPE accessibility_tool AS ENUM (
  'wheelchair', 'walkingstick', 'audio-navigator', 'visual-navigator', 'passenger-cart', 'pushchair', 'umbrella',
  'buggy', 'other'
);

CREATE TYPE accessibility_info_facility AS ENUM (
  'audio-for-hearing-impaired', 'audio-information', 'visual-displays', 'displays-for-visually-impaired',
  'large-print-timetables', 'other'
);

CREATE TYPE accessibility_facility AS ENUM (
  'unknown', 'lift', 'escalator', 'travelator', 'ramp', 'stairs', 'shuttle', 'narrow-entrance', 'barrier',
  'pallet-access-low-floor', 'validator', 'other'
);

CREATE TYPE mobility_facility AS ENUM (
  'unknown', 'low-floor', 'step-free-access', 'suitable-for-wheelchairs', 'suitable-for-heavily-disabled',
  'boarding-assistance', 'onboard-assistance', 'unaccompanied-minor-assistance', 'tactile-patform-edges',
  'tactile-guiding-strips', 'other'
);

CREATE TYPE passenger_information_facility AS ENUM (
  'next-stop-indicator', 'stop-announcements', 'passenger-information-display', 'real-time-connections', 'other'
);

CREATE TYPE safety_facility AS ENUM (
  'cc-tv', 'mobile-coverage', 'sos-points', 'staffed', 'other'
);

CREATE TYPE parking_facility AS ENUM (
  'unknown','car-park','park-and-ride-park','motorcycle-park', 'cycle-park', 'rental-car-park', 'coach-park'
);

CREATE TYPE payment_method AS ENUM (
  'cash', 'debit-card', 'credit-card', 'mobilepay', 'contactless-payment', 'invoice', 'other'
);

CREATE TYPE price_class AS (
   name VARCHAR(200),
   "price-per-unit" NUMERIC,
   unit VARCHAR(128), -- name of unit, like "km" or "mi"
   currency VARCHAR(3)
);

CREATE TYPE additional_services AS ENUM (
  'child-seat','animal-transport', 'other'
  -- FIXME: This list is incomplete
);

CREATE TYPE pick_up_type AS ENUM ('pick-up','return','pick-up-return');

CREATE TYPE brokerage_service_type AS ENUM (
  'car', 'bisycle', 'motorcycle', 'other'
);


-- Service link is a http link to service info page and description what information could be found from the link
CREATE TYPE service_link AS (
  url VARCHAR(1024),
  description localized_text[]
);

CREATE TYPE terminal_information AS (
  location geometry,
  "service-hours" service_hours[],
  "indoor-map" service_link, -- URL-address to image or page
  "information-service-accessibility" accessibility_info_facility[],
  "accessibility-tool" accessibility_tool[],
  accessibility accessibility_facility[],
  mobility mobility_facility[],
  "accessibility-description" localized_text[], -- Free text of accessibility
  services service_link[]
);

CREATE TYPE passenger_transportation_info AS (
  "luggage-restrictions" localized_text[],
  "real-time-information" service_link, -- URL to real time information
  "booking-service" service_link, -- link and description of online booking service
  "payment-methods" payment_method[],
  "price-classes" price_class[],
  "additional-services" additional_services[],
  "service-hours" service_hours[],
  "accessibility-tool" accessibility_tool[],
  "accessibility-description" localized_text[] -- Free text of accessibility
  -- TODO:
  -- passenger_transportation additional info:
  -- route, means of transport, parking services, schedule, delays, annulment, additional services, price information
);


CREATE TYPE pick_up_location AS (
  name VARCHAR(100),
  "pick-up-type" pick_up_type,
  "pick-up-times" service_hours[]
);


CREATE TYPE rental_provider_informaton AS (
  "mobility-facilities" mobility_facility[],
  "eligibility-requirements" TEXT, -- Free text of eligibility requirements
  "booking-service" service_link,
  "additional-services" additional_services[],
  "pick-up-locations" pick_up_location[]
);



CREATE TYPE parking_area AS (
  "office-hours" service_hours[],
  "service-hours" service_hours[],
  "payment-methods" payment_method[],
  "information-service-accessibility" accessibility_info_facility[],
  accessibility accessibility_facility[],
  mobility mobility_facility[],
  "accessibility-description" localized_text[], -- Free text of accessibility
  "charging-points" localized_text[], -- Free text of possible charging points
  "additional-service" service_link,
  "parking-facilities" parking_facility[]
);

CREATE TYPE parking_provider_information AS (
  "parking-areas" parking_area[]
);


CREATE TYPE brokerage_service AS (
  name VARCHAR(100),
  description localized_text[],
  "brokerage-service-type" brokerage_service_type,
  pricing service_link -- URL to price information
);

CREATE TYPE brokerage_provider_informaton AS (
  "brokerage-services" brokerage_service[]
);

CREATE TABLE "transport-service" (
  id SERIAL PRIMARY KEY,
  "transport-operator-id" INTEGER REFERENCES "transport-operator" (id) NOT NULL,
  "type" transport_provider_type NOT NULL,
  terminal terminal_information,
  "passenger-transportation" passenger_transportation_info,
  rental rental_provider_informaton,
  parking parking_provider_information, -- FIXME: currently nothing to store here
  brokerage brokerage_provider_informaton -- FIXME: currently nothing to store here
);

CREATE TABLE operation_area (
  id SERIAL PRIMARY KEY,
  "transport-service-id" INTEGER REFERENCES "transport-service" (id) NOT NULL,
  description localized_text[], -- Free text about the operation area, e.g. commune
  location GEOMETRY, -- possible more accurate geometry data
  "primary?" boolean
);