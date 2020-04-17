-- TODO improvements for later could include parametrization of certain more generic queries.

-- name: fetch-operators-no-services
SELECT op.name, op.id, op.phone, COALESCE(NULLIF(op.email, ''), u.email) AS "email"
  FROM "transport-operator" op
         JOIN "user" u ON u.name = (SELECT usr.name
                                      FROM "transport-operator" t
                                             JOIN member m ON m.group_id = t."ckan-group-id"
                                             JOIN "user" usr ON m.table_id = usr.id
                                     WHERE t.id = op.id
                                     ORDER BY usr.id
                                     LIMIT 1)
 WHERE (SELECT COUNT(*) FROM "transport-service" ts WHERE ts."transport-operator-id" = op.id) = 0
 ORDER BY op.name ASC;

--name: fetch-all-emails
SELECT u."email" as email
  FROM public.user u
 WHERE email IS NOT NULL
   AND u.state = 'active'

UNION

SELECT t."contact-email" AS email
  FROM "transport-service" t
 WHERE t."contact-email" IS NOT NULL

UNION

SELECT o.email AS email
  FROM "transport-operator" o
 WHERE o."deleted?" = FALSE
   AND o.email IS NOT NULL;

--name: fetch-operators-brokerage
SELECT op.name,
       op."business-id",
       COALESCE(NULLIF(op.phone, ''), NULLIF(ts."contact-phone", ''), ts."contact-gsm") AS phone,
       COALESCE(op.email, ts."contact-email", u.email) AS email,
       ts.name AS "service-name"
  FROM "transport-operator" op
  JOIN "transport-service" ts ON ts."transport-operator-id" = op.id
  JOIN "user" u ON u.id = ts."created-by"
 WHERE ts."brokerage?" IS TRUE
   AND ts.published IS NOT NULL
 ORDER BY op.name ASC;

-- name: fetch-operators-with-sub-contractors
-- In the first selection select operators that do not have any sub companies.
-- Then select all operators that have sub companies in transport-service table, but don't have anything in service_company table.
-- And finally select all operators that have sub companies from external url or from csv upload.
SELECT op.name as "operator", op."business-id" as "business-id", '-' as "sub-company", '-' as "sub-business-id",
       ts.name as "service-name",
        replace(
          replace(
            replace(
             replace(array_to_string(ts."transport-type",','),
             'road' , 'Tieliikenne')
             ,'sea', 'Merenkulku')
             ,'aviation', 'Ilmailu')
             ,'rail', 'Raideliikenne') as "transport-type"

  FROM "transport-operator" op, "transport-service" ts
 WHERE op.id = ts."transport-operator-id"
   AND ts."sub-type" = :subtype::transport_service_subtype
   AND op."deleted?" = FALSE
   AND ts.published IS NOT NULL
   AND (ts.companies = '{}' OR ts.companies = '{"(,)"}' OR ts.companies IS NULL)

UNION

SELECT op.name as "operator", op."business-id" as "business-id", c.name as "sub-company", c."business-id" as "sub-business-id",
       ts.name as "service-name",
        replace(
          replace(
            replace(
             replace(array_to_string(ts."transport-type",','),
             'road' , 'Tieliikenne')
             ,'sea', 'Merenkulku')
             ,'aviation', 'Ilmailu')
             ,'rail', 'Raideliikenne') as "transport-type"

  FROM "transport-operator" op, "transport-service" ts, unnest(ts.companies) with ordinality c
 WHERE op.id = ts."transport-operator-id"
   AND ts."sub-type" = :subtype::transport_service_subtype
   AND op."deleted?" = FALSE
   AND ts.published IS NOT NULL
   AND ts.companies  IS NOT NULL
   AND ts.companies != '{}'
   AND ts.companies != '{"(,)"}'
   AND ts.id NOT IN (select sc."transport-service-id" FROM service_company sc WHERE sc.companies != '{}')

 UNION

 SELECT * FROM (
   SELECT op.name as "operator", op."business-id" as "business-id", c.name as "sub-company", c."business-id" as "sub-business-id",
         ts.name as "service-name",
          replace(
            replace(
              replace(
               replace(array_to_string(ts."transport-type",','),
               'road' , 'Tieliikenne')
               ,'sea', 'Merenkulku')
               ,'aviation', 'Ilmailu')
               ,'rail', 'Raideliikenne') as "transport-type"

     FROM "transport-operator" op, "transport-service" ts, service_company sc, unnest(sc.companies) with ordinality c
    WHERE op.id = ts."transport-operator-id"
      AND ts."sub-type" = :subtype::transport_service_subtype
      AND op."deleted?" = FALSE
      AND ts.published IS NOT NULL
      AND sc."transport-service-id" = ts.id
    ORDER BY op.id ASC)
 AS x;

--name: fetch-operators-unpublished-services
SELECT x.*,
       (SELECT string_agg(CONCAT("name", ' (Muokattu: ',
                                 to_char(COALESCE("modified", "created"), 'mm.dd.yyyy hh24\:mi\:ss')::VARCHAR, ')'),
                          E',\n' ORDER BY name, modified)
          FROM "transport-service"
         WHERE "transport-operator-id" = x."op-id"
           AND published IS NULL) AS services

  FROM (SELECT op.name,
               op.id AS "op-id",
               (SELECT COALESCE(NULLIF(op.phone, ''), NULLIF(ts."contact-phone", ''), ts."contact-gsm") AS phone
                  FROM "transport-service" ts
                 WHERE "transport-operator-id" = op.id
                 LIMIT 1),
               COALESCE(NULLIF(op.email, ''), u.email) email,
               (SELECT COUNT(*)
                  FROM "transport-service" ts
                 WHERE ts."transport-operator-id" = op.id
                   AND ts.published IS NULL) AS "unpublished-services-count"

          FROM "transport-operator" op
          JOIN "user" u ON u.name = (SELECT usr.name
                                     FROM "transport-operator" t
                                            JOIN member m ON m.group_id = t."ckan-group-id"
                                            JOIN "user" usr ON m.table_id = usr.id
                                     WHERE t.id = op.id
                                     ORDER BY usr.id
                                     LIMIT 1)
         ORDER BY op.name ASC) x
 WHERE "unpublished-services-count" > 0;

-- name: fetch-operators-with-payment-services
SELECT top.name AS "operator", top."business-id" AS "business-id",
ts.name AS "service-name", ts."contact-address" AS "service-address",
CASE ts."sub-type"::TEXT
	WHEN 'terminal' THEN  'Asemat, satamat ja muut terminaalit'
	WHEN 'taxi' THEN  'Taksiliikenne (tieliikenne)'
	WHEN 'request' THEN  'Tilausliikenne ja muu kutsuun perustuva liikenne'
	WHEN 'rentals' THEN  'Liikennevälineiden vuokrauspalvelut ja kaupalliset yhteiskäyttöpalvelut'
	WHEN 'schedule' THEN  'Säännöllinen aikataulun mukainen liikenne'
	WHEN 'parking' THEN  'Yleiset kaupalliset pysäköintipalvelut'
	WHEN 'brokerage' THEN  'Välityspalvelut'
	ELSE 'Tuntematon'
END	AS "service-type",
(eid."external-interface").url AS "url", eid.format AS "format", eid.license AS licence
  FROM "transport-operator" top
  JOIN "transport-service" ts ON top.id = ts."transport-operator-id" AND ts.published IS NOT NULL
  JOIN "external-interface-description" eid ON ts.id = eid."transport-service-id"
 WHERE 'payment-interface' = ANY(eid."data-content");

-- name: all-registered-companies
SELECT DISTINCT ON (ac."business-id") "business-id",
       ac."business-name" AS name
  FROM "all-companies" ac;

-- name: monthly-registered-companies
-- returns a cumulative sum of companies, defined as distinct business-id's of "all-companies", created up until the row's month.
SELECT ac.c as month,
       sum(count(ac.providing)) over (order by c) as "sum-providing",
       sum(count(ac.participating)) over (order by c) as "sum-participating",
       sum(count(ac.participating) + count(ac.providing)) over (order by c) as sum
  FROM
       (SELECT DISTINCT ON ("business-id") "business-id",
              to_char(created, 'YYYY-MM') as c,
              CASE source
                WHEN 'transport_operator' THEN 'providing'
                END AS providing,
              CASE source
                WHEN 'transport_service' THEN 'participating'
                WHEN 'service_company' THEN 'participating'
                WHEN 'associated_service_operator' THEN 'participating'
                END AS participating
         FROM "all-companies") ac
GROUP BY month
ORDER BY month;

-- name: tertile-registered-companies
-- returns a cumulative sum of companies, defined as distinct business-id's of operators, created up until the row's tertile.
SELECT
       ac.tertile,
       sum(count(ac."business-id")) over (ORDER BY ac.tertile) AS sum,
       sum(count(ac.providing)) over (ORDER BY ac.tertile) AS "sum-providing",
       sum(count(ac.participating)) over (ORDER BY ac.tertile) AS "sum-participating"
   FROM
        (SELECT DISTINCT ON ("business-id") "business-id",
                concat(to_char(date_trunc('year', created), 'YYYY'), ' ',floor(extract(month FROM created)::int / 4.00001) + 1 , '/3 ') as tertile,
                CASE source
                  WHEN 'transport_operator' THEN 'providing'
                  END AS providing,
                CASE source
                  WHEN 'transport_service' THEN 'participating'
                  WHEN 'service_company' THEN 'participating'
                  WHEN 'associated_service_operator' THEN 'participating'
                  END AS participating
           FROM "all-companies") ac
group by tertile
order by tertile;

-- name: operator-type-distribution
-- returns a distrubution of transport-service sub-types among all transport services
SELECT ac."sub-type" as "sub-type", count(ac."business-id") as count
  FROM "all-companies" ac
 GROUP BY ac."sub-type"
 ORDER BY ac."sub-type" ASC;

-- subquery name legend is: stq = subtype grouping query, biq = business-id grouping query
-- 
-- name: monthly-producer-types-and-counts
 SELECT to_char(ac.created, 'YYYY-MM') as month,
        count(ac."sub-type") as sum,
        ac."sub-type"
   FROM
        "all-companies" ac
 GROUP BY month, ac."sub-type"
 ORDER BY month, ac."sub-type";

-- name: tertile-producer-types-and-counts
SELECT count(ac."sub-type") as sum,
       ac."sub-type" as "sub-type",
       concat(to_char(date_trunc('year', ac.created), 'YYYY'), ' ', (floor(extract(month FROM ac.created)::int / 4.00001)) + 1 , '/3 ') as tertile
  FROM
       "all-companies" ac
 GROUP BY tertile, ac."sub-type"
 ORDER BY tertile, ac."sub-type";

-- name: fetch-successfull-netex-conversion-interfaces-for-admin-with-max-date
-- Get interfaces that converts gtfs/kalkati to netex successfully and add traffic last date
-- This same query is in admin.sql without the max_date parameter - this is done because of last prod and smaller risks.
WITH interfaces AS (
    SELECT DISTINCT ON (e.id) e.id as "interface-id",
                              p.created as ladattu,
                              p.id as "package-id",
                              e."transport-service-id",
                              e."external-interface",
                              e."data-content",
                              e.format,
                              (upper(gtfs_package_date_range(p.id)))::date as max_date
      FROM "external-interface-description" e
           INNER JOIN gtfs_package p on p."external-interface-description-id" = e.id
     WHERE ('GTFS' = ANY (e.format) OR 'Kalkati.net' = ANY (e.format))
       AND 'route-and-schedule' = ANY (e."data-content")
     ORDER BY e.id, p.id desc
)
SELECT TRIM((eid."external-interface").url) as "interface-url",
       to_char(eid.max_date, 'DD.MM.YYYY') as "max-date",
       top.name as "top-name",
       ts.name as "service-name",
       array_to_string(eid."data-content", ',') as "interface-content",
       TRIM(top.email) as "operator-email",
       TRIM(ts."contact-email") as "service-email",
       array_to_string(array_agg(u.email), '|') as "user-email",
       eid.format[1] as "interface-format"
  FROM interfaces eid,
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
 GROUP BY top.name, ts.name, eid.format[1], eid."external-interface", eid."data-content", ts."contact-email", top.email,
       eid.max_date
 ORDER BY eid.max_date ASC;

