-- name: insert-price-information!
INSERT INTO taxi_service_prices(service_id, start_price_daytime, start_price_nighttime, start_price_weekend,
                                price_per_minute, price_per_kilometer, accessibility_service_stairs,
                                accessibility_service_stretchers, accessibility_service_fare, timestamp)
VALUES (:service-id,
        :start-price-daytime,
        :start-price-nighttime,
        :start-price-weekend,
        :price-per-minute,
        :price-per-kilometer,
        :accessibility-service-stairs,
        :accessibility-service-stretchers,
        :accessibility-service-fare,
        NOW());

-- name: select-price-information
SELECT id,
       timestamp,
       start_price_daytime,
       start_price_nighttime,
       start_price_weekend,
       price_per_minute,
       price_per_kilometer,
       accessibility_service_stairs,
       accessibility_service_stretchers,
       accessibility_service_fare
  FROM taxi_service_prices
 WHERE service_id = :service-id
 ORDER BY timestamp DESC
 LIMIT 1;

-- name: list-pricing-statistics
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
  FROM (SELECT tsp.id,
               o.id AS "operator-id",
               s.id AS "service-id",
               CONCAT(o."name", '/', s."name") AS "name",
               tsp.timestamp AS "timestamp",
               tsp.start_price_daytime AS "start-price-daytime",
               tsp.start_price_nighttime AS "start-price-nighttime",
               tsp.start_price_weekend AS "start-price-weekend",
               tsp.price_per_minute AS "price-per-minute",
               tsp.price_per_kilometer AS "price-per-kilometer",
               tsp."accessibility_service_stairs" AS "accessibility-service-stairs",
               tsp."accessibility_service_stretchers" AS "accessibility-service-stretchers",
               tsp."accessibility_service_fare" AS "accessibility-service-fare",
               tsp."approved?" AS "approved?",
               tsp."approved-by" AS "approved-by",
               (tsp.start_price_daytime + (tsp.price_per_minute * 15) + (tsp.price_per_kilometer * 10)) AS "example-trip",
               row_number() OVER (PARTITION BY service_id ORDER BY timestamp desc)
          FROM taxi_service_prices tsp
          JOIN "transport-service" s ON tsp."service_id" = s."id"
          JOIN "transport-operator" o ON s."transport-operator-id" = o."id"
         WHERE tsp."approved?" IS NOT NULL) prices  -- NOT NULL = approved
 WHERE prices."row_number" = 1;

-- name: list-service-pricing-statistics
SELECT id,
       timestamp,
       start_price_daytime as "start-price-daytime",
       start_price_nighttime as "start-price-nighttime",
       start_price_weekend as "start-price-weekend",
       price_per_minute as "price-per-minute",
       price_per_kilometer as "price-per-kilometer",
       accessibility_service_stairs as "accessibility-service-stairs",
       accessibility_service_stretchers as "accessibility-service-stretchers",
       accessibility_service_fare as "accessibility-service-fare"
  FROM taxi_service_prices
 WHERE service_id = :service-id
   AND "approved?" IS NOT NULL
 ORDER BY timestamp DESC
 LIMIT 1;

-- name: list-operating-areas
SELECT DISTINCT (oa_d.text) AS place
  FROM operation_area oa,
       UNNEST(description) oa_d
 WHERE oa_d.text ILIKE :term
 ORDER BY place;

-- name: list-service-summaries
SELECT o."id" AS "operator-id",
       s."id" AS "service-id",
       o."name" AS "operator-name",
       s."name" AS "service-name",
       st.timestamp,
       (st.start_price_daytime + (st.price_per_minute * 15) + (st.price_per_kilometer * 10)) AS "example-trip",
       st."price_per_kilometer" AS "price-per-kilometer",
       st."price_per_minute" AS "price-per-minute",
       (SELECT array_agg(oa_d.text)
          FROM operation_area oa,
               unnest(description) AS oa_d
         WHERE oa."transport-service-id" = st."service_id"
           AND "primary?" = TRUE) AS "operating-areas"
  FROM "transport-operator" o
           LEFT JOIN "transport-service" s ON s."transport-operator-id" = o.id
           LEFT JOIN (SELECT DISTINCT ON (service_id) *
                        FROM taxi_service_prices
                       ORDER BY service_id) st ON st."service_id" = s."id"
 WHERE o."id" IN (:operator-ids)
   AND s."sub-type" = 'taxi'
 ORDER BY "operator-id", "service-id";

-- name: list-unapproved-prices
SELECT *,
       (SELECT array_agg(oa.description[1].text)
          FROM operation_area oa
         WHERE oa."transport-service-id" = prices."service-id") AS "operating-areas"
  FROM (SELECT tsp.id,
               o.id AS "operator-id",
               s.id AS "service-id",
               CONCAT(o."name", '/', s."name") AS "name",
               tsp.timestamp AS "timestamp",
               tsp.start_price_daytime AS "start-price-daytime",
               tsp.start_price_nighttime AS "start-price-nighttime",
               tsp.start_price_weekend AS "start-price-weekend",
               tsp.price_per_minute AS "price-per-minute",
               tsp.price_per_kilometer AS "price-per-kilometer",
               tsp."accessibility_service_stairs" AS "accessibility-service-stairs",
               tsp."accessibility_service_stretchers" AS "accessibility-service-stretchers",
               tsp."accessibility_service_fare" AS "accessibility-service-fare",
               tsp."approved?" AS "approved?",
               tsp."approved-by" AS "approved-by",
               row_number() OVER (PARTITION BY service_id ORDER BY timestamp desc)
          FROM taxi_service_prices tsp
          JOIN "transport-service" s ON tsp."service_id" = s."id"
          JOIN "transport-operator" o ON s."transport-operator-id" = o."id"
         WHERE tsp."approved?" IS NULL) prices
 WHERE prices.row_number = 1
 ORDER BY "operator-id", "service-id";

-- name: update-approved-status!
  WITH pricing_to_approve AS (
      SELECT "service_id",
             "timestamp"
        FROM taxi_service_prices
       WHERE id = :pricing-id
  ),
       all_pricings AS (
      SELECT ap.id
        FROM taxi_service_prices ap,
             pricing_to_approve
       WHERE ap.service_id = pricing_to_approve.service_id
         AND ap."timestamp" <= pricing_to_approve."timestamp"
         AND ap."approved?" IS NULL
  )
UPDATE taxi_service_prices tsp
   SET "approved-by" = :user-id,
       "approved?"   = NOW()
  FROM all_pricings ap
 WHERE tsp.id IN (ap.id);