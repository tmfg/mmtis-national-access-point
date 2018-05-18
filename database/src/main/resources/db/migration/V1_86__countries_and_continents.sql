ALTER TABLE country
  ADD COLUMN nameeng TEXT;

CREATE TABLE continent (
  code CHAR(2),
  namefin TEXT,
  nameswe TEXT,
  nameeng TEXT,
  location geometry
);

CREATE OR REPLACE VIEW places AS
 SELECT CONCAT('finnish-municipality-', natcode) AS id,
        'finnish-municipality' as type,
        namefin, nameswe, ST_FlipCoordinates(location) as location
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
        namefin, nameswe, location
   FROM country
UNION ALL
 SELECT CONCAT('continent-',code) as id,
        'continent' AS type,
        namefin, nameswe, location
   FROM continent;
