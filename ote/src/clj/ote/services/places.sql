-- name: fetch-place-geojson-by-id
-- single?: true
SELECT ST_AsGeoJSON(location)
  FROM places
 WHERE id = :id


-- name: link-transport-service-place!
-- Copies operation area from places table.
-- Fills "simplified-location" column with information for spatial search
INSERT INTO operation_area
       ("transport-service-id", "description", "location", "simplified-location", "primary?")
VALUES (:transport-service-id,
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        (select "location" from places where id = :place-id),
        (SELECT ote_simplify(location) from places where id = :place-id),
        :primary?);

-- name: fetch-operation-area-geojson
SELECT id, "transport-service-id", ST_AsGeoJSON(location)
  FROM "operation_area"
 WHERE "transport-service-id" = :transport-service-id

-- name: insert-geojson-for-transport-service!
-- Insert an operation area with GeoJSON data. Sets SRID to 4326, the only projection supported by geojson since 2016.
-- Fills "simplified-location" column with information for spatial search
INSERT INTO operation_area ("transport-service-id", "location", "description", "primary?", "simplified-location")
VALUES (:transport-service-id,
        ST_SetSRID(ST_GeomFromGeoJSON(:geojson), 4326),
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        :primary?,
        ote_simplify(ST_GeomFromGeoJSON(:geojson)));

-- name: fetch-operation-area-search
-- sort operation area places search in following alphabetical order
-- finnish-region, finnish-municipality, finnish-postal, country, continent
SELECT namefin as "ote.db.places/namefin", id as "ote.db.places/id", type as "ote.db.places/type"
  FROM places
 WHERE namefin ilike :name
 ORDER BY
  CASE TYPE
  WHEN 'finnish-region' THEN 1
  WHEN 'finnish-municipality' THEN 2
  WHEN 'finnish-postal' THEN 3
  WHEN 'country' THEN 4
  WHEN 'continent' THEN 5
   END,
  namefin;
