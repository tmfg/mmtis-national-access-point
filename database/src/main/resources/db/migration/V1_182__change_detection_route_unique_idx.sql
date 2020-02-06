-- drop unique index to "detection-route" table using columns package-id and route-hash-id
DROP INDEX "detection-route_package-id_route-hash-id_uindex";
-- Create new unique index to "detection-route" table using columns package-id and route-hash-id
CREATE UNIQUE INDEX "detection-route_route_id_package-id_route-hash-id_uindex" ON "detection-route" ("route-id","package-id", "route-hash-id");