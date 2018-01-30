ALTER TYPE external_interface_search_result
 DROP ATTRIBUTE "license-url";

ALTER TYPE external_interface_search_result
  ADD ATTRIBUTE "data-content" interface_data_content[];

ALTER TABLE "external-interface-description"
  DROP COLUMN "license-url" CASCADE;


-- Recreate view for search results (transport_service_search_result is now deleted because of CASCADE)
-- Note, we added new data element "ei.data-content" that was created in previous migration.

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
                      ei.license, ei."data-content")::external_interface_search_result)
     FROM "external-interface-description" ei
     WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
  FROM "transport-service" t;
