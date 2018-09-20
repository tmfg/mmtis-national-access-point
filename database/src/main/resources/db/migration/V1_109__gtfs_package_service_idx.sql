-- Speed up selection of correct packages for service
CREATE INDEX g_package_service_idx ON gtfs_package ("transport-service-id");
