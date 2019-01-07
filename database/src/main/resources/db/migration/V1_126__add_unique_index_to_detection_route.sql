-- Create temp table for detection routes
CREATE TABLE "detection-route-temp"
(
  id                  SERIAL PRIMARY KEY,
  "gtfs-route-id"     INTEGER NOT NULL REFERENCES "gtfs-route" (id),
  "package-id"        INTEGER NOT NULL REFERENCES gtfs_package (id),
  "route-id"          text,
  "route-short-name"  text,
  "route-long-name"   text,
  "trip-headsign"     text,
  "route-hash-id"     text
);

-- Create index for temp table
CREATE INDEX "package-route-temp" ON "detection-route-temp" ("package-id", "route-id");

-- Move all rows from detection-route to new temp table that aren't removed from "gtfs-route" table
INSERT INTO "detection-route-temp" (SELECT r.* from "detection-route" r, "gtfs-route" gr WHERE r."gtfs-route-id" = gr.id );

-- DELETE all rows from detection-route so we can add new unique index to it
DELETE FROM "detection-route";

-- Create new unique index to "detection-route" table using columns package-id and route-hash-id
CREATE UNIQUE INDEX "detection-route_package-id_route-hash-id_uindex" ON "detection-route" ("package-id", "route-hash-id");

-- Move all rows from detection-route-temp to old table and skip all problematic rows
INSERT INTO "detection-route" SELECT r.* from "detection-route-temp" r ON CONFLICT DO NOTHING;

-- Drop temp taple
DROP TABLE "detection-route-temp";

-- Add missing index to gtfs-stop table to speed up queries
CREATE INDEX "gtfs-stop_package-id_stop-id_index" ON "gtfs-stop" ("package-id", "stop-id");
