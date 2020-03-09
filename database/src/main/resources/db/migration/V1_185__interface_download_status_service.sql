-- Remove interface-id from external-interface-download-status and replace it with service id
-- Add service-id
ALTER TABLE "external-interface-download-status"
    ADD COLUMN "transport-service-id" INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE ON UPDATE CASCADE;


-- UPDATE all existing rows with service-id
UPDATE  "external-interface-download-status" eid
   SET "transport-service-id" = i."transport-service-id"
  FROM "external-interface-description" i
 WHERE i.id = eid."external-interface-description-id";

-- Remove foreign key from external-interface-download-status
ALTER TABLE "external-interface-download-status"
    DROP CONSTRAINT "external-interface-download-s_external-interface-descripti_fkey";

-- Remove interface if
--ALTER TABLE "external-interface-download-status"
--    DROP COLUMN "external-interface-description-id";
