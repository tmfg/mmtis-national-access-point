-- Add fussy location for stops to reduce change findings
ALTER TABLE "gtfs-stop"
  ADD COLUMN "stop-fussy-lat" numeric,
  ADD COLUMN "stop-fussy-lon" numeric;

UPDATE "gtfs-stop" set "stop-fussy-lat" = round("stop-lat", 3), "stop-fussy-lon" = round("stop-lon", 3);
