-- name: fetch-operation-area-with-id-for-service
-- Fetch the operation area for a transportation service as a GeoJSON document with unique ID
SELECT id,
       ST_AsGeoJSON(ST_SetSRID(location,4326)) as geojson,
       "primary?",
       (CASE WHEN "feature_id" IS NULL THEN
           ("description"[1]::localized_text).text
        ELSE
           "feature_id"
        END) as "feature-id"
  FROM operation_area
 WHERE "transport-service-id" = :transport-service-id;
