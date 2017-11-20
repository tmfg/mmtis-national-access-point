-- name: fetch-operation-area-for-service
-- single?: true
-- Fetch the operation area for a transportation service as a GeoJSON document
SELECT ST_AsGeoJSON(ST_Collect(location))
  FROM operation_area
 WHERE "transport-service-id" = :transport-service-id;
