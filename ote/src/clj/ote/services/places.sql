-- name: fetch-place-geojson-by-id
-- single?: true
SELECT ST_AsGeoJSON(location)
  FROM places
 WHERE id = :id


-- name: link-transport-service-place!
INSERT INTO operation_area
       ("transport-service-id", "description", "location", "primary?")
VALUES (:transport-service-id,
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        (SELECT "location"
           FROM places
          WHERE id = :place-id),
        true);

-- name: fetch-operation-area-geojson
SELECT id, "transport-service-id", ST_AsGeoJSON(location)
  FROM "operation_area"
 WHERE "transport-service-id" = :transport-service-id

-- name: insert-geojson-for-transport-service!
-- Insert an operation area with GeoJSON data.
INSERT INTO operation_area ("transport-service-id", "location", "description", "primary?")
VALUES (:transport-service-id,
        ST_GeomFromGeoJSON(:geojson),
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        true);
