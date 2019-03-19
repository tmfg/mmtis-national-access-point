-- DROP views to allow updating depending columns
DROP VIEW places RESTRICT;

--- Update SRIDs to location columns to make Spatial joins more efficient
SELECT UpdateGeometrySRID('finnish_municipalities','location',4326);
SELECT UpdateGeometrySRID('finnish_postal_codes','location',4326);
SELECT UpdateGeometrySRID('finnish_regions','location',4326);
SELECT UpdateGeometrySRID('country','location',4326);
SELECT UpdateGeometrySRID('continent','location',4326);

--- Rephrase view so that SRIDs are set to 4326.
CREATE OR REPLACE VIEW places AS
 SELECT CONCAT('finnish-municipality-', natcode) AS id,
        'finnish-municipality' as type,
        namefin,
        nameswe,
        ST_FlipCoordinates(location) as location
   FROM finnish_municipalities
UNION ALL
 SELECT CONCAT('finnish-postal-',posti_alue) as id,
        'finnish-postal' as type,
        CONCAT(posti_alue,' ',nimi) AS namefin,
        CONCAT(posti_alue,' ',namn) AS nameswe,
        location
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
