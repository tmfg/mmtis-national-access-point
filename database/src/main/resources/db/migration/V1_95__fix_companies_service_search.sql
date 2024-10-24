-- Fix "cannot accumulate empty arrays" error in view

-- Drop old
DROP VIEW transport_service_search_result;

-- Create new with business-id
CREATE VIEW transport_service_search_result AS
  SELECT t.*, op.name as "operator-name", op."business-id" as "business-id",
    (SELECT array_agg(sc."companies")
     FROM "service_company" sc
     WHERE sc."transport-service-id" = t.id
       AND array_length(sc.companies,1) > 0) AS "service-companies",
    (SELECT array_agg(oaf."operation-area")
     FROM "operation-area-facet" oaf
     WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
    (SELECT array_agg(ROW(ei."external-interface", ei.format,
                      ei."ckan-resource-id",
                      ei.license, ei."data-content")::external_interface_search_result)
     FROM "external-interface-description" ei
     WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
  FROM "transport-service" t
    JOIN  "transport-operator" op ON op.id = t."transport-operator-id";
