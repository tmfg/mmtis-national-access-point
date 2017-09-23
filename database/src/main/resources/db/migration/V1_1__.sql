-- Transport Provider olennaiset tiedot


CREATE TYPE address AS (
  street VARCHAR(128),
  postal_code CHAR(5),
  post_office VARCHAR(64)
);

CREATE TABLE transport_provider (
  id SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL, -- PRH doesn't tell maximum length
  business_id CHAR(9),
  homepage VARCHAR(1024),
  visiting_address address,
  mailing_address address,
  phone VARCHAR(16),
  gsm VARCHAR(16),
  email VARCHAR(200)
);

CREATE TYPE week_day AS ENUM ('MA','TI','KE','TO','PE','LA','SU');

CREATE TYPE opening_hours AS (
  week_days week_day[],
  opening_time TIME,
  closing_time TIME
);

CREATE TYPE transport_type AS ENUM (
  'terminal', -- Ports, stations and terminals
  'passenger_tranportation', -- passenger transportation services
  'renting', -- Renting and community use
  'parking', -- Parking services
  'exchange' -- Exchange services
);

CREATE TYPE accessibility_tool AS ENUM (
  'wheelchair', 'walkingstick', 'audioNavigator', 'visualNavigator', 'passengerCart', 'pushchair', 'umbrella', 'buggy',
  'other'
);

CREATE TYPE accessibility_info_facility AS ENUM (
  'audioForHearingImpaired', 'audioInformation', 'visualDisplays', 'displaysForVisuallyImpaired',
  'largePrintTimetables', 'other'
);

CREATE TYPE accessibility_facility AS ENUM (
  'unknown', 'lift', 'escalator', 'travelator', 'ramp', 'stairs', 'shuttle', 'narrowEntrance', 'barrier',
  'palletAccess_lowFloor', 'validator'
);

CREATE TYPE mobility_facility AS ENUM (
  'unknown', 'lowFloor', 'stepFreeAccess', 'suitableForWheelchairs', 'suitableForHeaviliyDisabled',
  'boardingAssistance', 'onboardAssistance', 'unaccompaniedMinorAssistance', 'tactilePatformEdges',
  'tactileGuidingStrips'
);

CREATE TYPE passenger_information_facility AS ENUM (
  'nextStopIndicator', 'stopAnnouncements', 'passengerInformationDisplay', 'realTimeConnections', 'other'
);

CREATE TYPE safety_facility AS ENUM (
  'ccTv', 'mobileCoverage', 'sosPoints', 'staffed'
);

CREATE TYPE parking_facility AS ENUM (
  'unknown','carPark','parkAndRidePark','motorcyclePark', 'cyclePark', 'rentalCarPark', 'coachPark'
);

CREATE TYPE payment_method AS ENUM (
  'cash', 'debit_card', 'credit_card', 'mobilepay', 'contactless_payment', 'invoice', 'other'
);

CREATE TYPE additional_rental_services AS ENUM (
  'child_seat','animal_transport'
  -- FIXME: This list is incomplete
);

CREATE TYPE pick_up_type AS ENUM ('pick_up','return','pick_up_return');

CREATE TYPE exchange_service_type AS ENUM (
  'car'
  -- FIXME: List types of exchange services
);


-- Service link is a http link to service info page and description what information could be found from the link
CREATE TYPE service_link AS (
  www VARCHAR(1024),
  description TEXT
);

CREATE TYPE terminal_provider_information AS (
  location geometry,
  opening_hours opening_hours[],
  indoor_map VARCHAR(1024), -- URL-address to image or page
  accessibility_info accessibility_info_facility[],
  accessibility accessibility_facility[],
  mobility mobility_facility[],
  accessibility_description TEXT, -- Free text of accessibility
  services service_link[]
);

CREATE TYPE operation_area AS (
  area_description TEXT, -- Free text about the operation area, e.g. commune
  location GEOMETRY -- possible more accurate geometry data
);

CREATE TYPE passenger_transportation_provider_info AS (
  -- FIXME: onko vapaatekstinä taulukko ok rajoitukset vai pitääkö mallintaa?
  luggage_restrictions TEXT[],
  real_time_information_url VARCHAR(1024), -- URL to real time information
  main_operation_area operation_area[],
  secondary_operation_area operation_area[],
  rental_service_url service_link -- link and description of online rental service

  -- TODO:
  -- passenger_transportation additional info:
  -- route, means of transport, parking services, schedule, delays, annulment, additional services, price information
);


CREATE TYPE pick_up_location AS (
  name VARCHAR(100),
  selected_pick_up_type pick_up_type,
  pick_up_times opening_hours[]
);


CREATE TYPE rental_provider_informaton AS (
  mobility_facilities mobility_facility[],
  eligibility_requirements TEXT, -- Free text of eligibility requirements
  booking_service service_link,
  additional_services additional_rental_services[],
  pick_up_locations pick_up_location[]
);



CREATE TYPE parking_area AS (
  area operation_area,
  office_hours opening_hours[],
  payment_methods payment_method[],
  accessibility_info accessibility_info_facility[],
  accessibility accessibility_facility[],
  mobility mobility_facility[],
  accessibility_description TEXT, -- Free text of accessibility
  charging_points TEXT, -- Free text of possible charging points
  additional_service_url service_link,
  parking_facilities parking_facility[]
);

CREATE TYPE parking_provider_information AS (
  parking_areas parking_area[]
);


CREATE TYPE exchange_service AS (
  name VARCHAR(100),
  description TEXT,
  exchange_service_type exchange_service_type,
  main_operation_area operation_area[],
  secondary_operation_area operation_area[],
  price_information service_link -- URL to price information
  -- FIXME: dynamic price information, vapaa capacity, discount information?
);

CREATE TYPE exchange_provider_informaton AS (
  services_for_exchange exchange_service[]
);

CREATE TABLE transport_service (
  id SERIAL PRIMARY KEY,
  transport_provider_id INTEGER REFERENCES transport_provider (id) NOT NULL,
  "type" transport_type NOT NULL,
  terminal terminal_provider_information,
  passenger_transportation passenger_transportation_provider_info,
  rental_provider rental_provider_informaton,
  parking_provider parking_provider_information, -- FIXME: currently nothing to store here
  exchange_provider exchange_provider_informaton -- FIXME: currently nothing to store here
);



-- Aikatauluja varten
