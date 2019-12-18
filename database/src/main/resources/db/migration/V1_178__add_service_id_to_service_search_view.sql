-- Service search view was slow due to too many lines in interface-download table without linking them to service-id
-- Drop current view
DROP VIEW transport_service_search_result;

-- Create fixed view
CREATE VIEW transport_service_search_result AS
SELECT t.*,
       op.name as "operator-name",
       op."business-id" as "business-id",
       array_cat(
               array_cat(
                       (SELECT sc."companies"
                        FROM "service_company" sc
                        WHERE sc."transport-service-id" = t.id),
                       t."companies"),
               array_agg((aso.name, aso."business-id")::company)) AS "service-companies",
       (SELECT array_agg(oaf."operation-area")
        FROM "operation-area-facet" oaf
        WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
       (SELECT array_agg(
                       ROW(ei."external-interface",
                           ei.format,
                           ei."ckan-resource-id",
                           ei.license, ei."data-content",
                           eids."download-error",
                           eids."db-error",
                           ei."id")::external_interface_search_result)
        FROM "external-interface-description" ei
                 LEFT JOIN (SELECT DISTINCT ON (eids."external-interface-description-id") eids."external-interface-description-id",
                                                                                          eids.id,
                                                                                          eids."db-error",
                                                                                          eids."download-error"
                            -- Changed code Starts
                             FROM "external-interface-download-status" eids, "external-interface-description" e
                            WHERE eids."external-interface-description-id" = e.id AND e."transport-service-id" = t.id
                            -- Changed code Ends
                 ORDER BY eids."external-interface-description-id" DESC,
                                     eids.id DESC)
            eids ON ei.id = eids."external-interface-description-id"
        WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
FROM "transport-service" t
         LEFT JOIN (SELECT top.name as name,
                           top."business-id" as "business-id",
                           a."service-id"
                    FROM "transport-operator" top,
                         "associated-service-operators" a
                    WHERE a."operator-id" = top.id) as aso ON aso."service-id" = t.id
         JOIN  "transport-operator" op ON op.id = t."transport-operator-id"
GROUP BY t.id, op.name, op."business-id";