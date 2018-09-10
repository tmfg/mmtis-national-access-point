DROP FUNCTION refresh_nightly_transit_changes ();
DROP MATERIALIZED VIEW "nightly-transit-changes";

ALTER TABLE gtfs_package ADD "finnish-regions" CHARACTER(2)[];
ALTER TABLE gtfs_package ADD "envelope" geometry;

-- Add per route hash to gtfs-date-hash
CREATE TYPE "gtfs-route-hash" AS (
  "route-short-name" TEXT,
  "route-long-name" TEXT,
  "trip-headsign" TEXT,
  hash bytea
);

ALTER TABLE "gtfs-date-hash"
  ADD "route-hashes" "gtfs-route-hash"[];

CREATE TYPE "gtfs-route-change-type" AS ENUM ('no-change','added','removed','changed');

CREATE TYPE "gtfs-route-change-info" AS (
  "route-short-name" TEXT,
  "route-long-name" TEXT,
  "trip-headsign" TEXT,
  "change-type" "gtfs-route-change-type",
  "added-trips" INTEGER,
  "removed-trips" INTEGER,
  "trip-stop-sequence-changes" INT4RANGE, -- Per trip range (min-max) of changes in stop sequences
  "trip-stop-time-changes" INT4RANGE, -- Per trip range of changes in stop times
  "current-week-date" DATE,
  "different-week-date" DATE,
  "change-date" DATE
);

CREATE TYPE "gtfs-trip-change-info" AS (
 "trip-stop-sequence-changes" INTEGER,
 "trip-stop-time-changes" INTEGER
);

CREATE TYPE "gtfs-package-trip-info" AS (
 "package-id" INTEGER,
 trip "gtfs-trip-info"
);

CREATE TYPE route_trips_for_date AS (
 "route-short-name" TEXT,
 "route-long-name" TEXT,
 "trip-headsign" TEXT,
 trips INTEGER,
 tripdata "gtfs-package-trip-info"[]
);

CREATE TYPE service_ref AS (
  "package-id" INTEGER,
  "service-id" TEXT
);

CREATE TYPE gtfs_week_diff AS (
  "beginning-of-current-week" DATE,
  "current-weekhash" TEXT,
  "beginning-of-different-week" DATE,
  "different-weekhash" TEXT
);

CREATE TYPE gtfs_stoptime_display AS (
  "stop-sequence" INTEGER,
  "stop-name" TEXT,
  "arrival-time" INTERVAL,
  "departure-time" INTERVAL
);

COMMENT ON TYPE gtfs_stoptime_display IS
E'Type used for querying stoptimes to for display in the frontend';

CREATE TABLE "gtfs-transit-changes" (
  date DATE,
  "transport-service-id" INTEGER REFERENCES "transport-service" (id),
  "current-week-date" DATE, -- Date of the previous "normal" traffic
  "different-week-date" DATE,  -- Date of the differing traffic after the change
  "change-date" DATE, -- Date when the change occurs (beginning of the week that is different from previous traffic)
  "added-routes" INTEGER, -- How many new routes are in the different week
  "removed-routes" INTEGER, -- How many routes are missing from the different week
  "changed-routes" INTEGER, -- How many routes have changes (stop sequence or stop time)
  "route-changes" "gtfs-route-change-info"[],
  "package-ids" INTEGER[], -- GTFS package ids which were used for calculation
  PRIMARY KEY (date, "transport-service-id")
);

COMMENT ON TABLE "gtfs-transit-changes" IS
E'Store detected changes in transit traffic.';
