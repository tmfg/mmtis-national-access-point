-- name: insert-price-information!
INSERT INTO taxi_service_prices(service_id, start_price_daytime, start_price_nighttime, start_price_weekend,
                                price_per_minute, price_per_kilometer, timestamp)
VALUES (:service-id,
        :start-price-daytime,
        :start-price-nighttime,
        :start-price-weekend,
        :price-per-minute,
        :price-per-kilometer,
        NOW());

-- name: select-price-information
SELECT id,
       start_price_daytime,
       start_price_nighttime,
       start_price_weekend,
       price_per_minute,
       price_per_kilometer,
       timestamp
  FROM taxi_service_prices
 WHERE service_id = :service-id
 ORDER BY timestamp DESC
 LIMIT 1;

-- name: fetch-services-with-prices
SELECT DISTINCT service_id FROM taxi_service_prices;

-- name: list-pricing-statistics
SELECT o.id AS "operator-id",
       s.id AS "service-id",
       CONCAT(o.name, '/', s.name) AS name,
       l.timestamp AS timestamp,
       l.start_price_daytime AS "start-price-daytime",
       l.start_price_nighttime AS "start-price-nighttime",
       l.start_price_weekend AS "start-price-weekend",
       l.price_per_minute AS "price-per-minute",
       l.price_per_kilometer AS "price-per-kilometer"
  FROM list_taxi_pricing_statistics(:column, :direction) l
  JOIN "transport-service" s ON l.service_id = s.id
  JOIN "transport-operator" o ON s."transport-operator-id" = o.id;