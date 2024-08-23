-- name: ping-database
-- single?: true
SELECT :now;

-- name: simple-data-check
-- single?: true
SELECT COUNT(*) FROM "transport-operator";