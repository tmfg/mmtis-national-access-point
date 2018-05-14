CREATE EXTENSION pgcrypto;

CREATE TABLE "gtfs-date-hash" (
  "package-id" INTEGER REFERENCES "gtfs_package" (id),
  date DATE,
  hash bytea,
  UNIQUE("package-id", date)
);

COMMENT ON TABLE "gtfs-date-hash" IS
E'Stores a hash of a packages traffic per date so that different days can be compared';

ALTER TYPE "gtfs-stop-time-info" ADD ATTRIBUTE "stop-id" TEXT;
