
CREATE TABLE "gtfs-date-hash" (
  "package-id" INTEGER REFERENCES "gtfs_package" (id),
  date DATE,
  hash bytea,
  UNIQUE("package-id", date)
);

COMMENT ON TABLE "gtfs-date-hash" IS
E'Stores a hash of a packages traffic per date so that different days can be compared';

ALTER TYPE "gtfs-stop-time-info" ADD ATTRIBUTE "stop-id" TEXT;

-- Create indexes based on package id

CREATE INDEX "gtfs-agency-package-idx" ON "gtfs-agency" ("package-id");
CREATE INDEX "gtfs-calendar-package-idx" ON "gtfs-calendar" ("package-id");
CREATE INDEX "gtfs-calendar-date-package-idx" ON "gtfs-calendar-date" ("package-id");
CREATE INDEX "gtfs-date-hash-package-idx" ON "gtfs-date-hash" ("package-id");
CREATE INDEX "gtfs-route-package-idx" ON "gtfs-route" ("package-id");
CREATE INDEX "gtfs-shape-package-idx" ON "gtfs-shape" ("package-id");
CREATE INDEX "gtfs-stop-package-idx" ON "gtfs-stop" ("package-id");
CREATE INDEX "gtfs-transfer-package-idx" ON "gtfs-transfer" ("package-id");
CREATE INDEX "gtfs-trip-package-idx" ON "gtfs-trip" ("package-id");
