-- name: fetch-service-business-ids
   SELECT c.name as "operator", c."business-id" as "business-id",
          s."contact-phone" as "phone", s."contact-gsm" as "gsm", s."contact-email" as "email", CAST('service' AS text) as "source"
     FROM "transport-service" s
          LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
          LEFT JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
    WHERE c."business-id" IS NOT NULL
      AND s."published?" = TRUE;

-- name: fetch-operator-business-ids
SELECT o.id, o."name" as "operator", o."business-id" as "business-id",
       o."phone" as "phone", o."gsm" as "gsm", o."email" as "email", CAST('operator' AS text) as "source"
  FROM "transport-operator" o
 WHERE o."business-id" IS NOT NULL;

-- name: delete-transport-operator
-- Delete all operator data except published external interface data from ckan
SELECT del_operator(:operator-group-name);

-- name: search-interfaces-by-operator
-- Find published interfaces using operator name
SELECT i.id as "interface-id", ts.id as "service-id",
op.id as "operator-id", op.name as "operator-name", op.email as "operator-email", op.phone as "operator-phone", op.gsm as "operator-gsm",
ts.name as "service-name", ts."contact-phone" as "service-phone", ts."contact-email" as "service-email",
i."data-content" as "data-content", (i."external-interface").url as url,
i.format as format, i."gtfs-imported" as imported, i."gtfs-import-error" as "import-error"
  FROM "transport-operator" as op, "transport-service" as ts, "external-interface-description" as i
 WHERE op.name ilike :name
   AND ts."published?" = TRUE
   AND ts."transport-operator-id" = op.id
   AND i."transport-service-id" = ts.id
   AND ('GTFS' = ANY(i.format) OR 'Kalkati.net' = ANY(i.format))
 GROUP BY ts.id, op.id, i.id
 ORDER BY i.format ASC, i."gtfs-import-error" DESC;

-- name: search-interfaces-by-service
-- Find published interfaces using service name
SELECT i.id as "interface-id", ts.id as "service-id",
op.id as "operator-id", op.name as "operator-name", op.email as "operator-email", op.phone as "operator-phone", op.gsm as "operator-gsm",
ts.name as "service-name", ts."contact-phone" as "service-phone", ts."contact-email" as "service-email",
i."data-content" as "data-content", (i."external-interface").url as url,
i.format as format, i."gtfs-imported" as imported, i."gtfs-import-error" as "import-error"
  FROM "transport-operator" as op, "transport-service" as ts, "external-interface-description" as i
 WHERE ts.name ilike :name
   AND ts."published?" = TRUE
   AND ts."transport-operator-id" = op.id
   AND i."transport-service-id" = ts.id
   AND ('GTFS' = ANY(i.format) OR 'Kalkati.net' = ANY(i.format))
 GROUP BY ts.id, op.id, i.id
 ORDER BY i.format ASC, i."gtfs-import-error" DESC;
