-- New table to store route change history
CREATE TABLE "detected-change-history" (
  id SERIAL PRIMARY KEY,
  "transport-service-id" INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE,
  "route-hash-id" TEXT,
  "change-type" "gtfs-route-change-type",
  "change-str" TEXT,
  "email-sent" TIMESTAMP,
  "change-detected" DATE,
  "different-week-date" DATE, -- Date when the change happens
  "package-ids" INTEGER[] -- GTFS package ids which were used for calculation
);

-- Update "detected-route-change" - Add change-str
ALTER TABLE "detected-route-change"
  ADD COLUMN "change-str" TEXT;

-- Add indexes
CREATE INDEX detected_change_history_index_id ON "detected-change-history" ("transport-service-id");
CREATE INDEX detected_change_history_index_change_str ON "detected-change-history" ("change-str");
CREATE INDEX detected_change_history_index_id_week ON "detected-change-history" ("different-week-date");
CREATE INDEX detected_route_change_index_id ON "detected-route-change" ("transit-service-id");
CREATE INDEX detected_route_change_index_change_str ON "detected-route-change" ("change-str");