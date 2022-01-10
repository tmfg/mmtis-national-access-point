-- the enum happened to use a name which already existed, which causes inserts not to work. Thankfully the existing enum
-- doesn't get overwritten, the redefinition in previous migration just didn't work.
CREATE TYPE taxi_pricing_category AS ENUM (
    'start_price_daytime',
    'start_price_nighttime',
    'start_price_weekend',
    'price_per_minute',
    'price_per_kilometer');

-- Set column type. Also remove the null constraint to enable conversion.
ALTER TABLE taxi_service_prices
    ALTER COLUMN identifier DROP NOT NULL,
    ALTER COLUMN identifier TYPE taxi_pricing_category USING NULL;

-- This may feel dangerous, but won't affect anything important since this hasn't been run in production at this point
-- so there's no real data to remove.
DELETE FROM taxi_service_prices WHERE identifier IS NULL;

-- Restore null constraint.
ALTER TABLE taxi_service_prices
    ALTER COLUMN identifier SET NOT NULL;