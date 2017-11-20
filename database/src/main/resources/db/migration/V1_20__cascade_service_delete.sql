ALTER TABLE operation_area
 DROP CONSTRAINT "operation_area_transport-service-id_fkey",
  ADD CONSTRAINT "operation_area_transport-service-id_fkey"
      FOREIGN KEY ("transport-service-id")
      REFERENCES "transport-service" (id)
      ON DELETE CASCADE;

ALTER TABLE "external-interface-description"
 DROP CONSTRAINT "external-interface-description_transport-service-id_fkey",
  ADD CONSTRAINT "external-interface-description_transport-service-id_fkey"
      FOREIGN KEY ("transport-service-id")
      REFERENCES "transport-service" (id)
      ON DELETE CASCADE;

-- When CKAN dataset is deleted, remove OTE information as well
ALTER TABLE "transport-service"
 DROP CONSTRAINT "transport-service_ckan-dataset-id_fkey",
  ADD CONSTRAINT "transport-service_ckan-dataset-id_fkey"
      FOREIGN KEY ("ckan-dataset-id")
      REFERENCES package (id)
      ON DELETE CASCADE;

ALTER TABLE "external-interface-description"
 DROP CONSTRAINT "external-interface-description_ckan-resource-id_fkey",
  ADD CONSTRAINT "external-interface-description_ckan-resource-id_fkey"
      FOREIGN KEY ("ckan-resource-id")
      REFERENCES resource (id)
      ON DELETE CASCADE;

ALTER TABLE "transport-service"
 DROP CONSTRAINT "transport-service_ckan-resource-id_fkey",
  ADD CONSTRAINT "transport-service_ckan-resource-id_fkey"
      FOREIGN KEY ("ckan-resource-id")
      REFERENCES  resource (id)
      ON DELETE CASCADE;
