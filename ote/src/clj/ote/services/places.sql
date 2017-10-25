-- name: fetch-place-geojson-by-id
-- single?: true
SELECT ST_AsGeoJSON(location)
  FROM places
 WHERE id = :id


-- name: clear-transport-service-places!
DELETE FROM operation_area WHERE "transport-service-id" = :transport-service-id

-- name: link-transport-service-place!
INSERT INTO operation_area
       ("transport-service-id", "description", "location", "primary?")
VALUES (:transport-service-id,
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        (SELECT "location"
           FROM places
          WHERE id = :place-id),
        true);

-- name: save-point-for-transport-service!
INSERT INTO operation_area
       ("id", "transport-service-id", "location", "primary?")
VALUES (
        :id,
        :transport-service-id,
        ST_MakePoint(:x, :y)::geometry,
        true)
ON CONFLICT ("id") DO UPDATE
  SET location = ST_MakePoint(:x, :y)::geometry;

-- name: insert-point-for-transport-service!
INSERT INTO operation_area
       ("transport-service-id", "location", "primary?")
VALUES (
        :transport-service-id,
        ST_MakePoint(:x, :y)::geometry,
        true);

-- name: fetch-operation-area-geojson
SELECT id, "transport-service-id", ST_AsGeoJSON(location)
  FROM "operation_area"
 WHERE "transport-service-id" = :transport-service-id