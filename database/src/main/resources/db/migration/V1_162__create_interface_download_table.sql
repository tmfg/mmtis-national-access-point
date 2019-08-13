-- We need to store interface download state (success, failure) and errors to db.
-- This table is reserved for that.

-- Create state enum
CREATE TYPE interface_download_status AS ENUM ('success','failure');

-- And create the table
CREATE TABLE "interface-download" (
  id SERIAL PRIMARY KEY,
  "external-interface-description-id" INTEGER REFERENCES "external-interface-description" (id) NOT NULL,
  "download-error" text,
  "db-error" text,
  "download-status" interface_download_status,
  "package-id" INTEGER REFERENCES gtfs_package (id),
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Move all existing download information (errors) to new table
INSERT INTO "interface-download" ("external-interface-description-id", "download-error", "db-error",
            "download-status", "package-id", created)
SELECT eid.id, eid."gtfs-import-error", eid."gtfs-db-error",
  CASE
       WHEN eid."gtfs-import-error" != '' THEN 'failure'::interface_download_status
       WHEN eid."gtfs-db-error" != '' THEN 'failure'::interface_download_status
       ELSE 'success'::interface_download_status
   END,
       p.id as "package-id", p."created"
  FROM "external-interface-description" eid
       JOIN "gtfs_package" p ON p."external-interface-description-id" = eid.id;
