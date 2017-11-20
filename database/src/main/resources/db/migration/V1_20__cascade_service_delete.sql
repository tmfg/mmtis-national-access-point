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
