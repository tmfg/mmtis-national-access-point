DROP FUNCTION refresh_nightly_transit_changes ();
DROP MATERIALIZED VIEW "nightly-transit-changes";

CREATE TYPE "gtfs-route-change-info" AS (
  "route-short-name" TEXT,
  "route-long-name" TEXT,
  "trip-headsign" TEXT,
  "added-trips" INTEGER,
  "removed-trips" INTEGER,
  "trip-stop-sequence-changes" INTEGER,
  "trip-stop-time-changes" INTEGER
);

CREATE TABLE "gtfs-transit-changes" ( -- FIXME: ehkä parempi nimi, jos joku keksii?
  date DATE,
  "transport-service-id" INTEGER REFERENCES "transport-service" (id),
  "current-week-date" DATE, -- Date of the previous "normal" traffic
  "different-week-date" DATE,  -- Date of the differing traffic after the change
  "change-date" DATE, -- Date when the change occurs (beginning of the week that is different from previous traffic)
  "added-routes" INTEGER, -- How many new routes are in the different week
  "removed-routes" INTEGER, -- How many routes are missing from the different week
  "added-trips" INTEGER, -- How many new trips  are in the different week
  "removed-trips" INTEGER, -- How many trips are missing from the different week
  "trip-stop-sequence-changes" INTEGER, -- How many stop sequence changes are in the trips
  "trip-stop-time-changes" INTEGER, -- How many stop times have changed in the different week
  "route-changes" "gtfs-route-change-info"[],
  "package-ids" INTEGER[], -- GTFS package ids which were used for calculation
  PRIMARY KEY (date, "transport-service-id")
);

COMMENT ON TABLE "gtfs-transit-changes" IS
E'Store detected changes in transit traffic.';

CREATE FUNCTION gtfs_should_calculate_transit_change(service_id INTEGER)
RETURNS BOOLEAN
AS $$
SELECT NOT EXISTS(
  SELECT date
    FROM "gtfs-transit-changes" gtc
   WHERE "change-date" > CURRENT_DATE
     AND "transport-service-id" = service_id
     AND NOT EXISTS(SELECT id
                      FROM gtfs_package p
                     WHERE p."transport-service-id" = gtc."transport-service-id"
                       AND p.created > gtc.date));
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION gtfs_should_calculate_transit_change(INTEGER) IS
E'Check if transit changes should be calculated for the given transport-service-id.';
