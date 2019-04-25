-- Use UNION to take only unique companies (name,id,created,source) into account.
-- This view is really necessary because fetching data from three different source is difficult.
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
