-- Add more metadata to external interface

ALTER TABLE "external-interface-description"
  -- The name or description of the license and a possible link to it
  ADD COLUMN license TEXT,
  ADD COLUMN "license-url" VARCHAR(1024);

ALTER TYPE external_interface_search_result
  ADD ATTRIBUTE license TEXT,
  ADD ATTRIBUTE "license-url" VARCHAR(1024);

-- Recreate view for search results
DROP VIEW transport_service_search_result;

CREATE VIEW transport_service_search_result AS
SELECT t.*,
       (SELECT op.name
          FROM "transport-operator" op
         WHERE op.id = t."transport-operator-id") as "operator-name",
       (SELECT array_agg(oaf."operation-area")
          FROM "operation-area-facet" oaf
         WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
       (SELECT array_agg(ROW(ei."external-interface", ei.format,
                             ei."ckan-resource-id",
                             ei.license, ei."license-url")::external_interface_search_result)
          FROM "external-interface-description" ei
         WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
  FROM "transport-service" t;
