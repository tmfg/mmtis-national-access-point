
-- GTFS route (in gtfs-route table) is different that the route that we want to use when detecting differences.
-- GTFS route might have same name, but different trip headsign which means that we were using same route in two or occasions
-- depending on how many headsigns there were on that routes trips.
-- This table explicitly describes all routes. So they might not match the count of gtfs-route table.
CREATE TABLE "detection-route"
(
  id                  SERIAL PRIMARY KEY,
  "gtfs-route-id"     INTEGER NOT NULL REFERENCES "gtfs-route" (id),
  "package-id"        INTEGER NOT NULL REFERENCES gtfs_package (id),
  "route-id"          text,
  "route-short-name"  text,
  "route-long-name"   text,
  "trip-headsign"     text,
  "route-hash-id"     text
);

CREATE INDEX "package-route" ON public."detection-route" ("package-id", "route-id");

