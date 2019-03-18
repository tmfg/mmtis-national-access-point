--- Add Spatial indices for increasing search performance on spatial joins
CREATE INDEX operation_area_gix ON operation_area USING GIST (location);
CREATE INDEX finnish_municipalities_gix ON finnish_municipalities USING GIST (location);
CREATE INDEX finnish_regions_gix ON finnish_regions USING GIST (location);
CREATE INDEX country_gix ON country USING GIST (location);
CREATE INDEX continent_gix ON continent USING GIST (location);
CREATE INDEX finnish_postal_codes_gix ON finnish_postal_codes USING GIST (location);
