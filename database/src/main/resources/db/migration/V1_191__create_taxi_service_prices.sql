CREATE TYPE taxi_service_identifier AS ENUM (
    'start_price_daytime',
    'start_price_nighttime',
    'start_price_weekend',
    'price_per_minute',
    'price_per_kilometer');

CREATE TABLE taxi_service_prices
(
    id         SERIAL PRIMARY KEY,
    service_id INT                     NOT NULL,
    identifier taxi_service_identifier NOT NULL,
    price      NUMERIC                 NOT NULL,
    timestamp  TIMESTAMP               NOT NULL,
    FOREIGN KEY (service_id) REFERENCES "transport-service" (id)
);

CREATE UNIQUE INDEX taxi_service_prices_id_uindex
    ON taxi_service_prices (id);