CREATE TYPE feature_type AS ENUM (
    'maintenance-break-sea-route'
    );

COMMENT ON TYPE feature_type IS
E'Enumerates possible ids of dynamic feature flags';

CREATE TABLE feature_variation(
    id SERIAL PRIMARY KEY,
    feature feature_type UNIQUE NOT NULL,
    value INTEGER DEFAULT NULL,
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE feature_variation IS
E'Stores feature flags which can be toggled dynamically when a more dynamic variation than build-time configuration is needed';

CREATE OR REPLACE FUNCTION update_modified() RETURNS TRIGGER AS $$
BEGIN
    NEW.modified = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER feature_timestamp
    BEFORE INSERT OR UPDATE ON feature_variation
    FOR EACH ROW
EXECUTE PROCEDURE update_modified();

-- First feature flag
INSERT INTO feature_variation (feature, value)
VALUES ('maintenance-break-sea-route'::feature_type, NULL);
