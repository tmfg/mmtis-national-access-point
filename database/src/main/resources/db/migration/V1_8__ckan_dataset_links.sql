-- Add foreign key references to CKAN dataset (package) and resource

ALTER TABLE "transport-service"
  ADD COLUMN name VARCHAR(200),
  ADD COLUMN "ckan-dataset-id" TEXT REFERENCES package (id),
  ADD COLUMN "ckan-resource-id" TEXT REFERENCES resource (id),
  ADD COLUMN "ckan-external-resource-id" TEXT REFERENCES resource (id);

COMMENT ON COLUMN "transport-service".name IS
E'Human readable name of the service, not the same as CKAN name which is part of URL';

COMMENT ON COLUMN "transport-service"."ckan-dataset-id" IS
E'When publishing a service to CKAN, this id links to the dataset (package) in CKAN.
NULL for unpublished services.';

COMMENT ON COLUMN "transport-service"."ckan-resource-id" IS
E'When publishing a service to CKAN, this id links to the resource that points to the
OTE published GeoJSON export URL.
NULL for unpublished services.';

COMMENT ON COLUMN "transport-service"."ckan-external-resource-id" IS
E'When publishing a service that has an external API URL to CKAN, this id links to the
resource that points to the external URL. A service may have both the OTE resource and
an external one.
NULL FOR unpublished services.';
