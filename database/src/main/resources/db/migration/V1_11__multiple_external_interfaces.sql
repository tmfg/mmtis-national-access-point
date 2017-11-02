-- Allow multiple external interfaces to be defined

CREATE TABLE "external-interface-description" (
  "transport-service-id" INTEGER REFERENCES "transport-service" (id),
  "external-interface" service_link,
  "format" TEXT,
  "ckan-resource-id" TEXT REFERENCES "resource" (id)
);
COMMENT ON TABLE "external-interface-description" IS
E'Store links to other machine readable interfaces that describe the transport service. These are URLs to APIs or descriptors that are not created within NAP';

-- Remove old external resource id (as we can now have multiple external resources)
ALTER TABLE "transport-service" DROP COLUMN "ckan-external-resource-id";
