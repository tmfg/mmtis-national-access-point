-- Transit changes are updated every night to match current date situation.
-- We need to store those nightly changes to different columns than original detection to store original changes somewhere
ALTER TABLE "gtfs-transit-changes"
    ADD COLUMN "current-added-routes" INTEGER, -- How many new routes are in the different week
    ADD COLUMN "current-removed-routes" INTEGER, -- How many routes are missing from the different week
    ADD COLUMN "current-changed-routes" INTEGER, -- How many routes have changes (stop sequence or stop time)
    ADD COLUMN "current-no-traffic-routes" INTEGER; -- How many routes have no-traffic segments

-- update current values
UPDATE "gtfs-transit-changes" SET "current-added-routes" = "added-routes";
UPDATE "gtfs-transit-changes" SET "current-removed-routes" = "removed-routes";
UPDATE "gtfs-transit-changes" SET "current-changed-routes" = "changed-routes";
UPDATE "gtfs-transit-changes" SET "current-no-traffic-routes" = "no-traffic-routes";
