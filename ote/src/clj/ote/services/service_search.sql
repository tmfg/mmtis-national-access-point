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
                   WHERE t."published?" = true
                   GROUP BY t."sub-type") count ON type."sub-type" = count."sub-type"
 -- PENDING: remove value from enum if we decide not to implement brokerage type at all
 WHERE type."sub-type" NOT IN ('brokerage')
 ORDER BY count DESC

-- name: latest-service-ids
-- row-fn: :id
SELECT id FROM "transport-service"
 WHERE "published?" = TRUE
 ORDER BY COALESCE(modified, created) DESC, name ASC
 LIMIT 1000;

-- name: search
-- Search for transport services by name, operation area or subtype
SELECT t.id, t.name, t.type, t."sub-type",

-- name: total-service-count
-- single?: true
SELECT COUNT(id) FROM "transport-service" WHERE "published?" = TRUE;

-- name: service-search-by-operator
-- Finds operators by name and by business-id and services that have companies added as "operators.
SELECT op.name as "operator", op."business-id" as "business-id"
  FROM "transport-operator" op
 WHERE ( op.name ILIKE :name OR op."business-id" ILIKE :businessid)
   AND op."deleted?" = FALSE
UNION
SELECT c.name as "operator", c."business-id" as "business-id"
  FROM "transport-service" s
  LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
  JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
 WHERE (c.name ILIKE :name OR c."business-id" ILIKE :businessid)
   AND s."published?" = TRUE;

-- name: service-ids-by-business-id
-- Find service id's using business-id
SELECT ts.id as id
  FROM "transport-operator" op, "transport-service" ts
 WHERE op."business-id" IN (:operators)
   AND op.id = ts."transport-operator-id"
   AND ts."published?" = TRUE
UNION
SELECT ts.id as id
  FROM "transport-service" ts
  LEFT JOIN service_company sc ON sc."transport-service-id" = ts.id
  JOIN LATERAL unnest(COALESCE(sc.companies, ts.companies)) AS c ON TRUE
 WHERE c."business-id" IN (:operators)
   AND ts."published?" = TRUE;

-- name: participating-companies
-- Search all services companies and their business-ids
 SELECT c."business-id" as "Y-tunnus", c.name as "Nimi"
   FROM "transport-service" s
        LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
        LEFT JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
  WHERE c."business-id" IS NOT NULL
    AND s.id = :id
    AND s."published?" = TRUE;
