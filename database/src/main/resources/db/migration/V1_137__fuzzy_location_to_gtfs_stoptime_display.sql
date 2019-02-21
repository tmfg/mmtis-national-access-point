-- fuzzy-location were not added when new packages were added. So we fix it with this update
UPDATE "gtfs-stop"
   SET "stop-fuzzy-lat" = round("stop-lat", 3), "stop-fuzzy-lon" = round("stop-lon", 3)
 WHERE "gtfs-stop"."stop-fuzzy-lat" IS NULL;

-- Add stop-fuzzy-lat and stop-fuzzy-p-lon to gtfs_stoptime_display type to enable stop postition comparison
ALTER TYPE gtfs_stoptime_display
  ADD ATTRIBUTE "stop-fuzzy-lat" numeric,
  ADD ATTRIBUTE "stop-fuzzy-lon" numeric;