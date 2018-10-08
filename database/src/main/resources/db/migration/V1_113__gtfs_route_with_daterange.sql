-- Create a type for route with minimum and maximum operating date
CREATE TYPE gtfs_route_with_daterange AS (
  "route-short-name" TEXT,
  "route-long-name" TEXT,
  "trip-headsign" TEXT,
  "min-date" DATE,
  "max-date" DATE
);
