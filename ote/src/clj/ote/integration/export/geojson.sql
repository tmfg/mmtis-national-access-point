-- name: fetch-operation-area-for-service
-- Fetch the operation area for a transportation service as a GeoJSON document
SELECT ST_AsGeoJSON(ST_SetSRID(location,4326)) as geojson, "primary?"
  FROM operation_area
 WHERE "transport-service-id" = :transport-service-id;
