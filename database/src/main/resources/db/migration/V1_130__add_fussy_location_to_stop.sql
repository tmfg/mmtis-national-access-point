-- Add fuzzy location for stops to reduce change findings
ALTER TABLE "gtfs-stop"
  ADD COLUMN "stop-fuzzy-lat" numeric,
  ADD COLUMN "stop-fuzzy-lon" numeric;

UPDATE "gtfs-stop" set "stop-fuzzy-lat" = round("stop-lat", 3), "stop-fuzzy-lon" = round("stop-lon", 3);
