-- Change some of the name attributes/columns of transit model to localized text --

DROP TABLE transit_route;

ALTER TYPE transit_stop
  ALTER ATTRIBUTE "name" TYPE localized_text[];

CREATE TABLE transit_route (
  id SERIAL PRIMARY KEY,
  "transport-operator-id" INTEGER REFERENCES "transport-operator" (id) NOT NULL,
  name localized_text[] NOT NULL,
  "route-type" transit_route_type NOT NULL,
  url VARCHAR(1024),
  color CHAR(6),
  "departure-point-name" localized_text[],
  "destination-point-name" localized_text[],
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


-- Convert finnish port names to localized_text[] type --
CREATE OR REPLACE FUNCTION text_to_localized_text(TEXT)
  RETURNS localized_text[] AS $$
SELECT ARRAY[ROW('FI', $1)::localized_text]::localized_text[];
$$ LANGUAGE SQL;

ALTER TABLE finnish_ports
  ALTER COLUMN name TYPE localized_text[] USING text_to_localized_text(name)