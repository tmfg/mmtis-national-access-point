-- name: operation-area-facet
SELECT "operation-area" as text, COUNT(*) as count
  FROM "operation-area-facet" o
 GROUP BY text
 ORDER BY text ASC;

-- name: sub-type-facet
SELECT type."sub-type", COALESCE(count.count, 0) AS count
  FROM (SELECT UNNEST(enum_range(NULL::transport_service_subtype)) "sub-type") type
       LEFT JOIN (SELECT t."sub-type", COUNT(*) as count
                    FROM "transport-service" t
                   WHERE t.published IS NOT NULL
                   GROUP BY t."sub-type") count ON type."sub-type" = count."sub-type"
 -- PENDING: remove value from enum if we decide not to implement brokerage type at all
 -- Brokerage enabled, but it only affects on dropdown list in ui. Brokerage isn't a subtype anymore, and we cant
 -- calculate how many brokering services we have using this query.
 -- WHERE type."sub-type" NOT IN ('brokerage')
 ORDER BY count DESC;

-- name: latest-service-ids
-- row-fn: :id
SELECT id FROM "transport-service"
 WHERE published IS NOT NULL
 ORDER BY COALESCE(modified, created) DESC, name ASC
 LIMIT 1000;

-- name: search
-- Search for transport services by name, operation area or subtype
SELECT t.id, t.name, t.type, t."sub-type",

-- name: total-service-count
-- single?: true
SELECT COUNT(id) FROM "transport-service" WHERE published IS NOT NULL;

-- name: total-company-count
-- single?: true
-- Count the total number of companies providing transport services
-- this includes companies reported in CSV files
SELECT COUNT(*)
  FROM (SELECT DISTINCT op."business-id"
          FROM "transport-service" ts
          JOIN "transport-operator" op ON ts."transport-operator-id" = op.id
         WHERE published IS NOT NULL
        UNION
        SELECT DISTINCT (x.c)."business-id"
          FROM (SELECT unnest(COALESCE(sc.companies, ts.companies)) c
                  FROM "transport-service" ts
                  LEFT JOIN service_company sc ON sc."transport-service-id" = ts.id
                 WHERE ts.published IS NOT NULL) x) y;

-- name: service-search-by-operator
-- Finds operators by name and by business-id and services that have companies added as "operators.
SELECT op.name as "operator", op."business-id" as "business-id"
  FROM "transport-operator" op
 WHERE ( op.name ILIKE :name OR op."business-id" = :businessid)
   AND op."deleted?" = FALSE
UNION
SELECT c.name as "operator", c."business-id" as "business-id"
  FROM "transport-service" s
  LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
  JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
 WHERE (c.name ILIKE :name OR c."business-id" = :businessid)
   AND published IS NOT NULL;


-- name: service-search-by-service-name
-- Finds services by service name or operator name.
SELECT ts.name as "service-name",
       ts.id as "service-id",
       top."business-id" as "operator-business-id",
       top.id as "operator-id",
       top.name as "operator-name",
       CONCAT(ts.name, ' - ', top.name) as "service-operator"
  FROM "transport-service" ts
  JOIN "transport-operator" top ON ts."transport-operator-id" = top.id
 WHERE ts.name ILIKE :name
    OR top.name ILIKE :name;


-- name: service-ids-by-business-id
-- Find service id's using business-id
SELECT ts.id as id
  FROM "transport-operator" op, "transport-service" ts
 WHERE op."business-id" IN (:operators)
   AND op.id = ts."transport-operator-id"
   AND ts.published IS NOT NULL
UNION
SELECT ts.id as id
  FROM "transport-service" ts
  LEFT JOIN service_company sc ON sc."transport-service-id" = ts.id
  JOIN LATERAL unnest(COALESCE(sc.companies, ts.companies)) AS c ON TRUE
 WHERE c."business-id" IN (:operators)
   AND ts.published IS NOT NULL
UNION
    SELECT a."service-id" as id
      FROM "associated-service-operators" a,
           "transport-operator" top
     WHERE top."business-id" in (:operators)
       AND a."operator-id" = top.id;


-- name: service-ids-by-transport-type
-- Find services using transport type
  SELECT ts.id as id
    FROM "transport-service" ts, unnest(ts."transport-type") as str
   WHERE str::text IN (:tt)
     AND ts.published IS NOT NULL;

-- name: participating-companies
-- Search all services companies and their business-ids
 SELECT c."business-id" as "Y-tunnus", c.name as "Nimi"
   FROM "transport-service" s
        LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
        LEFT JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
  WHERE c."business-id" IS NOT NULL
    AND s.id = :id
    AND s.published IS NOT NULL;

-- name: service-ids-by-data-content
-- Find services using external url data content
SELECT eid."transport-service-id" as id
  FROM "external-interface-description" eid, unnest(eid."data-content") as str
 WHERE  str::text IN (:dc)
   AND EXISTS(SELECT ts.id
                FROM "transport-service" ts
               WHERE ts.id = eid."transport-service-id"
                 AND ts.published IS NOT NULL)

-- name: service-ids-by-operation-areas
-- Find services by operation area names
SELECT oa."transport-service-id" as id
  FROM "operation_area" oa,
      (SELECT ST_Union(array_agg(pl.location)) as "location"
         FROM places pl
        WHERE pl.namefin IN (:operation-area)) as "sa",
       "transport-service" ts
 WHERE ts.published IS NOT NULL
   AND oa.id not in (4046, 5013)
   AND oa."primary?" = true
   AND ts.id = oa."transport-service-id"
   AND ST_Intersects(oa.location, sa.location);

-- name: service-match-quality-to-operation-area
-- Finds service's match quality to a given operation area
SELECT "oa-agg"."transport-service-id" as id,
       ST_Area(ST_Intersection(ST_SetSRID("oa-agg".location, 4326), sa.location)) as intersection,
       ST_Area(ST_SymDifference(ST_SetSRID("oa-agg".location, 4326), sa.location)) as "difference"
  FROM
      (SELECT oa."transport-service-id" as "transport-service-id",
              ST_Union(array_agg(ST_SetSRID(oa.location, 4326))) as "location"
         FROM operation_area oa
        WHERE oa."transport-service-id" in (:id)
          AND oa."primary?" = true
     GROUP BY oa."transport-service-id") as "oa-agg",
      (SELECT ST_Union(array_agg(pl.location)) as "location"
         FROM places pl
        WHERE pl.namefin IN (:operation-area)) as "sa";

