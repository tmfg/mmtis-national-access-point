-- name: fetch-service-business-ids
WITH
services AS (
 SELECT s1.id, to_json(array_agg(json_build_object(
           'id',             s1.id,
           'name',           s1.name,
           'transport-type', s1."transport-type",
           'sub-type',       s1."sub-type",
           'brokerage?',     s1."brokerage?",
           'operation-area', (SELECT array_agg(description[1].text)
                                FROM "operation_area" oa
                               WHERE oa."transport-service-id" = s1.id)))) AS services
   FROM "transport-service" s1
  GROUP BY s1.id
),
companies AS (
SELECT c.name as "operator", c."business-id" as "business-id",
               s.id as "transport-service-id", s.name as "transport-service-name",
               s."contact-phone" as "phone", s."contact-gsm" as "gsm", s."contact-email" as "email", CAST('service' AS text) as "source"
          FROM "transport-service" s
          LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
          LEFT JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
         WHERE c."business-id" IS NOT NULL
           AND s.published IS NOT NULL
)
SELECT *
  FROM companies c
  JOIN services s ON c."transport-service-id" = s.id;

-- name: fetch-operator-business-ids
SELECT o.id, o."name" as "operator", o."business-id" as "business-id",
       o."phone" as "phone", o."gsm" as "gsm", o."email" as "email", CAST('operator' AS text) as "source",
       (SELECT to_json(array_agg(json_build_object('id', s.id, 'name', s."name", 'transport-type', s."transport-type", 'sub-type', s."sub-type", 'brokerage?', s."brokerage?", 'operation-area',
          (SELECT array_agg(description[1].text) FROM "operation_area" oa
             where oa."transport-service-id" = s.id))))
          FROM "transport-service" s
           WHERE s."transport-operator-id" = o.id) AS services
  FROM "transport-operator" o
  JOIN "transport-service" s ON s."transport-operator-id" = o.id
 WHERE o."business-id" IS NOT NULL
 GROUP BY o.id;

-- name: search-interfaces
-- Find published interfaces using operator name, service name
SELECT i.id as "interface-id", ts.id as "service-id",
op.id as "operator-id", op.name as "operator-name", op.email as "operator-email", op.phone as "operator-phone", op.gsm as "operator-gsm",
ts.name as "service-name", ts."contact-phone" as "service-phone", ts."contact-email" as "service-email",
i."data-content" as "data-content", (i."external-interface").url as url,
i.format as format, i."gtfs-imported" as imported, i."gtfs-import-error" as "import-error", i."gtfs-db-error" as "db-error"
  FROM "transport-operator" as op, "transport-service" as ts, "external-interface-description" as i
 WHERE (:operator-name::TEXT IS NULL OR op.name ilike :operator-name)
  AND (:service-name::TEXT IS NULL OR ts.name ilike :service-name)
  AND (:interface-url::TEXT IS NULL OR (i."external-interface").url ilike :interface-url)
  AND (:import-error::BOOLEAN IS NULL OR i."gtfs-import-error" IS NOT NULL)
  AND (:db-error::BOOLEAN IS NULL OR i."gtfs-db-error" IS NOT NULL)
  AND (:interface-format::TEXT IS NULL OR :interface-format = ANY(lower(i.format::text)::text[]))
   AND ts.published IS NOT NULL
   AND ts."transport-operator-id" = op.id
   AND i."transport-service-id" = ts.id
   AND ('gtfs' = ANY(lower(i.format::text)::text[]) OR 'kalkati.net' = ANY(lower(i.format::text)::text[]))
 GROUP BY ts.id, op.id, i.id
 ORDER BY i.format ASC, i."gtfs-import-error" DESC;

-- name: fetch-commercial-services
SELECT t.id as "service-id", t.name as "service-name", t."commercial-traffic?" as "commercial?",
       o.id as "operator-id", o.name as "operator-name"
  FROM
       "transport-service" t,
       "transport-operator" o,
       "external-interface-description" eid
 WHERE t."transport-operator-id" = o.id
   AND eid."transport-service-id" = t.id
   AND t."sub-type" = 'schedule'
   AND t.published IS NOT NULL
   AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
 GROUP BY t.id, o.id
 ORDER BY o.name asc;

