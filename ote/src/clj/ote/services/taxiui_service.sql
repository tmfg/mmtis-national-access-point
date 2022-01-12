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