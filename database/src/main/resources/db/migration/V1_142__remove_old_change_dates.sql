-- Remove gtfs-transit-changes.change-date dates from database to prevent unwanted change-detections
-- In local and test environments there might be correct change-date's so clean up only those that are within the next week (7 days)
UPDATE "gtfs-transit-changes" SET "change-date" = null
 WHERE "change-date" >= CURRENT_DATE
   AND "change-date" <= (CURRENT_DATE + '7 day'::interval) ;