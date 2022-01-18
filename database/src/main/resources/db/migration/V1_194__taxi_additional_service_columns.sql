-- Customer wants more service pricing fields.

ALTER TABLE taxi_service_prices
    ADD COLUMN accessibility_tool_wheelchair NUMERIC NOT NULL DEFAULT 0,
    ADD COLUMN accessibility_tool_walker     NUMERIC NOT NULL DEFAULT 0,
    ADD COLUMN cargo_large_luggage           NUMERIC NOT NULL DEFAULT 0;
