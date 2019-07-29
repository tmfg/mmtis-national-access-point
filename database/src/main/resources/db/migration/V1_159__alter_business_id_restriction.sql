-- Need to drop dependant views before altering
DROP VIEW transport_service_search_result;
DROP VIEW "all-companies";


-- These changes are made in between creation and deletion of views.
ALTER TABLE "transport-operator" ALTER COLUMN "business-id" type text;

ALTER TYPE company RENAME to oldcompany;

CREATE TYPE company AS (
    name VARCHAR(200),
    "business-id" text
);

ALTER TABLE service_company ALTER COLUMN companies TYPE company[] USING companies::text::company[];

ALTER TABLE "transport-service" ALTER COLUMN companies TYPE company[] USING companies::text::company[];

DROP TYPE oldcompany;

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
           ei."gtfs-import-error",
           ei."gtfs-db-error")::external_interface_search_result)
        FROM "external-interface-description" ei
        WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
FROM "transport-service" t
         LEFT JOIN (SELECT top.name as name, top."business-id" as "business-id", a."service-id"
                    FROM "transport-operator" top, "associated-service-operators" a WHERE a."operator-id" = top.id) as aso ON aso."service-id" = t.id
         JOIN  "transport-operator" op ON op.id = t."transport-operator-id"
GROUP BY t.id, op.name, op."business-id";

-- Expose company information from four different sources:
-- transport-operator, transport-service.companies, service_company.companies and from associated-service-operators
CREATE OR REPLACE VIEW "all-companies" AS
    -- Get companies only from transport-service.companies composite type
    SELECT null as "operator-id",
           tsc.name as "business-name",
           tsc."business-id" AS "business-id",
           CASE WHEN ts.published > '2000-01-01' THEN ts.published ELSE g.created END as created,
           'transport_service' as "source",
           ts."sub-type" as "sub-type"
    FROM
        "group" g,
        "transport-operator" top
            JOIN "transport-service" as ts ON
                    ts."transport-operator-id" = top.id
                AND ts."published" IS NOT NULL
                AND ts.id NOT IN (select sc."transport-service-id" FROM service_company sc WHERE sc.companies != '{}'),
        unnest(ts.companies) with ordinality tsc
    WHERE
            g.id = top."ckan-group-id"
      AND top."business-id" IS NOT NULL
      AND top."deleted?" = FALSE
      AND g.created IS NOT NULL
      AND ts.published IS NOT NULL
      AND tsc."business-id" IS NOT NULL

    UNION
    -- Get companies only from transport-operator table
    SELECT DISTINCT ON (top.id) top.id as "operator-id",
                                top.name as "business-name",
                                top."business-id" AS "business-id",
                                g.created as created,
                                'transport_operator' as "source",
                                CASE "brokerage?" WHEN true THEN 'brokerage' ELSE "sub-type" END as "sub-type"
    FROM
        "group" g,
        "transport-operator" top
            JOIN "transport-service" as ts ON ts."transport-operator-id" = top.id AND ts."published" IS NOT NULL
    WHERE
            g.id = top."ckan-group-id"
      AND top."business-id" IS NOT NULL
      AND top."deleted?" = FALSE
      AND g.created IS NOT NULL
      AND ts.published IS NOT NULL

    UNION

    -- Get companies only from service-companies table
    SELECT null as "operator-id",
           c.name as "business-name",
           c."business-id" AS "business-id",
           sc.created as created,
           'service_company' as "source",
           ts."sub-type" as "sub-type"
    FROM
        "group" g,
        "transport-operator" top
            JOIN "transport-service" as ts ON ts."transport-operator-id" = top.id AND ts."published" IS NOT NULL
            JOIN service_company sc ON sc."transport-service-id" = ts.id,
        unnest(sc.companies) with ordinality c
    WHERE
            g.id = top."ckan-group-id"
      AND top."business-id" IS NOT NULL
      AND top."deleted?" = FALSE
      AND g.created IS NOT NULL
      AND ts.published IS NOT NULL
      AND c."business-id" IS NOT NULL

    UNION

    -- Get companies only from associated-service-operators table
    SELECT a."operator-id" as "operator-id",
           top.name as "business-name",
           top."business-id" AS "business-id",
           g.created as created,
           'associated_service_operator' as "source",
           ts."sub-type" as "sub-type"
    FROM
        "associated-service-operators" a,
        "transport-operator" top,
        "group" g,
        "transport-service" ts
    WHERE
            g.id = top."ckan-group-id"
      AND ts."id" = a."service-id"
      AND ts."published" IS NOT NULL
      AND top.id = a."operator-id"
      AND top."business-id" IS NOT NULL
      AND top."deleted?" = FALSE
      AND g.created IS NOT NULL

    ORDER BY created;
