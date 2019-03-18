-- DROP views to allow updating depending columns
DROP VIEW operation_area_geojson RESTRICT;

--- Update SRIDs to location columns to make Spatial joins more efficient
SELECT UpdateGeometrySRID('operation_area','location',4326);

-- Rebuild operation_area_geojson
CREATE VIEW operation_area_geojson AS
 SELECT oa.*, ST_AsGeoJSON(oa.location) AS "location-geojson"
   FROM operation_area oa;
