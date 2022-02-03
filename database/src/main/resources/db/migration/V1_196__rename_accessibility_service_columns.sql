-- Traficom requires different set of accessibility pricings to be given than what was originally planned. Renaming
-- fields was agreed to be enough.

ALTER TABLE taxi_service_prices RENAME COLUMN accessibility_tool_wheelchair TO accessibility_service_stairs;
ALTER TABLE taxi_service_prices RENAME COLUMN accessibility_tool_walker TO accessibility_service_stretchers;
ALTER TABLE taxi_service_prices RENAME COLUMN cargo_large_luggage TO accessibility_service_fare;