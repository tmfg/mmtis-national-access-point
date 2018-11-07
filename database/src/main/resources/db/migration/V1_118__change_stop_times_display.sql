-- Add stop-lat and stop-lon to gtfs_stoptime_display type to enable stop postition comparison
ALTER TYPE gtfs_stoptime_display
  ADD ATTRIBUTE "stop-lat" numeric,
  ADD ATTRIBUTE "stop-lon" numeric;
