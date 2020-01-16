-- name: fetch-place-geojson-by-id
-- single?: true
SELECT ST_AsGeoJSON(location)
  FROM places
 WHERE id = :id


-- name: link-transport-service-place!
-- Copies operation area from places table.
INSERT INTO operation_area
       ("transport-service-id", "description", "location", "primary?")
VALUES (:transport-service-id,
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        (select "location" from places where id = :place-id),
        :primary?);

-- name: fetch-operation-area-geojson
SELECT id, "transport-service-id", ST_AsGeoJSON(location)
  FROM "operation_area"
 WHERE "transport-service-id" = :transport-service-id;

-- name: insert-geojson-for-transport-service<!
-- return-keys: ["id"]
-- Insert an operation area with GeoJSON data. Sets SRID to 4326, the only projection supported by geojson since 2016.
INSERT INTO operation_area ("transport-service-id", "location", "description", "primary?")
VALUES (:transport-service-id,
        ST_SetSRID(ST_GeomFromGeoJSON(:geojson), 4326),
        ARRAY[ROW('FI',:name)::localized_text]::localized_text[],
        :primary?);

-- name: insert-spatial-search-custom-area!
-- Inserts the custom area to a search table which is used in spatial search.
-- For geometries without surface area, use a carefully selected constant value
INSERT INTO "spatial-relations-custom-areas" ("search-area", "operation-area-id", "search-area-size", "matching-area-size")
SELECT
    pl.id,
    oa.id,
    ST_Area(pl.location),
    CASE
     WHEN ST_GeometryType(oa.location) in ('ST_Point', 'ST_Linestring')
          THEN 3e-4
          ELSE ST_Area(ST_Intersection(ST_MakeValid(oa.location), ST_MakeValid(pl.location)))
    END
FROM operation_area oa,
     places pl
WHERE oa.id = :operation-area-id
  AND oa.location && pl.location
  AND ST_Relate(ST_MakeValid(oa.location), pl.location, 'T********');

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

-- name: copy-operation-area
INSERT INTO operation_area ("transport-service-id", description, location, "primary?")
 SELECT :new-service-id as "transport-service-id", oa.description, oa.location, oa."primary?"
   FROM operation_area oa
  WHERE oa."transport-service-id" = :old-service-id
    AND oa.id IN (:ids)
    RETURNING id;