-- Make the taxi_service_prices table easier to query by exploding the identifier enum to individual price columns.


-- replace price and identifier columns with individual pricing columns
ALTER TABLE taxi_service_prices
    DROP COLUMN identifier,
    DROP COLUMN price,
    ADD COLUMN start_price_daytime   NUMERIC NOT NULL DEFAULT 0,
    ADD COLUMN start_price_nighttime NUMERIC NOT NULL DEFAULT 0,
    ADD COLUMN start_price_weekend   NUMERIC NOT NULL DEFAULT 0,
    ADD COLUMN price_per_minute      NUMERIC NOT NULL DEFAULT 0,
    ADD COLUMN price_per_kilometer   NUMERIC NOT NULL DEFAULT 0;
