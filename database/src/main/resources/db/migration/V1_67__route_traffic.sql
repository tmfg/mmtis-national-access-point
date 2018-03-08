-- Tables and types for scheduled route traffic

CREATE TYPE transit_agency AS (
  name TEXT,
  lang CHAR(2), -- ISO 639-1 two letter language code
  phone TEXT,
  email TEXT,
  timezone TEXT -- e.g. Europe/Helsinki
);

CREATE TYPE transit_stop_type AS ENUM (
  'stop',
  'station',
  'station-entrance'
);

CREATE TYPE transit_route_type AS ENUM (
  'light-rail',
  'subway',
  'rail',
  'bus',
  'ferry',
  'cable-car',
  'gondola',
  'funicular'
);

CREATE TYPE transit_stop AS (
  "stop-id" INTEGER,
  code TEXT,
  name TEXT,
  location GEOMETRY,
  "stop-type" transit_stop_type
);

CREATE TYPE transit_service_rule AS (
  "from-date" DATE,
  "to-date" DATE,
  monday BOOLEAN,
  tuesday BOOLEAN,
  wednesday BOOLEAN,
  thursday BOOLEAN,
  friday BOOLEAN,
  saturday BOOLEAN,
  sunday BOOLEAN
);

CREATE TYPE transit_service_calendar AS (
  "service-rules" transit_service_rule[],
  "service-added-dates" DATE[],
  "service-removed-dates" DATE[]
);

CREATE TYPE transit_stopping_type AS ENUM (
  'regular',
  'not-available',
  'phone-agency',
  'coordinate-with-driver'
);

COMMENT ON TYPE transit_stopping_type IS
E'Enumerates possible types of stopping (same for pickup and drop off)';

CREATE TYPE transit_stop_time AS (
  "stop-idx" INTEGER,
  "arrival-time" TIME,
  "departure-time" TIME,
  "pickup-type" transit_stopping_type,
  "drop-off-type" transit_stopping_type
);

CREATE TYPE transit_trip AS (
  "service-calendar-idx" INTEGER, -- indexes route service-calendars array
  "stop-times" transit_stop_time[]
);

CREATE TABLE transit_route (
  id SERIAL PRIMARY KEY,
  "transport-operator-id" INTEGER REFERENCES "transport-operator" (id) NOT NULL,
  name TEXT NOT NULL,
  "route-type" transit_route_type NOT NULL,
  url VARCHAR(1024),
  color CHAR(6),
  "departure-point-name" TEXT,
  "destination-point-name" TEXT,
  stops transit_stop[],
  trips transit_trip[],
  "service-calendars" transit_service_calendar[],
  "available-from" DATE,
  "available-to" DATE,
  "published?" BOOLEAN,
  created timestamp with time zone DEFAULT NOW(),
  modified timestamp with time zone,
  "created-by" TEXT REFERENCES "user" (id),
  "modified-by" TEXT REFERENCES "user" (id)
);
