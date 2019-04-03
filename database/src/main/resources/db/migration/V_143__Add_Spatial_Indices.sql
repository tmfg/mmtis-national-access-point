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

-- Create a table for spatial search
CREATE TABLE "spatial-relations-places" (
 "search-area" VARCHAR(64),
 "place-id" VARCHAR(64),
 "operation-area-search-term" VARCHAR(64),
 "search-area-size" double,
 "matching-area-size" double,
 "mirrored" boolean default false
);

CREATE INDEX "spatial-relations-search-indx" on "spatial-relations-places" ("search-area");


-- Migrate existing operation areas to the table.
-- don't check matching types, because we don't want to link postal codes to postal codes, counties to counties etc
INSERT INTO "spatial-relations-places" ("search-area", "place-id", "operation-area-search-term", "search-area-size", "matching-area-size")
SELECT
    pl1.id,
    pl2.id,
    lower(pl2.namefin),
    ST_Area(pl1.location),
    ST_Area(ST_Intersection(pl1.location, pl2.location))
FROM places pl1,
     places pl2
WHERE (ST_Intersects(pl1.location, pl2.location)
 -- Match postal code areas to municipalities and regions
 AND (pl1.type = 'finnish-postal'
     AND pl2.type in ('finnish-municipality', 'finnish-region')
 -- Match municipalities to regions
 OR (pl1.type = 'finnish-municipality'
     AND pl2.type = 'finnish-region'))
 -- Match countries to continents
 OR (pl1.type = 'country' AND pl2.type = 'continent'))
 -- We know finnish areas are inside Finland
 OR (pl1.type in ('finnish-postal', 'finnish-municipality', 'finnish-region')
     AND pl2.namefin = 'Suomi')
 -- And that they are inside Europe
 OR (pl1.type in ('finnish-postal', 'finnish-municipality', 'finnish-region')
     AND pl2.namefin = 'Eurooppa');

-- Clean matches that are deemed not valid. If matching area is significantly smaller than the search area, consider the match invalid
-- However, we know matches to Europe and Finland must be correct nevermind their calculated match quality. For instance, the geometry for Europe does not cover all of the
-- islands in southwestern archipelago. Yet we know the islands _are_ in Europe.
DELETE FROM "spatial-relations-places"
       WHERE ("matching-area-size" / "search-area-size") < 0.1
       AND "place-id" NOT IN ('continent-EU', 'country-FI');

-- Mirror data in spatial-relations-places so searches can be done from bigger areas to smaller areas
INSERT INTO "spatial-relations-places" ("search-area", "place-id", "operation-area-search-term", "search-area-size", "matching-area-size", "mirrored")
SELECT
    pl2.id,
    pl1.id,
    lower(pl1.namefin),
    ST_Area(pl2.location),
    ST_Area(ST_Intersection(pl1.location, pl2.location)),
    true
  FROM "spatial-relations-places" sr,
       "places" pl1,
       "places" pl2
 WHERE sr."search-area" = pl1.id
   AND sr."place-id" = pl2.id
   AND sr.mirrored = false;

-- Match all search terms to themselves so we don't need to join backwards
INSERT INTO "spatial-relations-places" ("search-area", "place-id", "operation-area-search-term", "search-area-size", "matching-area-size")
SELECT
    pl1.id,
    pl1.id,
    lower(pl1.namefin),
    ST_Area(pl1.location),
    ST_Area(pl1.location)
  FROM "places" pl1;

-- Prefill search table for custom areas
CREATE TABLE "spatial-relations-custom-areas" (
 "search-area" VARCHAR(64),
 "operation-area-id" integer
 "search-area-size" float,
 "matching-area-size" float
);

CREATE INDEX "places-to-custom-operation-area-indx" on "spatial-relations-custom-areas" ("search-area");

INSERT INTO "spatial-relations-custom-areas" ("search-area", "operation-area-id", "search-area-size", "matching-area-size")
SELECT
    pl.id,
    oa.id,
    ST_Area(pl.location),
    -- Consider geometry types without surface area to fill the whole search area so they won't be discarded as invalid matches. Otherwise measure the area of the intersection.
    -- Perhaps it should be the area of the smallest search area instead? Or some fixed size?
    CASE
     WHEN ST_GeometryType(oa.location) in ('ST_Point', 'ST_Linestring')
          THEN ST_Area(pl.location)
          ELSE ST_Area(ST_Intersection(ST_MakeValid(oa.location), ST_MakeValid(pl.location)))
    END
FROM operation_area oa
 JOIN places pl ON pl.location && oa.location -- help ST_Relate by using spatial index in oa.location
               AND ST_Relate(ST_MakeValid(oa.location), ST_MakeValid(pl.location), 'T********')
-- No identifier in operation_area to tell us which are custom areas
WHERE oa.id NOT IN (SELECT oa.id FROM operation_area oa JOIN places pl ON ST_Equals(oa.location, pl.location));
