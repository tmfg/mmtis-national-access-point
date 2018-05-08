ALTER TABLE "gtfs-stop"
  ADD COLUMN "wheelchair-boarding" INTEGER CHECK ("wheelchair-boarding" BETWEEN 0 AND 2);

DROP TABLE "gtfs-trip";

CREATE TYPE "gtfs-stop-time-info" AS (
  "trip-id"           TEXT,
  "arrival-time"      INTERVAL,
  "departure-time"    INTERVAL,
  "stop-sequence"     INTEGER,
  "stop-headsign"     TEXT,
  "pickup-type"       INTEGER,
  "drop-off-type"     INTEGER,
  "shape-dist-traveled" NUMERIC
);

CREATE TYPE "gtfs-trip-info" AS (
  "trip-id"               TEXT,
  "trip-headsign"         TEXT,
  "direction-id"          INTEGER,
  "block-id"              TEXT,
  "shape-id"              TEXT,
  "wheelchair-accessible" INTEGER,
  "bikes-allowed"         INTEGER,
  "stop-times"            "gtfs-stop-time-info"[]
);


-- Group all trips into an array (of gtfs-trip-info) per package,route and service id
CREATE TABLE "gtfs-trip" (
  id           SERIAL PRIMARY KEY,
  "package-id" INTEGER NOT NULL REFERENCES gtfs_package (id),
  "route-id"   TEXT NOT NULL,
  "service-id" TEXT NOT NULL,
  trips        "gtfs-trip-info"[],
  UNIQUE ("package-id", "route-id", "service-id")
);

-- Drop the old stop time table
DROP TABLE "gtfs-stop-time";
