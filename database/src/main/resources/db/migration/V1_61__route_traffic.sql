-- Tables and types for planning scheduled route based traffic

CREATE TABLE locode (
  code VARCHAR,
  name VARCHAR,
  location GEOMETRY
);

COMMENT ON TABLE locode IS 'UN Transport locations.';

CREATE TYPE stop_type AS ENUM ('port','pier','bus','other');

CREATE VIEW "stop" AS
 SELECT CONCAT('locode:', code) AS id,
        'port'::stop_type AS type,
        name AS name,
        location AS location
  FROM locode;
