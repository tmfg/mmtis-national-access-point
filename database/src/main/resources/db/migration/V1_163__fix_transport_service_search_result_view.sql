-- Fix "external-interface-download-status" join to "external-interface-description"
-- Drop view first
DROP VIEW transport_service_search_result;

-- Create view again
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

               -- Changed section
        FROM "external-interface-description" ei
                 LEFT JOIN (SELECT DISTINCT ON (eids.id) eids.id, eids."external-interface-description-id",
                                                         eids."db-error",  eids."download-error"
                            FROM "external-interface-download-status" eids
                            ORDER BY eids.id DESC) eids ON ei.id = eids."external-interface-description-id"
             -- Changed section ends

        WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
FROM "transport-service" t
         LEFT JOIN (SELECT top.name as name, top."business-id" as "business-id", a."service-id"
                    FROM "transport-operator" top, "associated-service-operators" a WHERE a."operator-id" = top.id) as aso ON aso."service-id" = t.id
         JOIN  "transport-operator" op ON op.id = t."transport-operator-id"
GROUP BY t.id, op.name, op."business-id";
