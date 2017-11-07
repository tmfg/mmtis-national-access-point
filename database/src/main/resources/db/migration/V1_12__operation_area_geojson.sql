-- View that shows operation area geometry as geojson
CREATE VIEW operation_area_geojson AS
 SELECT oa.*, ST_AsGeoJSON(oa.location) AS "location-geojson"
   FROM operation_area oa;
