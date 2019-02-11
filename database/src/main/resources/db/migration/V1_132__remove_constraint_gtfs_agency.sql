-- Fixes gtfs import breaking when the package doesn't give a url.
ALTER TABLE "gtfs-agency" ALTER COLUMN "agency-url" DROP NOT NULL;