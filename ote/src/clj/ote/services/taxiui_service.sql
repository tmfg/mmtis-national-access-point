-- name: insert-price-information!
INSERT INTO taxi_service_prices(service_id, start_price_daytime, start_price_nighttime, start_price_weekend,
                                price_per_minute, price_per_kilometer, accessibility_tool_wheelchair,
                                accessibility_tool_walker, cargo_large_luggage, timestamp)
VALUES (:service-id,
        :start-price-daytime,
        :start-price-nighttime,
        :start-price-weekend,
        :price-per-minute,
        :price-per-kilometer,
        :accessibility-tool-wheelchair,
        :accessibility-tool-walker,
        :cargo-large-luggage,
        NOW());

-- name: select-price-information
SELECT id,
       timestamp,
       start_price_daytime,
       start_price_nighttime,
       start_price_weekend,
       price_per_minute,
       price_per_kilometer,
       accessibility_tool_wheelchair,
       accessibility_tool_walker,
       cargo_large_luggage
  FROM taxi_service_prices
 WHERE service_id = :service-id
 ORDER BY timestamp DESC
 LIMIT 1;

-- name: list-pricing-statistics
SELECT *
  FROM list_taxi_statistics(:primary-column, :primary-direction, :secondary-column, :secondary-direction)
 WHERE (CASE WHEN EXTRACT(YEAR FROM (:age-filter)::interval) > 0
             THEN timestamp < NOW() - INTERVAL '1 year'
             ELSE timestamp > NOW() - (:age-filter)::INTERVAL
        END)
   AND name ILIKE :name-filter
   AND (CASE WHEN (:area-filter)::text IS NOT NULL AND (:area-filter)::text = '' IS NOT TRUE
             THEN EXISTS (SELECT FROM unnest("operating-areas") areas WHERE areas = :area-filter)
             ELSE TRUE
        END);

-- name: list-operating-areas
SELECT DISTINCT (oa_d.text) AS place
  FROM operation_area oa,
       UNNEST(description) oa_d
 WHERE oa_d.text ILIKE :term
 ORDER BY place;