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
SELECT *
  FROM list_taxi_statistics(:primary-column, :primary-direction, :secondary-column, :secondary-direction, true)
 WHERE (CASE WHEN EXTRACT(YEAR FROM (:age-filter)::interval) > 0
             THEN timestamp < NOW() - INTERVAL '1 year'
             ELSE timestamp > NOW() - (:age-filter)::INTERVAL
        END)
   AND name ILIKE :name-filter
   AND (CASE WHEN (:area-filter)::text IS NOT NULL AND (:area-filter = '') IS NOT TRUE
             THEN EXISTS (SELECT FROM unnest("operating-areas") areas WHERE areas = :area-filter)
             ELSE TRUE
        END);

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
SELECT *
  FROM list_taxi_statistics(null, null, null, null, false)
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