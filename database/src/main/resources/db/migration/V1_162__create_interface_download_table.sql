-- We need to store interface download state (success, failure) and errors to db.
-- This table is reserved for that.

-- Create state enum
CREATE TYPE interface_download_status AS ENUM ('success','failure');

-- And create the table
CREATE TABLE "external-interface-download-status" (
  id SERIAL PRIMARY KEY,
  "external-interface-description-id" INTEGER REFERENCES "external-interface-description" (id) NOT NULL,
  "download-error" text,
  "db-error" text,
  "download-status" interface_download_status,
  "package-id" INTEGER REFERENCES gtfs_package (id),
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Copy all existing download information (errors) to new table
INSERT INTO "external-interface-download-status" ("external-interface-description-id", "download-error", "db-error",
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

-- Need to drop dependant views before altering
DROP VIEW transport_service_search_result;

-- Create dropped views again
CREATE VIEW transport_service_search_result AS
SELECT t.*, op.name as "operator-name", op."business-id" as "business-id",
       -- Changed Start
       array_cat(
               array_cat(
                       (SELECT sc."companies"
                        FROM "service_company" sc
                        WHERE sc."transport-service-id" = t.id),
                       t."companies"),
               array_agg((aso.name, aso."business-id")::company)) AS "service-companies",
       -- Changed End
       (SELECT array_agg(oaf."operation-area")
        FROM "operation-area-facet" oaf
        WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
       (SELECT array_agg(ROW(ei."external-interface", ei.format,
           ei."ckan-resource-id",
           ei.license, ei."data-content",
           eids."download-error",
           eids."db-error")::external_interface_search_result)
        FROM "external-interface-description" ei, "external-interface-download-status" eids
        WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
FROM "transport-service" t
         LEFT JOIN (SELECT top.name as name, top."business-id" as "business-id", a."service-id"
                    FROM "transport-operator" top, "associated-service-operators" a WHERE a."operator-id" = top.id) as aso ON aso."service-id" = t.id
         JOIN  "transport-operator" op ON op.id = t."transport-operator-id"
GROUP BY t.id, op.name, op."business-id";

-- Drop unused columns
ALTER TABLE "external-interface-description"
  DROP COLUMN "gtfs-import-error",
  DROP COLUMN "gtfs-db-error";