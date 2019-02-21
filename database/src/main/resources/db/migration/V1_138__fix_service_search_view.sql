-- Create new with "associated-service-operators"
CREATE OR REPLACE VIEW transport_service_search_result AS
SELECT t.*, op.name as "operator-name", op."business-id" as "business-id",
       -- Changed Start
       COALESCE((SELECT sc."companies"
                 FROM "service_company" sc
                 WHERE sc."transport-service-id" = t.id), t."companies", array_agg((aso.name, aso."business-id")::company)) AS "service-companies",
       -- Changed End
       (SELECT array_agg(oaf."operation-area")
        FROM "operation-area-facet" oaf
        WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
       (SELECT array_agg(ROW(ei."external-interface", ei.format,
         ei."ckan-resource-id",
         ei.license, ei."data-content",
         ei."gtfs-import-error",
         ei."gtfs-db-error")::external_interface_search_result)
        FROM "external-interface-description" ei
        WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
FROM "transport-service" t
       LEFT JOIN (SELECT top.name as name, top."business-id" as "business-id", a."service-id"
                  FROM "transport-operator" top, "associated-service-operators" a WHERE a."operator-id" = top.id) as aso ON aso."service-id" = t.id
       JOIN  "transport-operator" op ON op.id = t."transport-operator-id"
GROUP BY t.id, op.name, op."business-id";