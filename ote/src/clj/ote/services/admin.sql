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

-- name: search-services-with-interfaces
-- Find published interfaces using operator name, service name
WITH download_status AS (
    SELECT
           DISTINCT ON (s."external-interface-description-id") "external-interface-description-id",
           id,
           s.created,
           s."download-error",
           s."db-error",
           s.url
      FROM "external-interface-download-status" s
     ORDER BY s."external-interface-description-id" ASC,
              s.id DESC)
SELECT id.id as "download-id", eid.id as "interface-id", ts.id as "service-id", op.id as "operator-id",
       op.name as "operator-name", op.email as "operator-email", op.phone as "operator-phone",
       op.gsm as "operator-gsm", ts.name as "service-name", ts."contact-phone" as "service-phone",
       ts."contact-email" as "service-email", eid."data-content" as "data-content", id.url as url,
       eid.format as format, id."created" as imported, id."download-error" as "import-error",
       id."db-error" as "db-error"
  FROM
       "transport-operator" as op,
       "transport-service" as ts,
       "external-interface-description" as eid
       JOIN download_status id ON id."external-interface-description-id" = eid.id
 WHERE
       (:operator-name::TEXT IS NULL OR op.name ilike :operator-name)
   AND (:service-name::TEXT IS NULL OR ts.name ilike :service-name)
   AND (:interface-url::TEXT IS NULL OR (eid."external-interface").url ilike :interface-url)
   AND ((:import-error::BOOLEAN IS NULL AND (id."download-error" IS NULL OR id."download-error" IS NOT NULL))
        OR
        (:import-error::BOOLEAN IS NOT NULL AND id."download-error" IS NOT NULL))
   AND ((:db-error::BOOLEAN IS NULL AND (id."db-error" IS NULL OR id."db-error" IS NOT NULL ))
       OR
        (:db-error::BOOLEAN IS NOT NULL AND id."db-error" IS NOT NULL))
   AND (:interface-format::TEXT IS NULL OR :interface-format = ANY(lower(eid.format::text)::text[]))
   AND ts.published IS NOT NULL
   AND ts."transport-operator-id" = op.id
   AND eid."transport-service-id" = ts.id
   AND ts."sub-type" = 'schedule'
   AND ('gtfs' = ANY(lower(eid.format::text)::text[]) OR 'kalkati.net' = ANY(lower(eid.format::text)::text[]))
 GROUP BY eid.id, id.id, id.created, eid.id, id."download-error", id."db-error", ts.id, op.id, id.id, id.url
 ORDER BY eid.id ASC, id.id DESC;

-- name: search-services-wihtout-interface
select ts.id as "interface-id", ts.id as "service-id", op.id as "operator-id",
       op.name as "operator-name", op.email as "operator-email", op.phone as "operator-phone",
       op.gsm as "operator-gsm", ts.name as "service-name", ts."contact-phone" as "service-phone",
       ts."contact-email" as "service-email", '' as "data-content", 'Ei rajapintaa annettu' as url,
       '' as format, to_timestamp(0) as imported, 'Rajapinta puuttuu' as "import-error", 'no-db' as "db-error"
FROM
     "transport-operator" as op,
     "transport-service" as ts

WHERE (:operator-name::TEXT IS NULL OR op.name ilike :operator-name)
  AND (:service-name::TEXT IS NULL OR ts.name ilike :service-name)
  AND ts.published IS NOT NULL
  AND ts."transport-operator-id" = op.id
  AND ts."sub-type" = 'schedule'
  AND NOT EXISTS (SELECT
                    FROM "external-interface-description" i
                   WHERE i."transport-service-id" = ts.id)
GROUP BY ts.id, op.id
ORDER BY "format" ASC, "import-error" DESC;

-- name: search-interface-downloads
SELECT id.id as "download-id", eid.id as "interface-id", ts.id as "service-id", op.id as "operator-id",
       op.name as "operator-name", op.email as "operator-email", op.phone as "operator-phone",
       op.gsm as "operator-gsm", ts.name as "service-name", ts."contact-phone" as "service-phone",
       ts."contact-email" as "service-email", eid."data-content" as "data-content", id.url as url,
       eid.format as format, id."created" as imported, id."download-error" as "import-error",
       id."db-error" as "db-error"
FROM
    "transport-operator" as op,
    "transport-service" as ts,
    "external-interface-description" as eid,
    "external-interface-download-status" id
WHERE
      eid.id = :interface-id
  AND id."external-interface-description-id" = eid.id
  AND ts.published IS NOT NULL
  AND ts."transport-operator-id" = op.id
  AND eid."transport-service-id" = ts.id
  AND ts."sub-type" = 'schedule'
  AND ('gtfs' = ANY(lower(eid.format::text)::text[]) OR 'kalkati.net' = ANY(lower(eid.format::text)::text[]))
GROUP BY eid.id, id.created, eid.id, id."download-error", id."db-error", ts.id, op.id, id.id, id.url
ORDER BY eid.id ASC, id.id DESC;

-- name: fetch-commercial-services
SELECT t.id as "service-id", t.name as "service-name", t."commercial-traffic?" as "commercial?",
       o.id as "operator-id", o.name as "operator-name"
  FROM
       "transport-service" t
       LEFT JOIN "external-interface-description" eid ON eid."transport-service-id" = t.id,
       "transport-operator" o
 WHERE t."transport-operator-id" = o.id
   AND t."sub-type" = 'schedule'
   AND t.published IS NOT NULL
 GROUP BY t.id, o.id
 ORDER BY o.name asc;

-- name: fetch-all-ports
SELECT p.code as code, (p.name[1]::localized_text).text as name, ST_X(p.location) as lat, ST_Y(p.location) as lon,
       CASE WHEN p."created-by" IS NULL THEN 'ei' ELSE 'kyllä' END AS "user-added?", p.created as created
  FROM "finnish_ports" as p;

-- name: fetch-sea-routes-for-admin
SELECT
       DISTINCT ON (r.id) r.id,
       GREATEST(MAX(ru."to-date"), MAX("added-dates")) AS "to-date",
       EXTRACT(DOW FROM DATE (GREATEST(MAX(ru."to-date"), MAX("added-dates"))::DATE)) weekday, --(0 sunday, 6, saturday)
       ru.sunday, ru.monday, ru.tuesday, ru. wednesday, ru.thursday, ru.friday, ru.saturday,
       top.id AS "operator-id",
       top.name AS "operator-name",
       r.name AS "route-name",
       r."published?" AS "published?",
       r.modified AS "modified",
       r.created AS "created"
  FROM
       "transit_route" r,
       LATERAL unnest(
           CASE WHEN array_length(r."service-calendars", 1) >= 1
                    THEN r."service-calendars"
                ELSE '{null}'::transit_service_calendar[]
           END) c,
       LATERAL unnest (
          CASE WHEN array_length(c."service-rules", 1) >= 1
                   THEN c."service-rules"
               ELSE '{null}'::transit_service_rule[]
          END) ru,
       LATERAL unnest (
          CASE WHEN array_length(c."service-added-dates", 1) >= 1
                   THEN c."service-added-dates"
               ELSE '{null}'::date[]
          END) "added-dates",
       "transport-operator" top
 WHERE
       (:operator::TEXT IS NULL OR top.name ilike :operator)
   AND top.id = r."transport-operator-id"
 GROUP BY
          r.id, top.id, ru.sunday, ru.monday, ru.tuesday, ru. wednesday, ru.thursday, ru.friday, ru.saturday
 ORDER BY r.id, "to-date" DESC;

-- name: fetch-netex-conversions-for-admin
SELECT n.id as "netex-conversion-id",
       n."external-interface-description-id",
       n."transport-service-id",
       n.filename,
       n.status,
       n.created,
       n.modified,
       top.name as "operator-name",
       ts.name as "service-name"
  FROM "netex-conversion" n,
       "transport-service" ts,
       "transport-operator" top
 WHERE ts.id = n."transport-service-id"
   AND top.id = ts."transport-operator-id"
   AND (:operator::TEXT IS NULL OR top.name ilike :operator)
ORDER BY n.modified DESC, n.created, ts.name;

-- name: fetch-validation-services
SELECT s.name, s.id, s."sub-type", s."type", s.created, s.modified, s.published, s.validate, s."transport-operator-id", o.name as "operator-name"
    FROM "transport-service" s, "transport-operator" o
    WHERE s.validate IS NOT NULL
        AND s."transport-operator-id" = o.id
ORDER BY s.validate ASC;
