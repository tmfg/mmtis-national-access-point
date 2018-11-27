-- Add created column to transit changes
ALTER TABLE "gtfs-transit-changes"
  ADD COLUMN created TIMESTAMP WITH TIME ZONE;

-- Add created and modified columns to date-hash table
ALTER TABLE "gtfs-date-hash"
  ADD COLUMN created TIMESTAMP WITH TIME ZONE,
  ADD COLUMN modified TIMESTAMP WITH TIME ZONE;

-- ADD route-hash-id column to custom type route-hash
ALTER TYPE "gtfs-route-hash"
  ADD ATTRIBUTE "route-hash-id" TEXT;

-- Add route-hash-id to custom composite type route with daterage
ALTER TYPE gtfs_route_with_daterange
  ADD ATTRIBUTE "route-hash-id" TEXT;

-- ADD route-hash-id column to custom type route-change-info
ALTER TYPE "gtfs-route-change-info"
  ADD ATTRIBUTE "route-hash-id" TEXT;

-- ADD route-hash-id column to custom type route-trips-for-date
ALTER TYPE route_trips_for_date
  ADD ATTRIBUTE "route-hash-id" TEXT;
