--- Add Spatial indices for increasing search performance on spatial joins
CREATE INDEX operation_area_gix ON operation_area USING GIST (location);
CREATE INDEX finnish_municipalities_gix ON finnish_municipalities USING GIST (location);
CREATE INDEX finnish_regions_gix ON finnish_regions USING GIST (location);
CREATE INDEX country_gix ON country USING GIST (location);
CREATE INDEX continent_gix ON continent USING GIST (location);
CREATE INDEX finnish_postal_codes_gix ON finnish_postal_codes USING GIST (location);

-- DROP views to allow updating depending columns
DROP VIEW operation_area_geojson RESTRICT;
DROP VIEW places RESTRICT;

--- Update SRIDs to location columns to make Spatial joins more efficient
--- Data is already in this projection
SELECT UpdateGeometrySRID('operation_area','location',4326);
SELECT UpdateGeometrySRID('finnish_regions','location',4326);
SELECT UpdateGeometrySRID('country','location',4326);
SELECT UpdateGeometrySRID('continent','location',4326);

-- Rebuild operation_area_geojson
CREATE VIEW operation_area_geojson AS
 SELECT oa.*, ST_AsGeoJSON(oa.location) AS "location-geojson"
   FROM operation_area oa;

CREATE OR REPLACE FUNCTION ote_simplify(geometry) RETURNS geometry
  AS 'SELECT ST_SetSRID(ST_MakeValid($1), 4326);'
  LANGUAGE SQL
  IMMUTABLE
  RETURNS NULL ON NULL INPUT;

-- Create simplified version of operation-area.location. 0.01 is the tolerance for the algorithm. Shrink the operation area to make sure
-- new overlaps have not been created.
ALTER TABLE "operation_area" ADD COLUMN "simplified-location" GEOMETRY;
UPDATE "operation_area" SET "simplified-location" = ote_simplify(location);
SELECT UpdateGeometrySRID('operation_area', 'simplified-location', 4326);
CREATE INDEX "operation-area-simplified-gix" ON "operation_area" USING GIST("simplified-location");


--- Recreate view
CREATE OR REPLACE VIEW places AS
 SELECT CONCAT('finnish-municipality-', natcode) AS id,
        'finnish-municipality' as type,
        namefin,
        nameswe,
        ST_FlipCoordinates(ST_SetSRID(location, 4326)) as location
   FROM finnish_municipalities
UNION ALL
 SELECT CONCAT('finnish-postal-',posti_alue) as id,
        'finnish-postal' as type,
        CONCAT(posti_alue,' ',nimi) AS namefin,
        CONCAT(posti_alue,' ',namn) AS nameswe,
        ST_SetSRID(location, 4326)
   FROM finnish_postal_codes
UNION ALL
 SELECT CONCAT('finnish-region-',numero) as id,
        'finnish-region' AS type,
        nimi AS namefin,
        '' AS nameswe,
        location
   FROM finnish_regions
UNION ALL
 SELECT CONCAT('country-',code) as id,
        'country' AS type,
        namefin,
        nameswe,
        location
   FROM country
UNION ALL
 SELECT CONCAT('continent-',code) as id,
        'continent' AS type,
        namefin,
        nameswe,
        location
   FROM continent;

-- Table intended to store unique place information
CREATE TABLE "spatial-search-areas" ();

-- Operation areas not in known spatial nomenclature
CREATE TABLE "custom-operation-areas" (
 "id" INTEGER PRIMARY KEY,
 "transport-service-id" integer,
 "description" localized_text[],
 "location" geometry(Geometry, 4326),
 "primary?" boolean
);

CREATE INDEX "custom-operation-areas-indx" on "custom-operation-areas" (id);

-- Create a table for spatial search
CREATE TABLE "spatial-intersections" (
 "place-id" VARCHAR(64),
 "operation-area-id" INTEGER
);

CREATE INDEX "spatial-intersections-indx" on "spatial-intersections" ("place-id");

-- Migrate existing operation areas to the table
INSERT INTO "spatial-intersections" ("place-id", "operation-area-id")
SELECT pl.id, oa.id FROM places pl JOIN operation_area oa ON ST_Intersects(ST_MakeValid(pl.location), ST_MakeValid(oa.location)) AND NOT ST_Touches(ST_MakeValid(pl.location), ST_MakeValid(oa.location)); 
