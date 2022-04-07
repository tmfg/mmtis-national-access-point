-- Fixes faulty constraint preventing updating/deleting services
ALTER TABLE taxi_service_prices
    DROP CONSTRAINT taxi_service_prices_service_id_fkey
    , ADD  CONSTRAINT taxi_service_prices_service_id_fkey
    FOREIGN KEY (service_id) REFERENCES "transport-service" (id) ON DELETE CASCADE;
