
CREATE TYPE company_source AS ENUM ('SERVICE','FILE','URL');

CREATE TABLE service_company (
  id SERIAL PRIMARY KEY,
  companies company[],
  "transport-service-id" INTEGER UNIQUE REFERENCES "transport-service" (id) NOT NULL,
  source company_source NOT NULL,
  created timestamp with time zone,
  modified timestamp with time zone
);