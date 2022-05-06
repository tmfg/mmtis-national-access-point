-- add index for approval state to support statistics queries
CREATE INDEX taxi_service_prices_approved ON taxi_service_prices ("approved?");