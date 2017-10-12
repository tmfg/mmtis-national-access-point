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
           FROM finnish_municipalities
          WHERE natcode = :place-id),
        true);
