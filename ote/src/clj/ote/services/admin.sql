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
       LEFT JOIN download_status id ON id."external-interface-description-id" = eid.id
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
   AND ('gtfs' = ANY(lower(eid.format::text)::text[]) OR 'kalkati.net' = ANY(lower(eid.format::text)::text[]) OR 'netex' = ANY(lower(eid.format::text)::text[]))
 GROUP BY eid.id, id.id, id.created, eid.id, id."download-error", id."db-error", ts.id, op.id, id.id, id.url
 ORDER BY eid.id ASC, id.id DESC;

-- name: search-vaco-status-packages
SELECT op.name as "operator-name", ts.name as "service-name", ts.id as "transport-service-id",
       eid."data-content" as "data-content", (eid."external-interface").url as url, eid.id as "interface-id",
       eid.format as format, gp.id, gp."tis-success", gp."tis-complete", gp."tis-magic-link", gp.created, gp."tis-entry-public-id"
FROM
    "transport-operator" as op,
    "transport-service" as ts,
    "external-interface-description" as eid
       left join lateral (SELECT gp.id, gp."tis-entry-public-id", gp."tis-complete", gp."tis-success", gp."tis-magic-link", gp.created
                      FROM gtfs_package gp
                      WHERE gp."transport-service-id" = ts.id
                        AND gp."external-interface-description-id" = eid.id
                      ORDER BY gp.created DESC
                      LIMIT 1) gp ON true
WHERE
    (:operator-name::TEXT IS NULL OR op.name ilike :operator-name)
  AND (:service-name::TEXT IS NULL OR ts.name ilike :service-name)
  AND (:interface-url::TEXT IS NULL OR (eid."external-interface").url ilike :interface-url)
  AND (:interface-format::TEXT IS NULL OR :interface-format = ANY(lower(eid.format::text)::text[]))
  AND ts.published IS NOT NULL
  AND ts."transport-operator-id" = op.id
  AND eid."transport-service-id" = ts.id
  AND ts."sub-type" = 'schedule'
  AND ('gtfs' = ANY(lower(eid.format::text)::text[]) OR 'kalkati.net' = ANY(lower(eid.format::text)::text[]) OR 'netex' = ANY(lower(eid.format::text)::text[]))
ORDER BY gp."tis-success" DESC;

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

-- name: fetch-successfull-netex-conversion-interfaces-for-admin
-- Get interfaces that converts gtfs/kalkati to netex successfully.
select TRIM((eid."external-interface").url) as "interface-url",
       top.name as "top-name",
       ts.name as "service-name",
       array_to_string(eid."data-content", ',') as "interface-content",
       TRIM(top.email) as "operator-email",
       TRIM(ts."contact-email") as "service-email",
       array_to_string(array_agg(u.email), '|') as "user-email",
       eid.format[1] as "interface-format"
FROM
    "external-interface-description" eid,
    "transport-service" ts,
    "netex-conversion" n,
    "transport-operator" top,
    "group" g,
    "member" m,
    "user" u
WHERE ts.id = eid."transport-service-id"
  AND n."transport-service-id" = ts.id
  AND n.status = 'ok'
  AND ts.published IS NOT NULL
  AND top."ckan-group-id" = g.id
  AND m.table_name = 'user'
  AND m.state = 'active'
  AND m.table_id = u.id
  AND m.group_id = g.id
  AND u.email NOT LIKE '%@matkahuolto.fi'
  AND top.id = ts."transport-operator-id"
  AND ('GTFS' = ANY(eid.format) OR 'Kalkati.net' = ANY(eid.format))
  AND 'route-and-schedule' = ANY(eid."data-content")
GROUP BY top.name, ts.name, eid.format[1], eid."external-interface", eid."data-content", ts."contact-email", top.email;



-- name: fetch-netex-conversions-for-admin
SELECT n.id as "netex-conversion-id",
       n."external-interface-description-id",
       n."transport-service-id",
       n.filename,
       n.status,
       n.created,
       n.modified,
       n."validation-file-error",
       n."input-file-error",
       top.name as "operator-name",
       ts.name as "service-name"
  FROM "netex-conversion" n,
       "transport-service" ts,
       "transport-operator" top
 WHERE ts.id = n."transport-service-id"
   AND top.id = ts."transport-operator-id"
   AND (:operator::TEXT IS NULL OR top.name ilike :operator)
ORDER BY n.modified DESC, n.created, ts.name;

-- name: fetch-associated-companies-for-admin
select ts.name as "joined-service",
       ts.id as "service-id",
       tstop.name as "operator",
       tstop.id as "operator-id",
       tstop."business-id" as "operator-business-id",
       top.name as "joined-operator",
       top."business-id" as "joined-operator-business-id",
       aso.timestamp::DATE as "joined-date",
       top.id as "joined-operator-id"
from "associated-service-operators" aso
         join "transport-operator" top on top.id = aso."operator-id"
         join "transport-service" ts on ts.id = aso."service-id"
         join "transport-operator" tstop on tstop.id = ts."transport-operator-id";

-- name: fetch-reported-taxi-prices
SELECT *,
       -- summarize areas to municipality level to make the prices easier to compare
       (SELECT array_agg(DISTINCT parent_namefin)
          FROM location_relations
         WHERE parent_type = 'finnish-municipality'
             AND child_namefin IN (SELECT oa.description[1].text
                                     FROM operation_area oa
                                    WHERE oa."transport-service-id" = prices."service-id")
            OR parent_namefin IN (SELECT oa.description[1].text
                                    FROM operation_area oa
                                   WHERE oa."transport-service-id" = prices."service-id")) AS "operating-areas"
  FROM (SELECT o."business-id",
               o."name" AS "operator-name",
               s."id" AS "service-id",
               s."name" AS "service-name",
               tsp.start_price_daytime AS "start-price-daytime",
               tsp.start_price_nighttime AS "start-price-nighttime",
               tsp.start_price_weekend AS "start-price-weekend",
               tsp.price_per_minute AS "price-per-minute",
               tsp.price_per_kilometer AS "price-per-kilometer",
               tsp."accessibility_service_stairs" AS "accessibility-service-stairs",
               tsp."accessibility_service_stretchers" AS "accessibility-service-stretchers",
               tsp."accessibility_service_fare" AS "accessibility-service-fare",
               tsp."approved?" AS "approved?",
               row_number() OVER (PARTITION BY service_id ORDER BY timestamp desc)
          FROM taxi_service_prices tsp
          JOIN "transport-service" s ON tsp."service_id" = s."id"
          JOIN "transport-operator" o ON s."transport-operator-id" = o."id"
         WHERE tsp."approved?" IS NOT NULL) prices  -- NOT NULL = approved
 WHERE prices."row_number" = 1;

-- name: fetch-validation-services
SELECT s.name, s.id, s."sub-type", s."type", s.created, s.modified, s.published,
       s.validate, s."transport-operator-id", o.name as "operator-name", s."re-edit", s."parent-id"
    FROM "transport-service" s, "transport-operator" o
    WHERE s.validate IS NOT NULL
      AND s."transport-operator-id" = o.id
    ORDER BY s.validate ASC;

-- name: fetch-service-company-csv-for-admin
SELECT ts.published, ts.name as "service-name",
       c.id, c."transport-service-id", c."file-key", c."csv-file-name", c.created, c."validation-warning", c."failed-companies-count", c."valid-companies-count"
  FROM
       "transport_service_company_csv" c,
       "transport-service" ts
 WHERE
       c."transport-service-id" = ts.id
 ORDER BY c.created DESC;
