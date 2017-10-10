-- Add tables for different types of "places" that can be searched with the place search

CREATE TABLE finnish_municipalities (
  namefin text,
  nameswe text,
  location geometry,
  natcode text,
  gml_id numeric(15,0)
);

COMMENT ON TABLE finnish_municipalities IS
E'Data loaded from Kuntaliitto open data. Contains geometries and names (fi/sv) of all\n
Finnish municipalities. Updated yearly.\n\n
Data is transferred to PostgreSQL from ESRI shapefile with the ogr2ogr tool.';
