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
 ORDER BY published DESC, name ASC
 LIMIT 1000;

-- name: total-service-count
-- single?: true
SELECT COUNT(id) FROM "transport-service" WHERE published IS NOT NULL;

-- name: total-company-count
-- single?: true
-- Count the total number of companies providing transport services or participating in providing
SELECT COUNT(x."business-id")
  FROM (SELECT DISTINCT a."business-id" FROM "all-companies" a) x;

-- name: service-search-by-operator
-- Finds operators by name and by business-id and services that have companies added as "operators.
SELECT op.name as "operator", op."business-id" as "business-id"
  FROM "transport-operator" op
       JOIN "transport-service" ts ON ts."transport-operator-id" = op.id AND ts.published IS NOT NULL
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
 WHERE (ts.name ILIKE :name OR top.name ILIKE :name)
   AND ts.published IS NOT NULL;

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
                 AND ts.published IS NOT NULL);

-- name: service-ids-by-operation-areas
-- Find services by operation area names using two search tables. Notice the UNION of two selects.
SELECT oa."transport-service-id" as id
  FROM "places" pl1,
       "operation-area-facet" oa,
       "spatial-relations-places" sr,
       "transport-service" ts
 WHERE pl1.namefin in (:operation-area)
   AND pl1.id = sr."search-area"
   AND sr."operation-area-search-term" = oa."operation-area"
   AND oa."primary?" = true
   AND oa."transport-service-id" = ts.id
   AND ts.published IS NOT NULL

 UNION

   SELECT oa."transport-service-id" as id
     FROM "places" pl,
          "spatial-relations-custom-areas" sr,
          "operation_area" oa,
          "transport-service" ts
    WHERE pl.namefin in (:operation-area)
      AND pl.id = sr."search-area"
      AND sr."operation-area-id" = oa.id
      AND oa."primary?" = true
      AND oa."transport-service-id" = ts.id
      AND ts.published IS NOT NULL;

-- name: service-match-quality-to-operation-area
-- Finds service's match quality to a given operation area. Uses ST_Envelope to create a rough estimate of the quality instead of calculating an exact one
-- If geometry type is not a surface area of some kind, use a carefully chosen constant area. For instance railway stations mark their operating areas as points
SELECT oa."transport-service-id" as id,
       CASE
        WHEN ST_GeometryType(oa.location) IN ('ST_Point', 'ST_Linestring') THEN 3e-4
        ELSE ST_Area(ST_Intersection(oa.location, sa.location))
        END as intersection,
       ST_Area(ST_SymDifference(oa.location, sa.location)) as "difference",
       EXTRACT(epoch FROM (NOW() - (COALESCE (ts.modified, ts.created))))::INTEGER as "modified"
  FROM
      (SELECT oa."transport-service-id" as "transport-service-id",
              ST_Envelope(ST_Union(array_agg(ST_Envelope(oa.location)))) as "location"
         FROM operation_area oa
        WHERE oa."transport-service-id" in (:id)
          AND oa."primary?" = true
        GROUP BY oa."transport-service-id") oa,
      (SELECT ST_Envelope(ST_Union(array_agg(ST_Envelope(pl.location)))) as "location"
         FROM places pl
        WHERE pl.namefin IN (:operation-area)) sa,
      "transport-service" ts
WHERE ts.id IN (:id)
  AND ts.id = oa."transport-service-id";

-- name: latest-published-service
-- Fetch the latest published service by modified date
SELECT id, name, modified, published
  FROM "transport-service"
 WHERE published IS NOT NULL
 ORDER BY published DESC, modified DESC
 LIMIT 1;
