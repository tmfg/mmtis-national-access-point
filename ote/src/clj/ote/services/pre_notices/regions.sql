-- name: fetch-regions
SELECT numero as id, nimi as name
  FROM finnish_regions;

-- name: fetch-region-geometry
-- single?: true
SELECT ST_AsGeoJSON(location)
  FROM finnish_regions
 WHERE numero = :id;
