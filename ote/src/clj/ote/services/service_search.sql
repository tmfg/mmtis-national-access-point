-- name: operation-area-facet
SELECT "operation-area" as text, COUNT(*) as count
  FROM "operation-area-facet"
 GROUP BY text
 ORDER BY count DESC;

-- name: sub-type-facet
SELECT type."sub-type", COALESCE(count.count, 0) AS count
  FROM (SELECT UNNEST(enum_range(NULL::transport_service_subtype)) "sub-type") type
       LEFT JOIN (SELECT t."sub-type", COUNT(*) as count
                    FROM "transport-service" t
                   WHERE t."published?" = true
                   GROUP BY t."sub-type") count ON type."sub-type" = count."sub-type"
 ORDER BY count DESC

-- name: latest-service-ids
-- row-fn: :id
SELECT id FROM "transport-service"
 ORDER BY COALESCE(modified, created) DESC, name ASC
 LIMIT 20;

-- name: search
-- Search for transport services by name, operation area or subtype
SELECT t.id, t.name, t.type, t."sub-type",
