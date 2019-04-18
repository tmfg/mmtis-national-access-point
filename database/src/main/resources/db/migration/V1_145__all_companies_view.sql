-- Use UNION to take only unique companies (name,id,created,source) into account.
-- This view is really necessary because fetching data from three different source is difficult.
CREATE OR REPLACE VIEW "all-companies" AS
  SELECT null as "operator-id",
         tsc.name as "business-name",
         tsc."business-id" AS "business-id",
         CASE WHEN ts.published > '2000-01-01' THEN ts.published ELSE g.created END as created,
         'transport_service' as "source",
         CASE "brokerage?" WHEN true THEN 'brokerage' ELSE "sub-type" END as "sub-type"
    FROM
         "group" g, "transport-operator" top
         JOIN "transport-service" as ts ON ts."transport-operator-id" = top.id AND ts."published" IS NOT NULL AND ts.companies != '{"(,)"}',
         unnest(ts.companies) with ordinality tsc
   WHERE
         g.id = top."ckan-group-id"
     AND top."business-id" is not null
     AND top."deleted?" = FALSE
     AND g.created is not null
     AND ts.published IS NOT NULL

UNION

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
     AND top."business-id" is not null
     AND top."deleted?" = FALSE
     AND g.created is not null
     AND ts.published IS NOT NULL
    -- Do not take services that has something ing service_company table
     AND ts.id NOT IN (select sc."transport-service-id" FROM service_company sc WHERE sc.companies != '{}')

 UNION

  SELECT null as "operator-id",
         c.name as "business-name",
         c."business-id" AS "business-id",
         sc.created as created,
         'service_company' as "source",
         CASE "brokerage?" WHEN true THEN 'brokerage' ELSE "sub-type" END as "sub-type"
   FROM
        "group" g, "transport-operator" top
        JOIN "transport-service" as ts ON ts."transport-operator-id" = top.id AND ts."published" IS NOT NULL AND ts.companies != '{"(,)"}',
        service_company sc,
        unnest(sc.companies) with ordinality c
  WHERE
        g.id = top."ckan-group-id"
    AND top."business-id" is not null
    AND top."deleted?" = FALSE
    AND g.created is not null
    AND ts.published IS NOT NULL
  ORDER BY created;