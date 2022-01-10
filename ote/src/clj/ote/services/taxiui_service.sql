-- name: insert-price-information!
INSERT INTO taxi_service_prices(service_id, identifier, price, timestamp)
VALUES (:service-id, CAST(:identifier AS taxi_pricing_category), :price, NOW());

-- name: select-price-information
SELECT DISTINCT ON (identifier) price, timestamp, identifier
  FROM taxi_service_prices
 WHERE service_id = :service-id
 ORDER BY identifier, timestamp DESC;