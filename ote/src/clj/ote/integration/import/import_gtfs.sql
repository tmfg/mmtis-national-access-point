-- name: gtfs-set-package-geometry
SELECT gtfs_set_package_geometry(:package-id::INTEGER);

-- name: fetch-count-service-packages
SELECT COUNT(p.id) as "package-count" FROM gtfs_package p WHERE p."transport-service-id" = :service-id;
