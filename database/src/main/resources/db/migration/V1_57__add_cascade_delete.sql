-- ADD on delete cascade to service_company table
ALTER TABLE service_company DROP CONSTRAINT "service_company_transport-service-id_fkey";

ALTER TABLE service_company
  ADD CONSTRAINT "service_company_transport-service-id_fkey"
  FOREIGN KEY ("transport-service-id")
  REFERENCES "transport-service" (id)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;