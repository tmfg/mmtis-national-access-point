-- Drop search result view before changing referenced column
DROP VIEW transport_service_search_result;


-- Change external interfaces format type
ALTER TABLE "external-interface-description"
  ALTER "format" TYPE TEXT[] USING string_to_array(format, '');

ALTER TYPE external_interface_search_result
    ALTER ATTRIBUTE "format" TYPE TEXT[];

-- Recreate transport_service_search_result
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