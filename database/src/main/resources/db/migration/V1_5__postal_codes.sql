-- Create table with the same structure as tilastokeskus export
CREATE TABLE finnish_postal_codes (
  posti_alue TEXT,
  nimi TEXT,
  namn TEXT,
  location GEOMETRY
);

CREATE INDEX finnish_postal_codes_code_idx ON finnish_postal_codes (posti_alue);
CREATE INDEX finnish_postal_codes_name_idx ON finnish_postal_codes (nimi);

COMMENT ON TABLE finnish_postal_codes IS
E'Data loaded from Tilastokeskus open data. Contains all postal codes with their names and geometries';

CREATE VIEW places AS
 SELECT CONCAT('finnish-municipality-', natcode) AS id,
        'finnish-municipality' as type,
        namefin, nameswe, location
   FROM finnish_municipalities
UNION ALL
 SELECT CONCAT('finnish-postal-',posti_alue) as id,
        'finnish-postal' as type,
        CONCAT(posti_alue,' ',nimi) AS namefin,
        CONCAT(posti_alue,' ',namn) as nameswe,
        location
   FROM finnish_postal_codes;
